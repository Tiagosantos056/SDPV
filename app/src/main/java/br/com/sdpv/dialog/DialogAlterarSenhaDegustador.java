package br.com.sdpv.dialog;

import android.app.AlertDialog;
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
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.sdpv.R;

public class DialogAlterarSenhaDegustador extends AppCompatDialogFragment {

    // Constants
    public static final String TAG = "DialogAlterarSenhaDegus";

    // Views
    private EditText edtSenhaDialogAlterarEmailDegustador;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_alterar_senha_degustador, null);

        // Init Views
        Log.d(TAG, "onCreateDialog: InitDialogViews");
        edtSenhaDialogAlterarEmailDegustador = view
                .findViewById(R.id.edtSenhaAlterarEmailDialogDegustador);

        // Init Firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("degustador");

        builder.setView(view)
                .setTitle(R.string.alterar_senha)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.confirmar_acao, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String senha = edtSenhaDialogAlterarEmailDegustador
                                .getText()
                                .toString()
                                .trim();

                        Log.d(TAG, "onClick: Atualizando a senha do usuário no Firebase auth");
                        user.updatePassword(senha)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()){
                                            Log.d(TAG, "onComplete: Erro ao atualizar senha do usuário!");

//                                            Toast.makeText(getActivity(),
//                                                    R.string.senha_atualizada_erro,
//                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.d(TAG, "onComplete: Atualizando a senha no " +
                                                    "Realtime database");
                                            myRef.child(user.getUid()).child("senha").setValue(senha);

//                                            Toast.makeText(getActivity(), R.string.senha_atualizada_sucesso,
//                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                });

        return builder.create();
    }
}