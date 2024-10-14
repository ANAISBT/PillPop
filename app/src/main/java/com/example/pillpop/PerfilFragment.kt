package com.example.pillpop

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class PerfilFragment : Fragment() {

    private lateinit var profileName: TextView
    private lateinit var profileAge: TextView
    private lateinit var btnAboutUs: LinearLayout
    private lateinit var btnChangePassword: LinearLayout
    private lateinit var editarButton: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        super.onViewCreated(view, savedInstanceState)

        profileName = view.findViewById(R.id.tvProfileName)
        profileName.text = Nombrepaciente
        profileAge = view.findViewById(R.id.tvProfileAge)

        profileAge.text = Edadpaciente.toString() + " años"

        btnAboutUs = view.findViewById(R.id.btnAboutUs)
        btnAboutUs.setOnClickListener{
            val intent = Intent(requireContext(),AcercaPillPop::class.java)
            startActivity(intent)
        }

        btnChangePassword = view.findViewById(R.id.btnChangePassword)
        btnChangePassword.setOnClickListener{
            val intent = Intent(requireContext(),CambiarContraseñaView::class.java)
            startActivity(intent)
        }

        editarButton = view.findViewById(R.id.editarButton)
        editarButton.setOnClickListener{
            val intent = Intent(requireContext(),EditarPerfilView::class.java)
            startActivity(intent)
        }

        return view
    }

}