package br.com.sdpv.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import br.com.sdpv.R;

public class SplashScreenFirstTime extends AppCompatActivity {

    // Constants
    public static final String TAG = "SplashScreenFirstTime";

    // Views
    private Button btnSairApp;
    private Button btnCadAdminSplashScreenFirstTime;

    // Context
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_first_time);
        Log.d(TAG, "onCreate: InitViews");
        initViews();

        // Init Context
        mContext = SplashScreenFirstTime.this;

        btnSairApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0); finish();
            }
        });

        btnCadAdminSplashScreenFirstTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                direcionarTelaCadastroAdministrador();
            }
        });
    }

    private void initViews(){
        btnSairApp = findViewById(R.id.btnSairApp);
        btnCadAdminSplashScreenFirstTime = findViewById(R.id.btnCadastroAdminSplashScreen);
    }

    // Método para abrir a activity "CadastroAdministrador"
    private void direcionarTelaCadastroAdministrador(){
        Intent intent = new Intent(mContext, CadastroAdministrador.class);
        startActivity(intent);
    }
}
