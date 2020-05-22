package br.com.sdpv.activity;

import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.sdpv.R;
import br.com.sdpv.model.Administrador;

public class CadastroAdministrador extends AppCompatActivity {

    // Constants
    public static final String TAG = "CadastroAdministrador";

    // Views
    private EditText edtNomeAdmin;
    private EditText edtEmailAdmin;
    private EditText edtTelefoneAdmin;
    private EditText edtCPF_CNPJAdmin;
    private EditText edtSenhaAdmin;
    private Button btnCadastroAdmin;
    private Button btnCancelar;
    private ProgressBar pgrCadastroAdmin;

    // Context
    private Context mContext;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_administrador);
        Log.d(TAG, "onCreate: Init Views");
        initViews();
        toolbarConfig();

        // Init context
        mContext = CadastroAdministrador.this;

        // Init Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("administrador");

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelarCadastroAdmin();
            }
        });

        btnCadastroAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pgrCadastroAdmin.setVisibility(View.VISIBLE);
                criarContaAdminstrador();
            }
        });
    }

    private void toolbarConfig(){
        Toolbar toolbar = findViewById(R.id.toolbarCadastroAdmin);
        setSupportActionBar(toolbar);
        setTitle(R.string.toolbar_cadastro_admin);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelarCadastroAdmin();
            }
        });
    }

    private void initViews(){
        edtNomeAdmin = findViewById(R.id.edtNomeAdmin);
        edtEmailAdmin = findViewById(R.id.edtEmailAdmin);
        edtTelefoneAdmin = findViewById(R.id.edtTelefoneAdmin);
        edtCPF_CNPJAdmin = findViewById(R.id.edtCPF_CNPJAdmin);
        edtSenhaAdmin = findViewById(R.id.edtSenhaAdmin);
        btnCadastroAdmin = findViewById(R.id.btnCadastrarCadastroAdmin);
        btnCancelar = findViewById(R.id.btnCancelarCadastroAdmin);
        pgrCadastroAdmin = findViewById(R.id.pgrCadastroAdmin);

        pgrCadastroAdmin.setVisibility(View.GONE);

        // Adcionando TextWatcher para os campos
        edtNomeAdmin.addTextChangedListener(cadastroAdministradorTW);
        edtEmailAdmin.addTextChangedListener(cadastroAdministradorTW);
        edtTelefoneAdmin.addTextChangedListener(cadastroAdministradorTW);
        edtCPF_CNPJAdmin.addTextChangedListener(cadastroAdministradorTW);
        edtSenhaAdmin.addTextChangedListener(cadastroAdministradorTW);
    }

    private void direcionarTelaLogin(){
        Intent intent = new Intent(mContext, Login.class);
        startActivity(intent);
    }

    private void direcionarTelaSplashScreenFirstTime(){
        Intent intent =  new Intent(mContext, SplashScreenFirstTime.class);
        startActivity(intent);
    }

    // TextWatcher para habilitar o botão de Cadastro somente se os campos estiverem preenchidos.
    private TextWatcher cadastroAdministradorTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String nomeDegustador = edtNomeAdmin.getText().toString();
            String emailDegustador = edtEmailAdmin.getText().toString();
            String telefoneDegustador = edtTelefoneAdmin.getText().toString();
            String cpfDegustador = edtCPF_CNPJAdmin.getText().toString();
            String senhaDegustador = edtSenhaAdmin.getText().toString();

            btnCadastroAdmin.setEnabled(!nomeDegustador.isEmpty()
                    && !emailDegustador.isEmpty()
                    && !telefoneDegustador.isEmpty()
                    && !cpfDegustador.isEmpty()
                    && !senhaDegustador.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    // Método para criar uma caixa de dialogo para a cofirmação do cancelamento do cadastro
    private void dialogCancelarCadastroAdmin(){
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.cancelar_title)
                .setMessage(R.string.cancelar_cadastro)
                .setPositiveButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        direcionarTelaSplashScreenFirstTime();
                    }
                })
                .setNegativeButton(R.string.voltar_acao, null)
                .show();
    }

    /*
    Método sobrescrito para quando o usuário apertar o botão "Voltar", a caixa de dialogo irá
    aparecer.
     */
    @Override
    public void onBackPressed() { dialogCancelarCadastroAdmin(); }

    /*
    --------------------------------------Firebase--------------------------------------
     */

    /**
     * Firebase - Cadastrando o administrador e armazenando seus dados na base
     */
    private void criarContaAdminstrador() {
        final String nome = edtNomeAdmin.getText().toString();
        final String email = edtEmailAdmin.getText().toString();
        final String telefone = edtTelefoneAdmin.getText().toString();
        final String cpf = edtCPF_CNPJAdmin.getText().toString();
        final String senha = edtSenhaAdmin.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userID = mAuth.getCurrentUser().getUid();
                    Log.d(TAG, "onComplete: Admin cadastrado com sucesso! - ID do usuário: "
                            + userID);

                    Administrador administrador = new Administrador(userID,
                            nome,
                            email,
                            cpf,
                            telefone,
                            senha,
                            "");

                    Log.d(TAG, "onComplete: Armazenando os dados do degustador na " +
                            "base de dados");
                    myRef.child(userID).setValue(administrador);

                    pgrCadastroAdmin.setVisibility(View.GONE);
                    Toast.makeText(mContext, R.string.cadastro_realizado_sucesso,
                            Toast.LENGTH_SHORT).show();
                    direcionarTelaLogin();
                } else if (!task.isSuccessful()){
                    Log.d(TAG, "onComplete: Erro: " + task.getException());
                    pgrCadastroAdmin.setVisibility(View.GONE);
                    Toast.makeText(mContext, "Erro: " + task.getException(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
