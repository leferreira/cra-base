package br.com.ieptbto.cra.conversor.arquivo;

import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.vo.CancelamentoSerproVO;
import br.com.ieptbto.cra.entidade.vo.CartorioDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.entidade.vo.ComarcaDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.entidade.vo.TituloDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;
import br.com.ieptbto.cra.enumeration.regra.CodigoIrregularidade;
import br.com.ieptbto.cra.util.DataUtil;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ConversorCancelamentoSerpro {
	
	private int sequenciaRegistro = 2;
	private int quantidadeDesistencias = 0;
	private int quantidadeRegistrosTipo2 = 0;
	private BigDecimal somatorioValor;

	public Arquivo converterParaArquivo(Instituicao instituicao, CancelamentoSerproVO cancelamentoSerpro, Arquivo arquivo, List<Exception> erros) {
		RemessaCancelamentoProtesto remessaCancelamento = new RemessaCancelamentoProtesto();
		remessaCancelamento.setArquivo(arquivo);
		remessaCancelamento.setCancelamentoProtesto(getCancelamentosProtesto(remessaCancelamento, cancelamentoSerpro));
		remessaCancelamento.setCabecalho(getCabecalhoArquivoCancelamento(instituicao));
		remessaCancelamento.setRodape(getRodapeArquivoCancelamento(instituicao));
		
		arquivo.setRemessaCancelamentoProtesto(remessaCancelamento);
		arquivo.getRemessaCancelamentoProtesto().setArquivo(arquivo);
		return arquivo;
	}
	
	private List<CancelamentoProtesto> getCancelamentosProtesto(RemessaCancelamentoProtesto remessa, CancelamentoSerproVO cancelamentoSerpro) {
		HashMap<String, CancelamentoProtesto> mapaCancelamentoPorComarca = new HashMap<String, CancelamentoProtesto>(); 
		List<CancelamentoProtesto> listaCancelamentos = new ArrayList<CancelamentoProtesto>();
		
		for (ComarcaDesistenciaCancelamentoSerproVO comarca : cancelamentoSerpro.getComarcaDesistenciaCancelamento()) {	
			String codigoMunicipio = comarca.getCodigoMunicipio();
			
			// add na comarca existente
			if (mapaCancelamentoPorComarca.containsKey(codigoMunicipio)) {
				CancelamentoProtesto cancelamento = mapaCancelamentoPorComarca.get(codigoMunicipio);
				adicionarRegistrosCancelamentos(comarca, cancelamento);				
			} else {
				CancelamentoProtesto cancelamento = criarNovaCancelamentoProtestoComarca(remessa, comarca);
				mapaCancelamentoPorComarca.put(codigoMunicipio, cancelamento);
				listaCancelamentos.add(cancelamento);
			}
		}
		return listaCancelamentos;
	}

	/**
	 * Criar nova autorizacao de cancelamento para a comarca
	 * @param remessa
	 * @param comarca
	 */
	private CancelamentoProtesto criarNovaCancelamentoProtestoComarca(RemessaCancelamentoProtesto remessaCancelamento,
			ComarcaDesistenciaCancelamentoSerproVO comarca) {
		CancelamentoProtesto cancelamentoProtesto = new CancelamentoProtesto();
		cancelamentoProtesto.setRemessaCancelamentoProtesto(remessaCancelamento);

		CabecalhoCartorio cabecalhoCartorio = new CabecalhoCartorio();
		CartorioDesistenciaCancelamentoSerproVO cartorio = comarca.getCartorioDesistenciaCancelamento();
		cabecalhoCartorio.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.HEADER_CARTORIO);
		cabecalhoCartorio.setCodigoCartorio(StringUtils.leftPad(cartorio.getCodigoCartorio(), 2, "0"));
		cabecalhoCartorio.setQuantidadeDesistencia(cartorio.getTituloDesistenciaCancelamento().size());
		cabecalhoCartorio.setCodigoMunicipio(comarca.getCodigoMunicipio());
		cabecalhoCartorio.setSequencialRegistro(StringUtils.leftPad(Integer.toString(2), 5, "0"));

		List<PedidoCancelamento> pedidosCancelamento = new ArrayList<PedidoCancelamento>();
		for (TituloDesistenciaCancelamentoSerproVO titulo : cartorio.getTituloDesistenciaCancelamento()) {
			PedidoCancelamento registro = new PedidoCancelamento();
			registro.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.REGISTRO_PEDIDO_DESISTENCIA);
			registro.setNumeroProtocolo(titulo.getNumeroProtocoloCartorio());
			registro.setDataProtocolagem(DataUtil.stringToLocalDate("ddMMyyyy", titulo.getDataProtocolo()));
			registro.setNumeroTitulo(titulo.getNumeroTitulo());
			registro.setNomePrimeiroDevedor(titulo.getNomeDevedor());
			registro.setValorTitulo(new BigDecimal(titulo.getValorTitulo()));
			registro.setSolicitacaoCancelamentoSustacao("C");
			registro.setReservado(CodigoIrregularidade.IRREGULARIDADE_60.getCodigoIrregularidade());
			registro.setSequenciaRegistro(StringUtils.leftPad(Integer.toString(getSequenciaRegistro()), 5, "0"));
			
			this.sequenciaRegistro = getSequenciaRegistro() + 1;
			this.somatorioValor = getSomatorioValor().add(registro.getValorTitulo());
			pedidosCancelamento.add(registro);
		}
		this.quantidadeDesistencias = getQuantidadeDesistencias() + cartorio.getTituloDesistenciaCancelamento().size();
		this.quantidadeRegistrosTipo2 = getQuantidadeRegistrosTipo2() + cartorio.getTituloDesistenciaCancelamento().size();

		RodapeCartorio rodapeCartorio = new RodapeCartorio();
		rodapeCartorio.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.TRAILLER_CARTORIO);
		rodapeCartorio.setCodigoCartorio(cartorio.getCodigoCartorio());
		rodapeCartorio.setSomaTotalCancelamentoDesistencia(cartorio.getTituloDesistenciaCancelamento().size() * 2);
		rodapeCartorio.setSequencialRegistro(StringUtils.leftPad(Integer.toString(getSequenciaRegistro()), 5, "0"));
		cancelamentoProtesto.setCabecalhoCartorio(cabecalhoCartorio);
		cancelamentoProtesto.setCancelamentos(pedidosCancelamento);
		cancelamentoProtesto.setRodapeCartorio(rodapeCartorio);
		return cancelamentoProtesto;
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
	private void adicionarRegistrosCancelamentos(ComarcaDesistenciaCancelamentoSerproVO comarca, CancelamentoProtesto cancelamento) {
		CartorioDesistenciaCancelamentoSerproVO cartorio = comarca.getCartorioDesistenciaCancelamento();

		for (TituloDesistenciaCancelamentoSerproVO titulo : cartorio.getTituloDesistenciaCancelamento()) {
			PedidoCancelamento registro = new PedidoCancelamento();
			registro.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.REGISTRO_PEDIDO_DESISTENCIA);
			registro.setNumeroProtocolo(titulo.getNumeroProtocoloCartorio());
			registro.setDataProtocolagem(DataUtil.stringToLocalDate("ddMMyyyy", titulo.getDataProtocolo()));
			registro.setNumeroTitulo(titulo.getNumeroTitulo());
			registro.setNomePrimeiroDevedor(titulo.getNomeDevedor());
			registro.setValorTitulo(new BigDecimal(titulo.getValorTitulo()));
			registro.setSolicitacaoCancelamentoSustacao("C");
			registro.setReservado(CodigoIrregularidade.IRREGULARIDADE_60.getCodigoIrregularidade());
			registro.setSequenciaRegistro(StringUtils.leftPad(Integer.toString(getSequenciaRegistro()), 5, "0"));
			
			this.sequenciaRegistro = getSequenciaRegistro() + 1;
			this.somatorioValor = getSomatorioValor().add(registro.getValorTitulo());
			cancelamento.getCancelamentos().add(registro);
		}
		this.quantidadeDesistencias = getQuantidadeDesistencias() + cartorio.getTituloDesistenciaCancelamento().size();
		this.quantidadeRegistrosTipo2 = getQuantidadeRegistrosTipo2() + cartorio.getTituloDesistenciaCancelamento().size();
		
		RodapeCartorio rodapeCartorio = cancelamento.getRodapeCartorio();
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