package br.com.ieptbto.cra.conversor.arquivo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CabecalhoArquivo;
import br.com.ieptbto.cra.entidade.CabecalhoCartorio;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RodapeArquivo;
import br.com.ieptbto.cra.entidade.RodapeCartorio;
import br.com.ieptbto.cra.entidade.vo.AutorizacaoCancelamentoSerproVO;
import br.com.ieptbto.cra.entidade.vo.CartorioDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.entidade.vo.ComarcaDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.entidade.vo.TituloDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;
import br.com.ieptbto.cra.util.DataUtil;

@Service
public class ConversorAutorizacaoSerpro {
	
	private int sequenciaRegistro = 2;
	private int quantidadeDesistencias = 0;
	private int quantidadeRegistrosTipo2 = 0;
	private BigDecimal somatorioValor;

	public Arquivo converterParaArquivo(Instituicao instituicao, AutorizacaoCancelamentoSerproVO autorizacaoSerpro, Arquivo arquivo, List<Exception> erros) {
		RemessaAutorizacaoCancelamento remessaAutorizacao = new RemessaAutorizacaoCancelamento();
		remessaAutorizacao.setArquivo(arquivo);
		remessaAutorizacao.setAutorizacaoCancelamento(getAutorizacaoCancelamento(remessaAutorizacao, autorizacaoSerpro));
		remessaAutorizacao.setCabecalho(getCabecalhoArquivoCancelamento(instituicao));
		remessaAutorizacao.setRodape(getRodapeArquivoCancelamento(instituicao));
		
		arquivo.setRemessaAutorizacao(remessaAutorizacao);
		arquivo.getRemessaAutorizacao().setArquivo(arquivo);
		return arquivo;
	}

	private List<AutorizacaoCancelamento> getAutorizacaoCancelamento(RemessaAutorizacaoCancelamento remessa, AutorizacaoCancelamentoSerproVO autorizacaoSerpro) {
		HashMap<String, AutorizacaoCancelamento> mapaAutorizacaoPorComarca = new HashMap<String, AutorizacaoCancelamento>(); 
		List<AutorizacaoCancelamento> listaAutorizacoes = new ArrayList<AutorizacaoCancelamento>();
		
		for (ComarcaDesistenciaCancelamentoSerproVO comarca : autorizacaoSerpro.getComarcaDesistenciaCancelamento()) {	
			String codigoMunicipio = comarca.getCodigoMunicipio();
			
			// add na comarca existente
			if (mapaAutorizacaoPorComarca.containsKey(codigoMunicipio)) {
				AutorizacaoCancelamento autorizacao = mapaAutorizacaoPorComarca.get(codigoMunicipio);
				adicionarRegistrosCancelamentos(comarca, autorizacao);				
			} else {
				AutorizacaoCancelamento autorizacao = criarNovaAutorizacaoCancelamentoComarca(remessa, comarca);
				mapaAutorizacaoPorComarca.put(codigoMunicipio, autorizacao);
				listaAutorizacoes.add(autorizacao);
			}
		}
		return listaAutorizacoes;
	}

	/**
	 * Criar nova autorizacao de cancelamento para a comarca
	 * @param remessa
	 * @param comarca
	 */
	private AutorizacaoCancelamento criarNovaAutorizacaoCancelamentoComarca(RemessaAutorizacaoCancelamento remessa, 
			ComarcaDesistenciaCancelamentoSerproVO comarca) {
		AutorizacaoCancelamento autorizacaoCancelamento = new AutorizacaoCancelamento();
		autorizacaoCancelamento.setRemessaAutorizacaoCancelamento(remessa);

		CabecalhoCartorio cabecalhoCartorio = new CabecalhoCartorio();
		CartorioDesistenciaCancelamentoSerproVO cartorio = comarca.getCartorioDesistenciaCancelamento();
		cabecalhoCartorio.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.HEADER_CARTORIO);
		cabecalhoCartorio.setCodigoCartorio(cartorio.getCodigoCartorio());
		cabecalhoCartorio.setQuantidadeDesistencia(cartorio.getTituloDesistenciaCancelamento().size());
		cabecalhoCartorio.setCodigoMunicipio(comarca.getCodigoMunicipio());
		cabecalhoCartorio.setSequencialRegistro(StringUtils.leftPad(Integer.toString(2), 5, "0"));

