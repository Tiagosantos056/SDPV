package br.com.sdpv.activity;

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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.sdpv.R;
import br.com.sdpv.dialog.DialogAlterarDadosDegustador;

public class Login extends AppCompatActivity {

    // Constants
    public static final String TAG = "Login";

    // Views
    private Button btnLogin;
    private EditText edtEmailLogin;
    private EditText edtSenhaLogin;
    private TextView txtRecuperarSenha;
    private TextView txtCadastroDegustador;
    private ProgressBar pgrLogin;
    private RadioButton rBtnDegustador;
    private RadioButton rBtnAdministrador;

    // Context
    private Context mContext;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: InitViews");
        initViews();
        firebaseAuthConfig();
        rBtnDegustador.setChecked(true);

        // Inicializando o Contexto
        mContext = Login.this;

        // Configurando a ProgressBar
        pgrLogin.setVisibility(View.GONE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pgrLogin.setVisibility(View.VISIBLE);
                if (rBtnDegustador.isChecked()){
                    Log.d(TAG, "onClick: Realizando login como degustador");
                    realizarLoginDegustador();
                } else if (rBtnAdministrador.isChecked()){
                    Log.d(TAG, "onClick: Realizando login com admin");
                    realizarLoginAdministrador();
                }
            }
        });

        txtCadastroDegustador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaCadastro();
            }
        });

        txtRecuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaRecuperarSenha();
            }
        });
    }

    private void initViews() {
        btnLogin = findViewById(R.id.btnLogin);
        edtEmailLogin = findViewById(R.id.edtEmailLogin);
        edtSenhaLogin = findViewById(R.id.edtSenhaLogin);
        txtRecuperarSenha = findViewById(R.id.txtRecuperarSenha);
        txtCadastroDegustador = findViewById(R.id.txtCadastroDegustadorLogin);
        pgrLogin = findViewById(R.id.pgrLogin);
        rBtnDegustador = findViewById(R.id.rBtnDegustador);
        rBtnAdministrador = findViewById(R.id.rBtnAdministrador);

        edtEmailLogin.addTextChangedListener(loginTW);
        edtSenhaLogin.addTextChangedListener(loginTW);

        if (rBtnDegustador.isChecked() || rBtnAdministrador.isChecked()){
            btnLogin.setEnabled(true);
        }
    }

    private void abrirTelaCadastro() {
        Intent intent = new Intent(mContext, CadastroDegustador.class);
        startActivity(intent);
    }

    private void abrirTelaRecuperarSenha() {
        Intent intent = new Intent(mContext, RecuperarSenha.class);
        startActivity(intent);
    }

    // Método para direcionar o degustador a tela inicial
    private void direcionarTelaIncialDegustador() {
        Intent intent = new Intent(mContext, DegustadorTelaPrincipal.class);
        startActivity(intent);
    }

    // Método para direcionar o admin a tela inicial
    private void direcionarTelaIncialAdmin(){
        Intent intent = new Intent(mContext, TelaPrincipalAdministrador.class);
        startActivity(intent);
    }

    // TextWatcher para ativar o Botão de Login somente se o usuário digitar algo nos Campos.
    private TextWatcher loginTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String emailUsuario = edtEmailLogin.getText().toString().trim();
            String senhaUsuario = edtSenhaLogin.getText().toString().trim();

            btnLogin.setEnabled(!emailUsuario.isEmpty() && !senhaUsuario.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    // Método para exibir um Dialog quando o usuário pressionar o botão "Voltar".
    private void dialogSairApp(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.sair_title)
                .setMessage(R.string.sair_app)
                .setPositiveButton(R.string.sair_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setNegativeButton(R.string.voltar_acao, null)
                .show();
    }

    @Override
    public void onBackPressed() { dialogSairApp(Login.this); }

    /*
    --------------------------------------Firebase--------------------------------------
     */

    /**
     * Método para realizar Login com Firebase Auth
     */
    private void realizarLoginDegustador(){
        final String email = edtEmailLogin.getText().toString();
        final String senha = edtSenhaLogin.getText().toString();

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(mContext, R.string.login_sucesso,
                                    Toast.LENGTH_SHORT).show();
                            direcionarTelaIncialDegustador();
                        } else {
                            Log.d(TAG, "signInWithEmail:failure", task.getException());

                            pgrLogin.setVisibility(View.GONE);
                            Toast.makeText(Login.this, R.string.falha_autenticacao,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Método para checar os dados do admin na base, e se os dados baterem, o usuário é logado.
     */
    private void realizarLoginAdministrador(){
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDatabase.getReference("administrador");

        final String email = edtEmailLogin.getText().toString();
        final String senha = edtSenhaLogin.getText().toString();

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();

                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Log.d(TAG, "onDataChange: Chacando se o dados de admin " +
                                            "prestes a logar correpondem aos dados cadastrados na base");
                                    if (email.equals(dataSnapshot
                                            .child(user.getUid()).child("email").getValue().toString())
                                            &&
                                            senha.equals(dataSnapshot
                                            .child(user.getUid()).child("senha").getValue().toString())){
                                        Log.d(TAG, "onDataChange: Dados checados com sucesso!");
                                        Toast.makeText(mContext, R.string.login_sucesso,
                                                Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onDataChange: Direcionando a tela inicial do admin");
                                        direcionarTelaIncialAdmin();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    pgrLogin.setVisibility(View.GONE);
                                    Log.d(TAG, "onCancelled: Erro: " + databaseError.getMessage());
                                    Toast.makeText(
                                            mContext,
                                            "Erro: " + databaseError.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Log.d(TAG, "signInWithEmail:failure", task.getException());

                            pgrLogin.setVisibility(View.GONE);
                            Toast.makeText(Login.this, R.string.falha_autenticacao,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Configurando o Firebase Auth
     */
    private void firebaseAuthConfig(){
        Log.d(TAG, "firebaseAuthConfig: Configurando o Firebase Auth");
        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: Usuário logado: " + user.getUid());
                }
                else {
                    Log.d(TAG, "onAuthStateChanged: Nenhum usuário logado.");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }
}