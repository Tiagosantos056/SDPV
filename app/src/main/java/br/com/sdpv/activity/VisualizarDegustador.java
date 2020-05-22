package br.com.sdpv.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.sdpv.R;

public class VisualizarDegustador extends AppCompatActivity {

    // Constants
    public static final String TAG = "VisualizarDegustador";
    public static final String KEY = "itemKey";

    // Views
    private ImageView imgFotoPerfilDegustadorVisualizar;
    private TextView txtNomeDegustadorVisualizar;
    private TextView txtEmailDegustadorVisualizar;
    private TextView txtTelefoneDegustadorVisualizar;
    private TextView txtCPFDegustadorVisualizar;

    // Variáveis
    private String userID;

    // Context
    private Context mContext;

    // Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_degustador);
        Log.d(TAG, "onCreate: InitViews");
        toolbarConfig();
        initViews();

        // Init Context
        mContext = VisualizarDegustador.this;

        // Init Firebase
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("degustador");

        // Recuperando a ID do usuário da Activity ListaDegustadores
        Intent intent = getIntent();
        userID = intent.getExtras().getString(KEY);

        // Chamando o método para recuperar os dados do degustador da base de dados
        recuperarDadosDegustador();
    }

    private void toolbarConfig(){
        Toolbar toolbar = findViewById(R.id.toolbarVisualizarDadosDegustador);
        setSupportActionBar(toolbar);
        setTitle(R.string.toolbar_visualizar_degustador);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltarTelaListaDegustadores();
            }
        });
    }

    private void initViews(){
        imgFotoPerfilDegustadorVisualizar = findViewById(R.id.imgFotoPerfilDegustadorVisualizar);
        txtNomeDegustadorVisualizar = findViewById(R.id.txtNomeDegustadorVisualizar);
        txtEmailDegustadorVisualizar = findViewById(R.id.txtEmailDegustadorVisualizar);
        txtTelefoneDegustadorVisualizar = findViewById(R.id.txtTelefoneDegustadorVisualizarTexto);
        txtCPFDegustadorVisualizar = findViewById(R.id.txtCPFDegustadorVisualizarTexto);
    }

    private void voltarTelaListaDegustadores(){
        Intent intent = new Intent(mContext, ListaDegustadores.class);
        startActivity(intent);
    }

    /*
    --------------------------------------Firebase--------------------------------------
     */

    /**
     *  Firebase - Recuperando os dados do degustador da base de dados.
     */
    private void recuperarDadosDegustador(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userID).exists()){
                    Log.d(TAG, "onDataChange: Recuperando dados do degustador");
                    txtNomeDegustadorVisualizar.setText(
                            dataSnapshot.child(userID)
                                    .child("nome")
                                    .getValue()
                                    .toString());

                    txtEmailDegustadorVisualizar.setText(dataSnapshot.child(userID)
                            .child("email")
                            .getValue()
                            .toString());

                    txtTelefoneDegustadorVisualizar.setText(dataSnapshot.child(userID)
                            .child("telefone")
                            .getValue()
                            .toString());

                    txtCPFDegustadorVisualizar.setText(dataSnapshot.child(userID)
                            .child("cpf")
                            .getValue()
                            .toString());

                    if (dataSnapshot.child(userID).child("urlFotoPerfil").getValue().equals("")) {
                        imgFotoPerfilDegustadorVisualizar
                                .setImageResource(R.drawable
                                        .ic_account_circle_tela_principal_degustador);
                    } else {
                        Log.d(TAG, "onDataChange: Recuperando a URL da foto de " +
                                "perfil do degustador");
                        String downloadUrl = dataSnapshot.child(userID)
                                .child("urlFotoPerfil")
                                .getValue()
                                .toString();

                        Log.d(TAG, "onDataChange: Realizando o download, " +
                                "e aplicando a foto de peril com Glide");
                        Glide.with(mContext)
                                .load(downloadUrl)
                                .apply(RequestOptions.circleCropTransform())
                                .into(imgFotoPerfilDegustadorVisualizar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Erro: " + databaseError.getDetails());
                Toast.makeText(mContext, "Erro: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}