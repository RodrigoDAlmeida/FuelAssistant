package ifmg.rodrigo.fuelassistant.modelo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rodrigo on 06/03/2017.
 */

public class Corrida {


    //Variaveis do BD
    private long codigo;
    private Date data;
    public Double distanciaPercorrida;
    public Double mediaAceleracao;
    public Double mediaConsumo;
    private Date inicio;
    private Date fim;
    private long registroAtual;


    //Variaveis auxiliares para o funcionamento

    private Long tempoInicial;
    private Double tempoTotal;
    private Integer somatorioVelocidades;
    private Integer qntAmostrasVelocidade;
    private Double somaMediaAceleracao;
    private Double somaConsumo;
    public Double consumoAtual;



    public Corrida() {

        somatorioVelocidades = 0;
        qntAmostrasVelocidade = 0;
        distanciaPercorrida = 0d;
        somaMediaAceleracao = 0d;
        somaConsumo = 0d;
        mediaConsumo = 0d;





    }

    public Corrida (Date data, Double distanciaPercorrida, Double mediaAceleracao, long registroAtual) {
        this.data = data;
        this.distanciaPercorrida = distanciaPercorrida;
        this.mediaAceleracao = mediaAceleracao;
        this.registroAtual = registroAtual;


        SimpleDateFormat hdf = new SimpleDateFormat("HH:mm:ss");


    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Double getDistanciaPercorrida() {
        return distanciaPercorrida;
    }

    public void setDistanciaPercorrida(Double distanciaPercorrida) {
        this.distanciaPercorrida = distanciaPercorrida;
    }

    public Double getMediaAceleracao() {
        return mediaAceleracao;
    }

    public void setMediaAceleracao(Double mediaAceleracao) {
        this.mediaAceleracao = mediaAceleracao;
    }

    public long getRegistroAtual() {
        return registroAtual;
    }

    public void setRegistroAtual(long registroAtual) {
        this.registroAtual = registroAtual;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }


    public Double getMediaConsumo() {
        return mediaConsumo;
    }

    public void setMediaConsumo(Double mediaConsumo) {
        this.mediaConsumo = mediaConsumo;
    }


    //Funções para funcionamento interno:

    public void iniciar(long registroAtual){
        somatorioVelocidades = 0;
        qntAmostrasVelocidade = 0;
        tempoTotal = 0d;
        distanciaPercorrida = 0d;
        somaMediaAceleracao = 0d;


        this.registroAtual = registroAtual;
        this.inicio = new Date();
        this.data = new Date();
        tempoInicial = System.currentTimeMillis();
    }

    public void capturarVelocidade(Integer velocidadeAtual, Double posicaoBorboleta, Double massaAr){

        somatorioVelocidades += velocidadeAtual;
        somaMediaAceleracao += posicaoBorboleta;

        consumoAtual = (7.107*((velocidadeAtual)/(massaAr))*0.4251);
        somaConsumo += consumoAtual;

        qntAmostrasVelocidade++;
    }

    public Double finalizar(){

        tempoTotal = (((System.currentTimeMillis() - tempoInicial)  * 0.001) / 3600);    //Tempo em horas
        distanciaPercorrida = (somatorioVelocidades / qntAmostrasVelocidade) * tempoTotal;

        mediaAceleracao = somaMediaAceleracao / qntAmostrasVelocidade;
        mediaConsumo = somaConsumo / qntAmostrasVelocidade;
        this.fim = new Date();

        return distanciaPercorrida;
    }

    public Double distanciaParcial(){

        tempoTotal = (((System.currentTimeMillis() - tempoInicial)  * 0.001) / 3600);
        return (somatorioVelocidades / qntAmostrasVelocidade) * tempoTotal;
    }

    public Double mediaConsumoParcial(){

        return somaConsumo/qntAmostrasVelocidade;
    }


}
