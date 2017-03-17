package br.com.ieptbto.cra.mediator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.arquivo.ConversorDesistenciaCancelamento;
import br.com.ieptbto.cra.conversor.arquivo.ConversorDesistenciaSerpro;
import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.DesistenciaDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.DesistenciaSerproVO;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class DesistenciaProtestoMediator extends BaseMediator {

	@Autowired
	ArquivoDAO arquivoDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;
	@Autowired
	TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	DesistenciaDAO desistenciaDAO;
	@Autowired
	ConversorDesistenciaCancelamento conversorDesistenciaProtesto;
	@Autowired
	ConversorDesistenciaSerpro conversorDesistenciaSerpro;

	@Transactional
	public RemessaDesistenciaProtesto buscarRemessaDesistenciaPorPK(RemessaDesistenciaProtesto remessaDesistenciaProtesto) {
		return desistenciaDAO.buscarPorPK(remessaDesistenciaProtesto, RemessaDesistenciaProtesto.class);
	}
	
	public DesistenciaProtesto buscarDesistenciaPorPK(Integer id) {
		return desistenciaDAO.buscarPorPK(id, DesistenciaProtesto.class);
	}

	public List<PedidoDesistencia> buscarPedidosDesistenciaProtesto(DesistenciaProtesto desistenciaProtesto) {
		return desistenciaDAO.buscarPedidosDesistenciaProtesto(desistenciaProtesto);
	}

	public List<PedidoDesistencia> buscarPedidosDesistenciaProtestoPorTitulo(TituloRemessa tituloRemessa) {
		return desistenciaDAO.buscarPedidosDesistenciaProtestoPorTitulo(tituloRemessa);
	}
	
	public List<DesistenciaProtesto> consultarDesistencias(String nomeArquivo, Instituicao bancoConvenio, Instituicao cartorio,
			LocalDate dataInicio, LocalDate dataFim, List<TipoArquivoFebraban> tiposArquivo, Usuario usuario) {
		return desistenciaDAO.consultarDesistencias(nomeArquivo, bancoConvenio, cartorio, dataInicio, dataFim, tiposArquivo, usuario);
	}

	/**
	 * @param nomeArquivo
	 * @param dados
	 * @param erros
	 * @param usuario
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Arquivo processarDesistencia(String nomeArquivo, String dados, List<Exception> erros, Usuario usuario) {
		Arquivo arquivo = new Arquivo();

		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.CRA_NACIONAL)) {
			arquivo = conversorDesistenciaProtesto.converterParaArquivo(converterStrinParaDesistenciaCancelamentoVO(dados), arquivo, erros);
		} else if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			arquivo = conversorDesistenciaSerpro.converterParaArquivo(usuario.getInstituicao(), converterParaDesistenciaSerproVO(dados), arquivo, erros);
		}
		arquivo.setNomeArquivo(nomeArquivo);
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setInstituicaoEnvio(usuario.getInstituicao());
		arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicaoPorNomeFantasia(TipoInstituicaoCRA.CRA.toString()));
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setTipoArquivo(tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO));
		arquivo.setStatusArquivo(getStatusArquivo());
		return arquivoDAO.salvar(arquivo, usuario, erros);
	}

	private StatusArquivo getStatusArquivo() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setStatusDownload(StatusDownload.ENVIADO);
		return status;
	}

	private ArquivoDesistenciaProtestoVO converterStrinParaDesistenciaCancelamentoVO(String dados) {
		JAXBContext context;
		ArquivoDesistenciaProtestoVO desistenciaVO = null;

		try {
			context = JAXBContext.newInstance(ArquivoDesistenciaProtestoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			dados = dados.replaceAll("& ", "&amp;");
			if (dados.contains("<?xml version=")) {
				dados = dados.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", "");
			}
			InputStream xml = new ByteArrayInputStream(dados.getBytes());
			desistenciaVO = (ArquivoDesistenciaProtestoVO) unmarshaller.unmarshal(new InputSource(xml));
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(e.getMessage(), e.getCause());
		}
		return desistenciaVO;
	}

	private DesistenciaSerproVO converterParaDesistenciaSerproVO(String dados) {
		JAXBContext context;
		DesistenciaSerproVO desistenciaVO = null;

		try {
			context = JAXBContext.newInstance(DesistenciaSerproVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			dados = dados.replaceAll("& ", "&amp;");
			if (dados.contains("<?xml version=")) {
				dados = dados.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", "");
				dados = dados.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>", "");
			}
			InputStream xml = new ByteArrayInputStream(dados.getBytes());
			desistenciaVO = (DesistenciaSerproVO) unmarshaller.unmarshal(new InputSource(xml));
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(e.getMessage(), e.getCause());
		}
		return desistenciaVO;
	}


	/**
	 * @param nomeArquivo
	 * @param instituicao
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Arquivo verificarDesistenciaJaEnviadaAnteriormente(String nomeArquivo, Instituicao instituicao) {
		return arquivoDAO.buscarArquivosPorNomeArquivoInstituicaoEnvio(instituicao, nomeArquivo);
	}
}