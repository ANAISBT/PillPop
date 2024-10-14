package com.example.pillpop

import android.annotation.SuppressLint
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

    @SuppressLint("MissingInflatedId")
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

        // Inicializa las vistas para cambiar contraseña
        val editContrasenaView: ImageView = view.findViewById(R.id.EditContrasenaView)
        val editContrasenaViewText: TextView = view.findViewById(R.id.EditContrasenaViewText)

        val cambiarContrasenaIntent = Intent(requireContext(), CambiarContraseñaView::class.java)

        editContrasenaView.setOnClickListener {
            startActivity(cambiarContrasenaIntent)
        }

        editContrasenaViewText.setOnClickListener {
            startActivity(cambiarContrasenaIntent)
        }

        // Inicializa las vistas para cerrar sesión
        val cerrarSesionImg: ImageView = view.findViewById(R.id.CerrarSesionImg)
        val cerrarSesionText: TextView = view.findViewById(R.id.CerrarSesionText)

        val cerrarSesionIntent = Intent(requireContext(), InicioView::class.java) // Cambia esto al nombre de tu actividad de inicio de sesión

        val cerrarSesionListener = View.OnClickListener {
            // Establece doctorId a 0
            Idpaciente = 0 // Asegúrate de que Idpaciente sea una variable accesible desde aquí

            // Aquí cierras la sesión
            val cerrarSesionIntent = Intent(requireContext(), InicioView::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(cerrarSesionIntent)
        }

        cerrarSesionImg.setOnClickListener(cerrarSesionListener)
        cerrarSesionText.setOnClickListener(cerrarSesionListener)

        return view
    }

}