package br.com.ieptbto.cra.entidade.vo.retornoRecebimentoEmpresa;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;

/**
 * @author Thasso Araujo
 *
 */
@SuppressWarnings("serial")
public class HeaderRetornoRecebimentoVO extends AbstractArquivoVO {
	
	@IAtributoArquivo(ordem = 1, posicao = 1, formato = "A", tamanho = 1, obrigatoriedade = false, descricao = "")
	private String codigoRegistro;
	
	@IAtributoArquivo(ordem = 2, posicao = 2, formato = "0", tamanho = 1, obrigatoriedade = false, descricao = "")
	private String codigoRemessa;
	
	@IAtributoArquivo(ordem = 3, posicao =3, tamanho = 20, obrigatoriedade = false, descricao = "", formato = "0")
	private String codigoConvenio;
	
	@IAtributoArquivo(ordem = 4, posicao = 23, tamanho = 20, obrigatoriedade = false, descricao = "", formato = " ")
	private String nomeEmpresaOrgao;
	
	@IAtributoArquivo(ordem = 5, posicao = 43, tamanho = 3, obrigatoriedade = false, descricao = "", formato = "0")
	private String codigoBanco;
	
	@IAtributoArquivo(ordem = 6, posicao = 46, tamanho = 20, obrigatoriedade = false, descricao = "", formato = " ")
	private String nomeBanco;
	
	@IAtributoArquivo(ordem = 7, posicao = 66, tamanho = 8, obrigatoriedade = false, descricao = "", formato = "0")
	private String dataGeracaoArquivo;
	
	@IAtributoArquivo(ordem = 8, posicao = 74, tamanho = 6, obrigatoriedade = false, descricao = "", formato = "0")
	private String numeroSequencialArquivo;
	
	@IAtributoArquivo(ordem = 9, posicao = 80, tamanho = 2, obrigatoriedade = false, descricao = "", formato = "0")
	private String versaoLayout;
	
	@IAtributoArquivo(ordem = 10, posicao = 82, tamanho = 17, obrigatoriedade = false, descricao = "", formato = "0")
	private String codigoDeBarras;
	
	@IAtributoArquivo(ordem = 11, posicao = 99, tamanho = 52, obrigatoriedade = false, descricao = "", formato = " ")
	private String reservado;

	public String getCodigoRegistro() {
		if (codigoRegistro == null) {
			this.codigoRegistro = "A";
		}
		return codigoRegistro;
	}

	public void setCodigoRegistro(String codigoRegistro) {
		this.codigoRegistro = codigoRegistro;
	}

	public String getCodigoRemessa() {
		return codigoRemessa;
	}

	public void setCodigoRemessa(String codigoRemessa) {
		this.codigoRemessa = codigoRemessa;
	}

	public String getCodigoConvenio() {
		return codigoConvenio;
	}

	public void setCodigoConvenio(String codigoConvenio) {
		this.codigoConvenio = codigoConvenio;
	}

	public String getNomeEmpresaOrgao() {
		return nomeEmpresaOrgao;
	}

	public void setNomeEmpresaOrgao(String nomeEmpresaOrgao) {
		this.nomeEmpresaOrgao = nomeEmpresaOrgao;
	}

	public String getCodigoBanco() {
		return codigoBanco;
	}

	public void setCodigoBanco(String codigoBanco) {
		this.codigoBanco = codigoBanco;
	}

	public String getNomeBanco() {
		return nomeBanco;
	}

	public void setNomeBanco(String nomeBanco) {
		this.nomeBanco = nomeBanco;
	}

	public String getDataGeracaoArquivo() {
		return dataGeracaoArquivo;
	}

	public void setDataGeracaoArquivo(String dataGeracaoArquivo) {
		this.dataGeracaoArquivo = dataGeracaoArquivo;
	}

	public String getNumeroSequencialArquivo() {
		return numeroSequencialArquivo;
	}

	public void setNumeroSequencialArquivo(String numeroSequencialArquivo) {
		this.numeroSequencialArquivo = numeroSequencialArquivo;
	}

	public String getVersaoLayout() {
		return versaoLayout;
	}

	public void setVersaoLayout(String versaoLayout) {
		this.versaoLayout = versaoLayout;
	}

	public String getCodigoDeBarras() {
		return codigoDeBarras;
	}

	public void setCodigoDeBarras(String codigoDeBarras) {
		this.codigoDeBarras = codigoDeBarras;
	}

	public String getReservado() {
		if (reservado == null) {
			this.reservado = StringUtils.EMPTY;
		}
		return reservado;
	}

	public void setReservado(String reservado) {
		this.reservado = reservado;
	}

	@Override
	public String getIdentificacaoRegistro() {
		return getCodigoRegistro();
	}
}