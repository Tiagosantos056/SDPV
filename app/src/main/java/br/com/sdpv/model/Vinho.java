package br.com.sdpv.model;

public class Vinho {

    private String id;
    private String id_nota;
    private String id_degustador;
    private String cor;
    private String variacaoTonalidade;
    private String viscosidade;
    private String aspecto;
    private String condicaoAromatica;
    private String itensidadeAromatica;
    private String caracteristicasAromaticas;
    private String docura;
    private String corpo;
    private String acidez;
    private String tanino;
    private String caracteristicasAromaticaRetronasal;
    private String finalPaladar;
    private String pontuacao;
    private String comentariosVinho;
    private String qualidadeGeral;
    private String nomeVinicola;
    private String nomeVinho;
    private String tipoVinho;
    private String pais_regiao;
    private String uvaVinho;
    private String volAlcoolicoVinho;

    public Vinho() {
    }

    public Vinho(String id, String id_nota, String id_degustador, String cor, String variacaoTonalidade,
                 String viscosidade, String aspecto, String condicaoAromatica, String itensidadeAromatica,
                 String caracteristicasAromaticas, String docura, String corpo, String acidez,
                 String tanino, String caracteristicasAromaticaRetronasal, String finalPaladar, String pontuacao,
                 String comentariosVinho, String qualidadeGeral, String nomeVinicola, String nomeVinho,
                 String tipoVinho, String pais_regiao, String uvaVinho, String volAlcoolicoVinho) {
        this.id = id;
        this.id_nota = id_nota;
        this.id_degustador = id_degustador;
        this.cor = cor;
        this.variacaoTonalidade = variacaoTonalidade;
        this.viscosidade = viscosidade;
        this.aspecto = aspecto;
        this.condicaoAromatica = condicaoAromatica;
        this.itensidadeAromatica = itensidadeAromatica;
        this.caracteristicasAromaticas = caracteristicasAromaticas;
        this.docura = docura;
        this.corpo = corpo;
        this.acidez = acidez;
        this.tanino = tanino;
        this.caracteristicasAromaticaRetronasal = caracteristicasAromaticaRetronasal;
        this.finalPaladar = finalPaladar;
        this.pontuacao = pontuacao;
        this.comentariosVinho = comentariosVinho;
        this.qualidadeGeral = qualidadeGeral;
        this.nomeVinicola = nomeVinicola;
        this.nomeVinho = nomeVinho;
        this.tipoVinho = tipoVinho;
        this.pais_regiao = pais_regiao;
        this.uvaVinho = uvaVinho;
        this.volAlcoolicoVinho = volAlcoolicoVinho;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_nota() {
        return id_nota;
    }

    public void setId_nota(String id_nota) {
        this.id_nota = id_nota;
    }

    public String getId_degustador() {
        return id_degustador;
    }

    public void setId_degustador(String id_degustador) {
        this.id_degustador = id_degustador;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getVariacaoTonalidade() {
        return variacaoTonalidade;
    }

    public void setVariacaoTonalidade(String variacaoTonalidade) {
        this.variacaoTonalidade = variacaoTonalidade;
    }

    public String getViscosidade() {
        return viscosidade;
    }

    public void setViscosidade(String viscosidade) {
        this.viscosidade = viscosidade;
    }

    public String getAspecto() {
        return aspecto;
    }

    public void setAspecto(String aspecto) {
        this.aspecto = aspecto;
    }

    public String getCondicaoAromatica() {
        return condicaoAromatica;
    }

    public void setCondicaoAromatica(String condicaoAromatica) {
        this.condicaoAromatica = condicaoAromatica;
    }

    public String getItensidadeAromatica() {
        return itensidadeAromatica;
    }

    public void setItensidadeAromatica(String itensidadeAromatica) {
        this.itensidadeAromatica = itensidadeAromatica;
    }

    public String getCaracteristicasAromaticas() {
        return caracteristicasAromaticas;
    }

    public void setCaracteristicasAromaticas(String caracteristicasAromaticas) {
        this.caracteristicasAromaticas = caracteristicasAromaticas;
    }

    public String getDocura() {
        return docura;
    }

    public void setDocura(String docura) {
        this.docura = docura;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public String getAcidez() {
        return acidez;
    }

    public void setAcidez(String acidez) {
        this.acidez = acidez;
    }

    public String getTanino() {
        return tanino;
    }

    public void setTanino(String tanino) {
        this.tanino = tanino;
    }

    public String getCaracteristicasAromaticaRetronasal() {
        return caracteristicasAromaticaRetronasal;
    }

    public void setCaracteristicasAromaticaRetronasal(String caracteristicasAromaticaRetronasal) {
        this.caracteristicasAromaticaRetronasal = caracteristicasAromaticaRetronasal;
    }

    public String getFinalPaladar() {
        return finalPaladar;
    }

    public void setFinalPaladar(String finalPaladar) {
        this.finalPaladar = finalPaladar;
    }

    public String getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(String pontuacao) {
        this.pontuacao = pontuacao;
    }

    public String getComentariosVinho() {
        return comentariosVinho;
    }

    public void setComentariosVinho(String comentariosVinho) {
        this.comentariosVinho = comentariosVinho;
    }

    public String getQualidadeGeral() {
        return qualidadeGeral;
    }

    public void setQualidadeGeral(String qualidadeGeral) {
        this.qualidadeGeral = qualidadeGeral;
    }

    public String getNomeVinicola() {
        return nomeVinicola;
    }

    public void setNomeVinicola(String nomeVinicola) {
        this.nomeVinicola = nomeVinicola;
    }

    public String getNomeVinho() {
        return nomeVinho;
    }

    public void setNomeVinho(String nomeVinho) {
        this.nomeVinho = nomeVinho;
    }

    public String getTipoVinho() {
        return tipoVinho;
    }

    public void setTipoVinho(String tipoVinho) {
        this.tipoVinho = tipoVinho;
    }

    public String getPais_regiao() {
        return pais_regiao;
    }

    public void setPais_regiao(String pais_regiao) {
        this.pais_regiao = pais_regiao;
    }

    public String getUvaVinho() {
        return uvaVinho;
    }

    public void setUvaVinho(String uvaVinho) {
        this.uvaVinho = uvaVinho;
    }

    public String getVolAlcoolicoVinho() {
        return volAlcoolicoVinho;
    }

    public void setVolAlcoolicoVinho(String volAlcoolicoVinho) {
        this.volAlcoolicoVinho = volAlcoolicoVinho;
    }
}
