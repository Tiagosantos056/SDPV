package br.com.sdpv.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.sdpv.R;

public class DialogConfirmarCredencias extends AppCompatDialogFragment {

    // Constants
    public static final String TAG = "DialogConfCredenciais";

    // View
    private EditText edtConfirmarEmail;
    private EditText edtConfirmarSenha;

    // Firebase
    private FirebaseUser mUser;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_confirmar_senha, null);

        // Init Views
        edtConfirmarEmail = view.findViewById(R.id.edtConfirmarEmail);
        edtConfirmarSenha = view.findViewById(R.id.edtConfirmarSenha);

        // Init Firebase
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        builder.setView(view)
                .setTitle(R.string.confirmar_senha)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.confirmar_acao, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String email = edtConfirmarEmail.getText().toString();
                        String senha = edtConfirmarSenha.getText().toString();

                        AuthCredential credential = EmailAuthProvider.getCredential(email, senha);

                        mUser.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d(TAG, "onComplete: Usuário Re-autenticado " +
                                            "- ID do usuário: " + mUser.getUid());

                                    Toast.makeText(getActivity(),
                                            R.string.reautenticacao_sucesso,
                                            Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Log.d(TAG, "onComplete: Erro: " + task.getException());
                                    Toast.makeText(getActivity(),
                                            "Erro: " + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

        return builder.create();
    }
}
