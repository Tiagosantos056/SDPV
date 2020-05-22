package br.com.sdpv.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.sdpv.R;

public class DialogListVinhosDegustador extends AppCompatDialogFragment {

    // Constants
    public static final String TAG = "DialogListVinhosDegus";

    // Views
    private TextView txtDialogListVinhosDeletar;
    private TextView txtDialogListVinhosVisualizar;

    // Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_onclick_lista_vinhos_degustador,
                null);

        builder.setView(view)
                .setTitle(R.string.opcoes_dialog_title);

        // Init Views
        txtDialogListVinhosDeletar = view.findViewById(R.id.txtDialogListVinhosDeletar);
        txtDialogListVinhosVisualizar = view.findViewById(R.id.txtDialogListVinhosVisualizar);

        // Init Firebase
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        return builder.create();
    }
}
