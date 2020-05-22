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
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.com.sdpv.R;
import br.com.sdpv.model.Degustador;

public class ListaDegustadoresRelatorio extends AppCompatActivity {

    // Constants
    public static final String TAG = "ListaDegusRelatorio";
    public static final String KEY = "itemKey";

    // Views
    private ListView lvListaDegustadoresRelatorio;

    // FirebaseListAdapter
    FirebaseListAdapter<Degustador> adapter;

    // Context
    private Context mContext;

    // Firebase
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_degustadores_relatorio);
        Log.d(TAG, "onCreate: InitViews");
        toolbarConfig();
        initViews();

        // Init Context
        mContext = ListaDegustadoresRelatorio.this;

        // Init Firebase
        mDatabase = FirebaseDatabase.getInstance();

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
        lvListaDegustadoresRelatorio.setAdapter(adapter);

        lvListaDegustadoresRelatorio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Recuperando a chave do item selecionado
                String itemKey = adapter.getRef(position).getKey();
                Log.d(TAG, "onItemClick: Chave do item selecionado: " + itemKey);

                // Colocando a chave em um Intent e mandando ela para outra activity
                Intent intent = new Intent(mContext, RelatorioDegustador.class);
                intent.putExtra(KEY, itemKey);
                startActivity(intent);
                Log.d(TAG, "onItemClick: Chave enviado pelo intent: " + itemKey);
            }
        });
    }

    private void toolbarConfig(){
        Toolbar toolbar = findViewById(R.id.toolbarListaDegustadoresRelatorio);
        setSupportActionBar(toolbar);
        setTitle(R.string.toolbar_lista_degustadores);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltarTelaPrincipalDegustador();
            }
        });
    }

    private void initViews(){
        lvListaDegustadoresRelatorio = findViewById(R.id.lvListaDegustadoresRelatorio);
    }

    private void voltarTelaPrincipalDegustador(){
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
