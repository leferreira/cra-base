package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "titulos")
public class DesistenciaProtestoVO extends AbstractArquivoVO {

	@XmlElement(name = "hdc")
	private CabecalhoCartorioDesistenciaProtestoVO cabecalhoCartorio;
	@XmlElement(name = "tr")
	private List<RegistroDesistenciaProtestoVO> registroDesistenciaProtesto;
	@XmlElement(name = "tlc")
	private RodapeCartorioDesistenciaProtestoVO rodapeCartorio;
	
	private String identificacaoRegistro;

	public CabecalhoCartorioDesistenciaProtestoVO getCabecalhoCartorio() {
		return cabecalhoCartorio;
	}

	public List<RegistroDesistenciaProtestoVO> getRegistroDesistenciaProtesto() {
		return registroDesistenciaProtesto;
	}

	public RodapeCartorioDesistenciaProtestoVO getRodapeCartorio() {
		return rodapeCartorio;
	}

	public void setCabecalhoCartorio(CabecalhoCartorioDesistenciaProtestoVO cabecalhoCartorio) {
		this.cabecalhoCartorio = cabecalhoCartorio;
	}

	public void setRegistroDesistenciaProtesto(List<RegistroDesistenciaProtestoVO> registroDesistenciaProtesto) {
		this.registroDesistenciaProtesto = registroDesistenciaProtesto;
	}

	public void setRodapeCartorio(RodapeCartorioDesistenciaProtestoVO rodapeCartorio) {
		this.rodapeCartorio = rodapeCartorio;
	}

	@Override
	public String getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	public void setIdentificacaoRegistro(String identificacaoRegistro) {
		this.identificacaoRegistro = identificacaoRegistro;
	}

}
