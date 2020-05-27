package mx.edu.ittepic.ladm_u4_practica3_gonzalezcruz

import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var dataLista = ArrayList<String>()
    var listaID= ArrayList<String>()
    val siPermiso = 1
    val siPermisoReceiver = 2
    val siPermisoLectura = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.RECEIVE_SMS),siPermisoReceiver)
        }

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_SMS),siPermisoLectura)
        }

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS) !=
            PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.SEND_SMS), siPermiso)
        }

        cargarLista()
        button.setOnClickListener {
            var vnombre = ""
            var noControl = ""
            var vcalificacion = ""
            var vunidad = ""

            //Verificación de campos
            if(nombre.text.toString().isEmpty()||
                numeroControl.text.toString().isEmpty()||
                calificacion.text.toString().isEmpty()||
                unidad.text.toString().isEmpty()){
                mensaje("Llenar todos los campos")
                return@setOnClickListener
            }

            if(numeroControl.text.toString().length != 8){
                mensaje("El número de control debe contener 8 numeros")
                return@setOnClickListener
            }
            if(calificacion.text.toString().toInt()<0 || calificacion.text.toString().toInt()>100){
                mensaje("La calificación debe estar entre 0 y 100")
                return@setOnClickListener
            }
            if(unidad.text.toString().length != 2){
                mensaje("La unidad lleva la letra U y el número de la unidad, ejemplo: 'U1' ")
                return@setOnClickListener
            }

            vnombre = nombre.text.toString()
            noControl = numeroControl.text.toString()
            vcalificacion = calificacion.text.toString()
            vunidad = unidad.text.toString()


            try {
                var baseDatos = BaseDatos(this,"CALIFICACIONES",null,1)
                var insertar = baseDatos.writableDatabase
                var SQL = "INSERT INTO CALIFICACIONES VALUES('${vnombre}','${noControl}','${vunidad}','${vcalificacion}')"
                insertar.execSQL(SQL)
                baseDatos.close()
            }catch (e: SQLiteException){
                mensaje(e.message!!)
            }

            cargarLista()
        }
        lista.setOnItemClickListener { parent, view, position, id ->
            if(listaID.size==0){
                return@setOnItemClickListener
            }
            AlertaEliminar(position)
        }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == siPermiso){

        }
        if(requestCode == siPermisoReceiver){
            Toast.makeText(this,"SE PERMITIO RECIBIR",Toast.LENGTH_LONG).show()
        }
        if(requestCode == siPermisoLectura){

        }
    }

    private fun cargarLista() {
        dataLista.clear()
        listaID.clear()

        try{
            val cursor = BaseDatos(this,"CALIFICACIONES",null,1)
                .readableDatabase
                .rawQuery("SELECT * FROM CALIFICACIONES",null)
            var temporal = ""

            if(cursor.moveToFirst()){
                do{
                    temporal ="Nombre: "+cursor.getString(0)+
                            " No. Control: "+cursor.getString(1)+
                            " Unidad: "+cursor.getString(2)+
                            " Calificacion: "+cursor.getString(3)

                    cursor.getString(1)
                    dataLista.add(temporal)
                    listaID.add(cursor.getString(1))
                }while(cursor.moveToNext())
            }else{
                dataLista.add("NO HAY CALIFICACIONES")
            }
            var adaptador = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataLista)
            lista.adapter = adaptador
        }catch (err: SQLiteException){
            Toast.makeText(this,err.message, Toast.LENGTH_LONG)
                .show()
        }
    }
    private fun mensaje(s: String) {
        Toast.makeText(this,s,Toast.LENGTH_LONG)
            .show()
    }
    private fun AlertaEliminar(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("¿Deseas eliminar la calificación con los siguientes datos?")
            .setMessage(dataLista[position])
            .setPositiveButton("Eliminar"){d,i-> EliminarCalificacion(position)}
            .setNeutralButton("Cancelar"){d,i->}
            .show()
    }
    private fun EliminarCalificacion(position: Int) {
        try {
            var base = BaseDatos(this,"CALIFICACIONES",null,1)
            var eliminar = base.writableDatabase
            var noControlEliminar = arrayOf(listaID[position])
            var respuesta =  eliminar.delete("CALIFICACIONES","NUMEROCONTROL=?",noControlEliminar)
            if(respuesta.toInt() == 0){
                mensaje("NO SE ELIMINÓ EL ALUMNO")
            }
        }catch (e:SQLiteException){
            mensaje(e.message!!)
        }
        cargarLista()
    }

}
