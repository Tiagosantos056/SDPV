package br.com.sdpv.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.RelativeLayout;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import br.com.sdpv.R;
import br.com.sdpv.dialog.DialogAlterarEmailDegustador;
import br.com.sdpv.dialog.DialogAlterarSenhaDegustador;
import br.com.sdpv.dialog.DialogAlterarTelefoneDegustador;
import br.com.sdpv.dialog.DialogConfirmarCredencias;
import br.com.sdpv.dialog.DialogVisualizarDadosCadastraisDegustador;

public class DegustadorTelaPrincipal extends AppCompatActivity {

    // Constants
    public static final String TAG = "DegustadorTelaPrincipal";
    private static final int RC_PHOTO_PICKER =  1;

    // Views
    private ImageView imgDegustadorProfilePic;
    private TextView txtNomeDegustadorTelaPrincipal;
    private TextView txtEmailDegustadorTelaPrincipal;
    private RelativeLayout rlOpcaoNotaDegustacao;
    private RelativeLayout rlOpcaoVisualizarDadosCadastrais;
    private RelativeLayout rlOpcaoVinhosDegustados;
    private RelativeLayout rlOpcaoAlterarEmailDegustador;
    private RelativeLayout rlOpcaoAlterarSenhaDegustador;
    private RelativeLayout rlOpcaoAlterarTelefoneDegustador;
    private RelativeLayout rlOpcaoDeletarContaDegustador;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_degustador_tela_principal);
        Log.d(TAG, "onCreate: InitViews");
        initViews();
        toolbarConfig();
        firebaseAuthConfig();

        // Inicializando a Variável "mContext"
        mContext = DegustadorTelaPrincipal.this;

        // Firebase storage
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference().child("profile_photos");

        rlOpcaoNotaDegustacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaNotaDegustacao();
            }
        });

        rlOpcaoVinhosDegustados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaVinhosDegustados();
            }
        });

        rlOpcaoVisualizarDadosCadastrais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null){
                    dialogConfirmarCredenciais();
                } else {
                    dialogVisualizarDadosDegustador();
                }
            }
        });

        rlOpcaoAlterarEmailDegustador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null){
                    dialogConfirmarCredenciais();
                } else {
                    dialogAlterarEmailDegustador();
                }
            }
        });

        rlOpcaoAlterarSenhaDegustador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null){
                    dialogConfirmarCredenciais();
                } else {
                    dialogAlterarSenhaDegustador();
                }
            }
        });

        rlOpcaoAlterarTelefoneDegustador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() ==  null){
                    dialogConfirmarCredenciais();
                } else {
                    dialogAlterarTelefoneDegustador();
                }
            }
        });

        rlOpcaoDeletarContaDegustador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null){
                    dialogConfirmarCredenciais();
                } else {
                    dialogExcluirConta();
                }
            }
        });

        imgDegustadorProfilePic.setOnClickListener(new View.OnClickListener() {
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
                                                .into(imgDegustadorProfilePic);
                                    }
                                });
                            }
                        }
                    });
        }
    }

    private void initViews() {
        imgDegustadorProfilePic = findViewById(R.id.imgDegustadorProfilePic);
        txtNomeDegustadorTelaPrincipal = findViewById(R.id.txtNomeDegustador);
        txtEmailDegustadorTelaPrincipal = findViewById(R.id.txtEmailDegustadorRelatorio);
        rlOpcaoNotaDegustacao = findViewById(R.id.rlOpcaoNotaDegustacao);
        rlOpcaoVisualizarDadosCadastrais = findViewById(R.id.rlVisualizarDadosCadastrais);
        rlOpcaoVinhosDegustados = findViewById(R.id.rlOpcaoVinhosDegustados);
        rlOpcaoAlterarEmailDegustador = findViewById(R.id.rlOpcaoAlterarEmailDegustador);
        rlOpcaoAlterarSenhaDegustador = findViewById(R.id.rlOpcaoAlterarSenhaDegustador);
        rlOpcaoAlterarTelefoneDegustador = findViewById(R.id.rlOpcaoAlterarTelefoneDegustador);
        rlOpcaoDeletarContaDegustador = findViewById(R.id.rlOpcaoDeletarContaUsuario);

        txtNomeDegustadorTelaPrincipal.setVisibility(View.GONE);
        txtEmailDegustadorTelaPrincipal.setVisibility(View.GONE);
        imgDegustadorProfilePic.setVisibility(View.GONE);
    }

    // Método para configurar a Toolbar da Activity
    private void toolbarConfig() {
        Toolbar toolbar = findViewById(R.id.toolbarDegustadorTelaPrincipal);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_name);
    }

    // Método que lida com o retorno a tela inicial
    private void voltarTelaLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    // Método para abrir a Activity de Nota de Degustação
    private void abrirTelaNotaDegustacao() {
        Intent intent = new Intent(mContext, NotaDegustacao.class);
        startActivity(intent);
    }

    private void abrirTelaVinhosDegustados(){
        Intent intent = new Intent(mContext, ListaVinhosDegustador.class);
        startActivity(intent);
    }

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

    private void dialogExcluirConta(){
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.deletar_conta_title)
                .setMessage(R.string.deletar_conta_msg)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.confirmar_acao, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletarConta();
                    }
                })
                .show();
    }

    // Método para criar uma instância da Classe DialogConfirmarCredenciais.
    private void dialogConfirmarCredenciais(){
        DialogConfirmarCredencias dialogConfirmarCredencias =
                new DialogConfirmarCredencias();

        dialogConfirmarCredencias.show(getSupportFragmentManager(),
                "DialogConfirmarCrdenciais");
    }

    // Método para criar uma instância da Classe DialogVisualizarDadosCadastraisDegustador
    private void dialogVisualizarDadosDegustador(){
        DialogVisualizarDadosCadastraisDegustador dialogVisualizarDadosCadastraisDegustador =
                new DialogVisualizarDadosCadastraisDegustador();

        dialogVisualizarDadosCadastraisDegustador.show(getSupportFragmentManager(),
                "DialogVisualizarDadosDegustador");
    }

    // Método para criar uma instância da Classe DialogAlterarEmailDegustador
    private void dialogAlterarEmailDegustador(){
        DialogAlterarEmailDegustador alterarEmailDegustador =
                new DialogAlterarEmailDegustador();

        alterarEmailDegustador.show(getSupportFragmentManager(),
                "DialogAlterarEmailDegustador");
    }

    // Método para criar uma instância da Classe DialogAlterarSenhaDegustador
    private void dialogAlterarSenhaDegustador(){
        DialogAlterarSenhaDegustador alterarSenhaDegustador =
                new DialogAlterarSenhaDegustador();

        alterarSenhaDegustador.show(getSupportFragmentManager(),
                "DialogAlterarSenhaDegustador");
    }

    // Método para criar uma instância da Classe DialogAlterarTelefoneDegustador
    private void dialogAlterarTelefoneDegustador(){
        DialogAlterarTelefoneDegustador alterarTelefoneDegustador =
                new DialogAlterarTelefoneDegustador();

        alterarTelefoneDegustador.show(getSupportFragmentManager(),
                "DialogAlterarTelefoneDegustador");
    }

    // Método para chamar o método da Dialog box quando o usuário pressiona o botão voltar.
    @Override
    public void onBackPressed() { dialogSairConta(DegustadorTelaPrincipal.this); }

    // Método para configurar o menu na Toolbar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_degustador_home_screen, menu);
        return true;
    }

    // Método para configurar a nevegação dos itens da Toolbar.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sair:
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
     * Configurando o Firebase Auth e checando se existe usuário logado e se está cadastrado na base
     * de dados, se não, ele é adcionado, se sim, as suas informações são recuperadas.
     */
    private void firebaseAuthConfig(){
        Log.d(TAG, "firebaseAuthConfig: Configurando os componentes do Firebase");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("degustador");

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
                                    Log.d(TAG, "onDataChange: Recuperando os dados do degustador: "
                                            + userID);
                                    txtNomeDegustadorTelaPrincipal.setText(
                                            ds.child("nome")
                                                    .getValue()
                                                    .toString());

                                    txtEmailDegustadorTelaPrincipal.setText(
                                            ds.child("email")
                                                    .getValue()
                                                    .toString());

                                    if (ds.child("urlFotoPerfil").getValue().equals("")){
                                        imgDegustadorProfilePic
                                                .setImageResource(R.drawable.ic_account_circle_tela_principal_degustador);
                                        imgDegustadorProfilePic.setVisibility(View.VISIBLE);

                                    } else {
                                        String urlFotoPerfil =
                                                ds.child("urlFotoPerfil").getValue().toString();

                                        Glide.with(mContext)
                                                .load(urlFotoPerfil)
                                                .apply(RequestOptions.circleCropTransform())
                                                .into(imgDegustadorProfilePic);

                                        imgDegustadorProfilePic.setVisibility(View.VISIBLE);
                                    }
                                    txtNomeDegustadorTelaPrincipal.setVisibility(View.VISIBLE);
                                    txtEmailDegustadorTelaPrincipal.setVisibility(View.VISIBLE);
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
     * Método para realizar o logout do usuário usando Firebase Auth
     */
    private void logout() {
        mAuth.signOut();
        voltarTelaLogin();

        Toast.makeText(DegustadorTelaPrincipal.this, R.string.logout_sucesso,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Firebase - Método para deletar a conta e os vinhos associados com o usuário logado atualmente.
     */
    private void deletarConta(){
        final FirebaseUser user = mAuth.getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Erro ao deletar o usuário: "
                                    + task.getException());
                        } else {
                            myRef = mDatabase.getReference("degustador");

                            myRef.child(user.getUid()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Log.d(TAG, "onComplete: Erro ao deletar " +
                                                        "dados pessoias do degustador: " + task.getException());
                                            } else {
                                                Log.d(TAG, "onComplete: Sucesso ao deletar os dados " +
                                                        "pessoais do degustador" + task.getResult());
                                            }
                                        }
                                    });

                            Query queryVinho = mDatabase.getReference("vinho")
                                    .orderByChild("id_degustador")
                                    .equalTo(user.getUid());

                            queryVinho.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        ds.getRef().removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (!task.isSuccessful()) {
                                                            Log.d(TAG, "onComplete: Erro ao deletar os " +
                                                                    "vinhos cadastrados com a id do usuário");

                                                            Toast.makeText(mContext,
                                                                    R.string.usuário_deletado_erro,
                                                                    Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Log.d(TAG, "onComplete: Vinhos cadastrados " +
                                                                    "com a ID do usuário deletados com sucesso");

                                                            Log.d(TAG, "onComplete: Chamando o método " +
                                                                    "para deletar as notas de degustação");
                                                            //deletarNotaDegustacao();

                                                            Toast.makeText(mContext,
                                                                    R.string.usuario_deletado_sucesso,
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d(TAG, "onCancelled: Erro: " + databaseError.getDetails());
                                }
                            });
                        }

                        Query queryNota = mDatabase.getReference("notaDegustacao")
                                .orderByChild("idDegustador")
                                .equalTo(user.getUid());

                        queryNota.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    ds.getRef().removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: Erro ao deletar as notas de degustação " +
                                                                "associadas ao ID do usuário");
                                                    } else {
                                                        Log.d(TAG, "onComplete: Sucesso ao deletar as notas de " +
                                                                "degustação associadas ao ID do usuário");
                                                    }
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: Erro: " + databaseError.getDetails());
                            }
                        });
                    }
                });
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