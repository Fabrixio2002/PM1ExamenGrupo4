package hn.uth.pm1examengrupo4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ContactoAdapter extends FirebaseRecyclerAdapter<Contacto, ContactoAdapter.ContactoViewHolder> {

    public ContactoAdapter(@NonNull FirebaseRecyclerOptions<Contacto> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ContactoViewHolder holder, int position, @NonNull Contacto model) {
        // Asignar los valores del modelo de Contacto a los elementos de la vista
        holder.nombreContacto.setText(model.getNombre());
        // Cargar y mostrar la imagen utilizando Picasso
        Picasso.get().load(model.getImagen()).into(holder.imagenContacto);

        // Asigna otros valores según los campos de tu modelo de Contacto

        holder.btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtén el ID del contacto actual
                String contactoId = getRef(holder.getAdapterPosition()).getKey();
                // Crea un Intent para abrir la actividad de actualización de contacto
                Intent intent = new Intent(v.getContext(), ActualizarContactoActivity.class);
                intent.putExtra("contactoId", contactoId);
                v.getContext().startActivity(intent);
            }
        });



        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Eliminar contacto");
                builder.setMessage("¿Estás seguro de que quieres eliminar este contacto?");
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Obtén el ID del contacto actual
                        String contactoId = getRef(holder.getAdapterPosition()).getKey();

                        // Crea una referencia al contacto en la base de datos y elimínalo
                        DatabaseReference contactoRef = FirebaseDatabase.getInstance().getReference("contactos").child(contactoId);
                        contactoRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(v.getContext(), "Contacto eliminado correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(v.getContext(), "Error al eliminar el contacto", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancelar", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño del elemento de contacto
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(view);
    }

    public static class ContactoViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreContacto;
        public Button btnEliminar;
        public Button btnActualizar;
        public Button btnDireccion;
        public ImageView imagenContacto;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Asignar los elementos de la vista a las variables
            nombreContacto = itemView.findViewById(R.id.nombreContacto);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnActualizar = itemView.findViewById(R.id.Neto);
            imagenContacto = itemView.findViewById(R.id.imagenContacto);
        }
    }
}

