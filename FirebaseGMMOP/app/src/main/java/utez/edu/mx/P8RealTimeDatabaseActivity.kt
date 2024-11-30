package utez.edu.mx

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import utez.edu.mx.adapters.TaskAdapter
import utez.edu.mx.models.Task

class P8RealTimeDatabaseActivity : AppCompatActivity() {
    // Referencias a la base de datos de Firebase
    private lateinit var database: DatabaseReference

    // RecyclerView y lista para mostrar las tareas
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskList: MutableList<Task>
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_p8_real_time_database)

        // Inicializa la referencia de la BD en el nodo "tasks"
        database = FirebaseDatabase.getInstance().reference.child("tasks")

        // Inicializa la lista de tareas y el adaptador
        taskList = mutableListOf()
        adapter = TaskAdapter(taskList)

        // Configura el RecyclerView con un layout lineal y el adaptador
        taskRecyclerView = findViewById(R.id.taskRecyclerView)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = adapter

        // Configura el botón de añadir para agregar una nueva tarea
        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener{ addTask()}

            // Cargar las tareas existentes
            loadTasks()
        }

        private fun addTask() {
            val title = findViewById<EditText>(R.id.titleEditText).text.toString()
            val description = findViewById<EditText>(R.id.descriptionEditText).text.toString()

            // Genera un ID único para la tarea
            val taskId = database.push().key ?: return

            // Crea un objeto Task con los datos ingresados
            val task = Task(taskId, title, description)

            // Guarda la tarea en la BD usando el ID como clave
            database.child(taskId).setValue(task).addOnCompleteListener{
                if (it.isSuccessful){
                    Toast.makeText(this, "Tarea añadida", Toast.LENGTH_SHORT).show()
                    findViewById<EditText>(R.id.titleEditText).text.clear()
                    findViewById<EditText>(R.id.descriptionEditText).text.clear()
                } else {
                    Toast.makeText(this, "Error al añadir la tarea", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Método para cargar las tareas desde la BD
        private fun loadTasks() {
            // Agregar un oyente a la BD para recibir actualizaciones en tiempo real
            database.addValueEventListener(object  : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Limpia la lista de tareas para eviar duplicaciones al volvera cargar
                    taskList.clear()
                    // Itera sobre cada entrada en el snapshot recibido de la BD
                    for (taskSnapshot in snapshot.children) {
                        // Obtiene los valores de los campos taskId, title y description de ccada tarea
                        val taskId = taskSnapshot.child("taskId").getValue(String::class.java)
                        val title = taskSnapshot.child("title").getValue(String::class.java)
                        val description = taskSnapshot.child("description").getValue(String::class.java)

                        // Si los valores de los campos no son nulos, crea una instancia de Task y la añade a la lista
                        if (taskId != null && title != null && description != null){
                            val task = Task(taskId, title, description)
                            taskList.add(task)
                        } else {
                            // Si algún campo falta o tiene un formato inesperado, registra  una advertencia en el log
                            Log.w("P8RealTimeDatabase", "skipping task with invalid structure")
                        }
                    }
                    // Notificar el adapter que la lista de tareas ha cambiado para actualizar la interfaz de usuario
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    // Muestra un mensaje de error si ocurre un problema al cargar los datos de la BD
                    Toast.makeText(this@P8RealTimeDatabaseActivity, "Error al cargar tareas", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }