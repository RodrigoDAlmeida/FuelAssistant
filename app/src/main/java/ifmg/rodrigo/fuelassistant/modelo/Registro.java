package ifmg.rodrigo.fuelassistant.modelo;

import java.util.Date;

/**
 * Created by Rodrigo on 29/08/2016.
 */
public class Registro {

    private long codigo;
    private Date data;

    private Double distancia;
    private Double consumo;
    private Double mediaAberturaBorboleta;
    private Double mediaConsumo;

    private Usuario usuario;
    private Carro carro;
    private TipoCombustivel tipoCombustivel;


    public Registro(){

    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }

    public Double getConsumo() {
        return consumo;
    }

    public void setConsumo(Double consumo) {
        this.consumo = consumo;
    }

    public Double getMediaAberturaBorboleta() {
        return mediaAberturaBorboleta;
    }

    public void setMediaAberturaBorboleta(Double mediaAberturaBorboleta) {
        this.mediaAberturaBorboleta = mediaAberturaBorboleta;
    }

    public Double getMediaConsumo() {
        return mediaConsumo;
    }

    public void setMediaConsumo(Double mediaConsumo) {
        this.mediaConsumo = mediaConsumo;
    }

    public Carro getCarro() {
        return carro;
    }

    public void setCarro(Carro carro) {
        this.carro = carro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public TipoCombustivel getTipoCombustivel() {
        return tipoCombustivel;
    }

    public void setTipoCombustivel(TipoCombustivel tipoCombustivel) {
        this.tipoCombustivel = tipoCombustivel;
    }

    @Override
    public String toString() {
        return  codigo +
                " " + carro + " "+usuario+ " "+tipoCombustivel;

    }
}
