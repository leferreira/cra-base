package br.com.ieptbto.cra.webservice.vo;

import javax.xml.bind.annotation.*;

/**
 * @author Thasso Araújo
 *
 */
@XmlRootElement(name = "comarca")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComarcaDetalhamentoSerproVO {

	@XmlElement(name = "datahora")
	private String dataHora;
	
	@XmlElement(name = "codigo")
	private String codigo;
	
	@XmlElement(name = "registro")
	private String registro;

	@XmlElement(name = "ocorrencia")
	private String ocorrencia;
	
	@XmlElement(name = "total_registros")
	private int totalRegistros;
	
	@XmlAttribute(name = "CodMun")
	private String codigoMunicipio;

	public String getDataHora() {
		return dataHora;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getOcorrencia() {
		return ocorrencia;
	}

	public int getTotalRegistros() {
		return totalRegistros;
	}

	public void setDataHora(String dataHora) {
		this.dataHora = dataHora;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setOcorrencia(String ocorrencia) {
		this.ocorrencia = ocorrencia;
	}

	public void setTotalRegistros(int totalRegistros) {
		this.totalRegistros = totalRegistros;
	}

	public String getCodigoMunicipio() {
		return codigoMunicipio;
	}

	public void setCodigoMunicipio(String codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}

	public String getRegistro() {
		return registro;
	}

	public void setRegistro(String registro) {
		this.registro = registro;
	}
}
