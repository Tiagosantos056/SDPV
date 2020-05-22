package br.com.sdpv.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.com.sdpv.R;
import br.com.sdpv.model.Vinho;

public class ListaVinhosDegustadosRelatorio extends AppCompatActivity {

    // Constants
    public static final String TAG = "ListVinhoDegusRelatorio";
    public static final String USERID = "userid";

    // Context
    private Context mContext;

    // Views e Variáveis
    private ListView lvVinhosDegustadosRelatorio;
    private FirebaseListAdapter<Vinho> adapter;
    private String userID;

    // Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vinhos_degustados_relatorio);
        Log.d(TAG, "onCreate: InitViews");
        toolbarConfig();
        initViewsVariaves();

        Intent intent = getIntent();
        userID = intent.getExtras().getString(USERID);

        // Init Context
        mContext = ListaVinhosDegustadosRelatorio.this;

        // Init Firebase
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();

        /*
        Objeto "Query" para fazer uma query na base de dados e recuperar os vinhos com o id do
        usuário.
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
        lvVinhosDegustadosRelatorio.setAdapter(adapter);
    }

    private void initViewsVariaves(){
        lvVinhosDegustadosRelatorio = findViewById(R.id.lvListaVinhosDegustadorRelatorio);
    }

    private void toolbarConfig(){
        Toolbar toolbar = findViewById(R.id.toolbarListaVinhosDegustadorRelatorio);
        setSupportActionBar(toolbar);
        setTitle(R.string.toolbar_lista_vinhos);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltarTelaListaDegustadores();
            }
        });
    }

    private void voltarTelaListaDegustadores(){
        Intent intent = new Intent(mContext, ListaDegustadoresRelatorio.class);
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
