package com.jinny.lookingforrooms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.jinny.lookingforrooms.databinding.ActivityResultBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView

class ResultActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener {
    private var binding: ActivityResultBinding? = null
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
    private val bottomSheetTextView: TextView by lazy {
        findViewById(R.id.bottomSheetTextView)
    }
    private val viewPagerAdpater = RoomViewPagerAdpater(itemClicked = {
        goDetailActivity(it)
    })
    private val recyclerViewAdapter = RoomListAdpater(itemClicked = {
        goDetailActivity(it)
    })
    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.recyclerView)
    }
    lateinit var datalist: ArrayList<RoomsModel>

    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        mapView.onCreate(savedInstanceState)
        datalist = intent.getSerializableExtra("datalist") as ArrayList<RoomsModel>


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

    private fun goDetailActivity(roomsModel: RoomsModel) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("data", roomsModel)
        startActivity(intent)
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map

        // 확대/축소 정도 조절!
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        // 일단 강남역으로 추가했으!!!
//        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.497885, 127.027512))
//        naverMap.moveCamera(cameraUpdate)

        val uiSetting = naverMap.uiSettings
        // true로 하면 기본 버튼
        uiSetting.isLocationButtonEnabled = false
        uiSetting.setLogoMargin(30, 0, 0, 230)
        // 내가 만든 버튼으로 연결
        currentLocationButton.map = naverMap

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        setListWithdata(datalist)
    }

    private fun setListWithdata(datalist: ArrayList<RoomsModel>) {
        updateMarker(datalist)
        viewPagerAdpater.submitList(datalist)
        recyclerViewAdapter.submitList(datalist)
        bottomSheetTextView.text = "${datalist.size}개의 결과보기"
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