package br.com.ieptbto.cra.entidade.vo;

import java.util.List;

/**
 * 
 * @author Lefer
 *
 */

@SuppressWarnings("serial")
public class RemessaDesistenciaProtestoVO extends AbstractArquivoVO {

	private CabecalhoArquivoDesistenciaProtestoVO cabecalhoArquivo;
	private List<DesistenciaProtestoVO> pedidoDesistencias;
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
