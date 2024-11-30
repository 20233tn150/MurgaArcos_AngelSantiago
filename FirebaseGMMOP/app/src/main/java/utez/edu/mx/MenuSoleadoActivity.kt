package utez.edu.mx

import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.radiobutton.MaterialRadioButton

class MenuSoleadoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_soleado)

        val icMenuView = findViewById<ImageView>(R.id.icMenuView)
        // Abrir el icono cuando sea tocado
        icMenuView.setOnClickListener {
            showPopupMenu(it)
        }

        val radioButton1 = findViewById<MaterialRadioButton>(R.id.radioButton)
        val radioButton2 = findViewById<RadioButton>(R.id.radioButton2)
        val radioButton3 = findViewById<RadioButton>(R.id.radioButton3)
        val radioButton4 = findViewById<RadioButton>(R.id.radioButton4)

        // Asignamos un OnClickListener a cada RadioButton
        radioButton1.setOnClickListener {
            showToast("Busca a tus compitas y sumergete en la emoción")
        }

        radioButton2.setOnClickListener {
            showToast("Si te hace falta rey/reina... 100 sentadillas con 200kg")
        }

        radioButton3.setOnClickListener {
            showToast("Mejor vete a dormir mi todo tibio")
        }

        radioButton4.setOnClickListener {
            showToast("Te recomendamos el de maracuyá. Una joya")
        }
    }

    // Método para mostrar el menú y te mande a otras vistas
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_popup, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.opcion1 -> {
                    // Navegar a otra vista
                    val intent = Intent(this, MenuModeradoActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.opcion2 -> {
                    // Otra acción
                    val intent = Intent(this, MenuLluviosoActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.opcion3 -> {
                    // Otra acción
                    val intent = Intent(this, MenuSoleadoActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.opcion4 -> {
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}