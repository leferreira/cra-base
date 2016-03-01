package br.com.ieptbto.cra.mediator;

import java.io.ByteArrayInputStream;
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

import br.com.ieptbto.cra.conversor.arquivo.ConversorCancelamentoProtesto;
import br.com.ieptbto.cra.conversor.arquivo.ConversorDesistenciaProtesto;
import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.DesistenciaDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoArquivo;
import br.com.ieptbto.cra.entidade.CabecalhoCartorio;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.RodapeArquivo;
import br.com.ieptbto.cra.entidade.RodapeCartorio;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.CartorioDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.entidade.vo.ComarcaDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.entidade.vo.DesistenciaSerproVO;
import br.com.ieptbto.cra.entidade.vo.RemessaDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.TituloDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class DesistenciaProtestoMediator {

	protected static final Logger logger = Logger.getLogger(DesistenciaProtestoMediator.class);
	@Autowired
	private ConversorDesistenciaProtesto conversorArquivoDesistenciaProtesto;
	@Autowired
	private ArquivoDAO arquivoDAO;
	@Autowired
	private InstituicaoDAO instituicaoDAO;
	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	private DesistenciaDAO desistenciaDAO;
	
	private int sequenciaRegistro = 2;
	private int quantidadeDesistencias = 0;
	private int quantidadeRegistrosTipo2 = 0;
	private BigDecimal somatorioValor;
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Arquivo processarDesistencia(String nomeArquivo, LayoutPadraoXML layoutPadraoXML, String dados, List<Exception> erros, Usuario usuario) {
		Arquivo arquivo = new Arquivo();
		
		if (layoutPadraoXML.equals(LayoutPadraoXML.CRA_NACIONAL)) {
			arquivo = conversorArquivoDesistenciaProtesto.converter(converterStringParaVO(dados), erros);
		} else if (layoutPadraoXML.equals(LayoutPadraoXML.SERPRO)){
			DesistenciaSerproVO desistenciaCancelamentoSerpro = converterStringParaDesistenciaCancelamentoSerproVO(dados);
			RemessaDesistenciaProtesto remessaDesistencia = converterDesistenciaCancelamentoSerpro(arquivo ,usuario.getInstituicao(), desistenciaCancelamentoSerpro, erros);
			arquivo.setRemessaDesistenciaProtesto(remessaDesistencia);
		}
		arquivo.setNomeArquivo(nomeArquivo);
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setInstituicaoEnvio(usuario.getInstituicao());
		arquivo.setInstituicaoRecebe(getCra());
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setTipoArquivo(getTipoArquivoDesistenciaProtesto());
		arquivo.setStatusArquivo(getStatusArquivo());
		return arquivoDAO.salvar(arquivo, usuario, erros);
	}

	private StatusArquivo getStatusArquivo() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setSituacaoArquivo(SituacaoArquivo.ENVIADO);
		return status;
	}

	private TipoArquivo getTipoArquivoDesistenciaProtesto() {
		return tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO);
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
	
	private DesistenciaSerproVO converterStringParaDesistenciaCancelamentoSerproVO(String dados) {
		JAXBContext context;
		DesistenciaSerproVO desistenciaVO = null;

		try {
			context = JAXBContext.newInstance(DesistenciaSerproVO.class);
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
			desistenciaVO = (DesistenciaSerproVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(e.getMessage(), e.getCause());
		}
		return desistenciaVO;
	}
	
	private RemessaDesistenciaProtesto converterDesistenciaCancelamentoSerpro(Arquivo arquivo, Instituicao instituicao, 
			DesistenciaSerproVO desistenciaCancelamentoSerpro, List<Exception> erros) {
		RemessaDesistenciaProtesto remessaDesistencia = new RemessaDesistenciaProtesto();
		remessaDesistencia.setArquivo(arquivo);
		remessaDesistencia.setDesistenciaProtesto(getDesistenciasCancelamentosProtesto(remessaDesistencia ,desistenciaCancelamentoSerpro));
		remessaDesistencia.setCabecalho(getCabecalhoArquivoDesistenciaCancelamento(instituicao));
		remessaDesistencia.setRodape(getRodapeArquivoDesistenciaCancelamento(instituicao));
		return remessaDesistencia;
	}
	
	private List<DesistenciaProtesto> getDesistenciasCancelamentosProtesto(RemessaDesistenciaProtesto remessaDesistencia, DesistenciaSerproVO desistenciaCancelamentoSerpro) {
		List<DesistenciaProtesto> desistencias = new ArrayList<DesistenciaProtesto>();
		
		for (ComarcaDesistenciaCancelamentoSerproVO comarca : desistenciaCancelamentoSerpro.getComarcaDesistenciaCancelamento()) {
			DesistenciaProtesto desistenciaProtesto = new DesistenciaProtesto();
			desistenciaProtesto.setRemessaDesistenciaProtesto(remessaDesistencia);
			
			CabecalhoCartorio cabecalhoCartorio = new CabecalhoCartorio();
			RodapeCartorio rodapeCartorio = new RodapeCartorio();
			List<PedidoDesistencia> pedidosDesistencia = new ArrayList<PedidoDesistencia>();
			for (CartorioDesistenciaCancelamentoSerproVO cartorio : comarca.getCartorioDesistenciaCancelamento()) {
				cabecalhoCartorio.setIdentificacaoRegistro(TipoRegistroDesistenciaProtesto.HEADER_CARTORIO);
				cabecalhoCartorio.setCodigoCartorio(cartorio.getCodigoCartorio());
				cabecalhoCartorio.setQuantidadeDesistencia(cartorio.getTituloDesistenciaCancelamento().size());
				cabecalhoCartorio.setCodigoMunicipio(comarca.getCodigoMunicipio());
				cabecalhoCartorio.setSequencialRegistro(StringUtils.leftPad(Integer.toString(2), 5, "0"));
				
				for (TituloDesistenciaCancelamentoSerproVO titulo : cartorio.getTituloDesistenciaCancelamento()){
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
				rodapeCartorio.setSomaTotalCancelamentoDesistencia(cartorio.getTituloDesistenciaCancelamento().size()*2);
				rodapeCartorio.setSequencialRegistro(StringUtils.leftPad(Integer.toString(getSequenciaRegistro()), 5, "0"));

				desistenciaProtesto.setCabecalhoCartorio(cabecalhoCartorio);
				desistenciaProtesto.setDesistencias(pedidosDesistencia);
				desistenciaProtesto.setRodapeCartorio(rodapeCartorio);
			}
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
	
	private CabecalhoArquivo getCabecalhoArquivoDesistenciaCancelamento(Instituicao instituicaoDesistenciaCancelamento){
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

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Arquivo verificarDesistenciaJaEnviadaAnteriormente(String nomeArquivo, Instituicao instituicao) {
		return arquivoDAO.buscarArquivosPorNomeArquivoInstituicaoEnvio(instituicao, nomeArquivo);
	}
	
	@Transactional
	public RemessaDesistenciaProtestoVO buscarDesistenciaCancelamentoCartorio(Instituicao cartorio, String nome) {
		Arquivo arquivo = desistenciaDAO.buscarDesistenciaCancelamentoCartorio(cartorio, nome);
		if (arquivo == null) {
			return null;
		} 
		
		if (arquivo.getRemessaDesistenciaProtesto() != null) {
			return new ConversorDesistenciaProtesto().converter(arquivo.getRemessaDesistenciaProtesto());
		} else if (arquivo.getRemessaCancelamentoProtesto() != null) {
			return new ConversorCancelamentoProtesto().converter(arquivo.getRemessaCancelamentoProtesto());
		} else if (arquivo.getRemessaAutorizacao() != null) {
			return new ConversorCancelamentoProtesto().converter(arquivo.getRemessaAutorizacao());
		}
		return null;
	}
}
