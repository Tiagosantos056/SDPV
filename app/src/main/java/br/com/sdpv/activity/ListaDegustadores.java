package br.com.sdpv.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.com.sdpv.R;
import br.com.sdpv.model.Degustador;

public class ListaDegustadores extends AppCompatActivity {

    // Connstants
    public static final String TAG = "ListaDegustadores";
    public static final String KEY = "itemKey";

    // Views e Variáveis
    private TextView txtNumeroDegustadoresCadastrados;
    private ListView lvDegustador;

    // Context
    private Context mContext;

    // Firebase ListAdapter
    FirebaseListAdapter<Degustador> adapter;

    // Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_degustadores);
        Log.d(TAG, "onCreate: InitViews and Variables");
        initViewsAndVariables();
        toolbarConfig();

        // Init Context
        mContext = ListaDegustadores.this;

        // Obtendo Referência e instância do Firebase
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("degustador");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Recuperando o número de filhos do nó Degustador");
                long nDegustadoresCadastrados = dataSnapshot.getChildrenCount();

                txtNumeroDegustadoresCadastrados
                        .setText(String.valueOf(nDegustadoresCadastrados));
                Log.d(TAG, "onDataChange: Número de nós filhos: " + nDegustadoresCadastrados);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Erro: " + databaseError.getDetails());
            }
        });

        Query query = mDatabase.getReference("degustador");

        FirebaseListOptions<Degustador> firebaseListOptions = new FirebaseListOptions
                .Builder<Degustador>()
                .setQuery(query, Degustador.class)
                .setLayout(android.R.layout.simple_list_item_2)
                .build();

         adapter = new FirebaseListAdapter<Degustador>
                (firebaseListOptions) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Degustador model, int position) {
                ((TextView)v.findViewById(android.R.id.text1)).setText(model.getNome());
                ((TextView)v.findViewById(android.R.id.text2)).setText(model.getEmail());
            }
        };
         // Configurando o adaptador do ListView
         lvDegustador.setAdapter(adapter);

         // Configurando o Clique nos items da ListView
         lvDegustador.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 // Recuperando a chave do usuário
                 String itemKey = adapter.getRef(position).getKey();
                 Log.d(TAG, "onItemClick: Chave do usuário: " + itemKey);

                 // Mandando a variável itemKey para a Activity VisualizarDegustador
                 Intent intent = new Intent(mContext, VisualizarDegustador.class);
                 intent.putExtra(KEY, itemKey);
                 startActivity(intent);
                 Log.d(TAG, "onItemClick: " +
                         "Chave do usuário enviada para a activity VisualizarDegustador " + itemKey);
             }
         });
    }

    private void initViewsAndVariables() {
        txtNumeroDegustadoresCadastrados = findViewById(R.id.txtNumeroDegustadoresCadastrados);
        lvDegustador = findViewById(R.id.lvListaDegustadores);
    }

    private void toolbarConfig() {
        Toolbar toolbar = findViewById(R.id.toolbarListaDegustadores);
        setSupportActionBar(toolbar);
        setTitle(R.string.toolbar_lista_degustadores);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltarTelaIncialAdmin();
            }
        });
    }

    private void voltarTelaIncialAdmin(){
        Intent intent = new Intent(mContext, TelaPrincipalAdministrador.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}