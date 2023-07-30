package hn.uth.pm1examengrupo4;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_IMAGE_GALLERY = 1;

    private TextView latitudTextView;
    private TextView longitudTextView;
    private Button ubicacionButton;
    private Button btn_fotoP;
    private Button btn_GuardarC;
    private Button btn_Verlist;

    private Button btn_Ubicacion;
    private ImageView IV_Persona;

    private EditText ET_NombreP;
    private EditText ET_Telefono;
    private LocationManager locationManager;
    private DatabaseReference contactosRef;
    private StorageReference storageReference;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de elementos de la interfaz
        latitudTextView = findViewById(R.id.ET_Latitud);
        longitudTextView = findViewById(R.id.ET_Longitud);
        btn_fotoP = findViewById(R.id.btn_fotoP);
        IV_Persona = findViewById(R.id.IV_Persona);
        btn_GuardarC = findViewById(R.id.btn_Actualizar);
        ET_NombreP = findViewById(R.id.ET_NombreP);
        ET_Telefono = findViewById(R.id.ET_Telefono);
        btn_Verlist=findViewById(R.id.btn_Verlist);

        // Referencias de Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        contactosRef = database.getReference("contactos");
        IV_Persona.setImageResource(R.drawable.perfil);

        // Referencia de Firebase Storage
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        // Botón para ver la lista de contactos
        btn_Verlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityLista.class);
                startActivity(intent);
            }
        });

        // Botón para guardar el contacto en Firebase
        btn_GuardarC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatosEnFirebase();
            }
        });

        // Botón para seleccionar una imagen de la galería
        btn_fotoP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
            }
        });

    }

    // Método para guardar los datos del contacto en Firebase
    private void guardarDatosEnFirebase() {
        String nombre = ET_NombreP.getText().toString();
        String apellido = ET_Telefono.getText().toString();
        String imagen = "Imagen No Agregada";
        String fecha = latitudTextView.getText().toString();
        String genero = longitudTextView.getText().toString();

        if (nombre.isEmpty() || apellido.isEmpty() || fecha.isEmpty() || genero.isEmpty()) {
            // Validación de campos vacíos
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Grupo 4");
            builder.setMessage("Debe Completar Los Campos");
            builder.setPositiveButton("Entendido", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (selectedImageUri == null) {
            // Validación de imagen no seleccionada
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Grupo 4");
            builder.setMessage("Debes agregar una imagen");
            builder.setPositiveButton("Aceptar", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            // Crear un nuevo objeto Contacto con los datos proporcionados
            String contactoId = contactosRef.push().getKey();
            Persona nuevapersona=new Persona(imagen,nombre,apellido,fecha,genero);
            // Guardar el nuevo contacto en Firebase Database
            contactosRef.child(contactoId).setValue(nuevapersona)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Subir imagen a Firebase Storage
                            StorageReference imageRef = storageReference.child("fotos_contacto").child(contactoId + ".jpg");

                            imageRef.putFile(selectedImageUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    // Actualizar el campo de imagen en el contacto
                                                    contactosRef.child(contactoId).child("imagen").setValue(uri.toString())
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    // Mostrar diálogo de éxito y limpiar los campos
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                                    builder.setTitle("Grupo 4");
                                                                    builder.setMessage("Datos y imagen guardados correctamente");
                                                                    builder.setPositiveButton("Aceptar", null);
                                                                    AlertDialog dialog = builder.create();
                                                                    dialog.show();
                                                                    Limpiar();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    // Manejar fallo de actualización del campo de imagen
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                                    builder.setTitle("Error :(");
                                                                    builder.setMessage("Error al actualizar la imagen");
                                                                    builder.setPositiveButton("Aceptar", null);
                                                                    AlertDialog dialog = builder.create();
                                                                    dialog.show();
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Manejar fallo de subida de imagen a Firebase Storage
                                            Toast.makeText(MainActivity.this, "Error al subir la imagen al Storage", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar fallo de guardado de datos en Firebase Database
                            Toast.makeText(getApplicationContext(), "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Método para solicitar la ubicación al LocationManager
    private void requestLocation() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Actualizar la latitud y longitud en las vistas de texto
                double latitud = location.getLatitude();
                double longitud = location.getLongitude();
                latitudTextView.setText(String.valueOf(latitud));
                longitudTextView.setText(String.valueOf(longitud));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Verificar permisos de ubicación
            return;
        }

        // Solicitar una sola actualización de ubicación al LocationManager
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
    }

    // Método para verificar si el GPS está activado
    private boolean GpsOn() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                // Obtener la URI de la imagen seleccionada y mostrarla en el ImageView
                selectedImageUri = data.getData();
                IV_Persona.setImageURI(selectedImageUri);
            }
        }
    }

    // Método para mostrar un diálogo para activar el GPS
    private void showGpsAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS desactivado")
                .setMessage("El GPS está desactivado. ¿Deseas activarlo?")
                .setPositiveButton("Configuración", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Abrir la configuración de ubicación del dispositivo
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Método para limpiar los campos de la interfaz
    private void Limpiar(){
        latitudTextView.setText("");
        longitudTextView.setText("");
        IV_Persona.setImageResource(R.drawable.perfil);
        ET_NombreP.setText("");
        ET_Telefono.setText("");
    }

    @Override
    public void onBackPressed() {
    }
}
