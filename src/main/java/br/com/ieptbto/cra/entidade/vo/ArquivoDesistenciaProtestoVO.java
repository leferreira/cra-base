package br.com.ieptbto.cra.entidade.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;

/**
 * 
 * @author Lefer
 *
 */

@SuppressWarnings("serial")
@XmlRootElement(name = "sustacao")
@XmlAccessorType(XmlAccessType.NONE)
public class ArquivoDesistenciaProtestoVO extends AbstractArquivoVO {

	@XmlElement(name = "hdb")
	private CabecalhoArquivoDesistenciaProtestoVO cabecalhoArquivo;
	@XmlElement(name = "hdc")
	private List<CabecalhoCartorioDesistenciaProtestoVO> cabecalhoCartorio;
	@XmlElement(name = "tr")
	private List<RegistroDesistenciaProtestoVO> registroDesistenciaProtesto;
	@XmlElement(name = "tlc")
	private List<RodapeCartorioDesistenciaProtestoVO> rodapeCartorio;
	@XmlElement(name = "tlb")
	private RodapeArquivoDesistenciaProtestoVO rodapeArquivo;

	@IAtributoArquivo(ordem = 1, posicao = 1, tamanho = 1, descricao = "Identificar o registro header no arquivo. Constante 0.", obrigatoriedade = true, validacao = "0", tipo = Integer.class)
	private String identificacaoRegistro;

	private TipoArquivo tipoArquivo;

	public CabecalhoArquivoDesistenciaProtestoVO getCabecalhoArquivo() {
		return cabecalhoArquivo;
	}

	public List<CabecalhoCartorioDesistenciaProtestoVO> getCabecalhoCartorio() {
		return cabecalhoCartorio;
	}

	public List<RegistroDesistenciaProtestoVO> getRegistroDesistenciaProtesto() {
		return registroDesistenciaProtesto;
	}

	public List<RodapeCartorioDesistenciaProtestoVO> getRodapeCartorio() {
		return rodapeCartorio;
	}

	public RodapeArquivoDesistenciaProtestoVO getRodapeArquivo() {
		return rodapeArquivo;
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	public void setCabecalhoArquivo(CabecalhoArquivoDesistenciaProtestoVO cabecalhoArquivo) {
		this.cabecalhoArquivo = cabecalhoArquivo;
	}

	public void setCabecalhoCartorio(List<CabecalhoCartorioDesistenciaProtestoVO> cabecalhoCartorio) {
		this.cabecalhoCartorio = cabecalhoCartorio;
	}

	public void setRegistroDesistenciaProtesto(List<RegistroDesistenciaProtestoVO> registroDesistenciaProtesto) {
		this.registroDesistenciaProtesto = registroDesistenciaProtesto;
	}

	public void setRodapeCartorio(List<RodapeCartorioDesistenciaProtestoVO> rodapeCartorio) {
		this.rodapeCartorio = rodapeCartorio;
	}

	public void setRodapeArquivo(RodapeArquivoDesistenciaProtestoVO rodapeArquivo) {
		this.rodapeArquivo = rodapeArquivo;
	}

	public void setIdentificacaoRegistro(String identificacaoRegistro) {
		this.identificacaoRegistro = identificacaoRegistro;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	@Override
	public String getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}
}
