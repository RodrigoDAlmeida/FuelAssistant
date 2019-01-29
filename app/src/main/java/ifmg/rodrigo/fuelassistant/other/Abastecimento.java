package ifmg.rodrigo.fuelassistant.other;

import android.util.Log;

/**
 * Created by Rodrigo on 14/11/2016.
 */
public class Abastecimento {

    private  final int N = 15;

    private Double[] valores = new Double[N];
    private int proximoIndice;
    private Double nvInicial;
    public Double nvTotalAbastecido;


    public Double getNvInicial() {
        return nvInicial;
    }

    public void setNvInicial(Double nvInicial) {
        this.nvInicial = nvInicial;
    }

    public Abastecimento(){

        for (int i = 0; i < N; i++){

            valores[i] = 0d;
        }
    }

    public void iniciar(Double nvCombust){

        this.nvInicial = nvCombust;

        for (int i = 0; i < N; i++){

            valores[i] = 0d;
        }

        proximoIndice = 0;
    }

    public void atualizaValor(Double valor){

        valores[proximoIndice] = valor;
        proximoIndice++;

        if(proximoIndice == N){
            proximoIndice = 0;
        }
    }

    public Boolean isEstavel(){


        for (int i = 0; i < N; i++){

            if(valores[i].intValue() != valores[0].intValue()){
                Log.d("[TESTE]", "[ABASTECIMENTO] Valor de ["+i+"] : "+valores[i]+", esperado: "+valores[0]);
                return false;
            }
        }

        return true;
    }

    public Double finalizar(Double nvCombust){

        nvTotalAbastecido = nvCombust - nvInicial;
        return nvTotalAbastecido;
    }
}
