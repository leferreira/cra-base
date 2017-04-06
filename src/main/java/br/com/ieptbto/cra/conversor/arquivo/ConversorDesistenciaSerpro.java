package br.com.ieptbto.cra.conversor.arquivo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoArquivo;
import br.com.ieptbto.cra.entidade.CabecalhoCartorio;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.RodapeArquivo;
import br.com.ieptbto.cra.entidade.RodapeCartorio;
import br.com.ieptbto.cra.entidade.vo.CartorioDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.entidade.vo.ComarcaDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.entidade.vo.DesistenciaSerproVO;
import br.com.ieptbto.cra.entidade.vo.TituloDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;
import br.com.ieptbto.cra.util.DataUtil;

@Service
public class ConversorDesistenciaSerpro {
	
	private int sequenciaRegistro = 2;
	private int quantidadeDesistencias = 0;
	private int quantidadeRegistrosTipo2 = 0;
	private BigDecimal somatorioValor;

	public Arquivo converterParaArquivo(Instituicao instituicao, DesistenciaSerproVO desistenciaSerproVO, Arquivo arquivo, List<Exception> erros) {
		RemessaDesistenciaProtesto remessaDesistencia = new RemessaDesistenciaProtesto();
		remessaDesistencia.setArquivo(arquivo);
		remessaDesistencia.setDesistenciaProtesto(getDesistenciasCancelamentosProtesto(remessaDesistencia, desistenciaSerproVO));
		remessaDesistencia.setCabecalho(getCabecalhoArquivoDesistenciaCancelamento(instituicao));
		remessaDesistencia.setRodape(getRodapeArquivoDesistenciaCancelamento(instituicao));
		
		arquivo.setRemessaDesistenciaProtesto(remessaDesistencia);
		arquivo.getRemessaDesistenciaProtesto().setArquivo(arquivo);
		return arquivo;
	}

	private List<DesistenciaProtesto> getDesistenciasCancelamentosProtesto(RemessaDesistenciaProtesto remessaDesistencia,
			DesistenciaSerproVO desistenciaCancelamentoSerpro) {
		List<DesistenciaProtesto> desistencias = new ArrayList<DesistenciaProtesto>();

		for (ComarcaDesistenciaCancelamentoSerproVO comarca : desistenciaCancelamentoSerpro.getComarcaDesistenciaCancelamento()) {
			DesistenciaProtesto desistenciaProtesto = new DesistenciaProtesto();
			desistenciaProtesto.setRemessaDesistenciaProtesto(remessaDesistencia);

			CabecalhoCartorio cabecalhoCartorio = new CabecalhoCartorio();
			RodapeCartorio rodapeCartorio = new RodapeCartorio();
			List<PedidoDesistencia> pedidosDesistencia = new ArrayList<PedidoDesistencia>();
			
			CartorioDesistenciaCancelamentoSerproVO cartorio = comarca.getCartorioDesistenciaCancelamento();
			cabecalhoCartorio.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.HEADER_CARTORIO);
			cabecalhoCartorio.setCodigoCartorio(cartorio.getCodigoCartorio());
			cabecalhoCartorio.setQuantidadeDesistencia(cartorio.getTituloDesistenciaCancelamento().size());
			cabecalhoCartorio.setCodigoMunicipio(comarca.getCodigoMunicipio());
			cabecalhoCartorio.setSequencialRegistro(StringUtils.leftPad(Integer.toString(2), 5, "0"));

			for (TituloDesistenciaCancelamentoSerproVO titulo : cartorio.getTituloDesistenciaCancelamento()) {
				PedidoDesistencia registro = new PedidoDesistencia();
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
				pedidosDesistencia.add(registro);
			}
			this.quantidadeDesistencias = getQuantidadeDesistencias() + cartorio.getTituloDesistenciaCancelamento().size();
			this.quantidadeRegistrosTipo2 = getQuantidadeRegistrosTipo2() + cartorio.getTituloDesistenciaCancelamento().size();

			rodapeCartorio.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.TRAILLER_CARTORIO);
			rodapeCartorio.setCodigoCartorio(cartorio.getCodigoCartorio());
			rodapeCartorio.setSomaTotalCancelamentoDesistencia(cartorio.getTituloDesistenciaCancelamento().size() * 2);
			rodapeCartorio.setSequencialRegistro(StringUtils.leftPad(Integer.toString(getSequenciaRegistro()), 5, "0"));

			desistenciaProtesto.setCabecalhoCartorio(cabecalhoCartorio);
			desistenciaProtesto.setDesistencias(pedidosDesistencia);
			desistenciaProtesto.setRodapeCartorio(rodapeCartorio);
			desistencias.add(desistenciaProtesto);
		}
		return desistencias;
	}

	private RodapeArquivo getRodapeArquivoDesistenciaCancelamento(Instituicao instituicao) {
		RodapeArquivo rodapeArquivo = new RodapeArquivo();
		rodapeArquivo.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.TRAILLER_APRESENTANTE);
		rodapeArquivo.setCodigoApresentante(instituicao.getCodigoCompensacao());
		rodapeArquivo.setNomeApresentante(instituicao.getRazaoSocial());
		rodapeArquivo.setDataMovimento(new LocalDate());
		rodapeArquivo.setQuantidadeDesistencia(getQuantidadeDesistencias() + getQuantidadeRegistrosTipo2());
		rodapeArquivo.setSomatorioValorTitulo(somatorioValor);
		return rodapeArquivo;
	}

	private CabecalhoArquivo getCabecalhoArquivoDesistenciaCancelamento(Instituicao instituicao) {
		CabecalhoArquivo cabecalhoArquivo = new CabecalhoArquivo();
		cabecalhoArquivo.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.HEADER_APRESENTANTE);
		cabecalhoArquivo.setCodigoApresentante(instituicao.getCodigoCompensacao());
		cabecalhoArquivo.setNomeApresentante(instituicao.getRazaoSocial());
		cabecalhoArquivo.setDataMovimento(new LocalDate());
		cabecalhoArquivo.setQuantidadeDesistencia(getQuantidadeDesistencias());
		cabecalhoArquivo.setQuantidadeRegistro(getQuantidadeRegistrosTipo2());
		cabecalhoArquivo.setSequencialRegistro("00001");
		return cabecalhoArquivo;
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
