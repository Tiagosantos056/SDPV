package br.com.sdpv.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.sdpv.R;
import br.com.sdpv.model.Vinho;

public class NotaDegustacao extends AppCompatActivity {

    // Constants
    public static final String TAG = "NotaDegustacao";

    // Views "InfoGarrafa"
    private EditText edtNomeVinicola;
    private EditText edtNomeVinho;
    private EditText edtTipoVinho;
    private EditText edtPaisRegiao;
    private EditText edtUva;
    private EditText edtVolAlcoolico;

    // Views "Análise Visual"
    private EditText edtCorVinho;
    private EditText edtAspectoVinho;
    private Spinner spnVarTonalidade;
    private Spinner spnViscosidade;

    // Views "Análise Olfativa"
    private Spinner spnIntensidadeAromatica;
    private EditText edtAromasNasais;
    private EditText edtCondicaoAroma;

    // Views "Análise do Paladar"
    private Spinner spnDocura;
    private Spinner spnCorpo;
    private Spinner spnAcidez;
    private Spinner spnTaninos;
    private Spinner spnFinalVinho;
    private EditText edtAromasRetronasais;

    // Views "Detalhes Finais"
    private TextView txtPontuacaoSelecionada;
    private TextView txtPontuacaoNumero;
    private Spinner spnQualidadeGeral;
    private EditText edtComentariosGerais;
    private EditText edtLocalDegustacao;
    private Button btnSalvarNota;
    private ProgressBar pgrFinalizar;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    // Variáveis
    private String userID;
    private Date date;

