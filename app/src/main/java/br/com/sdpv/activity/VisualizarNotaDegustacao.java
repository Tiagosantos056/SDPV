package br.com.sdpv.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.sdpv.R;

public class VisualizarNotaDegustacao extends AppCompatActivity {

    // Constants
    public static final String TAG = "Visu.NotaDegustacao";
    public static final String KEY = "itemKey";

    // Views
    private TextView txtNomeVinicolaTexto;
    private TextView txtNomeVinhoTexto;
    private TextView txtTipoVinhoTexto;
    private TextView txtPaisRegiaoTexto;
    private TextView txtUvaTexto;
    private TextView txtVolAlcoolicoTexto;

    private TextView txtCorVinhoTexto;
    private TextView txtVarTonalidadeTexto;
    private TextView txtViscosidadeTexto;
    private TextView txtAspectoGeralTexto;

    private TextView txtCondicaoAromaTexto;
    private TextView txtItensidadeAromaTexto;
    private TextView txtCaracteristicasAromaTexto;

    private TextView txtDocuraTexto;
    private TextView txtCorpoTexto;
    private TextView txtAcidezTexto;
    private TextView txtTaninosTexto;
    private TextView txtAromasRetroTexto;
    private TextView txtFinalTexto;

    private TextView txtQualidadeTexto;
    private TextView txtComentariosGerais;
    private TextView txtPontuacaoTexto;
    private TextView txtLocalDegustacaoTexto;

    // Várivaies
    private String itemKey;
    private String idNota;

    // Context
    private Context mContext;

    // Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_nota_degustacao);
        Log.d(TAG, "onCreate: InitViews");
        toolbarConfig();
        initViews();

        Intent intent = getIntent();
        itemKey = intent.getExtras().getString(KEY);
        Log.d(TAG, "onCreate: Chave recebida via intent: " + itemKey);

        // Init Context
        mContext = VisualizarNotaDegustacao.this;

        // Init Firebase
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("vinho");

