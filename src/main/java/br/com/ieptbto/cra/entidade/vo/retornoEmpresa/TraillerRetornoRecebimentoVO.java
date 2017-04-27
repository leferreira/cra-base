package br.com.ieptbto.cra.entidade.vo.retornoEmpresa;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;
import org.apache.commons.lang.StringUtils;

/**
 * @author Thasso Araujo
 *
 */
@SuppressWarnings("serial")
public class TraillerRetornoRecebimentoVO extends AbstractArquivoVO {

	@IAtributoArquivo(ordem = 1, posicao = 1, formato = "Z", tamanho = 1, obrigatoriedade = false, descricao = "")
	private String codigoRegistro;

	@IAtributoArquivo(ordem = 2, posicao = 2, tamanho = 6, obrigatoriedade = false, descricao = "")
	private String totalRegistrosArquivo;

	@IAtributoArquivo(ordem = 3, posicao = 8, tamanho = 17, obrigatoriedade = false, descricao = "")
	private String valorTotalRecebidoRegistros;

	@IAtributoArquivo(ordem = 4, posicao = 24, tamanho = 126, obrigatoriedade = false, descricao = "")
	private String reservadoParaFuturo;

	public String getCodigoRegistro() {
		if (codigoRegistro == null) {
			this.codigoRegistro = "Z";
		}
		return codigoRegistro;
	}

	@Override
	public String getIdentificacaoRegistro() {
		return this.getCodigoRegistro();
	}

	public String getTotalRegistrosArquivo() {
		return totalRegistrosArquivo;
	}

	public void setTotalRegistrosArquivo(String totalRegistrosArquivo) {
		this.totalRegistrosArquivo = totalRegistrosArquivo;
	}

	public String getValorTotalRecebidoRegistros() {
		return valorTotalRecebidoRegistros;
	}

	public void setValorTotalRecebidoRegistros(String valorTotalRecebidoRegistros) {
		this.valorTotalRecebidoRegistros = valorTotalRecebidoRegistros;
	}

	public String getReservadoParaFuturo() {
		if (reservadoParaFuturo == null) {
			this.reservadoParaFuturo = StringUtils.EMPTY;
		}
		return reservadoParaFuturo;
	}

	public void setReservadoParaFuturo(String reservadoParaFuturo) {
		this.reservadoParaFuturo = reservadoParaFuturo;
	}

	public void setCodigoRegistro(String codigoRegistro) {
		this.codigoRegistro = codigoRegistro;
	}
}