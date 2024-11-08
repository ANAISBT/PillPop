package com.example.pillpop

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pillpop.databinding.ActivityPrincipalViewBinding

class PrincipalView : AppCompatActivity() {

    private lateinit var binding: ActivityPrincipalViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si Idpaciente es null
        if (Idpaciente == null) {
            // Si es null, redirigir al Login
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // Opcional: finalizar esta actividad para que no esté en el back stack
            return
        }

        binding = ActivityPrincipalViewBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_principal_view)

        // Solicitar permiso de notificaciones si es necesario
        requestNotificationPermission()

        setContentView(binding.root)
        replaceFragment(InicioFragment())

        binding.bottomNavigationView.setOnItemSelectedListener{

            when(it.itemId){
                R.id.inicio -> replaceFragment(InicioFragment())
                R.id.progreso -> replaceFragment(ProgresoFragment())
                R.id.perfil -> replaceFragment(PerfilFragment())

                else ->{
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    @Suppress("DEPRECATION")
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf("android.permission.POST_NOTIFICATIONS"), REQUEST_NOTIFICATION_PERMISSION)
            }
        }
    }


    companion object {
        const val REQUEST_NOTIFICATION_PERMISSION = 1
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificación concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso de notificación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

}