package br.com.ieptbto.cra.processador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CabecalhoArquivo;
import br.com.ieptbto.cra.entidade.CabecalhoCartorio;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaCancelamentoProtesto;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.RodapeArquivo;
import br.com.ieptbto.cra.entidade.RodapeCartorio;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;
import br.com.ieptbto.cra.enumeration.TipoSolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ProcessadorDesistenciaCancelamentoConveniada extends Processador {

	@Autowired
	private TituloMediator tituloMediator;
	@Autowired
	private InstituicaoDAO instituicaoDAO;
	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	private RemessaMediator remessaMediator;

	private Usuario usuario;
	private HashMap<String, Arquivo> mapaArquivosCancelamento;
	private HashMap<String, Arquivo> mapaArquivosAutorizacao;
	private HashMap<String, Arquivo> mapaArquivosDesistencia;
	private HashMap<String, CancelamentoProtesto> mapaCancelamento;
	private HashMap<String, AutorizacaoCancelamento> mapaAutorizacao;
	private HashMap<String, DesistenciaProtesto> mapaDesistencia;

	/**
	 * Método resposável por receber as solicitaçoes de Cancelamento,
	 * Desistências e Autorizações dividir e criar os arquivos
	 * 
	 * @param solicitacoes
	 * @param usuario
	 * @return
	 */
	public List<Arquivo> processaDesistenciasCancelamentos(List<SolicitacaoDesistenciaCancelamento> solicitacoes, Usuario usuario) {
		this.mapaArquivosCancelamento = null;
		this.mapaCancelamento = null;
		this.mapaArquivosAutorizacao = null;
		this.mapaAutorizacao = null;
		this.mapaArquivosDesistencia = null;
		this.mapaDesistencia = null;
		this.usuario = usuario;

		for (SolicitacaoDesistenciaCancelamento solicitacao : solicitacoes) {
			TituloRemessa titulo = solicitacao.getTituloRemessa();
			titulo.setRemessa(remessaMediator.carregarRemessaPorId(titulo.getRemessa()));

			String key = new chaveTitulo(titulo.getCodigoPortador(), titulo.getRemessa().getCabecalho().getCodigoMunicipio()).toString();
			if (solicitacao.getTipoSolicitacao() == TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_CANCELAMENTO_PROTESTO) {
				processarCancelamentoProtesto(key, titulo, solicitacao);
			} else if (solicitacao.getTipoSolicitacao() == TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_AUTORIZACAO_CANCELAMENTO) {
				processarAutorizacaoCancelamento(key, titulo);
			} else if (solicitacao.getTipoSolicitacao() == TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_DESISTENCIA_PROTESTO
					|| solicitacao.getTipoSolicitacao() == TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_DESISTENCIA_PROTESTO_IRREGULARIDADE) {
				processarDesistenciaProtesto(key, titulo, solicitacao);
			}
		}

		List<Arquivo> arquivos = new ArrayList<Arquivo>();
		if (!getMapaArquivosAutorizacao().values().isEmpty()) {
			arquivos.addAll(getMapaArquivosAutorizacao().values());
		}
		if (!getMapaArquivosDesistencia().values().isEmpty()) {
			arquivos.addAll(getMapaArquivosDesistencia().values());
		}
		if (!getMapaArquivosCancelamento().values().isEmpty()) {
			arquivos.addAll(getMapaArquivosCancelamento().values());
		}
		return arquivos;
	}

	/**
	 * Processar Pedido Cancelamento
	 * 
	 * @param key
	 * @param titulo
	 * @param solicitacao
	 */
	private void processarCancelamentoProtesto(String key, TituloRemessa titulo, SolicitacaoDesistenciaCancelamento solicitacao) {
		if (getMapaCancelamento().containsKey(key)) {
			getMapaCancelamento().get(key).getCancelamentos().add(adicionarRegistroPedidoCancelamento(titulo, solicitacao));

			CancelamentoProtesto cp = getMapaCancelamento().get(key);
			cp.getCabecalhoCartorio().setQuantidadeDesistencia(cp.getCabecalhoCartorio().getQuantidadeDesistencia() + 1);
			cp.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(cp.getCabecalhoCartorio().getQuantidadeDesistencia() * 2);
		} else {
			getMapaCancelamento().put(key, criarRemessaCancelamentoProtesto(titulo, solicitacao));
			getMapaArquivosCancelamento().put(key,
					criarArquivoCancelamentoProtesto(titulo.getCodigoPortador(), getMapaCancelamento().get(key)));
		}
	}

	/**
	 * Processar Pedido Autorizacao
	 * 
	 * @param key
	 * @param titulo
	 */
	private void processarAutorizacaoCancelamento(String key, TituloRemessa titulo) {
		if (getMapaAutorizacao().containsKey(key)) {
			getMapaAutorizacao().get(key).getAutorizacoesCancelamentos().add(adicionarRegistroPedidoAutorizacao(titulo));

			AutorizacaoCancelamento ac = getMapaAutorizacao().get(key);
			ac.getCabecalhoCartorio().setQuantidadeDesistencia(ac.getCabecalhoCartorio().getQuantidadeDesistencia() + 1);
			ac.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(ac.getCabecalhoCartorio().getQuantidadeDesistencia() * 2);
		} else {
			getMapaAutorizacao().put(key, criarRemessaAutorizacaoCancelamento(titulo));
			getMapaArquivosAutorizacao().put(key,
					criarArquivoAutorizacaoCancelamento(titulo.getCodigoPortador(), getMapaAutorizacao().get(key)));
		}
	}

	/**
	 * Processar Pedido Desistencia
	 * 
	 * @param key
	 * @param titulo
	 * @param solicitacao
	 */
	private void processarDesistenciaProtesto(String key, TituloRemessa titulo, SolicitacaoDesistenciaCancelamento solicitacao) {
		if (getMapaDesistencia().containsKey(key)) {
			getMapaDesistencia().get(key).getDesistencias().add(adicionarRegistroPedidoDesistencia(titulo, solicitacao));

			DesistenciaProtesto dp = getMapaDesistencia().get(key);
			dp.getCabecalhoCartorio().setQuantidadeDesistencia(dp.getCabecalhoCartorio().getQuantidadeDesistencia() + 1);
			dp.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(dp.getCabecalhoCartorio().getQuantidadeDesistencia() * 2);
		} else {
			getMapaDesistencia().put(key, criarRemessaDesistenciaProtesto(titulo, solicitacao));
			getMapaArquivosDesistencia().put(key, criarArquivoDesistenciaProtesto(titulo.getCodigoPortador(), getMapaDesistencia().get(key)));
		}
	}

	/**
	 * Criando Arquivo de Cancelamento de Protesto
	 * 
	 * @param codigoPortador
	 * @param cancelamentoProtesto
	 * @return
	 */
	private Arquivo criarArquivoCancelamentoProtesto(String codigoPortador, CancelamentoProtesto cancelamentoProtesto) {
		Arquivo arquivo = new Arquivo();
		arquivo.setInstituicaoEnvio(instituicaoDAO.getInstituicaoPorCodigo(codigoPortador));
		arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicao("CRA"));
		arquivo.setTipoArquivo(tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO));
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setUsuarioEnvio(getUsuario());
		arquivo.setStatusArquivo(getStatusArquivo());
		arquivo.setNomeArquivo(gerarNomeArquivo(arquivo.getInstituicaoEnvio(), TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO));

		RemessaCancelamentoProtesto remessaCancelamento = new RemessaCancelamentoProtesto();
		remessaCancelamento.setArquivo(arquivo);
		remessaCancelamento.setCabecalho(getCabecalhoArquivo(arquivo.getInstituicaoEnvio()));
		remessaCancelamento.setCancelamentoProtesto(new ArrayList<CancelamentoProtesto>());
		remessaCancelamento.getCancelamentoProtesto().add(cancelamentoProtesto);
		remessaCancelamento.setRodape(getRodapeArquivo(arquivo.getInstituicaoEnvio()));

		arquivo.setRemessaCancelamentoProtesto(remessaCancelamento);
		return arquivo;
	}

	private CancelamentoProtesto criarRemessaCancelamentoProtesto(TituloRemessa titulo,
			SolicitacaoDesistenciaCancelamento solicitacaoCancelamento) {
		CancelamentoProtesto cp = new CancelamentoProtesto();
		cp.setCabecalhoCartorio(getCabecalhoCartorio(titulo.getRemessa().getCabecalho().getCodigoMunicipio()));
		cp.setCancelamentos(new ArrayList<PedidoCancelamento>());
		cp.getCancelamentos().add(adicionarRegistroPedidoCancelamento(titulo, solicitacaoCancelamento));
		cp.setRodapeCartorio(getRodapeCartorio());
		cp.setDownload(false);
		return cp;
	}

	private PedidoCancelamento adicionarRegistroPedidoCancelamento(TituloRemessa titulo,
			SolicitacaoDesistenciaCancelamento solicitacaoCancelamento) {
		PedidoCancelamento pedidoCancelamento = new PedidoCancelamento();
		Confirmacao conf = tituloMediator.buscarConfirmacao(titulo);
		pedidoCancelamento.setNumeroProtocolo(conf.getNumeroProtocoloCartorio());
		pedidoCancelamento.setDataProtocolagem(conf.getDataProtocolo());
		pedidoCancelamento.setNumeroTitulo(titulo.getNumeroTitulo());
		pedidoCancelamento.setNomePrimeiroDevedor(titulo.getNomeDevedor());
		pedidoCancelamento.setValorTitulo(titulo.getSaldoTitulo());
		pedidoCancelamento.setSolicitacaoCancelamentoSustacao("C");
		pedidoCancelamento.setAgenciaConta(titulo.getAgenciaCodigoCedente());
		pedidoCancelamento.setCarteiraNossoNumero(titulo.getNossoNumero());
		pedidoCancelamento.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.REGISTRO_PEDIDO_DESISTENCIA);
		pedidoCancelamento.setReservado(solicitacaoCancelamento.getCodigoIrregularidade().getCodigoIrregularidade());
		return pedidoCancelamento;
	}

	/**
	 * Criando Arquivo de Autorizacao de Cancelamento
	 * 
	 * @param codigoPortador
	 * @param autorizacaoCancelamento
	 * @return
	 */
	private Arquivo criarArquivoAutorizacaoCancelamento(String codigoPortador, AutorizacaoCancelamento autorizacaoCancelamento) {
		Arquivo arquivo = new Arquivo();
		arquivo.setInstituicaoEnvio(instituicaoDAO.getInstituicaoPorCodigo(codigoPortador));
		arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicao("CRA"));
		arquivo.setTipoArquivo(tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO));
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setUsuarioEnvio(getUsuario());
		arquivo.setStatusArquivo(getStatusArquivo());
		arquivo.setNomeArquivo(gerarNomeArquivo(arquivo.getInstituicaoEnvio(), TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO));

		RemessaAutorizacaoCancelamento remessaAc = new RemessaAutorizacaoCancelamento();
		remessaAc.setArquivo(arquivo);
		remessaAc.setCabecalho(getCabecalhoArquivo(arquivo.getInstituicaoEnvio()));
		remessaAc.setAutorizacaoCancelamento(new ArrayList<AutorizacaoCancelamento>());
		remessaAc.getAutorizacaoCancelamento().add(autorizacaoCancelamento);
		remessaAc.setRodape(getRodapeArquivo(arquivo.getInstituicaoEnvio()));

		arquivo.setRemessaAutorizacao(remessaAc);
		return arquivo;
	}

	private AutorizacaoCancelamento criarRemessaAutorizacaoCancelamento(TituloRemessa titulo) {
		AutorizacaoCancelamento ac = new AutorizacaoCancelamento();
		ac.setCabecalhoCartorio(getCabecalhoCartorio(titulo.getRemessa().getCabecalho().getCodigoMunicipio()));
		ac.setAutorizacoesCancelamentos(new ArrayList<PedidoAutorizacaoCancelamento>());
		ac.getAutorizacoesCancelamentos().add(adicionarRegistroPedidoAutorizacao(titulo));
		ac.setRodapeCartorio(getRodapeCartorio());
		ac.setDownload(false);
		return ac;
	}

	private PedidoAutorizacaoCancelamento adicionarRegistroPedidoAutorizacao(TituloRemessa titulo) {
		PedidoAutorizacaoCancelamento pedidoAutorizacaoCancelamento = new PedidoAutorizacaoCancelamento();
		Confirmacao conf = tituloMediator.buscarConfirmacao(titulo);
		pedidoAutorizacaoCancelamento.setNumeroProtocolo(conf.getNumeroProtocoloCartorio());
		pedidoAutorizacaoCancelamento.setDataProtocolagem(conf.getDataProtocolo());
		pedidoAutorizacaoCancelamento.setNumeroTitulo(titulo.getNumeroTitulo());
		pedidoAutorizacaoCancelamento.setNomePrimeiroDevedor(titulo.getNomeDevedor());
		pedidoAutorizacaoCancelamento.setValorTitulo(titulo.getSaldoTitulo());
		pedidoAutorizacaoCancelamento.setSolicitacaoCancelamentoSustacao("C");
		pedidoAutorizacaoCancelamento.setAgenciaConta(titulo.getAgenciaCodigoCedente());
		pedidoAutorizacaoCancelamento.setCarteiraNossoNumero(titulo.getNossoNumero());
		pedidoAutorizacaoCancelamento.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.REGISTRO_PEDIDO_DESISTENCIA);
		return pedidoAutorizacaoCancelamento;
	}

	/**
	 * Criando Arquivo de Desistencia de Protesto
	 * 
	 * @param codigoPortador
	 * @param desistenciaProtesto
	 * @return
	 */
	private Arquivo criarArquivoDesistenciaProtesto(String codigoPortador, DesistenciaProtesto desistenciaProtesto) {
		Arquivo arquivo = new Arquivo();
		arquivo.setInstituicaoEnvio(instituicaoDAO.getInstituicaoPorCodigo(codigoPortador));
		arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicao("CRA"));
		arquivo.setTipoArquivo(tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO));
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setUsuarioEnvio(getUsuario());
		arquivo.setStatusArquivo(getStatusArquivo());
		arquivo.setNomeArquivo(gerarNomeArquivo(arquivo.getInstituicaoEnvio(), TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO));

		RemessaDesistenciaProtesto remessaDesistencia = new RemessaDesistenciaProtesto();
		remessaDesistencia.setArquivo(arquivo);
		remessaDesistencia.setCabecalho(getCabecalhoArquivo(arquivo.getInstituicaoEnvio()));
		remessaDesistencia.setDesistenciaProtesto(new ArrayList<DesistenciaProtesto>());
		remessaDesistencia.getDesistenciaProtesto().add(desistenciaProtesto);
		remessaDesistencia.setRodape(getRodapeArquivo(arquivo.getInstituicaoEnvio()));

		arquivo.setRemessaDesistenciaProtesto(remessaDesistencia);
		return arquivo;
	}

	private DesistenciaProtesto criarRemessaDesistenciaProtesto(TituloRemessa titulo, SolicitacaoDesistenciaCancelamento solicitacao) {
		DesistenciaProtesto dp = new DesistenciaProtesto();
		dp.setCabecalhoCartorio(getCabecalhoCartorio(titulo.getRemessa().getCabecalho().getCodigoMunicipio()));
		dp.setDesistencias(new ArrayList<PedidoDesistencia>());
		dp.getDesistencias().add(adicionarRegistroPedidoDesistencia(titulo, solicitacao));
		dp.setRodapeCartorio(getRodapeCartorio());
		dp.setDownload(false);
		return dp;
	}

	private PedidoDesistencia adicionarRegistroPedidoDesistencia(TituloRemessa titulo, SolicitacaoDesistenciaCancelamento solicitacao) {
		PedidoDesistencia pedidoDesistencia = new PedidoDesistencia();
		Confirmacao conf = tituloMediator.buscarConfirmacao(titulo);
		pedidoDesistencia.setNumeroProtocolo(conf.getNumeroProtocoloCartorio());
		pedidoDesistencia.setDataProtocolagem(conf.getDataProtocolo());
		pedidoDesistencia.setNumeroTitulo(titulo.getNumeroTitulo());
		pedidoDesistencia.setNomePrimeiroDevedor(titulo.getNomeDevedor());
		pedidoDesistencia.setValorTitulo(titulo.getSaldoTitulo());
		pedidoDesistencia.setSolicitacaoCancelamentoSustacao("S");
		pedidoDesistencia.setAgenciaConta(titulo.getAgenciaCodigoCedente());
		pedidoDesistencia.setCarteiraNossoNumero(titulo.getNossoNumero());
		pedidoDesistencia.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.REGISTRO_PEDIDO_DESISTENCIA);

		TipoSolicitacaoDesistenciaCancelamento tipo = solicitacao.getTipoSolicitacao();
		if (tipo == TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_DESISTENCIA_PROTESTO_IRREGULARIDADE) {
			pedidoDesistencia.setReservado(solicitacao.getCodigoIrregularidade().getCodigoIrregularidade());
		} else {
			pedidoDesistencia.setReservado(ConfiguracaoBase.CODIGO_IRREGULARIDADE_AUTORIZACAO_DESISTENCIA_PROTESTO);
		}
		return pedidoDesistencia;
	}

	private RodapeArquivo getRodapeArquivo(Instituicao instituicaoEnvio) {
		RodapeArquivo rodape = new RodapeArquivo();
		rodape.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.TRAILLER_APRESENTANTE);
		rodape.setCodigoApresentante(instituicaoEnvio.getCodigoCompensacao());
		rodape.setNomeApresentante(instituicaoEnvio.getNomeFantasia().toUpperCase());
		rodape.setDataMovimento(new LocalDate());
		rodape.setQuantidadeDesistencia(1);
		rodape.setSomaTotalCancelamentoDesistencia(rodape.getQuantidadeDesistencia() * 2);
		return rodape;
	}

	private CabecalhoArquivo getCabecalhoArquivo(Instituicao instituicaoEnvio) {
		CabecalhoArquivo cabecalho = new CabecalhoArquivo();
		cabecalho.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.HEADER_APRESENTANTE);
		cabecalho.setCodigoApresentante(instituicaoEnvio.getCodigoCompensacao());
		cabecalho.setNomeApresentante(instituicaoEnvio.getNomeFantasia().toUpperCase());
		cabecalho.setDataMovimento(new LocalDate());
		cabecalho.setQuantidadeDesistencia(1);
		cabecalho.setQuantidadeRegistro(cabecalho.getQuantidadeDesistencia());
		return cabecalho;
	}

	private CabecalhoCartorio getCabecalhoCartorio(String codigoMunicipio) {
		CabecalhoCartorio cabecalho = new CabecalhoCartorio();
		cabecalho.setCodigoCartorio("01");
		cabecalho.setCodigoMunicipio(codigoMunicipio);
		cabecalho.setQuantidadeDesistencia(2);
		cabecalho.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.HEADER_CARTORIO);
		return cabecalho;
	}

	private RodapeCartorio getRodapeCartorio() {
		RodapeCartorio rodape = new RodapeCartorio();
		rodape.setCodigoCartorio("01");
		rodape.setSomaTotalCancelamentoDesistencia(1);
		rodape.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.TRAILLER_CARTORIO);
		return rodape;
	}

	private String gerarNomeArquivo(Instituicao instituicaoEnvio, TipoArquivoEnum tipoArquivo) {
		String codigoPortador = instituicaoEnvio.getCodigoCompensacao();
		String dataArquivo = new SimpleDateFormat("ddMM.yy").format(new Date()).toString();
		Long sequencialArquivo = tipoArquivoDAO.buscarSequencialProximoArquivo(instituicaoEnvio, tipoArquivo);
		return tipoArquivo.getConstante() + codigoPortador + dataArquivo + Long.toString(sequencialArquivo + 1);
	}

	private StatusArquivo getStatusArquivo() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setSituacaoArquivo(SituacaoArquivo.ENVIADO);
		return status;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public HashMap<String, CancelamentoProtesto> getMapaCancelamento() {
		if (mapaCancelamento == null) {
			this.mapaCancelamento = new HashMap<String, CancelamentoProtesto>();
		}
		return mapaCancelamento;
	}

	public HashMap<String, AutorizacaoCancelamento> getMapaAutorizacao() {
		if (mapaAutorizacao == null) {
			this.mapaAutorizacao = new HashMap<String, AutorizacaoCancelamento>();
		}
		return mapaAutorizacao;
	}

	public HashMap<String, DesistenciaProtesto> getMapaDesistencia() {
		if (mapaDesistencia == null) {
			this.mapaDesistencia = new HashMap<String, DesistenciaProtesto>();
		}
		return mapaDesistencia;
	}

	public HashMap<String, Arquivo> getMapaArquivosCancelamento() {
		if (mapaArquivosCancelamento == null) {
			this.mapaArquivosCancelamento = new HashMap<String, Arquivo>();
		}
		return mapaArquivosCancelamento;
	}

	public HashMap<String, Arquivo> getMapaArquivosAutorizacao() {
		if (mapaArquivosAutorizacao == null) {
			this.mapaArquivosAutorizacao = new HashMap<String, Arquivo>();
		}
		return mapaArquivosAutorizacao;
	}

	public HashMap<String, Arquivo> getMapaArquivosDesistencia() {
		if (mapaArquivosDesistencia == null) {
			this.mapaArquivosDesistencia = new HashMap<String, Arquivo>();
		}
		return mapaArquivosDesistencia;
	}

	class chaveTitulo {
		private String codigoPortador;
		private String codigoMunicipio;

		public chaveTitulo(String codigoPortador, String codigoMunicipio) {
			this.codigoPortador = codigoPortador;
			this.codigoMunicipio = codigoMunicipio;
		}

		public String getCodigoPortador() {
			return codigoPortador;
		}

		public String getCodigoMunicipio() {
			return codigoMunicipio;
		}

		public void setCodigoPortador(String codigoPortador) {
			this.codigoPortador = codigoPortador;
		}

		public void setCodigoMunicipio(String codigoMunicipio) {
			this.codigoMunicipio = codigoMunicipio;
		}

		@Override
		public String toString() {
			return String.valueOf(getCodigoPortador()).concat(getCodigoMunicipio());
		}
	}
}