        // Chamando o método para recuperar os dados da Nota de Degustação
        recuperarDadosNotaDegustacao();
    }

    private void toolbarConfig(){
        Toolbar toolbar = findViewById(R.id.toolbarVisualizarNotaDegustacao);
        setSupportActionBar(toolbar);
        setTitle(R.string.toolbar_visualizar_nota);
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

    private void voltarTelaVinhosDegustados(){
        Intent intent = new Intent(mContext, ListaVinhosDegustador.class);
        startActivity(intent);
    }

    private void initViews(){
        txtNomeVinicolaTexto = findViewById(R.id.txtNomeVinicolaVisualizarTexto);
        txtNomeVinhoTexto = findViewById(R.id.txtNomeVinhoVisualizarTexto);
        txtTipoVinhoTexto = findViewById(R.id.txtTipoVisualizarTexto);
        txtPaisRegiaoTexto = findViewById(R.id.txtPaisRegiaoVisualizarTexto);
        txtUvaTexto = findViewById(R.id.txtUvaVisualizarTexto);
        txtVolAlcoolicoTexto = findViewById(R.id.txtVolAlcoolicoVisualizarTexto);

        txtCorVinhoTexto = findViewById(R.id.txtCorVisualizarTexto);
        txtVarTonalidadeTexto = findViewById(R.id.txtVarTonalidadeVisualizarTexto);
        txtViscosidadeTexto = findViewById(R.id.txtViscosidadeVisualizarTexto);
        txtAspectoGeralTexto = findViewById(R.id.txtAspectoGeralVisualizarTexto);

        txtCondicaoAromaTexto = findViewById(R.id.txtCondicaoVisualizarTexto);
        txtItensidadeAromaTexto = findViewById(R.id.txtItensidadeVisualizarTexto);
        txtCaracteristicasAromaTexto = findViewById(R.id.txtCaracteristicasVisualizarTexto);

        txtDocuraTexto = findViewById(R.id.txtDocuraVisualizarTexto);
        txtCorpoTexto = findViewById(R.id.txtCorpoVisualizarTexto);
        txtAcidezTexto = findViewById(R.id.txtAcidezVisualizarTexto);
        txtTaninosTexto = findViewById(R.id.txtTaninosVisualizarTexto);
        txtAromasRetroTexto = findViewById(R.id.txtAromaRetroVisualizarTexto);
        txtFinalTexto = findViewById(R.id.txtFinalVisualizarTexto);

        txtQualidadeTexto = findViewById(R.id.txtQualidadeGeralVisualizarTexto);
        txtComentariosGerais = findViewById(R.id.txtComentariosGeralVisualizarTexto);
        txtPontuacaoTexto = findViewById(R.id.txtPontuacaoVisualizarTexto);
        txtLocalDegustacaoTexto = findViewById(R.id.txtLocalDegustacaoVisualizarTexto);
    }

    private void dialogConfirmarExclusaoNota(){
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.title_excluir_nota)
                .setMessage(R.string.confirmar_exclusao_nota)
                .setPositiveButton(R.string.confirmar_acao, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletarVinho();
                        deletarNotaDegustacao();
                    }
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_visualizar_nota_degus_deletar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.toolbar_menu_deletar_nota_degustador){
            dialogConfirmarExclusaoNota();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    --------------------------------------Firebase--------------------------------------
     */

    /**
     *  Firebase - Método para recuperar os dados do Vinho selecionado na lista da activity
     *  anterior
     */
    private void recuperarDadosNotaDegustacao(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.getKey().equals(itemKey)){

                        idNota = dataSnapshot
                                .child(itemKey).child("id_nota").getValue().toString();
                        Log.d(TAG, "onDataChange: ID da Nota: " + idNota);

                        txtNomeVinicolaTexto.setText(dataSnapshot.child(itemKey)
                                .child("nomeVinicola").getValue().toString());
                        txtNomeVinhoTexto.setText(dataSnapshot.child(itemKey)
                                .child("nomeVinho").getValue().toString());
                        txtTipoVinhoTexto.setText(dataSnapshot.child(itemKey)
                                .child("tipoVinho").getValue().toString());
                        txtPaisRegiaoTexto.setText(dataSnapshot.child(itemKey)
                                .child("pais_regiao").getValue().toString());
                        txtUvaTexto.setText(dataSnapshot.child(itemKey)
                                .child("uvaVinho").getValue().toString());
                        txtVolAlcoolicoTexto.setText(dataSnapshot.child(itemKey)
                                .child("volAlcoolicoVinho").getValue().toString());

                        txtCorVinhoTexto.setText(dataSnapshot.child(itemKey)
                                .child("cor").getValue().toString());
                        txtVarTonalidadeTexto.setText(dataSnapshot.child(itemKey)
                                .child("variacaoTonalidade").getValue().toString());
                        txtViscosidadeTexto.setText(dataSnapshot.child(itemKey)
                                .child("viscosidade").getValue().toString());
                        txtAspectoGeralTexto.setText(dataSnapshot.child(itemKey)
                                .child("aspecto").getValue().toString());

                        txtCondicaoAromaTexto.setText(dataSnapshot.child(itemKey)
                                .child("condicaoAromatica").getValue().toString());
                        txtItensidadeAromaTexto.setText(dataSnapshot.child(itemKey)
                                .child("itensidadeAromatica").getValue().toString());
                        txtCaracteristicasAromaTexto.setText(dataSnapshot.child(itemKey)
                                .child("caracteristicasAromaticas").getValue().toString());

                        txtDocuraTexto.setText(dataSnapshot.child(itemKey)
                                .child("docura").getValue().toString());
                        txtCorpoTexto.setText(dataSnapshot.child(itemKey)
                                .child("corpo").getValue().toString());
                        txtAcidezTexto.setText(dataSnapshot.child(itemKey)
                                .child("acidez").getValue().toString());
                        txtTaninosTexto.setText(dataSnapshot.child(itemKey)
                                .child("tanino").getValue().toString());
                        txtAromasRetroTexto.setText(dataSnapshot.child(itemKey)
                                .child("caracteristicasAromaticaRetronasal").getValue().toString());
                        txtFinalTexto.setText(dataSnapshot.child(itemKey)
                                .child("finalPaladar").getValue().toString());

                        txtQualidadeTexto.setText(dataSnapshot.child(itemKey)
                                .child("qualidadeGeral").getValue().toString());
                        txtComentariosGerais.setText(dataSnapshot.child(itemKey)
                                .child("comentariosVinho").getValue().toString());
                        txtPontuacaoTexto.setText(dataSnapshot.child(itemKey)
                                .child("pontuacao").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Erro: " + databaseError.getMessage());
            }
        });
    }

    /**
     *  Firebase - Método para deletar o Vinho sendo visualizado
     */
    private void deletarVinho(){
        myRef.child(itemKey).setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    Log.d(TAG, "onComplete: Erro ao deletar vinho: " + task.getException());
                    Toast.makeText(mContext, R.string.erro_deletar_nota,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onComplete: Vinho deletada com sucesso: " + task.getResult());
                    Toast.makeText(mContext, R.string.nota_deletada_sucesso,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     *  Firebase - Método para deletar a Nota de Degustação do vinho sendo visulizado
     */
    private void deletarNotaDegustacao(){
        myRef = mDatabase.getReference("notaDegustacao");
        myRef.child(idNota).setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    Log.d(TAG, "onComplete: Erro ao deletar nota: " + task.getException());
                } else {
                    voltarTelaVinhosDegustados();
                    Log.d(TAG, "onComplete: Nota deletada com sucesso: " + task.getResult());
                }
            }
        });
    }
}