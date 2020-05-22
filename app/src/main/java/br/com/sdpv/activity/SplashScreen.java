package br.com.sdpv.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.sdpv.R;

public class SplashScreen extends AppCompatActivity {

    // Constants
    public static final String TAG = "SplashScreen";
    public static final int DELAY_MILLIS = 2500;

    // Views
    private ProgressBar pgrSplashScreen;

    // Context
    private Context mContext;

    // Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Init Context
        mContext = SplashScreen.this;

        // Init Views
        pgrSplashScreen = findViewById(R.id.pgrSplashScreen);
        pgrSplashScreen.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checarAdminCadastro();
            }
        }, DELAY_MILLIS);
    }

    private void checarAdminCadastro(){
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("administrador");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    Log.d(TAG, "onDataChange: " +
                            "Checando se o path Administrador tem filhos");
                    Log.d(TAG, "onDataChange: Direcionando para a tela de login");
                    finish();
                    direcionarTelaLogin();
                } else {
                    Log.d(TAG, "onDataChange: " +
                            "Direcionando o usuário para a SplashScreenFirstTime");
                    direcionarTelaSplashScreenFirstTime();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Erro: " + databaseError.getMessage());
            }
        });
    }

    private void direcionarTelaLogin(){
        Intent intent = new Intent(mContext, Login.class);
        startActivity(intent);
    }

    private void direcionarTelaSplashScreenFirstTime(){
        Intent intent = new Intent(mContext, SplashScreenFirstTime.class);
        startActivity(intent);
    }
}
