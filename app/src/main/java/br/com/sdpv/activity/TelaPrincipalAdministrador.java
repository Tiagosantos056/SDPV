package br.com.sdpv.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import br.com.sdpv.R;
import br.com.sdpv.dialog.DialogAlterarDadosAdmin;

public class TelaPrincipalAdministrador extends AppCompatActivity
        implements DialogAlterarDadosAdmin.alterarDadosAdministrador {

    // Constants
    public static final String TAG = "TelaPrincipalAdmin";
    private static final int RC_PHOTO_PICKER =  1;

    // Views
    private ImageView imgAdminProfilePic;
    private ImageView imgEditProfileAdmin;
    private TextView txtNomeAdmin;
    private TextView txtEmailAdmin;
    private ConstraintLayout clOpcaoGerenciarDegustador;
    private ConstraintLayout clOpcaoGerenciarNotasDegustacao;
    private ConstraintLayout clOpcaoGerarRelatorio;

    // Context
    private Context mContext;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal_administrador);
        Log.d(TAG, "onCreate: Init Views");
        toolbarConfig();
        initViews();
        firebaseAuthConfig();

        // Init Context
        mContext = TelaPrincipalAdministrador.this;

        // Init FirebaseStorage
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference().child("profile_photos");

        clOpcaoGerenciarDegustador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { abrirTelaListaDegustador(); }
        });

        clOpcaoGerarRelatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaRelatorioDegustador();
            }
        });

        imgAdminProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(
                        intent,
                        "Complete a ação usando")
                        , RC_PHOTO_PICKER);
            }
        });

        imgEditProfileAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dialogAlterarDadosAdmin(); }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri imagemSelecionada = data.getData();
            final StorageReference photoRef = mStorageReference.child(
                    imagemSelecionada.getLastPathSegment());

            Log.d(TAG, "onActivityResult: Armazenado a photo de perfil na Base de Dados");
            photoRef.putFile(imagemSelecionada).addOnCompleteListener(this,
                    new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUri) {
                                        String downloadUrl = String.valueOf(downloadUri);

                                        Log.d(TAG, "onSuccess: Armazenado a url da photo na base de dados: " +
                                                downloadUrl);
                                        myRef.child(mAuth.getCurrentUser().getUid())
                                                .child("urlFotoPerfil")
                                                .setValue(downloadUrl);

                                        Toast.makeText(mContext, R.string.foto_perfil_add_sucesso,
                                                Toast.LENGTH_SHORT).show();

                                        Log.d(TAG, "onSuccess: Carregando a foto do usuário com Glide");
                                        Glide.with(mContext)
                                                .load(downloadUrl)
                                                .apply(RequestOptions.circleCropTransform())
                                                .into(imgAdminProfilePic);
                                    }
                                });
                            }
                        }
                    });
        }
    }

    private void toolbarConfig(){
        Toolbar toolbar = findViewById(R.id.toolbarAdminTelaPrincipal);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_name);
    }

    private void initViews(){
        imgAdminProfilePic = findViewById(R.id.imgAdminProfilePic);
        imgEditProfileAdmin = findViewById(R.id.imgEditProfileAdmin);
        txtNomeAdmin = findViewById(R.id.txtNomeAdmin);
        txtEmailAdmin = findViewById(R.id.txtEmailAdmin);
        clOpcaoGerenciarDegustador = findViewById(R.id.clGerenciarDegustador);
        clOpcaoGerenciarNotasDegustacao = findViewById(R.id.clGerenciarNotasDegustacao);
        clOpcaoGerarRelatorio = findViewById(R.id.clGerarRelatorios);

        imgAdminProfilePic.setVisibility(View.GONE);
        txtNomeAdmin.setVisibility(View.GONE);
        txtEmailAdmin.setVisibility(View.GONE);
    }

    // Método que lida com o retorno a tela inicial
    private void voltarTelaLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    // Método para chamar a caixa de diálogo "DialogAlterarDadosAdmin"
    public void dialogAlterarDadosAdmin(){
        DialogAlterarDadosAdmin dialogAlterarDadosAdmin = new DialogAlterarDadosAdmin();
        dialogAlterarDadosAdmin
                .show(getSupportFragmentManager(), "DialogAlterarDadosAdmin");
    }

    // Método para chamar a tela de lista de degustadores
    private void abrirTelaListaDegustador(){
        Intent intent = new Intent(mContext, ListaDegustadores.class);
        startActivity(intent);
    }

    private void abrirTelaRelatorioDegustador(){
        Intent intent = new Intent(mContext, ListaDegustadoresRelatorio.class);
        startActivity(intent);
    }

    // Método necessário para implementar a classe "DialogAlterarDadosAdmin"
    @Override
    public void alterarDadosAdministrador(String userID, String email, String telefone,
                                          String senha) {}

    // Método para construção de uma Dialog box.
    public void dialogSairConta(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.sair_title)
                .setMessage(R.string.sair_msg)
                .setPositiveButton(R.string.sair_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    @Override
    public void onBackPressed() {
        dialogSairConta(mContext);
    }

    // Método para configurar o menu na Toolbar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_admin_home_screen_sair, menu);
        return true;
    }

    // Método para configurar a nevegação dos itens da Toolbar.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sair_admin:
                logout();
            default:
                return false;
        }
    }

    /*
    --------------------------------------Firebase--------------------------------------
     */

    /**
     * Método para checar se o @param user está logado, se não, a activity de login é chamada.
     * @param user
     */
    private void checandoUsuarioLogado(FirebaseUser user){
        Log.d(TAG, "checandoUsuarioLogado: Checando se o usuário está logado.");

        if (user == null){
            voltarTelaLogin();
        }
    }

    /**
     * Configurando o Firebase Auth e recuperando os dados do usuário cadastrado na base
     */
    private void firebaseAuthConfig(){
        Log.d(TAG, "firebaseAuthConfig: Configurando os componentes do Firebase");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("administrador");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                // Checando se o usuário está logado.
                checandoUsuarioLogado(user);

                if (user != null) {
                    final String userID = user.getUid();
                    Log.d(TAG, "onAuthStateChanged: ID do usuário logado: " + userID);

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()){
                                if (ds.getKey().equals(userID)){
                                    Log.d(TAG, "onDataChange: Recuperando os dados do admin: "
                                            + userID);
                                    txtNomeAdmin.setText(
                                            ds.child("nome")
                                                    .getValue()
                                                    .toString());

                                    txtEmailAdmin.setText(
                                            ds.child("email")
                                                    .getValue()
                                                    .toString());

                                        if (ds.child("urlFotoPerfil").getValue().equals("")){
                                            imgAdminProfilePic
                                                    .setImageResource(R
                                                            .drawable
                                                            .ic_account_circle_tela_principal_degustador);
                                            imgAdminProfilePic.setVisibility(View.VISIBLE);

                                        } else {
                                            String urlFotoPerfil =
                                                    ds.child("urlFotoPerfil").getValue().toString();

                                            Glide.with(mContext)
                                                    .load(urlFotoPerfil)
                                                    .apply(RequestOptions.circleCropTransform())
                                                    .into(imgAdminProfilePic);

                                            imgAdminProfilePic.setVisibility(View.VISIBLE);
                                    }
                                    txtNomeAdmin.setVisibility(View.VISIBLE);
                                    txtEmailAdmin.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: Erro: " + databaseError.getMessage());
                        }
                    });

                } else {
                    Log.d(TAG, "onAuthStateChanged: Nenhum usuário logado.");
                }
            }
        };
    }

    /**
     * Método para realizar logout usando FirebaseAuth
     */
    private void logout() {
        mAuth.signOut();
        voltarTelaLogin();

        Toast.makeText(mContext, R.string.logout_sucesso,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        checandoUsuarioLogado(mAuth.getCurrentUser());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }
}