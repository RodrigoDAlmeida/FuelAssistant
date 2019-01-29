package ifmg.rodrigo.fuelassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ifmg.rodrigo.fuelassistant.R;
import ifmg.rodrigo.fuelassistant.modelo.Registro;
import ifmg.rodrigo.fuelassistant.servico.RegistroDAO;


public class RelatorioGrafico extends AppCompatActivity {


    RegistroDAO rDAO;
    boolean falha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Le os parametros da Activity anterior (Relatorios)

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        Long codCarro = Long.valueOf(args.getString("carro"));
        Long codTipoCombust = Long.valueOf(args.getString("tipoCombustivel"));
        String agrupamento = args.getString("agrupamento");
        Integer ano = args.getInt("ano");


        // Monta os vetores que alimentarão o grafico

        rDAO = new RegistroDAO(this);
        List<Registro> registros = new ArrayList<>();
        registros = rDAO.findBySql("SELECT * FROM REGISTRO WHERE codCarro=" + codCarro + " and codTipoCombustivel=" + codTipoCombust);

        if (registros.size() < 1) {
            Toasty.error(this,getResources().getString(R.string.NaoHaRegistrosRestricoes),Toast.LENGTH_SHORT,true).show();
            falha = true;
            finish();
        }
        Log.d("ERRO", "Tamanho do vetor = " + registros.size());


        if (!falha) {
            DataPoint[] pontos = new DataPoint[0];      //Instancia o vetor de pontos apenas para evitar "Warning"


            switch (agrupamento.toUpperCase()) {        //Seleciona os dados a partir da entrada do usuario

                case "TODOS": {


                /* Como são todos os registros, os dados não precisam ser selecionados ou agrupados, entao simplesmente
                 são transformados em pontos para o gráfico */


                    pontos = new DataPoint[registros.size()];

                    for (int i = 0; i < registros.size(); i++) {
                        pontos[i] = new DataPoint(registros.get(i).getData(), registros.get(i).getConsumo());
                    }

                    break;
                }

                case "SEMANAL": {

                    List<Integer> semanas = new ArrayList<>();
                    List<Double> medias = new ArrayList<>();

                    Calendar cal = Calendar.getInstance();

                    cal.setTime(registros.get(0).getData());
                    int semanaAtual = cal.get(Calendar.WEEK_OF_YEAR);   //Contagem da semana começará pelo primeiro registro
                    semanas.add(semanaAtual);

                    Double somaConsumos = 0d;
                    int count = 0;
                    int semana = 0;

                    for (Registro r : registros) {

                        cal.setTime(r.getData());

                        if (cal.get(Calendar.YEAR) == ano) {     //O registro deve pertencer ao ano definido pelo usuario

                            semana = cal.get(Calendar.WEEK_OF_YEAR);

                            if (semana == semanaAtual) {      //Se o registro faz parte desta semana

                                somaConsumos += r.getConsumo();
                                count++;

                            } else {                          //Se não for da mesma semana, fecha a media da semana anterior calculada

                                medias.add(somaConsumos / count);
                                semanas.add(semana);

                                semanaAtual = semana;
                                count = 0;
                                somaConsumos = 0d;


                            }
                        }
                    }
                    medias.add(somaConsumos / count);  //Quando o laço termina, o ultima semana precisa ser finalizada




                /*Produz os pontos para o vetor */
                    pontos = new DataPoint[semanas.size()];

                    for (int i = 0; i < semanas.size(); i++) {
                        pontos[i] = new DataPoint(semanas.get(i), medias.get(i)); //Eixo X = Medias semanais, Eixo Y = Semanas do Ano
                    }


                    break;
                }

                case "MENSAL": {

                    List<Integer> meses = new ArrayList<>();
                    List<Double> medias = new ArrayList<>();

                    Calendar cal = Calendar.getInstance();

                    cal.setTime(registros.get(0).getData());
                    int mesAtual = cal.get(Calendar.MONTH);   //Contagem do mes começará pelo primeiro registro
                    meses.add(mesAtual);

                    Double somaConsumos = 0d;
                    int count = 0;
                    int mes = 0;

                    for (Registro r : registros) {

                        cal.setTime(r.getData());
                        mes = cal.get(Calendar.MONTH);

                        if (mes == mesAtual) {      //Se o registro faz parte desta semana

                            somaConsumos += r.getConsumo();
                            count++;

                        } else {                          //Se não for da mesma semana, fecha a media da semana anterior calculada

                            medias.add(somaConsumos / count);
                            meses.add(mes);

                            mesAtual = mes;
                            count = 0;
                            somaConsumos = 0d;


                        }
                    }
                    medias.add(somaConsumos / count);  //Quando o laço termina, o ultima semana precisa ser finalizada




                /*Produz os pontos para o vetor */
                    pontos = new DataPoint[meses.size()];

                    for (int i = 0; i < meses.size(); i++) {
                        pontos[i] = new DataPoint(meses.get(i), medias.get(i)); //Eixo X = Medias semanais, Eixo Y = Semanas do Ano
                    }


                    break;
                }

                case "ANUAL": {


                    pontos = new DataPoint[registros.size()];

                    for (int i = 0; i < registros.size(); i++) {
                        pontos[i] = new DataPoint(registros.get(i).getData(), registros.get(i).getConsumo());
                    }


                    break;
                }


            }


            Log.d("[TESTE]", "Registros Recuperados: " + registros.size());

            //Monta os pontos que serão plotados


            //CONSTROI O GRÁFICO

            setContentView(R.layout.activity_relatorio_grafico);
            GraphView graph = (GraphView) findViewById(R.id.gvGraficoConsumo);
      /*  LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{

                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
      */

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(pontos);
            graph.addSeries(series);


            //CONVERTE PARA DATA

            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
            graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

// set manual x bounds to have nice steps

            for (int i = 0; i > registros.size(); i++) {

                graph.getViewport().setMinX(registros.get(i).getData().getTime());

            }

            graph.getViewport().setXAxisBoundsManual(true);

            // as we use dates as labels, the human rounding to nice readable numbers
            // is not necessary
            graph.getGridLabelRenderer().setHumanRounding(false);


        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
