package br.com.sdpv.model;

public class NotaDegustacao {

    private String idNota;
    private String idVinho;
    private String idDegustador;
    private String vinhoDegustado;
    private String dtaDegustacao;
    private String localDegustacao;

    public NotaDegustacao() {
    }

    public NotaDegustacao(String idNota, String idvinho, String iddegustador, String vinhoDegustado,
                          String dtaDegustacao, String localDegustacao) {
        this.idNota = idNota;
        this.idVinho = idvinho;
        this.idDegustador = iddegustador;
        this.vinhoDegustado = vinhoDegustado;
        this.dtaDegustacao = dtaDegustacao;
        this.localDegustacao = localDegustacao;
    }

    public String getIdNota() {
        return idNota;
    }

    public void setIdNota(String idNota) {
        this.idNota = idNota;
    }

    public String getIdVinho() {
        return idVinho;
    }

    public void setIdVinho(String idVinho) {
        this.idVinho = idVinho;
    }

    public String getIdDegustador() {
        return idDegustador;
    }

    public void setIdDegustador(String idDegustador) {
        this.idDegustador = idDegustador;
    }

    public String getVinhoDegustado() {
        return vinhoDegustado;
    }

    public void setVinhoDegustado(String vinhoDegustado) {
        this.vinhoDegustado = vinhoDegustado;
    }

    public String getDtaDegustacao() {
        return dtaDegustacao;
    }

    public void setDtaDegustacao(String dtaDegustacao) {
        this.dtaDegustacao = dtaDegustacao;
    }

    public String getLocalDegustacao() {
        return localDegustacao;
    }

    public void setLocalDegustacao(String localDegustacao) {
        this.localDegustacao = localDegustacao;
    }
}
