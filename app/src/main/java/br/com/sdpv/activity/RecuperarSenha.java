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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import br.com.sdpv.R;

public class RecuperarSenha extends AppCompatActivity {

    public static final String TAG = "RecuperarSenha";

    // Views
    private Button btnEnviar;
    private Button btnCancelar;
    private EditText edtEmail;

    // FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);
        Log.d(TAG, "onCreate: InitViews");

        // Instância do FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        initViews();
        toolbarConfig();

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelarRecuperacao(RecuperarSenha.this);
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordReset();
            }
        });
    }

    private void initViews() {
        btnEnviar = findViewById(R.id.btnEnviar);
        btnCancelar = findViewById(R.id.btnCancelarRecuperacao);
        edtEmail = findViewById(R.id.edtEmailRecuperacao);

        edtEmail.addTextChangedListener(twEmailRecuperacao);
    }

    private void toolbarConfig() {
        Toolbar toolbar = findViewById(R.id.toolbarRecuperarSenha);
        setSupportActionBar(toolbar);
        setTitle(R.string.toolbar_recuperar_senha);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelarRecuperacao(RecuperarSenha.this);
            }
        });
    }

    private void dialogCancelarRecuperacao(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.cancelar_title)
                .setMessage(R.string.cancelar_recuperacao)
                .setPositiveButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VoltarTelaLogin();
                    }
                })
                .setNegativeButton(R.string.voltar_acao, null)
                .show();
    }

    @Override
    public void onBackPressed() { dialogCancelarRecuperacao(RecuperarSenha.this); }

    private void VoltarTelaLogin() {
        Intent intent = new Intent(RecuperarSenha.this, Login.class);
        startActivity(intent);
        finish();
    }

    private TextWatcher twEmailRecuperacao = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String emailUsuario = edtEmail.getText().toString();

            btnEnviar.setEnabled(!emailUsuario.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    /**
     * Firebase Auth - Recuperação de Senha com Email.
     */
    private void passwordReset() {
        String emailUsuario = edtEmail.getText().toString();
        Log.d(TAG, "PasswordReset: Reg. Email: " + emailUsuario);

        mAuth.sendPasswordResetEmail(emailUsuario)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Success");
                            Toast.makeText(RecuperarSenha.this,
                                    R.string.recuperar_senha_caixa_entrada,
                                    Toast.LENGTH_LONG)
                                    .show();

                            VoltarTelaLogin();

                        } else {
                            Log.d(TAG, "onComplete: Error: " + task.getException());
                            String msgErroRecuperacao = task.getException().getMessage();
                            Toast.makeText(RecuperarSenha.this, msgErroRecuperacao,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}