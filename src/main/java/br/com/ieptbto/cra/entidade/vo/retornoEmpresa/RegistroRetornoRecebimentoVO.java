package br.com.ieptbto.cra.entidade.vo.retornoEmpresa;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;

/**
 * @author Thasso Araujo
 *
 */
@SuppressWarnings("serial")
public class RegistroRetornoRecebimentoVO extends AbstractArquivoVO {

	@IAtributoArquivo(ordem = 1, posicao = 1, tamanho = 1, formato="G", descricao = "")
	private String codigoRegistro;

	@IAtributoArquivo(ordem = 2, posicao = 2, tamanho = 20, descricao = "", formato = "0")
	private String identificacaoAgenciaContaDigitoCreditada;

	@IAtributoArquivo(ordem = 3, posicao = 22, tamanho = 8, descricao = "", formato = " ")
	private String dataPagamento; // Data Batimento

	@IAtributoArquivo(ordem = 4, posicao = 30, tamanho = 8, descricao = "", formato = " ")
	private String dataCredito; // Data da Ocorrencia de Pagamento

	@IAtributoArquivo(ordem = 5, posicao = 38, tamanho = 44, descricao = "", formato = "0")
	private String codigoDeBarras;

	@IAtributoArquivo(ordem = 6, posicao = 82, tamanho = 10, descricao = "", formato = "0")
	private String valorRecebido;

	@IAtributoArquivo(ordem = 7, posicao = 94, tamanho = 5, descricao = "", formato = "0")
	private String valorTarifa;

	@IAtributoArquivo(ordem = 8, posicao = 101, tamanho = 8, descricao = "", formato = "0")
	private String numeroSequencialRegistro;

	@IAtributoArquivo(ordem = 9, posicao = 109, tamanho = 84, descricao = "", formato = "0")
	private String codigoAgenciaArrecadadora;

	@IAtributoArquivo(ordem = 10, posicao = 117, tamanho = 1, descricao = "", formato = " ")
	private String formaArrecadacaoCaptura;

	@IAtributoArquivo(ordem = 11, posicao = 118, tamanho = 23, descricao = "", formato = "0")
	private String numeroAutenticacaoCaixaCodigotransacao;

	@IAtributoArquivo(ordem = 12, posicao = 141, tamanho = 1, descricao = "", formato = " ")
	private String formaDePagemento;

	@IAtributoArquivo(ordem = 13, posicao = 142, tamanho = 9, descricao = "", formato = " ")
	private String reservadoParaFuturo;

	public String getCodigoRegistro() {
		if (codigoRegistro == null) {
			this.codigoRegistro = "G";
		}
		return codigoRegistro;
	}

	public void setCodigoRegistro(String codigoRegistro) {
		this.codigoRegistro = codigoRegistro;
	}

	public String getIdentificacaoAgenciaContaDigitoCreditada() {
		return identificacaoAgenciaContaDigitoCreditada;
	}

	public void setIdentificacaoAgenciaContaDigitoCreditada(String identificacaoAgenciaContaDigitoCreditada) {
		this.identificacaoAgenciaContaDigitoCreditada = identificacaoAgenciaContaDigitoCreditada;
	}

	public String getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(String dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public String getDataCredito() {
		return dataCredito;
	}

	public void setDataCredito(String dataCredito) {
		this.dataCredito = dataCredito;
	}

	public String getCodigoDeBarras() {
		return codigoDeBarras;
	}

	public void setCodigoDeBarras(String codigoDeBarras) {
		this.codigoDeBarras = codigoDeBarras;
	}

	public String getValorRecebido() {
		return valorRecebido;
	}

	public void setValorRecebido(String valorRecebido) {
		this.valorRecebido = valorRecebido;
	}

	public String getValorTarifa() {
		return valorTarifa;
	}

	public void setValorTarifa(String valorTarifa) {
		this.valorTarifa = valorTarifa;
	}

	public String getNumeroSequencialRegistro() {
		return numeroSequencialRegistro;
	}

	public void setNumeroSequencialRegistro(String numeroSequencialRegistro) {
		this.numeroSequencialRegistro = numeroSequencialRegistro;
	}

	public String getCodigoAgenciaArrecadadora() {
		return codigoAgenciaArrecadadora;
	}

	public void setCodigoAgenciaArrecadadora(String codigoAgenciaArrecadadora) {
		this.codigoAgenciaArrecadadora = codigoAgenciaArrecadadora;
	}

	public String getFormaArrecadacaoCaptura() {
		return formaArrecadacaoCaptura;
	}

	public void setFormaArrecadacaoCaptura(String formaArrecadacaoCaptura) {
		this.formaArrecadacaoCaptura = formaArrecadacaoCaptura;
	}

	public String getNumeroAutenticacaoCaixaCodigotransacao() {
		return numeroAutenticacaoCaixaCodigotransacao;
	}

	public void setNumeroAutenticacaoCaixaCodigotransacao(String numeroAutenticacaoCaixaCodigotransacao) {
		this.numeroAutenticacaoCaixaCodigotransacao = numeroAutenticacaoCaixaCodigotransacao;
	}

	public String getFormaDePagemento() {
		return formaDePagemento;
	}

	public void setFormaDePagemento(String formaDePagemento) {
		this.formaDePagemento = formaDePagemento;
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

	@Override
	public String getIdentificacaoRegistro() {
		return this.getCodigoRegistro();
	}
}
