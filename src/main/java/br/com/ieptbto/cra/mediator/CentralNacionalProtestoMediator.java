package br.com.ieptbto.cra.mediator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.conversor.ConversorArquivoCnpVO;
import br.com.ieptbto.cra.dao.CentralNancionalProtestoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.MunicipioDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.ArquivoCnp;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.RemessaCnp;
import br.com.ieptbto.cra.entidade.RodapeCnp;
import br.com.ieptbto.cra.entidade.TituloCnp;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoCnpVO;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.validacao.FabricaValidacaoCNP;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class CentralNacionalProtestoMediator {

	protected static final Logger logger = Logger.getLogger(CentralNacionalProtestoMediator.class);

	@Autowired
	CentralNancionalProtestoDAO centralNancionalProtestoDAO;
	@Autowired
	TituloDAO tituloDAO;
	@Autowired
	FabricaValidacaoCNP fabricaValidacaoCNP;
	@Autowired
	MunicipioDAO municipioDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ArquivoCnpVO gerarArquivoNacional() {
		ArquivoCnp arquivoCnp = new ArquivoCnp();
		arquivoCnp.setRemessasCnp(centralNancionalProtestoDAO.buscarRemessasCnpPendentes());
		ArquivoCnpVO arquivoCnpVO = new ArquivoCnpVO();
		arquivoCnpVO.setRemessasCnpVO(ConversorArquivoCnpVO.converterParaRemessaCnpNacionalVO(arquivoCnp.getRemessasCnp()));

		centralNancionalProtestoDAO.salvarArquivoCnpNacional(arquivoCnp);
		return arquivoCnpVO;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ArquivoCnpVO buscarArquivoNacionalPorData(LocalDate dataLiberacao) {
		ArquivoCnp arquivoCnp = new ArquivoCnp();
		arquivoCnp.setRemessasCnp(centralNancionalProtestoDAO.buscarRemessasCnpPorData(dataLiberacao));

		ArquivoCnpVO arquivoCnpVO = new ArquivoCnpVO();
		arquivoCnpVO.setRemessasCnpVO(ConversorArquivoCnpVO.converterParaRemessaCnpVO(arquivoCnp.getRemessasCnp()));
		return arquivoCnpVO;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ArquivoCnp processarArquivoCartorio(Usuario usuario, ArquivoCnpVO arquivoCnpVO) {
		ArquivoCnp arquivoCnp = new ArquivoCnp();
		arquivoCnp.setDataEnvio(new LocalDate());
		arquivoCnp.setInstituicaoEnvio(usuario.getInstituicao());
		arquivoCnp.setRemessasCnp(ConversorArquivoCnpVO.converterParaRemessaCnp(arquivoCnpVO));

		fabricaValidacaoCNP.validarArquivoCnpCartorio(arquivoCnp);
		return centralNancionalProtestoDAO.salvarArquivoCartorioCentralNacionalProtesto(usuario, arquivoCnp);
	}

	public boolean isInstituicaoEnviouArquivoCnpHoje(Instituicao instituicao) {
		ArquivoCnp arquivoCnp = centralNancionalProtestoDAO.getArquivoCnpHojeInstituicao(instituicao);
		if (arquivoCnp != null) {
			return true;
		}
		return false;
	}

	public boolean isArquivoJaDisponibilizadoConsultaPorData(LocalDate dataLiberacao) {
		RemessaCnp remessaCnp = centralNancionalProtestoDAO.isArquivoJaDisponibilizadoConsultaPorData(dataLiberacao);
		if (remessaCnp != null) {
			return true;
		}
		return false;
	}

	public List<String> consultarProtestos(String documentoDevedor) {
		List<String> municipiosComProtesto = new ArrayList<String>();
		List<TituloCnp> titulosProtestados = centralNancionalProtestoDAO.consultarProtestos(documentoDevedor);

		for (TituloCnp titulo : titulosProtestados) {
			TituloCnp tituloCancelamento = centralNancionalProtestoDAO.consultarCancelamento(documentoDevedor, titulo.getNumeroProtocoloCartorio());

			if (tituloCancelamento == null) {
				Municipio municipio =
						centralNancionalProtestoDAO.carregarMunicipioCartorio(titulo.getRemessa().getArquivo().getInstituicaoEnvio().getMunicipio());
				if (!municipiosComProtesto.contains(municipio.getNomeMunicipio().toUpperCase())) {
					municipiosComProtesto.add(municipio.getNomeMunicipio().toUpperCase());
				}
			}
		}
		return municipiosComProtesto;
	}

	public List<Instituicao> consultarProtestosWs(String documentoDevedor) {
		List<Instituicao> cartorios = new ArrayList<Instituicao>();
		List<TituloCnp> titulosProtestados = centralNancionalProtestoDAO.consultarProtestos(documentoDevedor);

		for (TituloCnp titulo : titulosProtestados) {
			TituloCnp tituloCancelamento = centralNancionalProtestoDAO.consultarCancelamento(documentoDevedor, titulo.getNumeroProtocoloCartorio());

			if (tituloCancelamento == null) {
				if (!cartorios.contains(titulo.getRemessa().getArquivo().getInstituicaoEnvio())) {
					Municipio municipio = centralNancionalProtestoDAO
							.carregarMunicipioCartorio(titulo.getRemessa().getArquivo().getInstituicaoEnvio().getMunicipio());
					titulo.getRemessa().getArquivo().getInstituicaoEnvio().setMunicipio(municipio);
					cartorios.add(titulo.getRemessa().getArquivo().getInstituicaoEnvio());
				}
			}
		}
		return cartorios;
	}

	public int buscarSequencialCabecalhoCnp(String codigoMunicipio) {
		return centralNancionalProtestoDAO.buscarSequencialCabecalhoCnp(codigoMunicipio);
	}

	public List<Instituicao> consultarCartoriosCentralNacionalProtesto() {
		return centralNancionalProtestoDAO.consultarCartoriosCentralNacionalProtesto();
	}

	public void gerarArquivo5AnosTocantins() {
		File diretorioBase = new File(ConfiguracaoBase.DIRETORIO_BASE);
		if (!diretorioBase.exists()) {
			diretorioBase.mkdirs();
		}

		List<Municipio> municipios = municipioDAO.listarTodosTocantins();
		for (final Municipio municipio : municipios) {

			if (municipio.getId() == 14 || municipio.getId() == 11 || municipio.getId() == 20 || municipio.getId() == 10) {
				logger.info("==========================================================");
				logger.info(municipio.getNomeMunicipio() + " => [id=" + municipio.getId() + "] [codigoIbge=" + municipio.getCodigoIBGE()
						+ "]  deverá ser processado separadamente...");

			} else {
				File arquivo = new File(ConfiguracaoBase.DIRETORIO_BASE + municipio.getNomeMunicipio());
				if (arquivo.exists()) {
					logger.info("==========================================================");
					logger.info("Municipio:  " + municipio.getNomeMunicipio() + "   -   Arquivo já criado!");
				} else {

					RemessaCnp remessaCnp = new RemessaCnp();
					remessaCnp.setCabecalho(centralNancionalProtestoDAO.ultimoCabecalhoCnpCartorio(municipio));
					remessaCnp.setTitulos(centralNancionalProtestoDAO.buscarTitulosPorMunicipio(municipio));
					RodapeCnp rod = new RodapeCnp();
					rod.setCodigoRegistro("9");
					remessaCnp.setRodape(rod);

					if (!remessaCnp.getTitulos().isEmpty()) {
						logger.info("==========================================================");
						logger.info("Municipio:  " + municipio.getNomeMunicipio() + "  -  Qtd. Títulos:  " + remessaCnp.getTitulos().size());

						ArquivoCnp arquivoCnp = new ArquivoCnp();
						arquivoCnp.setRemessasCnp(new ArrayList<RemessaCnp>());
						arquivoCnp.getRemessasCnp().add(remessaCnp);

						ArquivoCnpVO arquivoCnpVO = new ArquivoCnpVO();
						arquivoCnpVO.setRemessasCnpVO(ConversorArquivoCnpVO.converterParaRemessaCnpVO5Anos(arquivoCnp.getRemessasCnp()));
						try {
							Writer writer = new StringWriter();
							JAXBContext context;
							context = JAXBContext.newInstance(arquivoCnpVO.getClass());

							Marshaller marshaller = context.createMarshaller();
							marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
							marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
							JAXBElement<Object> element = new JAXBElement<Object>(new QName("cnp"), Object.class, arquivoCnpVO);
							marshaller.marshal(element, writer);
							String msg = writer.toString();
							msg = msg.replace(" xsi:type=\"arquivoCnpVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
							writer.flush();
							writer.close();
							BufferedWriter bWrite = new BufferedWriter(new FileWriter(arquivo));
							logger.info("Escrevendo os dados no arquivo...");
							bWrite.write(msg);
							bWrite.flush();
							bWrite.close();
							logger.info("  ");
							logger.info("Arquivo 5 Anos gerado com sucesso!");
							logger.info("  ");
						} catch (JAXBException e) {
							logger.error(e.getMessage(), e.getCause());
							new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
						} catch (IOException e) {
							logger.error(e.getMessage(), e.getCause());
							new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
						}
					}
				}
			}
		}
	}

	public void gerarArquivo5AnosPorMunicipio(String municipioParametro) {
		File diretorioBase = new File(ConfiguracaoBase.DIRETORIO_BASE);
		if (!diretorioBase.exists()) {
			diretorioBase.mkdirs();
		}

		Municipio municipio = municipioDAO.buscaMunicipioPorCodigoIBGE(municipioParametro);
		if (municipio == null) {
			logger.info("Municipio com o código " + municipioParametro + " não encontrado!");
		} else {
			RemessaCnp remessaCnp = new RemessaCnp();
			remessaCnp.setCabecalho(centralNancionalProtestoDAO.ultimoCabecalhoCnpCartorio(municipio));
			remessaCnp.setTitulos(centralNancionalProtestoDAO.buscarTitulosPorMunicipio(municipio));
			RodapeCnp rod = new RodapeCnp();
			rod.setCodigoRegistro("9");
			remessaCnp.setRodape(rod);

			if (!remessaCnp.getTitulos().isEmpty()) {
				logger.info("==========================================================");
				logger.info("Municipio:  " + municipio.getNomeMunicipio() + "  -  Qtd. Títulos:  " + remessaCnp.getTitulos().size());

				ArquivoCnp arquivoCnp = new ArquivoCnp();
				arquivoCnp.setRemessasCnp(new ArrayList<RemessaCnp>());
				arquivoCnp.getRemessasCnp().add(remessaCnp);

				ArquivoCnpVO arquivoCnpVO = new ArquivoCnpVO();
				arquivoCnpVO.setRemessasCnpVO(ConversorArquivoCnpVO.converterParaRemessaCnpVO5Anos(arquivoCnp.getRemessasCnp()));
				try {
					Writer writer = new StringWriter();
					JAXBContext context;
					context = JAXBContext.newInstance(arquivoCnpVO.getClass());

					Marshaller marshaller = context.createMarshaller();
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
					JAXBElement<Object> element = new JAXBElement<Object>(new QName("cnp"), Object.class, arquivoCnpVO);
					marshaller.marshal(element, writer);
					String msg = writer.toString();
					msg = msg.replace(" xsi:type=\"arquivoCnpVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
					writer.flush();
					writer.close();
					File arquivo = new File(ConfiguracaoBase.DIRETORIO_BASE + municipio.getNomeMunicipio());
					BufferedWriter bWrite = new BufferedWriter(new FileWriter(arquivo));
					logger.info("Escrevendo os dados no arquivo...");
					bWrite.write(msg);
					bWrite.flush();
					bWrite.close();
					logger.info("  ");
					logger.info("Arquivo 5 Anos gerado com sucesso!");
					logger.info("  ");
				} catch (JAXBException e) {
					logger.error(e.getMessage(), e.getCause());
					new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
				} catch (IOException e) {
					logger.error(e.getMessage(), e.getCause());
					new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
				}
			}
		}
	}
}