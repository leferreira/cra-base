package br.com.ieptbto.cra.entidade.vo;

import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
public class DesistenciaProtestoVO extends AbstractArquivoVO {

	private CabecalhoCartorioDesistenciaProtestoVO cabecalhoCartorio;
	private List<RegistroDesistenciaProtestoVO> registroDesistenciaProtesto;
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
