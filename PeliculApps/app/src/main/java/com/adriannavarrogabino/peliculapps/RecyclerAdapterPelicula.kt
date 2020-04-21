package com.adriannavarrogabino.peliculapps

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adriannavarrogabino.peliculapps.entity.Pelicula
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RecyclerAdapterPelicula : RecyclerView.Adapter<RecyclerAdapterPelicula.ViewHolder>() {

    var peliculas: MutableList<Pelicula> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(peliculas: MutableList<Pelicula>, context: Context) {
        this.peliculas = peliculas
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = peliculas.get(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_pelicula_list, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return peliculas.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val nombre = view.findViewById(
            R.id.tituloPelicula) as TextView
        private val fechaVista = view.findViewById(
            R.id.fechaVista) as TextView

        fun bind(pelicula: Pelicula){
            nombre.text = pelicula.titulo
            val fechaMalFormateada = pelicula.fechaVista.toString().split("-")
            fechaVista.text = fechaMalFormateada[2] + "/" + fechaMalFormateada[1] + "/" + fechaMalFormateada[0]
            itemView.setOnClickListener {

                val myIntent: Intent = Intent(it.context, DetalleActivity::class.java).apply {
                    putExtra("id", pelicula.id)
                    putExtra("editar", 0)
                }
                it.context.startActivity(myIntent)
            }
            itemView.setOnLongClickListener{ v ->

                val builder =
                    AlertDialog.Builder(v.context)

                val options: MutableList<String> = ArrayList()
                options.add("Eliminar")

                val dataAdapter = ArrayAdapter(
                    v.context,
                    android.R.layout.simple_dropdown_item_1line, options
                )
                builder.setAdapter(
                    dataAdapter
                ) { _, which ->
                    when(which)
                    {
                        0 -> {
                            val dialogBuilder = AlertDialog.Builder(v.context)
                            dialogBuilder.setTitle("Eliminar película")
                            dialogBuilder.setMessage("¿Estás seguro que quieres eliminar la película?")
                            dialogBuilder.setPositiveButton(android.R.string.yes){_, _ ->
                                val borrarPelicula: EliminarPelicula = EliminarPelicula(v.context, pelicula)
                                borrarPelicula.execute(100,20)
                                remove(peliculas.indexOf(pelicula));
                            }
                            dialogBuilder.setNegativeButton(android.R.string.no, null)
                            dialogBuilder.show()
                        }
                    }
                }

                val dialog = builder.create()
                dialog.show()
                true
            }
        }
    }

    fun remove(position: Int) {
        peliculas.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, peliculas.size)
    }

    private inner class EliminarPelicula(
        val contexto: Context, val pelicula: Pelicula
    ): AsyncTask<Any, Any, Boolean>() {

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg p0: Any?): Boolean {

            URL(MainActivity.urlEndpoint + "peliculas/" + pelicula.id)
                .openConnection()
                .let {
                    it as HttpURLConnection
                }.apply {
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    requestMethod = "DELETE"
                }.let {
                    if (it.responseCode == 204) it.inputStream else it.errorStream
                }.let { streamToRead ->
                    BufferedReader(InputStreamReader(streamToRead)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                        response.toString()
                    }
                }
            return true
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
        }
    }
}