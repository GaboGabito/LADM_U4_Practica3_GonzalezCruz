package mx.edu.ittepic.ladm_u4_practica3_gonzalezcruz

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.widget.Toast

class SmsReceiver:BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent?) {
        val extras = intent!!.extras

        if (extras != null) {
            var sms = extras.get("pdus") as Array<Any>
            for (indice in sms.indices) {
                var formato = extras.getString("format")

                var smsMensaje = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    SmsMessage.createFromPdu(sms[indice] as ByteArray, formato)
                } else {
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }
                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()
                var cadena = contenidoSMS.split(" ")
                var mensaje = ""
                Toast.makeText(context, "MENSAJE ENTRANTE DE: " + celularOrigen, Toast.LENGTH_LONG)
                    .show()
                if(cadena.size !=3){

                }else {
                    if (!(cadena[0].equals("CALIFICACION"))) {
                        SmsManager.getDefault().sendTextMessage(
                            celularOrigen,
                            null,
                            "ERROR EN LA SINTAXIS(VERIFICAR): CALIFICACION NOCONTROL U[NUMERO DE LA MISMA]",
                            null,
                            null)
                    } else {
                        if (cadena[1].toString().length != 8) {
                            SmsManager.getDefault().sendTextMessage(
                                celularOrigen,
                                null,
                                "ERROR EN LA SINTAXIS(VERIFICAR): CALIFICACION NOCONTROL U[NUMERO DE LA MISMA]",
                                null,
                                null)

                        } else {
                            if (cadena[2].toString().length != 2) {

                                SmsManager.getDefault().sendTextMessage(
                                    celularOrigen,
                                    null,
                                    "ERROR EN LA SINTAXIS(VERIFICAR): CALIFICACION NOCONTROL U[NUMERO DE LA MISMA]",
                                    null,
                                    null)
                            } else {
                                //
                                try {

                                    val cursor = BaseDatos(context, "CALIFICACIONES", null, 1)
                                        .readableDatabase
                                        .rawQuery(
                                            "SELECT * FROM CALIFICACIONES WHERE NUMEROCONTROL = '${cadena[1]}' AND UNIDAD = '${cadena[2]}'",
                                            null)
                                    if (cursor.moveToNext()) {

                                        mensaje = "Saludos! " + cursor.getString(0) + ", tu calificacion de " + cursor.getString(
                                                2)+" es: " + cursor.getString(3)

                                        SmsManager.getDefault().sendTextMessage(
                                            celularOrigen, null,
                                            "" + mensaje, null, null)

                                    } else {
                                        SmsManager.getDefault().sendTextMessage(
                                            celularOrigen,
                                            null,
                                            "NO SE ENCONTRO LA CALIFICACION DE: No. Control: ${cadena[1]}, Unidad: ${cadena[2]}",
                                            null,
                                            null)
                                    }
                                } catch (e: SQLiteException) {
                                    SmsManager.getDefault().sendTextMessage(
                                        celularOrigen, null,
                                        e.message, null, null)
                                }

                            }
                        }
                    }
                }
            }
        }
    }

}