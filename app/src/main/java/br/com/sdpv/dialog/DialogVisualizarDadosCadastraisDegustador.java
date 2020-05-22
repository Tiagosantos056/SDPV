package br.com.sdpv.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.sdpv.R;

public class DialogVisualizarDadosCadastraisDegustador extends AppCompatDialogFragment {

    public static final String TAG = "DialogVisuDadosDegus";

    // Views
    private TextView txtEmailDegustadorCadastrado;
    private TextView txtTelefoneDegustadorCadastrado;
    private TextView txtCPFDegustadorCadastrado;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_visualizar_dados_cadastrais_degustador,
                null);

        // Init Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("degustador");

        // Init Views
        txtEmailDegustadorCadastrado = view.findViewById(R.id.txtEmailCadastradoDegustador);
        txtTelefoneDegustadorCadastrado = view.findViewById(R.id.txtTelefoneCadastradoDegustador);
        txtCPFDegustadorCadastrado = view.findViewById(R.id.txtCPFDegustadorCadastrado);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Recuperando o email do degustador cadastrado");
                txtEmailDegustadorCadastrado.setText(dataSnapshot
                        .child(mAuth.getCurrentUser().getUid())
                        .child("email")
                        .getValue()
                        .toString());

                Log.d(TAG, "onDataChange: Recuperando o telefone do degustador cadastrado");
                txtTelefoneDegustadorCadastrado.setText(dataSnapshot
                        .child(mAuth.getCurrentUser().getUid())
                        .child("telefone")
                        .getValue()
                        .toString());

                Log.d(TAG, "onDataChange: Recuperando o cpf do degustador cadastrado");
                txtCPFDegustadorCadastrado.setText(dataSnapshot
                        .child(mAuth.getCurrentUser().getUid())
                        .child("cpf")
                        .getValue()
                        .toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Erro: " + databaseError.getDetails());
            }
        });

        builder.setView(view)
                .setTitle(R.string.visualizar_dados_cadastrais)
                .setPositiveButton(R.string.ok_acao, null);

        return builder.create();
    }
}
