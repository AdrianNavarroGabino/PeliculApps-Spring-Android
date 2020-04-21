package com.adriannavarrogabino.peliculapps

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adriannavarrogabino.peliculapps.entity.Pelicula
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL


class MainActivity : AppCompatActivity() {

    companion object
    {
        var peliculas: MutableList<Pelicula> = mutableListOf()
        val urlEndpoint: String = "Your domain"
        val myAdapter : RecyclerAdapterPelicula = RecyclerAdapterPelicula()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT > 9) {
            val policy =
                    StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        var myAsyncTask: MyAsyncTask = MyAsyncTask(this)
        myAsyncTask.execute(100, 20)

        createBtn.setOnClickListener {
            val myIntent: Intent = Intent(it.context, DetalleActivity::class.java).apply {
                putExtra("editar", 1)
            }
            it.context.startActivity(myIntent)
        }
    }

    override fun onRestart() {
        super.onRestart()

        var myAsyncTask: MyAsyncTask = MyAsyncTask(this)
        myAsyncTask.execute(100, 20)
    }

    private inner class MyAsyncTask(
            val contexto: Context
    ): AsyncTask<Any, Any, MutableList<Pelicula>>() {

        override fun doInBackground(vararg p0: Any?): MutableList<Pelicula> {
            val json: String = URL(urlEndpoint + "peliculas").readText()

            val gson: Gson = Gson()

            peliculas = gson.fromJson(json, Array<Pelicula>::class.java).toMutableList()

            return peliculas;
        }

        override fun onPostExecute(result: MutableList<Pelicula>?) {
            super.onPostExecute(result)

            setUpRecyclerView()
        }

        fun setUpRecyclerView(){

            RVPeliculas.setHasFixedSize(true)

            RVPeliculas.layoutManager = LinearLayoutManager(applicationContext)

            myAdapter.RecyclerAdapter(peliculas, applicationContext)

            RVPeliculas.adapter = myAdapter
        }
    }
}
