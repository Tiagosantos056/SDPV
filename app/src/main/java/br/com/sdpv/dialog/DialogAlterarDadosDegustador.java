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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.sdpv.R;

public class DialogAlterarDadosDegustador extends AppCompatDialogFragment {

    // Constants
    public static final String TAG = "DialogAlterarDadosDegus";

    // Views
    private EditText edtEmailDegustadorDialog;
    private EditText edtTelefoneDegustadorDialog;
    private EditText edtSenhaDegustadorDialog;

    // Interface
    private alterarDadosDegustador alterarDadosDegustador;

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

        // Init views
        edtEmailDegustadorDialog = view.findViewById(R.id.edtNovoEmailDegustador);
        edtTelefoneDegustadorDialog = view.findViewById(R.id.edtNovoTelefoneDegustador);
        edtSenhaDegustadorDialog = view.findViewById(R.id.edtNovaSenhaDegustador);

        // Inicializando os componentes do Firebase
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("degustador");
        mAuth = FirebaseAuth.getInstance();

        builder.setView(view)
                .setTitle(R.string.alterar_dados)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.alterar_acao, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String email = edtEmailDegustadorDialog.getText().toString();
                        final String telefone = edtTelefoneDegustadorDialog.getText().toString();
                        final String senha = edtSenhaDegustadorDialog.getText().toString();

                        final String userID = mAuth.getCurrentUser().getUid();

                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()){
                                    if (ds.child(userID).exists()) {
                                        Log.d(TAG, "onDataChange: Alterando os dados do " +
                                                "Firebase Auth");

                                        if (email != null) {
                                            mAuth.getCurrentUser().updateEmail(email)
                                                .addOnCompleteListener(
                                                        new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Log.d(TAG, "onComplete: " +
                                                                        "Email Alterado com sucesso " +
                                                                        mAuth.getCurrentUser().getEmail());
                                                            }
                                                        });

                                            myRef.child(userID).child("email").setValue(email);
                                            Log.d(TAG, "onDataChange: " +
                                                    "Email do degustador alterado na base de dados.");}

                                        else {}

                                        if (senha != null) {
                                            mAuth.getCurrentUser().updatePassword(senha)
                                                    .addOnCompleteListener(
                                                            new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Log.d(TAG, "onComplete: " +
                                                                            "Senha alterado com sucesso " + senha);
                                                                }
                                                            });

                                            myRef.child(userID).child("senha").setValue(senha);
                                            Log.d(TAG, "onDataChange: " +
                                                    "Senha do degustador alterada na base de dados.");
                                        }

                                        else {}

                                        if (telefone != null) {
                                            myRef.child(userID).child("telefone").setValue(telefone);
                                            Log.d(TAG, "onDataChange: " +
                                                    "Telefone do degustador atualizado com sucesso.");
                                        }

                                        else {}
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: Erro: " + databaseError.getMessage());
                                Toast.makeText(getActivity(),
                                        "Erro: " + databaseError.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                        Toast.makeText(getActivity(), R.string.dados_alterados_sucesso_toast,
                                Toast.LENGTH_SHORT).show();

                        alterarDadosDegustador.alterarDadosDegustador(
                                userID,
                                email,
                                telefone,
                                senha);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            alterarDadosDegustador = (alterarDadosDegustador) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "Necessário implementar interface alterarDadosDegustador");
        }
    }

    public interface alterarDadosDegustador {
        void alterarDadosDegustador(String userID, String email, String telefone, String senha);
    }
}