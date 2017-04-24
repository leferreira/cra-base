package br.com.ieptbto.cra.webservice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Thasso Araujo on 24/04/2017.
 */
@XmlRootElement(name = "arquivo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArquivoRemessaPendente {

    @XmlElement(name = "nome_arquivo")
    private String nomeArquivo;

    @XmlElement(name = "codigo_apresentante")
    private String codigoApresentante;

    @XmlElement(name = "versao_atualizacao")
    private Integer versaoAtualizacao;

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getCodigoApresentante() {
        return codigoApresentante;
    }

    public void setCodigoApresentante(String codigoApresentante) {
        this.codigoApresentante = codigoApresentante;
    }

    public Integer getVersaoAtualizacao() {
        return versaoAtualizacao;
    }

    public void setVersaoAtualizacao(Integer versaoAtualizacao) {
        this.versaoAtualizacao = versaoAtualizacao;
    }
}