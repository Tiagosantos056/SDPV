package br.com.sdpv.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import br.com.sdpv.R;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class RelatorioGraficoDegustador extends AppCompatActivity {

    // Constants
    public static final String TAG = "RelatorioGraficoDegus";

    // Constantes relacionadas ao Número de Vinhos Degustados
    public static final String TOTAL = "total";
    public static final String ANO = "ano";
    public static final String MES = "mes";
    public static final String DIA = "dia";

    // Context
    private Context mContext;

    // Views
    private LineChartView chartView;

    // Váriaveis
    private String total;
    private String ano;
    private String mes;
    private String dia;
    private String[] xDadosEixo = {"Jan", "Feb", "Mar", "Apr", "Mai", "Jun", "Jul", "Aug", "Set",
            "Oct", "Nov", "Dec"};
    private int[] yDadosEixo = {0, 2, 2, 2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_grafico_degustador);
        Log.d(TAG, "onCreate: InitViews");
        toolbarConfig();
        initViews();

        // Init Context
        mContext = RelatorioGraficoDegustador.this;

        // Recuperando os dados do intent da activity RelatorioDegustador
        Intent intent = getIntent();
        total = intent.getExtras().getString(TOTAL);
        ano = intent.getExtras().getString(ANO);
        mes = intent.getExtras().getString(MES);
        dia = intent.getExtras().getString(DIA);

        // Chamando o método para configurar o Gráfico de Barra
        barChartConfig();
    }

    private void toolbarConfig(){
        Toolbar toolbar = findViewById(R.id.toolbarRelatorioGraficoDegustador);
        setSupportActionBar(toolbar);
        setTitle(R.string.toolbar_relatorio_grafico);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltarTelaListaDegustadoresRelatorio();
            }
        });
    }

    private void initViews(){
        chartView = findViewById(R.id.hcLineChartViewRelatorioDegustador);
    }

    private void voltarTelaListaDegustadoresRelatorio(){
        Intent intent = new Intent(mContext, ListaDegustadoresRelatorio.class);
        startActivity(intent);
    }

    private void barChartConfig(){
        List dadosEixoY = new ArrayList();
        List dadosEixoX = new ArrayList();

        Line line = new Line(dadosEixoY).setColor(Color.parseColor("#280680"));

        for (int i = 0; i < xDadosEixo.length; i++){
            dadosEixoX.add(i, new AxisValue(i).setLabel(xDadosEixo[i]));
        }

        for (int i = 0; i < yDadosEixo.length; i++){
            dadosEixoY.add(new PointValue(i, yDadosEixo[i]));
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis eixo = new Axis();
        eixo.setValues(dadosEixoX);
        eixo.setTextSize(16);
        eixo.setTextColor(Color.parseColor("#000000"));
        data.setAxisXBottom(eixo);

        Axis eixoY = new Axis();
        eixoY.setName("Número de Vinhos");
        eixoY.setTextColor(Color.parseColor("#000000"));
        eixoY.setTextSize(16);
        data.setAxisYLeft(eixoY);

        chartView.setLineChartData(data);
        Viewport viewport = new Viewport(chartView.getMaximumViewport());
        viewport.top = 50;
        chartView.setMaximumViewport(viewport);
        chartView.setCurrentViewport(viewport);
    }
}
