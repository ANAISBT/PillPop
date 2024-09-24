package com.example.pillpop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pillpop.databinding.ActivityPrincipalViewBinding

class PrincipalView : AppCompatActivity() {

    private lateinit var binding: ActivityPrincipalViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalViewBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_principal_view)
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

}