package hn.uth.pm1examengrupo4;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ActualizarContactoActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private EditText ET_NombreP;
    private EditText ET_Telefono;
    private EditText ET_Latitud;
    private EditText ET_Longitud;

    private ImageView IV_Persona;
    private LocationManager locationManager;

    private Button btn_Ubicacion;

    private Button btn_actualizar;

    private Button btn_Verlist;
    private String contactoId;
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    String nuevaUrlImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_contacto);

        // Obtener el ID del contacto de los extras del Intent
        contactoId = getIntent().getStringExtra("contactoId");

        // Inicializar la referencia a la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference("contactos");

        // Obtener referencias a los elementos de la interfaz de usuario
        ET_NombreP = findViewById(R.id.ET_NombreP);
        ET_Telefono = findViewById(R.id.ET_Telefono);
        ET_Latitud = findViewById(R.id.ET_Latitud);
        ET_Longitud = findViewById(R.id.ET_Longitud);
        btn_actualizar = findViewById(R.id.btn_Actualizar);
        IV_Persona = findViewById(R.id.IV_Persona);
        btn_Verlist = findViewById(R.id.btn_Verlist);

        // Obtener los datos del contacto de la base de datos y mostrarlos en la interfaz de usuario
        obtenerDatosContacto();

        // Agregar el OnClickListener al botón de actualización
        btn_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarContacto();
            }
        });

        btn_Verlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActualizarContactoActivity.this, ActivityLista.class);
                startActivity(intent);
            }
        });

        Button btnSeleccionarImagen = findViewById(R.id.btn_fotoP);
        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la galería para seleccionar una imagen
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
            }
        });


    }

    private void obtenerDatosContacto() {
        // Obtener la referencia al contacto específico en la base de datos
        DatabaseReference contactoRef = mDatabase.child(contactoId);
        // Escuchar los cambios en los datos del contacto
        contactoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Obtener los valores del contacto de la base de datos
                    String nombre = dataSnapshot.child("nombre").getValue(String.class);
                    String telefono = dataSnapshot.child("apellido").getValue(String.class);
                    String imagenUrl = dataSnapshot.child("imagen").getValue(String.class);
                    String latitudString = dataSnapshot.child("fechaNacimiento").getValue(String.class);
                    String longitudString = dataSnapshot.child("genero").getValue(String.class);

                    // Mostrar los valores en la interfaz de usuario
                    ET_NombreP.setText(nombre);
                    ET_Telefono.setText(telefono);
                    ET_Latitud.setText(latitudString);
                    ET_Longitud.setText(longitudString);

                    // Cargar y mostrar la imagen utilizando Picasso
                    Picasso.get().load(imagenUrl).into(IV_Persona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error de la base de datos si es necesario
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            // Obtiene la URI de la imagen seleccionada
            Uri imageUri = data.getData();

            // Ahora puedes cargar la imagen y obtener la URL de la imagen seleccionada
            // Utiliza la biblioteca que prefieras (Picasso, Glide, etc.) para cargar y mostrar la imagen en tu ImageView correspondiente
            // Una vez que tengas la URL de la imagen seleccionada, puedes asignarla a la variable nuevaUrlImagen y mostrarla en tu ImageView
            nuevaUrlImagen = imageUri.toString();
            Picasso.get().load(imageUri).into(IV_Persona);
        }
    }

    private void actualizarContacto() {
        // Obtener los nuevos valores del contacto desde la interfaz de usuario
        String nuevoNombre = ET_NombreP.getText().toString();
        String nuevoTelefono = ET_Telefono.getText().toString();
        String nuevaLatitud = ET_Latitud.getText().toString();
        String nuevaLongitud = ET_Longitud.getText().toString();

        // Actualizar los valores del contacto en la base de datos
        DatabaseReference contactoRef = mDatabase.child(contactoId);
        contactoRef.child("nombre").setValue(nuevoNombre);
        contactoRef.child("apellido").setValue(nuevoTelefono);
        contactoRef.child("genero").setValue(nuevaLatitud);
        contactoRef.child("fechaNacimiento").setValue(nuevaLongitud);
        if (nuevaUrlImagen != null) {
            // Obtiene una referencia al Storage de Firebase
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            // Crea una referencia al archivo en Firebase Storage usando un nombre único (por ejemplo, el ID del contacto)
            StorageReference imageRef = storageRef.child(contactoId + ".jpg");

            // Carga la imagen en Firebase Storage
            imageRef.putFile(Uri.parse(nuevaUrlImagen))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Obtiene la URL de descarga de la imagen subida
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    // Actualiza la URL de la imagen en la base de datos
                                    contactoRef.child("imagen").setValue(downloadUri.toString());
                                    // Continúa con cualquier acción adicional después de la actualización
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ActualizarContactoActivity.this);
                                    builder.setTitle("Grupo 4");
                                    builder.setMessage("Contacto actualizado correctamente");
                                    builder.setPositiveButton("Aceptar", null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Maneja el error en caso de que falle la carga de la imagen
                            Toast.makeText(ActualizarContactoActivity.this, "Error al actualizar el contacto", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Si no se seleccionó una nueva imagen, solo actualiza los demás campos en la base de datos
            contactoRef.child("nombre").setValue(nuevoNombre);
            contactoRef.child("apellido").setValue(nuevoTelefono);
            contactoRef.child("genero").setValue(nuevaLatitud);
            contactoRef.child("fechaNacimiento").setValue(nuevaLongitud);

            // Muestra un mensaje de éxito y cierra la actividad
            AlertDialog.Builder builder = new AlertDialog.Builder(ActualizarContactoActivity.this);
            builder.setTitle("Grupo 4");
            builder.setMessage("Contacto actualizado correctamente");
            builder.setPositiveButton("Aceptar", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void obtenerUbicacion() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Obtener la latitud y longitud actualizadas
                double latitud = location.getLatitude();
                double longitud = location.getLongitude();

                // Mostrar la ubicación en los campos correspondientes
                ET_Latitud.setText(String.valueOf(latitud));
                ET_Longitud.setText(String.valueOf(longitud));

                // Detener la actualización de ubicación
                locationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                // Si el proveedor de ubicación está desactivado, muestra un diálogo para permitir al usuario habilitarlo
                mostrarDialogoUbicacion();
            }
        };

        // Verificar si el proveedor de ubicación está habilitado
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Solicitar actualizaciones de ubicación
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            // Si el proveedor de ubicación está desactivado, muestra un diálogo para permitir al usuario habilitarlo
            mostrarDialogoUbicacion();
        }
    }

    private void mostrarDialogoUbicacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ubicación desactivada")
                .setMessage("La ubicación está desactivada. ¿Desea habilitarla?")
                .setPositiveButton("Configuración", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Abre la configuración de ubicación del dispositivo
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, inicia la obtención de ubicación
                obtenerUbicacion();
            } else {
                // Permiso denegado, muestra un mensaje de error
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // No se hace nada, se deshabilita la funcionalidad del botón de retroceso
    }
}
