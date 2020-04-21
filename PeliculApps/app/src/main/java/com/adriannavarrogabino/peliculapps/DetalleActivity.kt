package com.adriannavarrogabino.peliculapps

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.adriannavarrogabino.peliculapps.entity.Pelicula
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_detalle.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class DetalleActivity : AppCompatActivity() {

    /*
    0 -> Ver Película
    1 -> Añadir película
     */
    var pelicula: Pelicula? = null
    var id: Long? = null
    var editar: Int? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        id = intent.getLongExtra("id", 0)
        editar = intent.getIntExtra("editar", 0)

        if (editar != 1) {
            var myAsyncTask: CargaPelicula = CargaPelicula(this)
            myAsyncTask.execute(100, 20)
        }

        if (editar != 0) {
            tituloET.isEnabled = true
            resumenET.isEnabled = true
            fechaVistaET.isEnabled = true
            okLL.visibility = View.VISIBLE
            editarLL.visibility = View.GONE
        } else {
            tituloET.isEnabled = false
            resumenET.isEnabled = false
            fechaVistaET.isEnabled = false
            okLL.visibility = View.GONE
            editarLL.visibility = View.VISIBLE
        }

        volverBtn.setOnClickListener {
            finish()
        }

        cancelarBtn.setOnClickListener {
            if (editar == 1) {
                finish()
            } else {
                tituloET.isEnabled = false
                resumenET.isEnabled = false
                fechaVistaET.isEnabled = false
                okLL.visibility = View.GONE
                editarLL.visibility = View.VISIBLE
            }
        }

        editarBtn.setOnClickListener {
            tituloET.isEnabled = true
            resumenET.isEnabled = true
            fechaVistaET.isEnabled = true
            okLL.visibility = View.VISIBLE
            editarLL.visibility = View.GONE
        }

        okBtn.setOnClickListener {
            if (tituloET.text.toString() != "" && resumenET.text.toString() != "" &&
                fechaVistaET.text.toString() != ""
            ) {

                if (editar == 1) {
                    var asyncGuardar: GuardarPelicula = GuardarPelicula(this)
                    asyncGuardar.execute(100, 20)
                } else {
                    var actualizarPelicula: ActualizarPelicula = ActualizarPelicula(this)
                    actualizarPelicula.execute(100, 20)
                }

                finish()
            }
        }

        fechaVistaET.setOnFocusChangeListener { _, hasFocus ->
            elegirFecha(hasFocus)
        }

        fechaVistaET.setOnClickListener {
            elegirFecha(fechaVistaET.hasFocus())
        }
    }

    fun elegirFecha(hasFocus: Boolean) {
        if (fechaVistaET.isEnabled && hasFocus) {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _,
                                                                       i, i2, i3 ->
                cal.set(Calendar.YEAR, i)
                cal.set(Calendar.MONTH, i2)
                cal.set(Calendar.DAY_OF_MONTH, i3)
                fechaVistaET.setText(
                    String.format(
                        "%02d/%02d/%04d",
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.MONTH) + 1,
                        cal.get(Calendar.YEAR)
                    )
                )
            }
            DatePickerDialog(
                this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private inner class CargaPelicula(
        val contexto: Context
    ) : AsyncTask<Any, Any, Pelicula>() {

        override fun doInBackground(vararg p0: Any?): Pelicula {
            if (id != 0L) {
                val json: String = URL(MainActivity.urlEndpoint + "peliculas/" + id).readText()

                println(json)

                val gson: Gson = Gson()

                pelicula = gson.fromJson(json, Pelicula::class.java)
            }

            if (pelicula == null) {
                pelicula = Pelicula()
            }

            return pelicula!!
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: Pelicula?) {
            super.onPostExecute(result)

            if (pelicula != null) {
                tituloET.setText(pelicula!!.titulo.toString())
                resumenET.setText(pelicula!!.resumen.toString())
                val fechaMalFormateada = pelicula!!.fechaVista.toString().split("-")
                fechaVistaET.setText(
                    String.format(
                        "%02d/%02d/%04d",
                        fechaMalFormateada[2].toInt(),
                        fechaMalFormateada[1].toInt(),
                        fechaMalFormateada[0].toInt()
                    )
                )
            }
        }
    }

    private inner class GuardarPelicula(
        val contexto: Context
    ) : AsyncTask<Any, Any, Pelicula>() {

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg p0: Any?): Pelicula {
            val peliculaNueva: Pelicula = Pelicula()
            peliculaNueva.titulo = tituloET.text.toString()
            peliculaNueva.resumen = resumenET.text.toString()
            var fechaMalFormateada = fechaVistaET.text.toString().split("/")
            peliculaNueva.fechaVista = String.format(
                "%04d-%02d-%02d",
                fechaMalFormateada[2].toInt(),
                fechaMalFormateada[1].toInt(),
                fechaMalFormateada[0].toInt()
            )

            var gson = Gson()
            var jsonPelicula = gson.toJson(peliculaNueva)

            println(jsonPelicula)

            URL(MainActivity.urlEndpoint + "peliculas")
                .openConnection()
                .let {
                    it as HttpURLConnection
                }.apply {
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    requestMethod = "POST"
                    doOutput = true
                    val outputWriter = OutputStreamWriter(outputStream)
                    outputWriter.write(jsonPelicula)
                    outputWriter.flush()
                }.let {
                    if (it.responseCode == 201) it.inputStream else it.errorStream
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

            return peliculaNueva
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: Pelicula?) {
            super.onPostExecute(result)

            tituloET.setText("")
            resumenET.setText("")
            fechaVistaET.setText("")
        }
    }

    private inner class ActualizarPelicula(
        val contexto: Context
    ) : AsyncTask<Any, Any, Pelicula>() {

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg p0: Any?): Pelicula {
            val peliculaNueva: Pelicula = Pelicula()
            peliculaNueva.titulo = tituloET.text.toString()
            peliculaNueva.resumen = resumenET.text.toString()
            var fechaMalFormateada = fechaVistaET.text.toString().split("/")
            peliculaNueva.fechaVista = String.format(
                "%04d-%02d-%02d",
                fechaMalFormateada[2].toInt(),
                fechaMalFormateada[1].toInt(),
                fechaMalFormateada[0].toInt()
            )

            var gson = Gson()
            var jsonPelicula = gson.toJson(peliculaNueva)

            println(jsonPelicula)

            URL(MainActivity.urlEndpoint + "peliculas/" + pelicula!!.id)
                .openConnection()
                .let {
                    it as HttpURLConnection
                }.apply {
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    requestMethod = "PUT"
                    doOutput = true
                    val outputWriter = OutputStreamWriter(outputStream)
                    outputWriter.write(jsonPelicula)
                    outputWriter.flush()
                }.let {
                    if (it.responseCode == 201) it.inputStream else it.errorStream
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

            return peliculaNueva
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: Pelicula?) {
            super.onPostExecute(result)

            tituloET.setText("")
            resumenET.setText("")
            fechaVistaET.setText("")
        }
    }
}
