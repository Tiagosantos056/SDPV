package br.com.sdpv.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.sdpv.R;
import br.com.sdpv.model.NotaDegustacao;
import br.com.sdpv.model.Vinho;

public class ListaVinhosDegustador extends AppCompatActivity {

    // Constants
    public static final String TAG = "ListaVinhosDegustador";
    public static final String KEY = "itemKey";

    // Context
    private Context mContext;

    // Views e Variáveis
    private ListView lvVinhosDegustados;
    private FirebaseListAdapter<Vinho> adapter;
    private String userID;

    // Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vinhos_degustador);
        // Init Views
        Log.d(TAG, "onCreate: Init Views");
        initViewsVariaves();
        toolbarConfig();

        // Init Context
        mContext = ListaVinhosDegustador.this;

        // Init Firebase
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = mDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();

        /*
        Objeto "Query" para fazer uma query na base de dados e recuperar os vinhos com o id do
        usuário logado.
         */
        Query query = myRef.child("vinho").orderByChild("id_degustador").equalTo(userID);

        /*
        Objeto "FirebaseListOptions" para configurar a exibição do adapter.
         */
        FirebaseListOptions<Vinho> listOptions = new FirebaseListOptions
                .Builder<Vinho>()
                .setQuery(query, Vinho.class)
                .setLayout(android.R.layout.simple_list_item_2)
                .build();

        /*
        Objeto "FirebaseListAdapter" que serve como adapter para o ListView.
         */
        adapter = new FirebaseListAdapter<Vinho>(listOptions) {
            @Override
            protected void populateView(@NonNull View v, @NonNull final Vinho model, int position) {
                ((TextView)v.findViewById(android.R.id.text1)).setText(model.getNomeVinho());
                ((TextView)v.findViewById(android.R.id.text2)).setText(model.getNomeVinicola());
            }
        };

        // Configurando o adapter do ListView
        lvVinhosDegustados.setAdapter(adapter);

        lvVinhosDegustados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemKey = adapter.getRef(position).getKey();
                Log.d(TAG, "onItemClick: Chave do item: " + itemKey);

                Intent intent = new Intent(mContext, VisualizarNotaDegustacao.class);
                intent.putExtra(KEY, itemKey);
                startActivity(intent);
                Log.d(TAG, "onItemClick: Chave do item enviado via intent " + itemKey);
            }
        });
    }

    private void initViewsVariaves(){
        lvVinhosDegustados = findViewById(R.id.lvListaVinhosDegustador);
    }

    private void toolbarConfig(){
        Toolbar toolbar = findViewById(R.id.toolbarListaVinhosDegustador);
        setSupportActionBar(toolbar);
        setTitle(R.string.toolbar_lista_vinhos);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltarTelaPrincipalDegustador();
            }
        });
    }

    private void voltarTelaPrincipalDegustador(){
        Intent intent = new Intent(mContext, DegustadorTelaPrincipal.class);
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