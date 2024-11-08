package com.example.pillpop

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context, intent: Intent) {
        val nombreMedicamento = intent.getStringExtra("nombreMedicamento") ?: "Medicamento"
        val dosis = intent.getIntExtra("dosisMedicamento", 1)

        val channelId = "PillPopChannel"
        val notificationId = System.currentTimeMillis().toInt() // O cualquier otro valor único

        // Crear el canal de notificación
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Recordatorios de Tomas de Pastillas", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
            Log.d("NotificationReceiver", "Canal creado con éxito.")
        }else{
            Log.d("NotificationReceiver", "Versión anterior a Android O, no es necesario crear canal.")
        }


        // Crear la notificación
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo2)
            .setContentTitle("Hora de tomar tu pastilla")
            .setContentText("Es hora de tomar $dosis dosis de $nombreMedicamento")
            .setAutoCancel(true)

        // Intent para abrir la aplicación al tocar la notificación
        val resultIntent = Intent(context, PrincipalView::class.java)
        val resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        notificationBuilder.setContentIntent(resultPendingIntent)

        // Enviar la notificación
        notificationManager.notify(notificationId, notificationBuilder.build())
        Log.d("NotificationReceiver", "Notificación enviada.")

        // Programar la cancelación de la notificación en 4 horas
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val cancelIntent = Intent(context, CancelNotificationReceiver::class.java).apply {
            putExtra("notificationId", notificationId)
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fourHoursInMillis = 1 * 60 * 60 * 1000L // 4 horas en milisegundos
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + fourHoursInMillis,
            cancelPendingIntent
        )
        Log.d("NotificationReceiver", "Cancelación programada en 4 horas.")
    }
}
