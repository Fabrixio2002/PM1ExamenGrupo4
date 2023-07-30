package hn.uth.pm1examengrupo4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ActivityLista extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ContactoAdapter adapter;
    private RecyclerView recyclerView;
    private EditText etBusqueda;

    private Button btnAgregarContacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        btnAgregarContacto = findViewById(R.id.btnAgregarContacto);

        btnAgregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir la actividad principal para agregar un nuevo contacto
                Intent intent = new Intent(ActivityLista.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Obtener referencia a la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference("contactos");

        // Configurar el RecyclerView y el adaptador
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurar las opciones para el adaptador
        FirebaseRecyclerOptions<Contacto> options =
                new FirebaseRecyclerOptions.Builder<Contacto>()
                        .setQuery(mDatabase, Contacto.class)
                        .build();

        adapter = new ContactoAdapter(options);
        recyclerView.setAdapter(adapter);

        // Configurar el EditText de búsqueda
        etBusqueda = findViewById(R.id.etBusqueda);
        etBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se utiliza
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar la lista de contactos al escribir en el EditText
                filterContactList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se utiliza
            }
        });
    }

    private void filterContactList(String searchText) {
        Query query;
        if (searchText.isEmpty()) {
            // Si no hay texto de búsqueda, mostrar todos los contactos
            query = mDatabase;
        } else {
            // Si hay texto de búsqueda, filtrar los contactos por nombre
            query = mDatabase.orderByChild("nombre").startAt(searchText).endAt(searchText + "\uf8ff");
        }

        // Crear nuevas opciones para el adaptador con la consulta filtrada
        FirebaseRecyclerOptions<Contacto> options =
                new FirebaseRecyclerOptions.Builder<Contacto>()
                        .setQuery(query, Contacto.class)
                        .build();

        // Actualizar las opciones del adaptador y notificar los cambios
        adapter.updateOptions(options);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Iniciar la escucha de cambios en la base de datos
        adapter.startListening();
        //se utiliza para iniciar la escucha de cambios en la base de datos y actualizar automáticamente el RecyclerView con los nuevos datos
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Detener la escucha de cambios en la base de datos
        //se utiliza para detener la escucha de cambios en la base de datos y dejar de actualizar automáticamente el RecyclerView
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        // No se hace nada, se deshabilita la funcionalidad del botón de retroceso
    }
}

