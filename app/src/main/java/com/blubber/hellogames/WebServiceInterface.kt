package com.blubber.hellogames

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WebServiceInterface {

    @GET("game/list")
    fun getAllGames() : Call<List<GameObject>>

    @GET("game/details")
    fun getDetailsByGameId(@Query("game_id") gameId : Int) : Call<GameDetailsObject>
}