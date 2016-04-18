package br.com.ieptbto.cra.mediator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.arquivo.ConversorDesistenciaProtesto;
import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.CancelamentoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoArquivo;
import br.com.ieptbto.cra.entidade.CabecalhoCartorio;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaCancelamentoProtesto;
import br.com.ieptbto.cra.entidade.RodapeArquivo;
import br.com.ieptbto.cra.entidade.RodapeCartorio;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.CancelamentoSerproVO;
import br.com.ieptbto.cra.entidade.vo.CartorioDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.entidade.vo.ComarcaDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.entidade.vo.TituloDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.ProcessadorArquivo;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class CancelamentoProtestoMediator {

	protected static final Logger logger = Logger.getLogger(CancelamentoProtestoMediator.class);

	@Autowired
	ConversorDesistenciaProtesto conversorArquivoDesistenciaProtesto;
	@Autowired
	ArquivoDAO arquivoDAO;
	@Autowired
	CancelamentoDAO cancelamentoDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;
	@Autowired
	TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	ProcessadorArquivo processadorArquivo;

	private int sequenciaRegistro = 2;
	private int quantidadeDesistencias = 0;
	private int quantidadeRegistrosTipo2 = 0;
	private BigDecimal somatorioValor;

	public List<PedidoCancelamento> buscarPedidosCancelamentoProtesto(CancelamentoProtesto cancelamentoProtesto) {
		return cancelamentoDAO.buscarPedidosCancelamentoProtesto(cancelamentoProtesto);
	}

	public List<CancelamentoProtesto> buscarCancelamentoProtesto(Arquivo arquivo, Instituicao portador, Municipio municipio, LocalDate dataInicio,
			LocalDate dataFim, ArrayList<TipoArquivoEnum> tiposArquivo, Usuario usuario) {
		return cancelamentoDAO.buscarCancelamentoProtesto(arquivo, portador, municipio, dataInicio, dataFim, tiposArquivo, usuario);
	}

	public File baixarCancelamentoTXT(Usuario usuario, CancelamentoProtesto cancelamentoProtesto) {
		if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			cancelamentoDAO.alterarSituacaoCancelamentoProtesto(cancelamentoProtesto, true);
		}

		cancelamentoProtesto = cancelamentoDAO.buscarRemessaCancelamentoProtesto(cancelamentoProtesto);

		BigDecimal valorTotal = BigDecimal.ZERO;
		int totalRegistro = 0;
		for (PedidoCancelamento pedido : cancelamentoProtesto.getCancelamentos()) {
			valorTotal = valorTotal.add(pedido.getValorTitulo());
			totalRegistro++;
		}

		RemessaCancelamentoProtesto remessa = new RemessaCancelamentoProtesto();
		remessa.setCabecalho(cancelamentoProtesto.getRemessaCancelamentoProtesto().getCabecalho());
		remessa.getCabecalho().setQuantidadeDesistencia(1);
		remessa.getCabecalho().setQuantidadeRegistro(totalRegistro);
		remessa.setCancelamentoProtesto(new ArrayList<CancelamentoProtesto>());
		remessa.getCancelamentoProtesto().add(cancelamentoProtesto);
		remessa.setRodape(cancelamentoProtesto.getRemessaCancelamentoProtesto().getRodape());
		remessa.getRodape().setQuantidadeDesistencia(1);
		remessa.getRodape().setSomatorioValorTitulo(valorTotal);
		remessa.setArquivo(cancelamentoProtesto.getRemessaCancelamentoProtesto().getArquivo());
		return processadorArquivo.processarRemessaCancelamentoProtestoTXT(remessa, usuario);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Arquivo processarCancelamento(String nomeArquivo, LayoutPadraoXML layoutPadraoXML, String dados, List<Exception> erros, Usuario usuario) {
		Arquivo arquivo = new Arquivo();
		arquivo.setNomeArquivo(nomeArquivo);
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setInstituicaoEnvio(usuario.getInstituicao());
		arquivo.setInstituicaoRecebe(getCra());
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setRemessas(new ArrayList<Remessa>());
		arquivo.setTipoArquivo(getTipoArquivoCancelamentoProtesto());
		arquivo.setStatusArquivo(getStatusArquivo());

		if (layoutPadraoXML.equals(LayoutPadraoXML.SERPRO)) {
			RemessaCancelamentoProtesto remessaCancelamento =
					converterCancelamentoSerpro(arquivo, usuario.getInstituicao(), converterStringParaCancelamentoSerproVO(dados), erros);
			arquivo.setRemessaCancelamentoProtesto(remessaCancelamento);

			return cancelamentoDAO.salvarCancelamentoSerpro(arquivo, usuario, erros);
		}
		arquivo = conversorArquivoDesistenciaProtesto.converter(converterStringParaVO(dados), erros);
		return arquivoDAO.salvar(arquivo, usuario, erros);
	}

	private StatusArquivo getStatusArquivo() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setSituacaoArquivo(SituacaoArquivo.ENVIADO);
		return status;
	}

	private TipoArquivo getTipoArquivoCancelamentoProtesto() {
		return tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO);
	}

	private Instituicao getCra() {
		return instituicaoDAO.buscarInstituicao(TipoInstituicaoCRA.CRA.toString());
	}

	private ArquivoDesistenciaProtestoVO converterStringParaVO(String dados) {
		JAXBContext context;
		ArquivoDesistenciaProtestoVO desistenciaVO = null;

		try {
			context = JAXBContext.newInstance(ArquivoDesistenciaProtestoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlRecebido = "";

			Scanner scanner = new Scanner(new ByteArrayInputStream(new String(dados).getBytes()));
			while (scanner.hasNext()) {
				xmlRecebido = xmlRecebido + scanner.nextLine().replaceAll("& ", "&amp;");
				if (xmlRecebido.contains("<?xml version=")) {
					xmlRecebido = xmlRecebido.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", "");
					xmlRecebido = xmlRecebido.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>", "");
				}
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlRecebido.getBytes());
			desistenciaVO = (ArquivoDesistenciaProtestoVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(e.getMessage(), e.getCause());
		}
		return desistenciaVO;
	}

	private CancelamentoSerproVO converterStringParaCancelamentoSerproVO(String dados) {
		JAXBContext context;
		CancelamentoSerproVO cancelamentoVO = null;

		try {
			context = JAXBContext.newInstance(CancelamentoSerproVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlRecebido = "";

			Scanner scanner = new Scanner(new ByteArrayInputStream(new String(dados).getBytes()));
			while (scanner.hasNext()) {
				xmlRecebido = xmlRecebido + scanner.nextLine().replaceAll("& ", "&amp;");
				if (xmlRecebido.contains("<?xml version=")) {
					xmlRecebido = xmlRecebido.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", "");
					xmlRecebido = xmlRecebido.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>", "");
				}
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlRecebido.getBytes());
			cancelamentoVO = (CancelamentoSerproVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(e.getMessage(), e.getCause());
		}
		return cancelamentoVO;
	}

	private RemessaCancelamentoProtesto converterCancelamentoSerpro(Arquivo arquivo, Instituicao instituicao, CancelamentoSerproVO cancelamentoSerpro,
			List<Exception> erros) {
		RemessaCancelamentoProtesto remessaCancelamento = new RemessaCancelamentoProtesto();
		remessaCancelamento.setArquivo(arquivo);
		remessaCancelamento.setCancelamentoProtesto(getCancelamentosProtesto(remessaCancelamento, cancelamentoSerpro));
		remessaCancelamento.setCabecalho(getCabecalhoArquivoCancelamento(instituicao));
		remessaCancelamento.setRodape(getRodapeArquivoCancelamento(instituicao));
		return remessaCancelamento;
	}

	private List<CancelamentoProtesto> getCancelamentosProtesto(RemessaCancelamentoProtesto remessaCancelamento, CancelamentoSerproVO cancelamentoSerpro) {
		List<CancelamentoProtesto> cancelamentos = new ArrayList<CancelamentoProtesto>();

		for (ComarcaDesistenciaCancelamentoSerproVO comarca : cancelamentoSerpro.getComarcaDesistenciaCancelamento()) {
			CancelamentoProtesto cancelamentoProtesto = new CancelamentoProtesto();
			cancelamentoProtesto.setRemessaCancelamentoProtesto(remessaCancelamento);

			CabecalhoCartorio cabecalhoCartorio = new CabecalhoCartorio();
			RodapeCartorio rodapeCartorio = new RodapeCartorio();
			List<PedidoCancelamento> pedidosCancelamento = new ArrayList<PedidoCancelamento>();
			for (CartorioDesistenciaCancelamentoSerproVO cartorio : comarca.getCartorioDesistenciaCancelamento()) {
				cabecalhoCartorio.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.HEADER_CARTORIO);
				cabecalhoCartorio.setCodigoCartorio(StringUtils.leftPad(cartorio.getCodigoCartorio(), 2, "0"));
				cabecalhoCartorio.setQuantidadeDesistencia(cartorio.getTituloDesistenciaCancelamento().size());
				cabecalhoCartorio.setCodigoMunicipio(comarca.getCodigoMunicipio());
				cabecalhoCartorio.setSequencialRegistro(StringUtils.leftPad(Integer.toString(2), 5, "0"));

				for (TituloDesistenciaCancelamentoSerproVO titulo : cartorio.getTituloDesistenciaCancelamento()) {
					PedidoCancelamento registro = new PedidoCancelamento();
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
					pedidosCancelamento.add(registro);
				}
				this.quantidadeDesistencias = getQuantidadeDesistencias() + cartorio.getTituloDesistenciaCancelamento().size();
				this.quantidadeRegistrosTipo2 = getQuantidadeRegistrosTipo2() + cartorio.getTituloDesistenciaCancelamento().size();

				rodapeCartorio.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.TRAILLER_CARTORIO);
				rodapeCartorio.setCodigoCartorio(cartorio.getCodigoCartorio());
				rodapeCartorio.setSomaTotalCancelamentoDesistencia(cartorio.getTituloDesistenciaCancelamento().size() * 2);
				rodapeCartorio.setSequencialRegistro(StringUtils.leftPad(Integer.toString(getSequenciaRegistro()), 5, "0"));

				cancelamentoProtesto.setCabecalhoCartorio(cabecalhoCartorio);
				cancelamentoProtesto.setCancelamentos(pedidosCancelamento);
				cancelamentoProtesto.setRodapeCartorio(rodapeCartorio);
			}
			cancelamentos.add(cancelamentoProtesto);
		}
		return cancelamentos;
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

	public List<TituloRemessa> buscarTitulosParaSolicitarCancelamento(TituloRemessa tituloRemessa, Instituicao bancoConvenio, Municipio municipio,
			Usuario usuario) {
		return cancelamentoDAO.buscarTitulosParaSolicitarCancelamento(tituloRemessa, bancoConvenio, municipio, usuario);
	}

	public TituloRemessa salvarSolicitacaoCancelamento(TituloRemessa titulo) {
		return cancelamentoDAO.salvarSolicitacaoCancelamento(titulo);
	}
}
