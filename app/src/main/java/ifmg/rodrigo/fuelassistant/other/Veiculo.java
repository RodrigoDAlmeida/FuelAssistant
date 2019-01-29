package ifmg.rodrigo.fuelassistant.other;

/**
 * Created by Rodrigo on 14/11/2016.
 */
public class Veiculo {

    public enum Estado {PARADO, MOVIMENTO, ABASTECENDO}

    public final int VELOCIDADE_MINIMA = 4;

    ;
    private Estado estadoAtual;

    private Integer velocidadeAtual;


    private double massaAr;
    private double nvCombustivelAnterior;
    public double nvCombustAntAnterior;
    private double nvCombustivelAtual;
    private double aberturaBorboleta;


    public Veiculo() {
        velocidadeAtual = 0;
        nvCombustivelAnterior = Double.MAX_VALUE;
        nvCombustivelAtual = 0;
        aberturaBorboleta = 0;
        massaAr = 0;
        estadoAtual = Estado.PARADO;
    }

    public Estado getEstado() {

        return this.estadoAtual;
    }

    public void setEstado(Estado estado) {

        this.estadoAtual = estado;
    }

    public void setEstado(String estado) {

        if (estado.equals("MOVIMENTO")) {
            this.estadoAtual = Estado.MOVIMENTO;
        } else {

            if (estado.equals("ABASTECENDO")) {
                this.estadoAtual = Estado.ABASTECENDO;
            } else {
                this.estadoAtual = Estado.PARADO;
            }
        }
    }


    public double getMassaAr() {
        return massaAr;
    }

    public void setMassaAr(double massaAr) {
        this.massaAr = massaAr;
    }

    public Integer getVelocidadeAtual() {
        return velocidadeAtual;
    }

    public void setVelocidadeAtual(Integer velocidadeAtual) {
        this.velocidadeAtual = velocidadeAtual;
    }

    public double getNvCombustivel() {
        return nvCombustivelAtual;
    }

    public void setNvCombustivel(double nvCombustivel) {

        if(nvCombustivelAnterior == Double.MAX_VALUE){  //Primeira medição
            nvCombustivelAnterior = nvCombustivel;
            nvCombustAntAnterior = nvCombustivelAnterior;

        }else{
            this.nvCombustAntAnterior = this.nvCombustivelAnterior;
            this.nvCombustivelAnterior = nvCombustivelAtual;
        }

        this.nvCombustivelAtual = nvCombustivel;

    }

    public double getAberturaBorboleta() {
        return aberturaBorboleta;
    }

    public void setAberturaBorboleta(double aberturaBorboleta) {
        this.aberturaBorboleta = aberturaBorboleta;
    }


    public Boolean isMovimento() {

        if (velocidadeAtual > VELOCIDADE_MINIMA) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isAbastecendo() {

        if (nvCombustivelAtual > nvCombustivelAnterior) {
            return true;
        } else {
            return false;
        }

    }

    public void atualizaValores(Integer vel, Double abertBorboleta, Double nvCombus, Double massaAr) {

        setVelocidadeAtual(vel);
        setAberturaBorboleta(abertBorboleta);
        setNvCombustivel(nvCombus);
        setMassaAr(massaAr);

    }
}
