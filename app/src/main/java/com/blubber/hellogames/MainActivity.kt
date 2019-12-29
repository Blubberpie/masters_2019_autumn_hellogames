package com.blubber.hellogames

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import retrofit2.Retrofit
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity()
//    , View.OnClickListener
{

    private val gameBaseUrl = "https://androidlessonsapi.herokuapp.com/api/"
    private val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
    private val retrofit = Retrofit.Builder()
        .baseUrl(gameBaseUrl)
        .addConverterFactory(jsonConverter)
        .build()
    private val service = retrofit.create(WebServiceInterface::class.java)
    private var fourGamesMap = LinkedHashMap<ImageView, GameObject>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrieveGamesCallback = object : Callback<List<GameObject>> {
            override fun onFailure(call: Call<List<GameObject>>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "Failed to get list of games!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<List<GameObject>>,
                response: Response<List<GameObject>>
            ) {
                if (response.code() == 200) {
                    val gameList = response.body()!!.shuffled()
                    val gameImageViews =
                        arrayOf(game_top_left, game_top_right, game_bottom_left, game_bottom_right)
                    for ((i, gameImageView) in gameImageViews.withIndex()) {
                        val gameObj = gameList[i]
                        fourGamesMap[gameImageViews[i]] = gameObj
                        Glide.with(this@MainActivity).load(gameObj.picture).into(gameImageView)
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        service.getAllGames().enqueue(retrieveGamesCallback)

        val retrieveGameDetailsCallback = object : Callback<GameDetailsObject> {
            override fun onFailure(call: Call<GameDetailsObject>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to get game details!", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<GameDetailsObject>,
                response: Response<GameDetailsObject>
            ) {
                if(response.code() == 200) {
                    val gameDetailsObj = response.body()
                    Toast.makeText(this@MainActivity, "Yay, ${gameDetailsObj!!.name}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        game_top_left.setOnClickListener {
            service.getDetailsByGameId(fourGamesMap[it]!!.id).enqueue(retrieveGameDetailsCallback)
        }

        game_top_right.setOnClickListener {
            service.getDetailsByGameId(fourGamesMap[it]!!.id).enqueue(retrieveGameDetailsCallback)
        }

        game_bottom_left.setOnClickListener {
            service.getDetailsByGameId(fourGamesMap[it]!!.id).enqueue(retrieveGameDetailsCallback)
        }

        game_bottom_right.setOnClickListener {
            service.getDetailsByGameId(fourGamesMap[it]!!.id).enqueue(retrieveGameDetailsCallback)
        }
    }
}
