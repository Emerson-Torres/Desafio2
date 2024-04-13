package com.example.proyectorestdesafio2
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONException

class MainActivity : AppCompatActivity(), PlatilloAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlatilloAdapter

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_muestra, menu)
        return true
    }
    override fun onItemClick(idPlatillo: String) {
        // Abrir el nuevo layout y enviar el idPlatillo
        val intent = Intent(this, DetallesPlatilloActivity::class.java)
        intent.putExtra("ID_PLATILLO", idPlatillo)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PlatilloAdapter()
        recyclerView.adapter = adapter

        // Configurar el listener después de inicializar el adaptador
        adapter.setOnItemClickListener(this)

        val url = "http://10.0.18.42/apicodeigniter/listar"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONArray> { response ->
                try {
                    val platillos = ArrayList<Platillo>()
                    for (i in 0 until response.length()) {
                        val platillo = response.getJSONObject(i)
                        val idPlatillo = platillo.getString("idPlatillo")
                        val nombrePlatillo = platillo.getString("nombrePlatillo")
                        val categoria = platillo.getString("categoria")
                        val precio = platillo.getString("precio")
                        val rutaImg = platillo.getString("rutaImg")

                        platillos.add(Platillo(idPlatillo, nombrePlatillo, categoria, precio, rutaImg))
                    }

                    adapter.setData(platillos)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al procesar JSON", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error en la solicitud: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}

data class Platillo(
    val idPlatillo: String,
    val nombrePlatillo: String,
    val categoria: String,
    val precio: String,
    val rutaImg: String
)

class PlatilloAdapter : RecyclerView.Adapter<PlatilloAdapter.PlatilloViewHolder>() {

    private var platillos = listOf<Platillo>()

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatilloViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_platillo, parent, false)
        return PlatilloViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlatilloViewHolder, position: Int) {
        holder.bind(platillos[position])
    }

    override fun getItemCount(): Int {
        return platillos.size
    }

    fun setData(data: List<Platillo>) {
        platillos = data
        notifyDataSetChanged()
    }

    inner class PlatilloViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        private val buttonVerDetalles: Button = itemView.findViewById(R.id.buttonVerDetalles)

        fun bind(platillo: Platillo) {
            Glide.with(itemView.context).load(platillo.rutaImg).into(imageView)
            textViewNombre.text = "Nombre: ${platillo.nombrePlatillo}\n" +
                    "Categoría: ${platillo.categoria}\n" +
                    "Precio: $ ${platillo.precio}"
            buttonVerDetalles.setOnClickListener {
                onItemClickListener?.onItemClick(platillo.idPlatillo)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(idPlatillo: String)
    }
}
