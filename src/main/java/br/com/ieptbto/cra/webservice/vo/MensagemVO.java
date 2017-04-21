package br.com.ieptbto.cra.webservice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Leandro
 * 
 */
@XmlRootElement(name = "mensagem")
@XmlAccessorType(XmlAccessType.FIELD)
public class MensagemVO extends AbstractMensagemVO {

	@XmlAttribute(name = "descricao")
	private String descricao;

	@XmlAttribute(name = "codigo")
	private String codigo;

	@XmlAttribute(name = "municipio")
	private String municipio;

    @XmlAttribute(name = "nosso_numero")
    private String nossoNumero;

    @XmlAttribute(name = "numero_protocolo_cartorio")
    private Integer numeroProtocoloCartorio;

    @XmlAttribute(name = "numero_titulo")
    private String numeroTitulo;

    @XmlAttribute(name = "nome_devedor")
    private String nomeDevedor;

    @XmlAttribute(name = "numero_sequencial_registro")
    private int numeroSequencialRegistro;

    public String getDescricao() {
		return descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getNossoNumero() {
		return nossoNumero;
	}

	public int getNumeroSequencialRegistro() {
		return numeroSequencialRegistro;
	}

	public Integer getNumeroProtocoloCartorio() {
		return numeroProtocoloCartorio;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public void setNumeroSequencialRegistro(int numeroSequencialRegistro) {
		this.numeroSequencialRegistro = numeroSequencialRegistro;
	}

	public void setNumeroProtocoloCartorio(Integer numeroProtocoloCartorio) {
		this.numeroProtocoloCartorio = numeroProtocoloCartorio;
	}

    public String getNumeroTitulo() {
        return numeroTitulo;
    }

    public void setNumeroTitulo(String numeroTitulo) {
        this.numeroTitulo = numeroTitulo;
    }

    public String getNomeDevedor() {
        return nomeDevedor;
    }

    public void setNomeDevedor(String nomeDevedor) {
        this.nomeDevedor = nomeDevedor;
    }
}