    // Context
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota_degustacao);

        Log.d(TAG, "onCreate: Init Context");
        // Inicializando "mContext"
        mContext = NotaDegustacao.this;

        Log.d(TAG, "onCreate: Init views");
        toolbarConfig();
        initViews();
        numberPickerConfig();

        // Init Firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        // UserID
        userID = mAuth.getCurrentUser().getUid();

        // Date
        date = new Date();

        btnSalvarNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pgrFinalizar.setVisibility(View.VISIBLE);
                dialogConfirmarInclusaoNota();
            }
        });
    }

    private void initViews(){
        //region Views "InfoGarrafa"
        edtNomeVinicola = findViewById(R.id.edtNomeVinicola);
        edtNomeVinho = findViewById(R.id.edtNomeVinho);
        edtTipoVinho = findViewById(R.id.edtTipoVinho);
        edtPaisRegiao = findViewById(R.id.edtPaisRegiao);
        edtUva = findViewById(R.id.edtUva);
        edtVolAlcoolico = findViewById(R.id.edtVolAlcoolico);
        //endregion

        //region Views "Análise Visual"
        edtCorVinho = findViewById(R.id.edtCorVinho);
        edtAspectoVinho = findViewById(R.id.edtAspectoVinho);

        // Spinner para selecionar a variação de tonalidade do vinho
        spnVarTonalidade = findViewById(R.id.spnVarTonalidade);
        ArrayAdapter<CharSequence> adapterVarTonalidade = ArrayAdapter
                .createFromResource(mContext, R.array.array_tonalidade,
                        android.R.layout.simple_spinner_item);
        adapterVarTonalidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnVarTonalidade.setAdapter(adapterVarTonalidade);

        // Spinner para selecionar a variação de viscosidade do vinho
        spnViscosidade = findViewById(R.id.spnViscosidade);
        ArrayAdapter<CharSequence> adapterViscosidade = ArrayAdapter
                .createFromResource(mContext, R.array.array_viscosidade,
                        android.R.layout.simple_spinner_item);
        adapterViscosidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnViscosidade.setAdapter(adapterViscosidade);
        //endregion

        //region Views "Análise Olfativa"
        edtAromasNasais = findViewById(R.id.edtAromasNasais);
        edtCondicaoAroma = findViewById(R.id.edtCondicaoAromatica);

        // Spinner para selecionar a itensidade aromática do vinho
        spnIntensidadeAromatica = findViewById(R.id.spnItensidadeAromatica);
        ArrayAdapter<CharSequence> adapterIntensidadeAromatica = ArrayAdapter
                .createFromResource(mContext, R.array.array_itensidade_aromatica,
                        android.R.layout.simple_spinner_item);
        adapterIntensidadeAromatica
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnIntensidadeAromatica.setAdapter(adapterIntensidadeAromatica);
        //endregion

        //region Views "Análise do Paladar"
        edtAromasRetronasais = findViewById(R.id.edtAromasRetronasais);

        // Spinner para selecionar a doçura do vinho
        spnDocura = findViewById(R.id.spnDocura);
        ArrayAdapter<CharSequence> adapterDocura = ArrayAdapter
                .createFromResource(mContext, R.array.array_docura,
                        android.R.layout.simple_spinner_item);
        adapterDocura.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDocura.setAdapter(adapterDocura);

        // Spinner para selecionar o corpo do vinho
        spnCorpo = findViewById(R.id.spnCorpo);
        ArrayAdapter<CharSequence> adapterCorpo = ArrayAdapter
                .createFromResource(mContext, R.array.array_corpo,
                        android.R.layout.simple_spinner_item);
        adapterCorpo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCorpo.setAdapter(adapterCorpo);

        // Spinner para selecionar a acidez do vinho
        spnAcidez = findViewById(R.id.spnAcidez);
        ArrayAdapter<CharSequence> adapterAcidez = ArrayAdapter
                .createFromResource(mContext, R.array.array_acidez,
                        android.R.layout.simple_spinner_item);
        adapterAcidez.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAcidez.setAdapter(adapterAcidez);

        // Spinner para selecionar os taninos presentes no paladar
        spnTaninos = findViewById(R.id.spnTaninos);
        ArrayAdapter<CharSequence> adapterTaninos = ArrayAdapter
                .createFromResource(mContext, R.array.array_taninos,
                        android.R.layout.simple_spinner_item);
        adapterTaninos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTaninos.setAdapter(adapterTaninos);

        // Spinner para selecionar o final do paladar
        spnFinalVinho = findViewById(R.id.spnFinalVinho);
        ArrayAdapter<CharSequence> adapterFinalVinho = ArrayAdapter
                .createFromResource(mContext, R.array.array_final_vinho,
                        android.R.layout.simple_spinner_item);
        adapterFinalVinho.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFinalVinho.setAdapter(adapterFinalVinho);
        //endregion

        //region Views "Detalhes Finais"
        txtPontuacaoSelecionada = findViewById(R.id.txtPontuacaoSelecionada);
        txtPontuacaoNumero = findViewById(R.id.txtPontuacaoNumero);
        edtComentariosGerais = findViewById(R.id.edtComentariosGerais);
        edtLocalDegustacao = findViewById(R.id.edtLocalDegustacao);
        btnSalvarNota = findViewById(R.id.btnSalvarNota);
        pgrFinalizar = findViewById(R.id.pgrFinalizar);
        pgrFinalizar.setVisibility(View.GONE);

        // Spinner para a seleção da qualidade geral do vinho
        spnQualidadeGeral = findViewById(R.id.spnQualidadeGeral);
        ArrayAdapter<CharSequence> adapterQualidadeGeral = ArrayAdapter
                .createFromResource(mContext, R.array.array_qualidade_geral,
                        android.R.layout.simple_spinner_item);
        adapterQualidadeGeral.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnQualidadeGeral.setAdapter(adapterQualidadeGeral);
        //endregion
    }

    private void numberPickerConfig(){
        final NumberPicker npPontuação = findViewById(R.id.npPontuacao);

        // Configurando os valores minimos e máximos do NumberPicker
        npPontuação.setMinValue(60);
        npPontuação.setMaxValue(100);
        npPontuação.setWrapSelectorWheel(true);

        // Value change listener para o NumberPicker
        npPontuação.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String valorNumberPicker = Integer.toString(npPontuação.getValue());
                txtPontuacaoSelecionada.setVisibility(View.VISIBLE);
                txtPontuacaoNumero.setText(valorNumberPicker);
                txtPontuacaoNumero.setVisibility(View.VISIBLE);
            }
        });
    }

    // Método para configurar a Toolbar da activity
    private void toolbarConfig(){
        Toolbar toolbar = findViewById(R.id.toolbarNotaDegustacaoSingleScreen);
        setSupportActionBar(toolbar);
        setTitle(R.string.toolbar_nota_degustacao);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogVoltarTelaPrincipalDegustador();
            }
        });
    }

    private void dialogVoltarTelaPrincipalDegustador(){
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.cancelar_title)
                .setMessage(R.string.cancelar_nota)
                .setPositiveButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        voltarTelaPrincipalDegustador();
                    }
                })
                .setNegativeButton(R.string.voltar_acao, null)
                .show();
    }

    private void dialogConfirmarInclusaoNota(){
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.confirmar_nota_degustacao)
                .setMessage(R.string.confirmar_inclusão_nota_msg)
                .setPositiveButton(R.string.salvar_acao, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        salvarNotaDegustacao();
                    }
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    private void voltarTelaPrincipalDegustador(){
        Intent intent = new Intent(mContext, DegustadorTelaPrincipal.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() { dialogVoltarTelaPrincipalDegustador(); }

    /*
    --------------------------------------Firebase--------------------------------------
     */

    /**
     * Método para persistir uma nota de degustação na base de dados
     */
    private void salvarNotaDegustacao(){
        // Obtendo uma referencia do firebase database
        myRef = mFirebaseDatabase.getReference();

        Log.d(TAG, "salvarNotaDegustacao: Recuperando os inputs do usuário");
        String nomeVinicola = edtNomeVinicola.getText().toString();
        final String nomeVinho = edtNomeVinho.getText().toString();
        String tipoVinho = edtTipoVinho.getText().toString();
        String paisRegiao = edtPaisRegiao.getText().toString();
        String uvaVinho = edtUva.getText().toString();
        String volAlcoolico = edtVolAlcoolico.getText().toString();

        String corVinho = edtCorVinho.getText().toString();
        String aspectoVinho = edtAspectoVinho.getText().toString();
        String varTonalidade = spnVarTonalidade.getSelectedItem().toString();
        String viscosidade = spnViscosidade.getSelectedItem().toString();

        String itensidadeAroma = spnIntensidadeAromatica.getSelectedItem().toString();
        String aromasNasais = edtAromasNasais.getText().toString();
        String condicaoAroma = edtCondicaoAroma.getText().toString();

        String docura = spnDocura.getSelectedItem().toString();
        String corpo = spnCorpo.getSelectedItem().toString();
        String acidez = spnAcidez.getSelectedItem().toString();
        String taninos = spnTaninos.getSelectedItem().toString();
        String finalVinho = spnFinalVinho.getSelectedItem().toString();
        String aromaRetro = edtAromasRetronasais.getText().toString();

        String qualidadeGeral = spnQualidadeGeral.getSelectedItem().toString();
        String comentariosGerais = edtComentariosGerais.getText().toString();
        String pontuacao = txtPontuacaoNumero.getText().toString();
        String localDegustacao = edtLocalDegustacao.getText().toString();

        // Data da degustação
        Log.d(TAG, "salvarNotaDegustacao: Recuperando a data atual");
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String dtaDegustacao = format.format(date);

        // String com o ID da Nota de Degustação
        String idNota = myRef.push().getKey();
        String idVinho = myRef.push().getKey();

        // Inserindo o Vinho e a Nota na base de dados
        try {
            Vinho vinho = new Vinho(idVinho, idNota, userID, corVinho, varTonalidade, viscosidade,
                    aspectoVinho, condicaoAroma, itensidadeAroma, aromasNasais, docura, corpo, acidez,
                    taninos, aromaRetro, finalVinho, pontuacao, comentariosGerais, qualidadeGeral,
                    nomeVinicola, nomeVinho, tipoVinho, paisRegiao, uvaVinho, volAlcoolico);

            br.com.sdpv.model.NotaDegustacao notaDegustacao = new br.com.sdpv.model.NotaDegustacao(
                    idNota, idVinho, userID, nomeVinho,
                    dtaDegustacao, localDegustacao);

            Log.d(TAG, "salvarNotaDegustacao: Inserindo os dados do vinho e da nota na " +
                    "base de dados");
            myRef.child(mContext.getString(R.string.db_vinho)).child(idVinho).setValue(vinho);
            myRef.child(mContext.getString(R.string.db_nota_degustacao))
                    .child(idNota).setValue(notaDegustacao);

            Toast.makeText(mContext, R.string.nota_add_sucesso, Toast.LENGTH_SHORT).show();
            pgrFinalizar.setVisibility(View.GONE);
            voltarTelaPrincipalDegustador();
        } catch (Exception e) {
            Toast.makeText(mContext, R.string.nota_add_erro, Toast.LENGTH_SHORT)
                    .show();
            pgrFinalizar.setVisibility(View.GONE);

            Log.e(TAG, "salvarNotaDegustacao: Erro: " + e.getMessage());
        }
    }
}