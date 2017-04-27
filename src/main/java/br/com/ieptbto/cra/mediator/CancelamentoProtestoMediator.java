package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.conversor.arquivo.ConversorCancelamentoSerpro;
import br.com.ieptbto.cra.conversor.arquivo.ConversorDesistenciaCancelamento;
import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.CancelamentoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.vo.ArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.CancelamentoSerproVO;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class CancelamentoProtestoMediator extends BaseMediator {

	@Autowired
	private ArquivoDAO arquivoDAO;
	@Autowired
	private CancelamentoDAO cancelamentoDAO;
	@Autowired
	private InstituicaoDAO instituicaoDAO;
	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	private ConversorDesistenciaCancelamento conversorArquivoDesistenciaProtesto;
	@Autowired
	private ConversorCancelamentoSerpro conversorCancelamentoSerpro;

	public RemessaCancelamentoProtesto buscarRemessaCancelamentoPorPK(RemessaCancelamentoProtesto remessaCancelamentoProtesto) {
		return cancelamentoDAO.buscarPorPK(remessaCancelamentoProtesto, RemessaCancelamentoProtesto.class);
	}

	public CancelamentoProtesto buscarCancelamentoPorPK(Integer id) {
		return cancelamentoDAO.buscarPorPK(id, CancelamentoProtesto.class);
	}

	public List<PedidoCancelamento> buscarPedidosCancelamentoProtestoPorTitulo(TituloRemessa titulo) {
		return cancelamentoDAO.buscarPedidosCancelamentoProtestoPorTitulo(titulo);
	}
	
	public List<PedidoCancelamento> buscarPedidosCancelamentoProtesto(CancelamentoProtesto cancelamentoProtesto) {
		return cancelamentoDAO.buscarPedidosCancelamentoProtesto(cancelamentoProtesto);
	}

	public List<CancelamentoProtesto> consultarCancelamentos(String nomeArquivo, Instituicao bancoConvenio, Instituicao cartorio,
			LocalDate dataInicio, LocalDate dataFim, List<TipoArquivoFebraban> tiposArquivo, Usuario usuario) {
		return cancelamentoDAO.consultarCancelamentos(nomeArquivo, bancoConvenio, cartorio, dataInicio, dataFim, tiposArquivo, usuario);
	}

	/**
	 * @param nomeArquivo
	 * @param dados
	 * @param erros
	 * @param usuario
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Arquivo processarCancelamento(String nomeArquivo, String dados, List<Exception> erros, Usuario usuario) {
		Arquivo arquivo = new Arquivo();
		arquivo.setNomeArquivo(nomeArquivo);
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setInstituicaoEnvio(usuario.getInstituicao());
		arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicaoPorNomeFantasia(TipoInstituicaoCRA.CRA.toString()));
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setRemessas(new ArrayList<Remessa>());
		arquivo.setTipoArquivo(tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO));
		arquivo.setStatusArquivo(getStatusArquivo());

		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			arquivo = conversorCancelamentoSerpro.converterParaArquivo(usuario.getInstituicao(), converterParaCancelamentoSerproVO(dados), arquivo, erros);
			return cancelamentoDAO.salvarCancelamento(arquivo, usuario, erros);
		}
		arquivo = conversorArquivoDesistenciaProtesto.converterParaArquivo(converterStringParaVO(dados), arquivo, erros);
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

	private CancelamentoSerproVO converterParaCancelamentoSerproVO(String dados) {
		JAXBContext context;
		CancelamentoSerproVO cancelamentoVO = null;

		try {
			context = JAXBContext.newInstance(CancelamentoSerproVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			dados = dados.replaceAll("& ", "&amp;");
			if (dados.contains("<?xml version=")) {
				dados = dados.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", "");
				dados = dados.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>", "");
			}
			InputStream xml = new ByteArrayInputStream(dados.getBytes());
			cancelamentoVO = (CancelamentoSerproVO) unmarshaller.unmarshal(new InputSource(xml));
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(e.getMessage(), e.getCause());
		}
		return cancelamentoVO;
	}
}