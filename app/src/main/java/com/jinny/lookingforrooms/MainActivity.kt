package com.jinny.lookingforrooms

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jinny.lookingforrooms.databinding.ActivityMainBinding

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding!!.searchButton.setOnClickListener {
            if (binding!!.searchEditText.text.isNullOrEmpty()) {
                binding!!.textField.error = "검색어를 입력해주세요."
            } else {
                val searchWord = binding!!.searchEditText.text.toString()
                binding!!.progressbar.visibility = View.VISIBLE
                getRoomsList(searchWord)
            }
        }
    }

    private fun getRoomsList(searchWord: String) {

        val retrofit = Retrofit.Builder().baseUrl("https://map.naver.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(RoomsService::class.java).also {

            // 또한 getRooms리스트 함수를 실행할꾸
            it.getRoomsList(query = (searchWord.replace(" ","")  + " 방탈출카페"))
                .enqueue(object : Callback<RoomsResponse> {
                    override fun onResponse(
                        call: Call<RoomsResponse>,
                        response: Response<RoomsResponse>
                    ) {
                        if (response.isSuccessful.not()) {
                            // 실패 할 경우
                            binding!!.progressbar.visibility = View.INVISIBLE
                            Toast.makeText(
                                this@MainActivity,
                                "에러! 잠시 후 다시 시도해주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }
                        response.body()?.let { dto ->
                            binding!!.progressbar.visibility = View.INVISIBLE
                            if (dto.roomsResult.place == null) {
                                binding!!.textField.error = "검색 결과가 없습니다. 또는 지역명을 확인해주세요."
                                return
                            }
                            val datalist = dto.roomsResult.place.items
                            val intent = Intent(this@MainActivity, ResultActivity::class.java)
                            val list: ArrayList<RoomsModel> = ArrayList<RoomsModel>(datalist)
                            intent.putExtra("datalist", list)
                            startActivity(intent)

                        }
                    }

                    override fun onFailure(call: Call<RoomsResponse>, t: Throwable) {

                    }
                })
        }

    }
}