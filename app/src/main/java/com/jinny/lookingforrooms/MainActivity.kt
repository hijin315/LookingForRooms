package com.jinny.lookingforrooms

import android.content.Intent
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.TestLooperManager
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.jinny.lookingforrooms.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener {
    private var binding: ActivityMainBinding? = null
    private lateinit var locationSource: FusedLocationSource
    private val mapView: MapView by lazy {
        binding!!.mapView
    }
    private val viewPager: ViewPager2 by lazy {
        binding!!.roomsViewPager
    }
    private val currentLocationButton: LocationButtonView by lazy {
        binding!!.currentLocationButton
    }
    private val bottomSheetTextView : TextView by lazy {
        findViewById(R.id.bottomSheetTextView)
    }
    private val viewPagerAdpater = RoomViewPagerAdpater(itemClicked = {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "[지금 이가격에 예약해보세요!!] ${it.title} ${it.price} \n사진보러가기 : ${it.imgUrl}")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent,null))
    })
    private val recyclerViewAdapter = RoomListAdpater()
    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.recyclerView)
    }

    private lateinit var naverMap: NaverMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        mapView.onCreate(savedInstanceState)

        // OnMapReadyCallback 넘겨줌
        mapView.getMapAsync(this)
        viewPager.adapter = viewPagerAdpater
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val selectedRoomModel = viewPagerAdpater.currentList[position]
                val cameraUpdate =
                    CameraUpdate.scrollTo(LatLng(selectedRoomModel.lat, selectedRoomModel.lng))
                        .animate(CameraAnimation.Easing)

                naverMap.moveCamera(cameraUpdate)
            }
        })
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map

        // 확대/축소 정도 조절!
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        // 일단 강남역으로 추가했으!!!
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.497885, 127.027512))
        naverMap.moveCamera(cameraUpdate)

        val uiSetting = naverMap.uiSettings
        // true로 하면 기본 버튼
        uiSetting.isLocationButtonEnabled = false
        // 내가 만든 버튼으로 연결
        currentLocationButton.map = naverMap

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        //지도에 꽂을 마커!!
//        val marker = Marker()
//        marker.position = LatLng(37.497885, 127.027512)
//        marker.map = naverMap
//        marker.icon = MarkerIcons.RED
//        marker.iconTintColor = Color.RED

        getRoomsListFromAPI()
    }

    private fun getRoomsListFromAPI() {
        val retrofit = Retrofit.Builder().baseUrl("https://run.mocky.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(RoomsService::class.java).also {
            // 또한 getRooms리스트 함수를 실행할꾸
            it.getRoomsList()
                .enqueue(object : Callback<RoomsDto> {
                    override fun onResponse(call: Call<RoomsDto>, response: Response<RoomsDto>) {
                        if (response.isSuccessful.not()) {
                            // 실패 할 경우!
                            return
                        }
                        response.body()?.let { dto ->
                            //Log.d("rrrrr",dto.toString())
                            updateMarker(dto.items)
                            viewPagerAdpater.submitList(dto.items)
                            recyclerViewAdapter.submitList(dto.items)
                            bottomSheetTextView.text = "${dto.items.size}개의 숙소"
                        }
                    }

                    override fun onFailure(call: Call<RoomsDto>, t: Throwable) {

                    }
                })
        }
    }

    private fun updateMarker(rooms: List<RoomsModel>) {
        rooms.forEach { room ->
            val marker = Marker()
            marker.position = LatLng(room.lat, room.lng)
            marker.onClickListener = this
            marker.map = naverMap
            marker.tag = room.id
            marker.icon = MarkerIcons.RED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                // 권한이 거부되었음을 네이버맵에 알림!!!
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onClick(overlay: Overlay): Boolean {
        val selectedModel = viewPagerAdpater.currentList.firstOrNull() {
            it.id == overlay.tag
        }
        selectedModel?.let {
            val position = viewPagerAdpater.currentList.indexOf(it)
            viewPager.currentItem = position
        }
        return true
    }
}