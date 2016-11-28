package br.com.ieptbto.cra.webservice.VO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "arquivo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArquivoRemessa {

	@XmlElement(name = "nome_arquivo")
	private String nomeArquivo;

	@XmlElement(name = "codigo_apresentante")
	private String codigoApresentante;

	@XmlElement(name = "versao_atualizacao")
	private Integer versaoAtualizacao;

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public String getCodigoApresentante() {
		return codigoApresentante;
	}

	public Integer getVersaoAtualizacao() {
		return versaoAtualizacao;
	}

	public void setVersaoAtualizacao(Integer versaoAtualizacao) {
		this.versaoAtualizacao = versaoAtualizacao;
	}

	public void setCodigoApresentante(String codigoApresentante) {
		this.codigoApresentante = codigoApresentante;
	}

	public void setNomeArquivo(String arquivo) {
		this.nomeArquivo = arquivo;
	}
}