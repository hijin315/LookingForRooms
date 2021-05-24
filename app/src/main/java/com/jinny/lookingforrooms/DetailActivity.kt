package com.jinny.lookingforrooms

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.jinny.lookingforrooms.databinding.ActivityDetailBinding
import java.lang.Exception

class DetailActivity : AppCompatActivity() {
    private var binding: ActivityDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val data: RoomsModel = intent.getSerializableExtra("data") as RoomsModel

        Glide.with(binding!!.detailImageView.context)
            .load(data.imgUrl)
            .into(binding!!.detailImageView)

        binding!!.apply {
            detailTitleTextView.text = data.title ?: "정보없음"
            detailAddressTextView.text = data.address ?: "정보없음"
            if (!data.price.isNullOrBlank()) detailPriceTextView.text =
                data.price.replace(" | ", "\n")
            if (!data.hourInfo.isNullOrBlank()) detailHourTextView.text =
                data.hourInfo.replace(" | ", "\n")
            if (data.hasBooking) {
                bookingButton.visibility = View.VISIBLE
                bookingButton.setOnClickListener { goBookingSite(data.bookingUrl) }
            }

            detailCallTextView.text = data.tel ?: "정보없음"
            detailCallTextView.setOnClickListener { Calling(data.tel) }
            shareButton.setOnClickListener { shareInfo(data) }
        }
    }

    private fun shareInfo(data: RoomsModel) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "[방탈출 고고씽!\uD83C\uDFC3] 지금 \"${data.title}\"를 확인해보세요.\n\uD83D\uDC49 https://m.place.naver.com/place/${data.id}  "
            )
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun Calling(number: String) {
        Log.d("hihi", number)
//        var intent = Intent(Intent.ACTION_CALL)
//        intent.data = Uri.parse(number)
//        if (intent.resolveActivity(packageManager) != null) {
//            startActivity(intent)
//        }
    }

    private fun goBookingSite(dataUri: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(dataUri)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}