		List<PedidoAutorizacaoCancelamento> pedidosAutorizacao = new ArrayList<PedidoAutorizacaoCancelamento>();
		for (TituloDesistenciaCancelamentoSerproVO titulo : cartorio.getTituloDesistenciaCancelamento()) {
			PedidoAutorizacaoCancelamento registro = new PedidoAutorizacaoCancelamento();
			registro.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.REGISTRO_PEDIDO_DESISTENCIA);
			registro.setNumeroProtocolo(titulo.getNumeroProtocoloCartorio());
			registro.setDataProtocolagem(DataUtil.stringToLocalDate("ddMMyyyy", titulo.getDataProtocolo()));
			registro.setNumeroTitulo(titulo.getNumeroTitulo());
			registro.setNomePrimeiroDevedor(titulo.getNomeDevedor());
			registro.setValorTitulo(new BigDecimal(titulo.getValorTitulo()));
			registro.setSolicitacaoCancelamentoSustacao("S");
			registro.setSequenciaRegistro(StringUtils.leftPad(Integer.toString(getSequenciaRegistro()), 5, "0"));
			this.sequenciaRegistro = getSequenciaRegistro() + 1;
			this.somatorioValor = getSomatorioValor().add(registro.getValorTitulo());
			pedidosAutorizacao.add(registro);
		}
		this.quantidadeDesistencias = getQuantidadeDesistencias() + cartorio.getTituloDesistenciaCancelamento().size();
		this.quantidadeRegistrosTipo2 = getQuantidadeRegistrosTipo2() + cartorio.getTituloDesistenciaCancelamento().size();

		RodapeCartorio rodapeCartorio = new RodapeCartorio();
		rodapeCartorio.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.TRAILLER_CARTORIO);
		rodapeCartorio.setCodigoCartorio(cartorio.getCodigoCartorio());
		rodapeCartorio.setSomaTotalCancelamentoDesistencia(cartorio.getTituloDesistenciaCancelamento().size() * 2);
		autorizacaoCancelamento.setCabecalhoCartorio(cabecalhoCartorio);
		autorizacaoCancelamento.setAutorizacoesCancelamentos(pedidosAutorizacao);
		autorizacaoCancelamento.setRodapeCartorio(rodapeCartorio);
		return autorizacaoCancelamento;
	}

	private RodapeArquivo getRodapeArquivoCancelamento(Instituicao instituicao) {
		RodapeArquivo rodapeArquivo = new RodapeArquivo();
		rodapeArquivo.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.TRAILLER_APRESENTANTE);
		rodapeArquivo.setCodigoApresentante(instituicao.getCodigoCompensacao());
		rodapeArquivo.setNomeApresentante(instituicao.getRazaoSocial());
		rodapeArquivo.setDataMovimento(new LocalDate());
		rodapeArquivo.setQuantidadeDesistencia(getQuantidadeDesistencias() + getQuantidadeRegistrosTipo2());
		rodapeArquivo.setSomatorioValorTitulo(somatorioValor);
		return rodapeArquivo;
	}

	private CabecalhoArquivo getCabecalhoArquivoCancelamento(Instituicao instituicaoDesistenciaCancelamento) {
		CabecalhoArquivo cabecalhoArquivo = new CabecalhoArquivo();
		cabecalhoArquivo.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.HEADER_APRESENTANTE);
		cabecalhoArquivo.setCodigoApresentante(instituicaoDesistenciaCancelamento.getCodigoCompensacao());
		cabecalhoArquivo.setNomeApresentante(instituicaoDesistenciaCancelamento.getRazaoSocial());
		cabecalhoArquivo.setDataMovimento(new LocalDate());
		cabecalhoArquivo.setQuantidadeDesistencia(getQuantidadeDesistencias());
		cabecalhoArquivo.setQuantidadeRegistro(getQuantidadeRegistrosTipo2());
		cabecalhoArquivo.setSequencialRegistro("00001");
		return cabecalhoArquivo;
	}
	
	/**
	 * Inclui os registros de cancelamento independente do codigo do cartório
	 * @param comarca
	 * @param autorizacao 
	 */
	private void adicionarRegistrosCancelamentos(ComarcaDesistenciaCancelamentoSerproVO comarca, AutorizacaoCancelamento autorizacao) {
		CartorioDesistenciaCancelamentoSerproVO cartorio = comarca.getCartorioDesistenciaCancelamento();

		for (TituloDesistenciaCancelamentoSerproVO titulo : cartorio.getTituloDesistenciaCancelamento()) {
			PedidoAutorizacaoCancelamento registro = new PedidoAutorizacaoCancelamento();
			registro.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.REGISTRO_PEDIDO_DESISTENCIA);
			registro.setNumeroProtocolo(titulo.getNumeroProtocoloCartorio());
			registro.setDataProtocolagem(DataUtil.stringToLocalDate("ddMMyyyy", titulo.getDataProtocolo()));
			registro.setNumeroTitulo(titulo.getNumeroTitulo());
			registro.setNomePrimeiroDevedor(titulo.getNomeDevedor());
			registro.setValorTitulo(new BigDecimal(titulo.getValorTitulo()));
			registro.setSolicitacaoCancelamentoSustacao("S");
			registro.setSequenciaRegistro(StringUtils.leftPad(Integer.toString(getSequenciaRegistro()), 5, "0"));
			this.sequenciaRegistro = getSequenciaRegistro() + 1;
			this.somatorioValor = getSomatorioValor().add(registro.getValorTitulo());
			autorizacao.getAutorizacoesCancelamentos().add(registro);
		}
		this.quantidadeDesistencias = getQuantidadeDesistencias() + cartorio.getTituloDesistenciaCancelamento().size();
		this.quantidadeRegistrosTipo2 = getQuantidadeRegistrosTipo2() + cartorio.getTituloDesistenciaCancelamento().size();
		
		RodapeCartorio rodapeCartorio = autorizacao.getRodapeCartorio();
		rodapeCartorio.setSomaTotalCancelamentoDesistencia(cartorio.getTituloDesistenciaCancelamento().size() * 2);
		rodapeCartorio.setSequencialRegistro(StringUtils.leftPad(Integer.toString(getSequenciaRegistro()), 5, "0"));
	}

	public int getQuantidadeDesistencias() {
		return quantidadeDesistencias;
	}

	public int getQuantidadeRegistrosTipo2() {
		return quantidadeRegistrosTipo2;
	}

	public BigDecimal getSomatorioValor() {
		if (somatorioValor == null) {
			somatorioValor = BigDecimal.ZERO;
		}
		return somatorioValor;
	}

	public int getSequenciaRegistro() {
		return sequenciaRegistro;
	}
}
