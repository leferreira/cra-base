package br.com.ieptbto.cra.fabrica;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.AbstractFabricaDeArquivo;
import br.com.ieptbto.cra.conversor.arquivo.ConversorRemessaArquivo;
import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.vo.ArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoCampo51;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class FabricaDeArquivoXML extends AbstractFabricaDeArquivo {

	@Autowired
	private InstituicaoMediator instituicaoMediator;
	@Autowired
	private TipoArquivoMediator tipoArquivoMediator;
	@Autowired
	private ConversorRemessaArquivo conversorRemessaArquivo;

	public Arquivo converter(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		if (TipoArquivoEnum.REMESSA.equals(arquivo.getTipoArquivo().getTipoArquivo())) {
			return converterRemessa(arquivoFisico, arquivo, erros);
		} else if (TipoArquivoEnum.CONFIRMACAO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {
			return converterConfirmacao(arquivoFisico, arquivo, erros);
		} else if (TipoArquivoEnum.RETORNO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {
			return converterRetorno(arquivoFisico, arquivo, erros);
		} else if (TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {
			return converterDesistenciaProtesto(arquivoFisico, arquivo, erros);
		} else if (TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {

		} else if (TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO.equals(arquivo.getTipoArquivo().getTipoArquivo())) {

		}
		return null;
	}

	private Arquivo converterDesistenciaProtesto(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		JAXBContext context;
		ArquivoDesistenciaProtestoVO arquivoVO = new ArquivoDesistenciaProtestoVO();
		try {
			context = JAXBContext.newInstance(ArquivoDesistenciaProtestoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlGerado = "";
			Scanner scanner = new Scanner(new FileInputStream(arquivoFisico));
			while (scanner.hasNext()) {
				xmlGerado = xmlGerado + scanner.nextLine().replaceAll("& ", "&amp;");
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlGerado.getBytes());
			arquivoVO = (ArquivoDesistenciaProtestoVO) unmarshaller.unmarshal(new InputSource(xml));
			arquivoVO.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO));
		} catch (JAXBException e) {
			System.out.println(e.getMessage());
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		}
		return conversorRemessaArquivo.converter(arquivoVO, arquivo, erros);
	}

	private Arquivo converterRetorno(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		JAXBContext context;
		ArquivoVO arquivoVO = new ArquivoVO();
		try {
			context = JAXBContext.newInstance(ArquivoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlGerado = "";
			Scanner scanner = new Scanner(new FileInputStream(arquivoFisico));
			while (scanner.hasNext()) {

				xmlGerado = xmlGerado + scanner.nextLine().replaceAll("& ", "&amp;");
				xmlGerado = xmlGerado.replaceAll("retorno", "remessa").trim();
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlGerado.getBytes());
			arquivoVO = (ArquivoVO) unmarshaller.unmarshal(new InputSource(xml));
			arquivoVO.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.RETORNO));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		}
		return conversorRemessaArquivo.converter(arquivoVO, arquivo, erros);
	}

	private Arquivo converterConfirmacao(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		JAXBContext context;
		ArquivoVO arquivoVO = new ArquivoVO();
		try {
			context = JAXBContext.newInstance(ArquivoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlGerado = "";
			Scanner scanner = new Scanner(new FileInputStream(arquivoFisico));
			while (scanner.hasNext()) {

				xmlGerado = xmlGerado + scanner.nextLine().replaceAll("& ", "&amp;");
				xmlGerado = xmlGerado.replaceAll("confirmacao", "remessa").trim();
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlGerado.getBytes());
			arquivoVO = (ArquivoVO) unmarshaller.unmarshal(new InputSource(xml));
			arquivoVO.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.CONFIRMACAO));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		}
		return conversorRemessaArquivo.converter(arquivoVO, arquivo, erros);
	}

	private Arquivo converterRemessa(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		JAXBContext context;
		ArquivoVO arquivoVO = new ArquivoVO();
		try {
			context = JAXBContext.newInstance(ArquivoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlGerado = "";
			Scanner scanner = new Scanner(new FileInputStream(arquivoFisico));
			while (scanner.hasNext()) {

				xmlGerado = xmlGerado + scanner.nextLine().replaceAll("& ", "&amp;");
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlGerado.getBytes());
			arquivoVO = (ArquivoVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException(CodigoErro.CRA_ARQUIVO_CORROMPIDO.getDescricao());
		}
		return conversorRemessaArquivo.converter(arquivoVO, arquivo, erros);
	}

	public void fabrica(List<RemessaVO> arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		this.arquivoVO = arquivoFisico;
		this.arquivo = arquivo;
		this.erros = erros;
		converter();
	}

	public Arquivo converter() {
		for (RemessaVO remessaVO : getArquivoVO()) {
			Remessa remessa = new Remessa();
			remessa.setArquivo(getArquivo());
			remessa.setCabecalho(getCabecalho(remessaVO.getCabecalho()));
			remessa.getCabecalho().setRemessa(remessa);
			remessa.setRodape(getRodape(remessaVO.getRodape()));
			remessa.getRodape().setRemessa(remessa);
			remessa.setArquivo(getArquivo());
			remessa.setInstituicaoDestino(getInstituicaoDestino(remessaVO.getCabecalho()));
			remessa.setInstituicaoOrigem(getArquivo().getInstituicaoEnvio());
			remessa.setDataRecebimento(getDataRecebimento(remessaVO.getCabecalho().getDataMovimento()));
			remessa.setTitulos(getTitulos(remessaVO.getTitulos(), remessa));
			getArquivo().getRemessas().add(remessa);
		}
		return getArquivo();
	}

	private LocalDate getDataRecebimento(String dataMovimento) {
		if (dataMovimento.equals("00000000") || dataMovimento == null) {
			return new LocalDate();
		}
		return DataUtil.stringToLocalDate(DataUtil.PADRAO_FORMATACAO_DATA_DDMMYYYY, dataMovimento);
	}

	@SuppressWarnings("rawtypes")
	private List<Titulo> getTitulos(List<TituloVO> titulosVO, Remessa remessa) {
		List<Titulo> titulos = new ArrayList<Titulo>();
		Titulo titulo = null;
		for (TituloVO tituloVO : titulosVO) {
			if (TipoArquivoEnum.REMESSA.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
				titulo = TituloRemessa.parseTituloVO(tituloVO);
				verificarAnexoComplementoRegistro(remessa.getInstituicaoOrigem(), TituloRemessa.class.cast(titulo), tituloVO);
			} else if (TipoArquivoEnum.CONFIRMACAO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
				titulo = Confirmacao.parseTituloVO(tituloVO);
			} else if (TipoArquivoEnum.RETORNO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
				titulo = Retorno.parseTituloVO(tituloVO);
			}

			titulo.setRemessa(remessa);
			titulos.add(titulo);
		}
		return titulos;
	}

	private void verificarAnexoComplementoRegistro(Instituicao instituicaoEnvio, TituloRemessa titulo, TituloVO tituloVO) {
		if (instituicaoEnvio.getTipoCampo51().equals(TipoCampo51.DOCUMENTOS_COMPACTADOS)) {
			if (tituloVO.getComplementoRegistro() != null) {
				if (!tituloVO.getComplementoRegistro().trim().equals(StringUtils.EMPTY)) {
					titulo.setComplementoRegistro(tituloVO.getComplementoRegistro());

					Anexo anexoArquivo = new Anexo();
					anexoArquivo.setTitulo(titulo);
					anexoArquivo.setDocumentoAnexo(tituloVO.getComplementoRegistro());

					titulo.setAnexo(anexoArquivo);
					titulo.setComplementoRegistro(StringUtils.EMPTY);
				}
			}
		}
	}

	private Instituicao getInstituicaoDestino(CabecalhoVO cabecalho) {
		if (TipoArquivoEnum.REMESSA.equals(TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getNomeArquivo()))) {
			return instituicaoMediator.getCartorioPorCodigoIBGE(cabecalho.getCodigoMunicipio());
		} else if (TipoArquivoEnum.CONFIRMACAO.equals(TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getNomeArquivo()))
				|| TipoArquivoEnum.RETORNO.equals(TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getNomeArquivo()))) {
			return instituicaoMediator.getInstituicaoPorCodigoPortador(cabecalho.getNumeroCodigoPortador());
		}
		return null;
	}

	private Rodape getRodape(RodapeVO rodapeVO) {
		return Rodape.parseRodapeVO(rodapeVO);
	}

	private CabecalhoRemessa getCabecalho(CabecalhoVO cabecalhoVO) {
		return CabecalhoRemessa.parseCabecalhoVO(cabecalhoVO);
	}

	public List<RemessaVO> getArquivoVO() {
		return arquivoVO;
	}

	public void setArquivoVO(List<RemessaVO> arquivoVO) {
		this.arquivoVO = arquivoVO;
	}
}