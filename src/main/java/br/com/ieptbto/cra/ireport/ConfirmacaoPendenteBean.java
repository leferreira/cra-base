package br.com.ieptbto.cra.ireport;

import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.util.DataUtil;

public class ConfirmacaoPendenteBean {

	private String arquivo;
	private String instituicao;
	private String cartorio;
	private String dataArquivo;
	
	public String getArquivo() {
		return arquivo;
	}

	public String getInstituicao() {
		return instituicao;
	}

	public String getCartorio() {
		return cartorio;
	}

	public String getDataArquivo() {
		return dataArquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public void setInstituicao(String instituicao) {
		this.instituicao = instituicao;
	}

	public void setCartorio(String cartorio) {
		this.cartorio = cartorio;
	}

	public void setDataArquivo(String dataArquivo) {
		this.dataArquivo = dataArquivo;
	}
	
	public void parseToRemessa(Remessa remessa){
		this.arquivo = remessa.getArquivo().getNomeArquivo();
		this.instituicao = remessa.getInstituicaoOrigem().getNomeFantasia();
		this.cartorio = remessa.getInstituicaoDestino().getNomeFantasia();
		this.dataArquivo = DataUtil.localDateToString(remessa.getDataRecebimento());
	}
	
}
