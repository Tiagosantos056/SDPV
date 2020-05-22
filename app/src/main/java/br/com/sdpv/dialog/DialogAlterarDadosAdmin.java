package br.com.sdpv.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.sdpv.R;
import br.com.sdpv.model.Administrador;

public class DialogAlterarDadosAdmin extends AppCompatDialogFragment {

    // Constants
    public static final String TAG = "DialogAlterarDadosAdmin";

    // Views
    private EditText edtEmailAdministradorDialog;
    private EditText edtTelefoneAdministradorDialog;
    private EditText edtSenhaAdministradirDialog;

    // Interface
    private alterarDadosAdministrador alterarDadosAdministrador;

    // Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_alterar_dados_degustador, null);

        builder.setView(view)
                .setTitle(R.string.alterar_dados)
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setPositiveButton(R.string.alterar_acao, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String email = edtEmailAdministradorDialog.getText().toString();
                        final String telefone = edtTelefoneAdministradorDialog.getText().toString();
                        final String senha = edtSenhaAdministradirDialog.getText().toString();

                        final String userID = mAuth.getCurrentUser().getUid();

                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Administrador administrador = new Administrador();

                                administrador.setEmail(email);
                                administrador.setTelefone(telefone);
                                administrador.setSenha(senha);

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (dataSnapshot.child(userID).exists()) {
                                        Log.d(TAG, "onDataChange: Alterando os dados do " +
                                                "Firebase Auth");
                                        mAuth.getCurrentUser().updateEmail(email);
                                        mAuth.getCurrentUser().updatePassword(senha);
                                        Log.d(TAG, "onDataChange: Dados alterados do " +
                                                "Firebase Auth: " + email + " e " + senha);

                                        Log.d(TAG, "onDataChange: Alterando os dados da Base");
                                        myRef.child(userID).child("email").setValue(email);
                                        myRef.child(userID).child("telefone").setValue(telefone);
                                        myRef.child(userID).child("senha").setValue(senha);
                                        Log.d(TAG, "onDataChange: Dados do admin " +
                                                "alterados na base: " + email + ", " + telefone +
                                                " e " + senha);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: Erro: " + databaseError.getMessage());
                            }
                        });

                        alterarDadosAdministrador.alterarDadosAdministrador(
                                userID,
                                email,
                                telefone,
                                senha);
                    }

                });

        // Init views
        edtEmailAdministradorDialog = view.findViewById(R.id.edtNovoEmailAdministrador);
        edtTelefoneAdministradorDialog = view.findViewById(R.id.edtNovoTelefoneAdministrdor);
        edtSenhaAdministradirDialog = view.findViewById(R.id.edtNovaSenhaAdministrador);

        // Inicializando os componentes do Firebase
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("administrador");
        mAuth = FirebaseAuth.getInstance();

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            alterarDadosAdministrador = (DialogAlterarDadosAdmin.alterarDadosAdministrador) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "Necessário implementar interface alterarDadosAdministrador");
        }
    }

    public interface alterarDadosAdministrador {
        void alterarDadosAdministrador(String userID, String email, String telefone, String senha);
    }
}
