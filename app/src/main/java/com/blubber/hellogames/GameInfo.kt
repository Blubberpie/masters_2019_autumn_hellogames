package com.blubber.hellogames

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_game_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.android.synthetic.main.activity_main.*

class GameInfo : AppCompatActivity() {

    private val gameBaseUrl = "https://androidlessonsapi.herokuapp.com/api/"
    private val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
    private val retrofit = Retrofit.Builder()
        .baseUrl(gameBaseUrl)
        .addConverterFactory(jsonConverter)
        .build()
    private val service = retrofit.create(WebServiceInterface::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_info)
        val gameId : Int = intent.getIntExtra("game_id", -1)

        val retrieveGameDetailsCallback = object : Callback<GameDetailsObject> {
            override fun onFailure(call: Call<GameDetailsObject>, t: Throwable) {
                Toast.makeText(this@GameInfo, "Failed to get game details!", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<GameDetailsObject>,
                response: Response<GameDetailsObject>
            ) {
                if(response.code() == 200) {
                    val gameDetailsObj = response.body()
                    Glide.with(this@GameInfo).load(gameDetailsObj!!.picture).into(game_image)
                    val gameData = getString(R.string.game_data, gameDetailsObj.name, gameDetailsObj.type, gameDetailsObj.players, gameDetailsObj.year)
                    game_data.setText(HtmlCompat.fromHtml(gameData, HtmlCompat.FROM_HTML_MODE_LEGACY))
                    game_description.setText(gameDetailsObj.description_en)
                } else {
                    Toast.makeText(this@GameInfo, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        service.getDetailsByGameId(gameId).enqueue(retrieveGameDetailsCallback)
    }
}
