package utez.edu.mx

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.radiobutton.MaterialRadioButton

class MenuSoleadoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_soleado)

        // Capturar datos enviados por el Intent
        val placeName = intent.getStringExtra("place_name") ?: "Lugar desconocido"
        val temperature = intent.getDoubleExtra("temperature", 0.0)

        // Depuración de datos recibidos
        Log.d("DEBUG", "Recibido en MenuSoleadoActivity: Lugar: $placeName, Temp: $temperature°C")
        Toast.makeText(this, "Recibido: $placeName, $temperature°C", Toast.LENGTH_SHORT).show()

        // Actualizar los TextView con los datos recibidos
        val gradosTextView = findViewById<TextView>(R.id.gradosTextView)
        val municipioTextView = findViewById<TextView>(R.id.municipioTextView)

        gradosTextView.text = "$temperature°C"
        municipioTextView.text = placeName

        // Configuración del menú emergente
        val icMenuView = findViewById<ImageView>(R.id.icMenuView)
        icMenuView.setOnClickListener {
            showPopupMenu(it)
        }

        // Configuración de las actividades recomendadas (RadioButtons)
        val radioButton1 = findViewById<MaterialRadioButton>(R.id.radioButton)
        val radioButton2 = findViewById<RadioButton>(R.id.radioButton2)
        val radioButton3 = findViewById<RadioButton>(R.id.radioButton3)
        val radioButton4 = findViewById<RadioButton>(R.id.radioButton4)

        radioButton1.setOnClickListener {
            showToast("Busca a tus compitas y sumérgete en la emoción.")
        }

        radioButton2.setOnClickListener {
            showToast("Si te hace falta energía... 100 sentadillas con 200kg.")
        }

        radioButton3.setOnClickListener {
            showToast("Mejor vete a dormir, mi todo tibio.")
        }

        radioButton4.setOnClickListener {
            showToast("Te recomendamos un smoothie de maracuyá. ¡Una joya!")
        }
    }

    // Método para mostrar el menú emergente
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_popup, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.opcion1 -> {
                    val intent = Intent(this, MenuVoid::class.java)
                    startActivity(intent)
                    true
                }

                R.id.opcion2 -> {
                    Toast.makeText(this, "Cerrando Sesión...", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    // Método para mostrar mensajes cortos en Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
