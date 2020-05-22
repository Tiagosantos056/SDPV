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

public class DialogAlterarTelefoneDegustador extends AppCompatDialogFragment {

    // Constants
    public static final String TAG = "DialogAlterarTelefoneD";

    // Views
    private EditText edtTelefoneDialogAlterarEmailDegustador;

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
        View view = inflater.inflate(R.layout.layout_dialog_alterar_telefone_degustador, null);

        // Init Views
        Log.d(TAG, "onCreateDialog: InitDialogViews");
        edtTelefoneDialogAlterarEmailDegustador = view
                .findViewById(R.id.edtTelefoneAlterarEmailDialogDegustador);

        // Init Firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("degustador");

        builder.setView(view)
                .setTitle(R.string.alterar_telefone)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.confirmar_acao, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String telefone = edtTelefoneDialogAlterarEmailDegustador
                                .getText()
                                .toString()
                                .trim();

                        Log.d(TAG, "onClick: Atualizando o telefone do usuário no " +
                                "Realtime database");

                        myRef.child(user.getUid()).child("telefone").setValue(telefone)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()){
                                            Log.d(TAG, "onComplete: Erro ao atualizar o " +
                                                    "telefone do usuario: " + user.getUid());

//                                            Toast.makeText(getActivity(),
//                                                    R.string.telefone_atualizado_erro,
//                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.d(TAG, "onComplete: Telefone do usuário " +
                                                    "alterado com sucesso! " + user.getUid());

//                                            Toast.makeText(getActivity(),
//                                                    R.string.telefone_atualizado_sucesso,
//                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });

        return builder.create();
    }
}