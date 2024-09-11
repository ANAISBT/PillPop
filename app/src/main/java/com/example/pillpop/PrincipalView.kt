package com.example.pillpop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pillpop.databinding.ActivityPrincipalViewBinding

class PrincipalView : AppCompatActivity() {

    private lateinit var binding: ActivityPrincipalViewBinding
    private var perfilId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalViewBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_principal_view)
        setContentView(binding.root)
        // Obtener perfil_id del Intent
        perfilId = intent.getIntExtra("perfil_id", 0)
        replaceFragment(InicioFragment())

        binding.bottomNavigationView.setOnItemSelectedListener{

            when(it.itemId){
                R.id.inicio -> replaceFragment(InicioFragment())
                R.id.tratamiento -> replaceFragment(TratamientoFragment())
                R.id.progreso -> replaceFragment(ProgresoFragment())

                else ->{
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val bundle = Bundle()
        bundle.putInt("perfil_id", perfilId)
        fragment.arguments = bundle

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

}