package br.com.ieptbto.cra.mediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.AutorizacaoCancelamentoDAO;
import br.com.ieptbto.cra.dao.CancelamentoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.RemessaDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.dao.TituloFiliadoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CabecalhoArquivo;
import br.com.ieptbto.cra.entidade.CabecalhoCartorio;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaCancelamentoProtesto;
import br.com.ieptbto.cra.entidade.RodapeArquivo;
import br.com.ieptbto.cra.entidade.RodapeCartorio;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.StatusSolicitacaoCancelamento;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;
import br.com.ieptbto.cra.processador.ProcessadorRemessaConveniada;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ConvenioMediator extends BaseMediator {

	@Autowired
	private ProcessadorRemessaConveniada processadorRemessaConveniada;
	@Autowired
	private TituloMediator tituloMediator;
	@Autowired
	private TituloFiliadoDAO tituloFiliadoDAO;
	@Autowired
	private ArquivoDAO arquivoDAO;
	@Autowired
	private CancelamentoDAO cancelamentoDAO;
	@Autowired
	private AutorizacaoCancelamentoDAO autorizacaoCancelamentoDAO;
	@Autowired
	private RemessaDAO remessaDAO;
	@Autowired
	private InstituicaoDAO instituicaoDAO;
	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;

	private Usuario usuario;
	private Map<String, Arquivo> mapaArquivosCP;
	private Map<String, Arquivo> mapaArquivosAC;
	private Map<String, CancelamentoProtesto> mapaCancelamentoProtesto;
	private Map<String, AutorizacaoCancelamento> mapaAutorizacaoCancelamento;

	public List<TituloFiliado> buscarTitulosConvenios() {
		return tituloFiliadoDAO.buscarTitulosConvenios();
	}

	public void gerarRemessas(Usuario usuario, List<TituloFiliado> listaTitulosConvenios) {
		List<Arquivo> arquivos = processadorRemessaConveniada.processar(listaTitulosConvenios, usuario);
		for (Arquivo arquivo : arquivos) {
			arquivoDAO.salvar(arquivo, usuario, new ArrayList<Exception>());
		}
		tituloFiliadoDAO.marcarComoEnviadoParaCRA(listaTitulosConvenios);
	}

	public void gerarCancelamentos(Usuario user, List<TituloRemessa> titulosCancelamento) {
		this.mapaArquivosAC = new HashMap<String, Arquivo>();
		this.mapaArquivosCP = new HashMap<String, Arquivo>();
		this.mapaAutorizacaoCancelamento = new HashMap<String, AutorizacaoCancelamento>();
		this.mapaCancelamentoProtesto = new HashMap<String, CancelamentoProtesto>();
		this.usuario = user;

		for (TituloRemessa titulo : titulosCancelamento) {
			titulo.setRemessa(remessaDAO.buscarPorPK(titulo.getRemessa(), Remessa.class));
			String key = new chaveTitulo(titulo.getCodigoPortador(), titulo.getRemessa().getCabecalho().getCodigoMunicipio()).toString();
			if (titulo.getStatusSolicitacaoCancelamento() == StatusSolicitacaoCancelamento.SOLICITACAO_CANCELAMENTO_PROTESTO) {
				processarCancelamentoProtesto(key, titulo);
			} else if (titulo.getStatusSolicitacaoCancelamento() == StatusSolicitacaoCancelamento.SOLICITACAO_AUTORIZACAO_CANCELAMENTO) {
				processarAutorizacaoCancelamento(key, titulo);
			}
		}
		List<Arquivo> arquivosAutorizacao = new ArrayList<Arquivo>(mapaArquivosAC.values());
		List<Arquivo> arquivosCancelamento = new ArrayList<Arquivo>(mapaArquivosCP.values());
		salvarArquivosAutorizacao(arquivosAutorizacao, user);
		salvarArquivosCancelamento(arquivosCancelamento, user);
		cancelamentoDAO.marcarCancelamentoEnviado(titulosCancelamento);
	}

	private void salvarArquivosAutorizacao(List<Arquivo> arquivos, Usuario user) {
		for (Arquivo arquivo : arquivos) {
			autorizacaoCancelamentoDAO.salvarAutorizacao(arquivo, user, new ArrayList<Exception>());
		}
	}

	private void salvarArquivosCancelamento(List<Arquivo> arquivos, Usuario user) {
		for (Arquivo arquivo : arquivos) {
			cancelamentoDAO.salvarCancelamento(arquivo, user, new ArrayList<Exception>());
		}
	}

	private void processarAutorizacaoCancelamento(String key, TituloRemessa titulo) {
		if (mapaAutorizacaoCancelamento.containsKey(key)) {
			mapaAutorizacaoCancelamento.get(key).getAutorizacoesCancelamentos().add(adicionarRegistroPedidoAutorizacao(titulo));
			AutorizacaoCancelamento ac = mapaAutorizacaoCancelamento.get(key);
			ac.getCabecalhoCartorio().setQuantidadeDesistencia(ac.getCabecalhoCartorio().getQuantidadeDesistencia() + 1);
			ac.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(ac.getCabecalhoCartorio().getQuantidadeDesistencia() * 2);
		} else {
			mapaAutorizacaoCancelamento.put(key, criarAutorizacaoCancelamento(titulo));
		}
		separarArquivosAC(titulo.getCodigoPortador(), mapaAutorizacaoCancelamento.get(key));
	}

	private void processarCancelamentoProtesto(String key, TituloRemessa titulo) {
		if (mapaCancelamentoProtesto.containsKey(key)) {
			mapaCancelamentoProtesto.get(key).getCancelamentos().add(adicionarRegistroPedidoCancelamento(titulo));
			CancelamentoProtesto cp = mapaCancelamentoProtesto.get(key);
			cp.getCabecalhoCartorio().setQuantidadeDesistencia(cp.getCabecalhoCartorio().getQuantidadeDesistencia() + 1);
			cp.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(cp.getCabecalhoCartorio().getQuantidadeDesistencia() * 2);
		} else {
			mapaCancelamentoProtesto.put(key, criarCancelamentoProtesto(titulo));
		}
		separarArquivosCP(titulo.getCodigoPortador(), mapaCancelamentoProtesto.get(key));
	}

	private void separarArquivosAC(String codigoPortador, AutorizacaoCancelamento autorizacaoCancelamento) {
		if (mapaArquivosAC.containsKey(codigoPortador)) {
			mapaArquivosAC.get(codigoPortador).getRemessaAutorizacao().getAutorizacaoCancelamento().add(autorizacaoCancelamento);
		} else {
			mapaArquivosAC.put(codigoPortador, criarArquivoAC(codigoPortador, autorizacaoCancelamento));
		}
	}

	private void separarArquivosCP(String codigoPortador, CancelamentoProtesto cancelamentoProtesto) {
		if (mapaArquivosCP.containsKey(codigoPortador)) {
			mapaArquivosCP.get(codigoPortador).getRemessaCancelamentoProtesto().getCancelamentoProtesto().add(cancelamentoProtesto);
		} else {
			mapaArquivosCP.put(codigoPortador, criarArquivoCP(codigoPortador, cancelamentoProtesto));
		}
	}

	private Arquivo criarArquivoCP(String codigoPortador, CancelamentoProtesto cancelamentoProtesto) {
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

	private Arquivo criarArquivoAC(String codigoPortador, AutorizacaoCancelamento autorizacaoCancelamento) {
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

	private String gerarNomeArquivo(Instituicao instituicaoEnvio, TipoArquivoEnum tipoArquivo) {
		String codigoPortador = instituicaoEnvio.getCodigoCompensacao();
		String dataArquivo = new SimpleDateFormat("ddMM.yy").format(new Date()).toString();
		Long sequencialArquivo = tipoArquivoDAO.buscarSequencialProximoArquivo(instituicaoEnvio, tipoArquivo);
		return tipoArquivo.getConstante() + codigoPortador + dataArquivo + Long.toString(sequencialArquivo + 1);
	}

	private AutorizacaoCancelamento criarAutorizacaoCancelamento(TituloRemessa titulo) {
		AutorizacaoCancelamento ac = new AutorizacaoCancelamento();
		ac.setCabecalhoCartorio(getCabecalhoCartorio(titulo.getRemessa().getCabecalho().getCodigoMunicipio()));
		ac.setAutorizacoesCancelamentos(new ArrayList<PedidoAutorizacaoCancelamento>());
		ac.getAutorizacoesCancelamentos().add(adicionarRegistroPedidoAutorizacao(titulo));
		ac.setRodapeCartorio(getRodapeCartorio());
		ac.setDownload(false);
		return ac;
	}

	private CancelamentoProtesto criarCancelamentoProtesto(TituloRemessa titulo) {
		CancelamentoProtesto cp = new CancelamentoProtesto();
		cp.setCabecalhoCartorio(getCabecalhoCartorio(titulo.getRemessa().getCabecalho().getCodigoMunicipio()));
		cp.setCancelamentos(new ArrayList<PedidoCancelamento>());
		cp.getCancelamentos().add(adicionarRegistroPedidoCancelamento(titulo));
		cp.setRodapeCartorio(getRodapeCartorio());
		cp.setDownload(false);
		return cp;
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

	private PedidoCancelamento adicionarRegistroPedidoCancelamento(TituloRemessa titulo) {
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
		pedidoCancelamento.setReservado(titulo.getCodigoIrregularidadeCancelamento().getCodigoIrregularidade());
		return pedidoCancelamento;
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

	private StatusArquivo getStatusArquivo() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setSituacaoArquivo(SituacaoArquivo.ENVIADO);
		return status;
	}

	public Usuario getUsuario() {
		return usuario;
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
