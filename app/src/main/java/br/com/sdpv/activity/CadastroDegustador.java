package br.com.sdpv.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.sdpv.R;
import br.com.sdpv.model.Degustador;

public class CadastroDegustador extends AppCompatActivity {

    // Constants
    public static final String TAG = "CadastroDegustador";

    // Views
    private TextView txtFazerLogin;
    private TextView txtNomeDegustador;
    private TextView txtEmailDegustador;
    private TextView txtTelefoneDegustador;
    private TextView txtCPFDegustador;
    private TextView txtSenhaDegustador;
    private Button btnCadastrarDegustador;
    private Button btnCancelar;
    private Context mContext;
    private ProgressBar pgrCadastroDegustador;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_degustador);
        Log.d(TAG, "onCreate: InitViews");
        initViews();
        toolbarConfig();

        // Contexto da Activity
        mContext = CadastroDegustador.this;

        // Init Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("degustador");

        txtFazerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltarTelaLogin();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelar(CadastroDegustador.this);
            }
        });

        btnCadastrarDegustador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pgrCadastroDegustador.setVisibility(View.VISIBLE);
                criarContaDegustador();
            }
        });
    }

    private void initViews() {
        txtFazerLogin = findViewById(R.id.txtFazerLogin);
        txtNomeDegustador = findViewById(R.id.edtNomeDegustador);
        txtEmailDegustador = findViewById(R.id.edtEmailDegustador);
        txtTelefoneDegustador = findViewById(R.id.edtTelefoneDegustador);
        txtCPFDegustador = findViewById(R.id.edtCPFDegustador);
        txtSenhaDegustador = findViewById(R.id.edtSenhaDegustador);
        btnCadastrarDegustador = findViewById(R.id.btnCadastrarCadastro);
        btnCancelar = findViewById(R.id.btnCancelarCadastro);
        pgrCadastroDegustador = findViewById(R.id.pgrCadastroDegustador);

        pgrCadastroDegustador.setVisibility(View.GONE);

        // Adcionando TextWatcher para os campos
        txtNomeDegustador.addTextChangedListener(cadastroDegustadorTW);
        txtEmailDegustador.addTextChangedListener(cadastroDegustadorTW);
        txtTelefoneDegustador.addTextChangedListener(cadastroDegustadorTW);
        txtCPFDegustador.addTextChangedListener(cadastroDegustadorTW);
        txtSenhaDegustador.addTextChangedListener(cadastroDegustadorTW);
    }

    private void toolbarConfig() {
        Toolbar toolbar =  findViewById(R.id.toolbarDegustadorCadastro);
        setSupportActionBar(toolbar);
        setTitle(R.string.toolbar_cadastro_degustador);

        // Configurando o botão voltar na Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelar(CadastroDegustador.this);
            }
        });
    }

    // Método para voltar para a tela de login.
    private void voltarTelaLogin() {
        Intent intent = new Intent(CadastroDegustador.this, Login.class);
        startActivity(intent);
    }

    // TextWatcher para habilitar o botão de Cadastro somente se os campos estiverem preenchidos.
    private TextWatcher cadastroDegustadorTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String nomeDegustador = txtNomeDegustador.getText().toString();
            String emailDegustador = txtEmailDegustador.getText().toString();
            String telefoneDegustador = txtTelefoneDegustador.getText().toString();
            String cpfDegustador = txtCPFDegustador.getText().toString();
            String senhaDegustador = txtSenhaDegustador.getText().toString();

            btnCadastrarDegustador.setEnabled(!nomeDegustador.isEmpty()
                    && !emailDegustador.isEmpty()
                    && !telefoneDegustador.isEmpty()
                    && !cpfDegustador.isEmpty()
                    && !senhaDegustador.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    public void dialogCancelar(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.cancelar_title)
                .setMessage(R.string.cancelar_cadastro)
                .setPositiveButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        voltarTelaLogin();
                    }
                })
                .setNegativeButton(R.string.voltar_acao, null )
                .show();
    }

    @Override
    public void onBackPressed() { dialogCancelar(CadastroDegustador.this); }

    /*
    --------------------------------------Firebase--------------------------------------
     */

    /**
     * Firebase - Cadastro de Usuário
     */
    private void criarContaDegustador() {
        final String nome = txtNomeDegustador.getText().toString();
        final String email = txtEmailDegustador.getText().toString();
        final String telefone = txtTelefoneDegustador.getText().toString();
        final String cpf = txtCPFDegustador.getText().toString();
        final String senha = txtSenhaDegustador.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String userID = mAuth.getCurrentUser().getUid();
                    Log.d(TAG, "onComplete: Cadastro completado com sucesso! " +
                            "- ID do usuário " + userID);

                    Degustador degustador = new Degustador(userID,
                            nome,
                            email,
                            telefone,
                            cpf,
                            senha,
                            "");

                    Log.d(TAG, "onComplete: Salvando os dados do degustador na " +
                            "base de dados");
                    myRef.child(userID).setValue(degustador);

                    Toast.makeText(mContext, R.string.cadastro_realizado_sucesso,
                            Toast.LENGTH_SHORT).show();
                    pgrCadastroDegustador.setVisibility(View.GONE);
                    voltarTelaLogin();

                } else if (!task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Erro: " + task.getException());
                }
            }
        });
    }
}