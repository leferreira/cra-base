package br.com.ieptbto.cra.mediator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
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

import br.com.ieptbto.cra.conversor.arquivo.ConversorAutorizacaoSerpro;
import br.com.ieptbto.cra.conversor.arquivo.ConversorDesistenciaCancelamento;
import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.AutorizacaoCancelamentoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.AutorizacaoCancelamentoSerproVO;
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
public class AutorizacaoCancelamentoMediator extends BaseMediator {

	@Autowired
	ArquivoDAO arquivoDAO;
	@Autowired
	AutorizacaoCancelamentoDAO autorizacaoCancelamentoDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;
	@Autowired
	TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	ConversorDesistenciaCancelamento conversorDesistenciaCancelamento;
	@Autowired
	ConversorAutorizacaoSerpro conversorAutorizacaoSerpro;
	
	public RemessaAutorizacaoCancelamento buscarRemessaAutorizacaoPorPK(RemessaAutorizacaoCancelamento remessaAutorizacao) {
		return autorizacaoCancelamentoDAO.buscarPorPK(remessaAutorizacao, RemessaAutorizacaoCancelamento.class);
	}
	
	public AutorizacaoCancelamento buscarAutorizacaoPorPK(Integer id) {
		return autorizacaoCancelamentoDAO.buscarPorPK(id, AutorizacaoCancelamento.class);
	}

	public List<PedidoAutorizacaoCancelamento> buscarPedidosAutorizacaoCancelamentoPorTitulo(TituloRemessa titulo) {
		return autorizacaoCancelamentoDAO.buscarPedidosAutorizacaoCancelamentoPorTitulo(titulo);
	}

	public List<PedidoAutorizacaoCancelamento> buscarPedidosAutorizacaoCancelamento(AutorizacaoCancelamento autorizacaoCancelamento) {
		return autorizacaoCancelamentoDAO.buscarPedidosAutorizacaoCancelamento(autorizacaoCancelamento);
	}

	public List<AutorizacaoCancelamento> consultarAutorizacoes(String nomeArquivo, Instituicao bancoConvenio, Instituicao cartorio,
			LocalDate dataInicio, LocalDate dataFim, List<TipoArquivoFebraban> tiposArquivo, Usuario user) {
		return autorizacaoCancelamentoDAO.consultarAutorizacoes(nomeArquivo, bancoConvenio, cartorio, dataInicio, dataFim, tiposArquivo, user);
	}

	/**
	 * Processar arquivo de Autorização de Cancelamento via WS
	 * 
	 * @param nomeArquivo
	 * @param dados
	 * @param erros
	 * @param usuario
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Arquivo processarAutorizacaoCancelamento(String nomeArquivo, String dados, List<Exception> erros, Usuario usuario) {
		Arquivo arquivo = new Arquivo();
		arquivo.setNomeArquivo(nomeArquivo);
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setInstituicaoEnvio(usuario.getInstituicao());
		arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicaoPorNomeFantasia(TipoInstituicaoCRA.CRA.toString()));
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setRemessas(new ArrayList<Remessa>());
		arquivo.setTipoArquivo(tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO));
		arquivo.setStatusArquivo(getStatusArquivo());

		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			arquivo = conversorAutorizacaoSerpro.converterParaArquivo(usuario.getInstituicao(), converterParaAutorizacaoSerproVO(dados), arquivo, erros);
			return autorizacaoCancelamentoDAO.salvarAutorizacao(arquivo, usuario, erros);
		}
		arquivo = conversorDesistenciaCancelamento.converterParaArquivo(converterStringParaVO(dados), arquivo, erros);
		return arquivoDAO.salvar(arquivo, usuario, erros);
	}

	private StatusArquivo getStatusArquivo() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setStatusDownload(StatusDownload.ENVIADO);
		return status;
	}

	private ArquivoDesistenciaProtestoVO converterStringParaVO(String dados) {
		JAXBContext context;
		ArquivoDesistenciaProtestoVO desistenciaVO = null;

		try {
			context = JAXBContext.newInstance(ArquivoDesistenciaProtestoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			dados = dados.replaceAll("& ", "&amp;");
			if (dados.contains("<?xml version=")) {
				dados = dados.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", "");
				dados = dados.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>", "");
			}
			InputStream xml = new ByteArrayInputStream(dados.getBytes());
			desistenciaVO = (ArquivoDesistenciaProtestoVO) unmarshaller.unmarshal(new InputSource(xml));
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(e.getMessage(), e.getCause());
		}
		return desistenciaVO;
	}

	private AutorizacaoCancelamentoSerproVO converterParaAutorizacaoSerproVO(String dados) {
		JAXBContext context;
		AutorizacaoCancelamentoSerproVO acVO = null;

		try {
			context = JAXBContext.newInstance(AutorizacaoCancelamentoSerproVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			dados = dados.replaceAll("& ", "&amp;");
			if (dados.contains("<?xml version=")) {
				dados = dados.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", "");
				dados = dados.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>", "");
			}
			InputStream xml = new ByteArrayInputStream(dados.getBytes());
			acVO = (AutorizacaoCancelamentoSerproVO) unmarshaller.unmarshal(new InputSource(xml));
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(e.getMessage(), e.getCause());
		}
		return acVO;
	}
}