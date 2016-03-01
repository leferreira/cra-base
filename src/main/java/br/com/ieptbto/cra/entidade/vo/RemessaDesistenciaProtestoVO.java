package br.com.ieptbto.cra.entidade.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Lefer
 *
 */

@SuppressWarnings("serial")
@XmlRootElement(name = "sustacao")
@XmlAccessorType(XmlAccessType.FIELD)
public class RemessaDesistenciaProtestoVO extends AbstractArquivoVO {

	@XmlElement(name = "hdb")
	private CabecalhoArquivoDesistenciaProtestoVO cabecalhoArquivo;
	@XmlElement(name = "registros")
	private List<DesistenciaProtestoVO> pedidoDesistencias;
	@XmlElement(name = "tlb")
	private RodapeArquivoDesistenciaProtestoVO rodapeArquivo;
	private String identificacaoRegistro;

	public CabecalhoArquivoDesistenciaProtestoVO getCabecalhoArquivo() {
		return cabecalhoArquivo;
	}

	public List<DesistenciaProtestoVO> getPedidoDesistencias() {
		return pedidoDesistencias;
	}

	public RodapeArquivoDesistenciaProtestoVO getRodapeArquivo() {
		return rodapeArquivo;
	}

	public void setCabecalhoArquivo(CabecalhoArquivoDesistenciaProtestoVO cabecalhoArquivo) {
		this.cabecalhoArquivo = cabecalhoArquivo;
	}

	public void setPedidoDesistencias(List<DesistenciaProtestoVO> pedidoDesistencias) {
		this.pedidoDesistencias = pedidoDesistencias;
	}

	public void setRodapeArquivo(RodapeArquivoDesistenciaProtestoVO rodapeArquivo) {
		this.rodapeArquivo = rodapeArquivo;
	}

	@Override
	public String getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	public void setIdentificacaoRegistro(String identificacaoRegistro) {
		this.identificacaoRegistro = identificacaoRegistro;
	}

}
