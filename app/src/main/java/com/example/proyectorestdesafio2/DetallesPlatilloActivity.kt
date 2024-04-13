package com.example.proyectorestdesafio2
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONException

class DetallesPlatilloActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_platillo)


        // Obtener el idPlatillo enviado desde MainActivity
        val idPlatillo = intent.getStringExtra("ID_PLATILLO")

        // URL para obtener el platillo por su ID
        val url = "http://10.0.18.42/apicodeigniter/obtenerPorId/$idPlatillo"

        // Realizar la solicitud HTTP para obtener los detalles del platillo
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    // Obtener el primer elemento del JSONArray
                    val platillo = response.getJSONObject(0)

                    // Mostrar la información del platillo en los elementos de la vista
                    findViewById<TextView>(R.id.textViewNombre).text = "Nombre: ${platillo.getString("nombrePlatillo")}"
                    findViewById<TextView>(R.id.textViewCategoria).text = "Categoría: ${platillo.getString("categoria")}"
                    findViewById<TextView>(R.id.textViewPrecio).text = "Precio: ${platillo.getString("precio")}"

                    // Cargar la imagen del platillo en el ImageView
                    val imageView = findViewById<ImageView>(R.id.imageView)
                    Glide.with(this).load(platillo.getString("rutaImg")).into(imageView)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al procesar JSON", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error en la solicitud: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        // Agregar la solicitud a la cola de solicitudes de Volley
        Volley.newRequestQueue(this).add(request)
    }
}
