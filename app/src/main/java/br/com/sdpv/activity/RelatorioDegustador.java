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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.sdpv.R;

public class RelatorioDegustador extends AppCompatActivity {

    // Constants
    public static final String TAG = "RelatorioDegustador";
    public static final String KEY = "itemKey";
    public static final String USERID = "userid";

    // Constantes relacionadas ao Número de Vinhos Degustados
    public static final String TOTAL = "total";
    public static final String ANO = "ano";
    public static final String MES = "mes";
    public static final String DIA = "dia";

    // Views
    private ImageView imgFotoPerfilDegustador;
    private ImageView imgWineBottleRelatorioDegustador;
    private ImageView imgGerarRelatorioGraficoDegustador;
    private TextView txtNomeDegustadorRelatorio;
    private TextView txtEmailDegustadorRelatorio;
    private TextView txtNomeRelatorioDegustador;
    private TextView txtDtaRelatorioDegustador;
    private TextView txtTotal;
    private TextView txtAno;
    private TextView txtMes;
    private TextView txtDia;

    // Variáveis
    private String userID;
    private Date date;
    private String total;
    private String ano;
    private String mes;
    private String dia;

    // Context
    private Context mContext;

    // Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_degustador);
        Log.d(TAG, "onCreate: Init Views");
        toolbarConfig();
        initViews();

        // Init Context
        mContext = RelatorioDegustador.this;

        // Init Firebase
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("degustador");

        // Recuperando a chave do usuário do enviada pela activity ListaDegustadoresRelatorio
        final Intent intent = getIntent();
        userID = intent.getExtras().getString(KEY);

        // Init Date
        date = new Date();

        // Chamando o método para recuperar os dados do degustador
        recuperarDadosDegustador();

        imgWineBottleRelatorioDegustador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVinhosDegustados = new Intent(mContext,
                        ListaVinhosDegustadosRelatorio.class);

                intentVinhosDegustados.putExtra(USERID, userID);
                startActivity(intentVinhosDegustados);
            }
        });

        imgGerarRelatorioGraficoDegustador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRelatorioGrafico = new Intent(mContext,
                        RelatorioGraficoDegustador.class);

                intentRelatorioGrafico.putExtra(TOTAL, total);
                intentRelatorioGrafico.putExtra(ANO, ano);
                intentRelatorioGrafico.putExtra(MES, mes);
                intentRelatorioGrafico.putExtra(DIA, dia);
                startActivity(intentRelatorioGrafico);
            }
        });
    }

    private void toolbarConfig(){
        Toolbar toolbar = findViewById(R.id.toolbarRelatorioDegustador);
        setSupportActionBar(toolbar);
        setTitle(R.string.toolbar_relatorio_degustador);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltarTelaListaDegustadores();
            }
        });
    }

    private void initViews(){
        imgFotoPerfilDegustador = findViewById(R.id.imgFotoPerfilDegustador);
        imgWineBottleRelatorioDegustador = findViewById(R.id.imgWineBottleRelatorioDegustador);
        imgGerarRelatorioGraficoDegustador = findViewById(R.id.imgGerarRelatorioGrafico);
        txtNomeDegustadorRelatorio = findViewById(R.id.txtNomeDegustadorRelatorio);
        txtEmailDegustadorRelatorio = findViewById(R.id.txtEmailDegustadorRelatorio);
        txtNomeRelatorioDegustador = findViewById(R.id.txtNomeRelatorioDegustador);
        txtDtaRelatorioDegustador = findViewById(R.id.txtDtaRelatorioDegustador);
        txtTotal = findViewById(R.id.txtNVinhosDegustadosTotal);
        txtAno = findViewById(R.id.txtNVinhosDegustadosAno);
        txtMes = findViewById(R.id.txtNVinhosDegustadosMes);
        txtDia = findViewById(R.id.txtNVinhosDegustadosDia);
    }

    private void voltarTelaListaDegustadores(){
        Intent intent = new Intent(mContext, ListaDegustadoresRelatorio.class);
        startActivity(intent);
    }

    /*
    --------------------------------------Firebase--------------------------------------
     */

    /**
     *  Firebase - Método para recuperar os dados do degustador da base de dados
     */
    private void recuperarDadosDegustador(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userID).exists()) {
                    /*
                    Lógica para recuperar a foto de perfil do degustador, se ele tem uma foto
                    de perfil ela é mostrada, se não uma imagem padrão é mostrada no lugar.
                     */
                    if (dataSnapshot.child(userID).child("urlFotoPerfil").getValue().equals("")) {
                        imgFotoPerfilDegustador
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
                                .into(imgFotoPerfilDegustador);
                    }

                    // Recuperando o nome do degustador e armazenando ele em uma variável String
                    String nomeDegustadorRecuperado = dataSnapshot
                            .child(userID)
                            .child("nome")
                            .getValue()
                            .toString();

                    Log.d(TAG, "onDataChange: Nome do Degustador recuperado: " +
                            nomeDegustadorRecuperado);
                    // Configurando a view para exibir o nome do degustador
                    txtNomeDegustadorRelatorio.setText(nomeDegustadorRecuperado);

                    // Configurando a view para exibir o email do degustador
                    txtEmailDegustadorRelatorio.setText(dataSnapshot
                            .child(userID)
                            .child("email")
                            .getValue()
                            .toString());

                    // Configurando a view para exibir o nome do relatório
                    txtNomeRelatorioDegustador
                            .setText("Relatório do degustador " + nomeDegustadorRecuperado);

                    // Formatando a data recuperada e configurando a view para a fazer a exibição
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    txtDtaRelatorioDegustador.setText(format.format(date));

                    total = "2";
                    txtTotal.setText(total);

                    ano = "2";
                    txtAno.setText(ano);

                    mes = "2";
                    txtMes.setText(mes);

                    dia = "0";
                    txtDia.setText(dia);

                } else {
                    Log.d(TAG, "onDataChange: Degustador com esta USERID não existe");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Erro: " + databaseError.getDetails());
            }
        });
    }
}