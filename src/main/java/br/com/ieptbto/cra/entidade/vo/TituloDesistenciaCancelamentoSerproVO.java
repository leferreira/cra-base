package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "titulo")
@XmlAccessorType(XmlAccessType.FIELD)
public class TituloDesistenciaCancelamentoSerproVO extends AbstractArquivoVO {

	@XmlElement(name = "numero_protocolo")
	private String numeroProtocoloCartorio;
	
	@XmlElement(name = "data_protocolo")
	private String dataProtocolo;
	
	@XmlElement(name = "numero_titulo")
	private String numeroTitulo;
	
	@XmlElement(name = "nome_devedor")
	private String nomeDevedor;
	
	@XmlElement(name = "valor_titulo")
	private String valorTitulo;

	public String getNumeroProtocoloCartorio() {
		if (numeroProtocoloCartorio != null) {
			if (!numeroProtocoloCartorio.trim().equals(StringUtils.EMPTY)) {
				numeroProtocoloCartorio = String.valueOf(Long.parseLong(numeroProtocoloCartorio));
			}
		}
		return numeroProtocoloCartorio;
	}

	public String getDataProtocolo() {
		if (dataProtocolo != null) {
			if (dataProtocolo.equals("00000000") || dataProtocolo.trim().equals(StringUtils.EMPTY)) {
				dataProtocolo = new LocalDate().toString("ddMMyyyy");
			}
		}
		return dataProtocolo;
	}

	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	public String getNomeDevedor() {
		return nomeDevedor;
	}

	public String getValorTitulo() {
		return valorTitulo;
	}

	public void setNumeroProtocoloCartorio(String numeroProtocoloCartorio) {
		this.numeroProtocoloCartorio = numeroProtocoloCartorio;
	}

	public void setDataProtocolo(String dataProtocolo) {
		this.dataProtocolo = dataProtocolo;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public void setValorTitulo(String valorTitulo) {
		this.valorTitulo = valorTitulo;
	}

	@Override
	public String getIdentificacaoRegistro() {
		// TODO Auto-generated method stub
		return null;
	}
}