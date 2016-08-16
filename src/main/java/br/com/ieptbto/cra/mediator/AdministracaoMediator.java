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

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.cnp.ConversorCnpVO;
import br.com.ieptbto.cra.dao.AdministracaoDAO;
import br.com.ieptbto.cra.dao.CentralNancionalProtestoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.LoteCnp;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.RegistroCnp;
import br.com.ieptbto.cra.entidade.vo.ArquivoCnpVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoCnpVO;
import br.com.ieptbto.cra.entidade.vo.RemessaCnpVO;
import br.com.ieptbto.cra.entidade.vo.RodapeCnpVO;
import br.com.ieptbto.cra.entidade.vo.TituloCnpVO;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoRegistro;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;

@Service
public class AdministracaoMediator extends BaseMediator {

	@Autowired
	private AdministracaoDAO administracaoDAO;
	@Autowired
	private InstituicaoDAO instituicaoDAO;
	@Autowired
	private CentralNancionalProtestoDAO centralNancionalProtestoDAO;

	public List<Arquivo> buscarArquivosParaRemover(Arquivo arquivo, Municipio municipio, LocalDate dataInicio, LocalDate dataFim,
			ArrayList<TipoArquivoEnum> tiposArquivo) {
		return administracaoDAO.buscarArquivosRemover(arquivo, tiposArquivo, municipio, dataInicio, dataFim);
	}

	public void removerArquivo(Arquivo arquivo) {
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
			administracaoDAO.removerRemessa(arquivo);
		}
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
			if (arquivo.getInstituicaoEnvio().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
				administracaoDAO.removerConfirmacaoCRA(arquivo);
			} else {
				administracaoDAO.removerConfirmacaoCartorio(arquivo);
			}
		}
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
			if (arquivo.getInstituicaoEnvio().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
				administracaoDAO.removerRetornoCRA(arquivo);
			} else {
				administracaoDAO.removerRetornoCartorio(arquivo);
			}
		}
	}

	public void gerarArquivo5AnosTocantins() {
		File diretorioBase = new File(ConfiguracaoBase.DIRETORIO_BASE);
		if (!diretorioBase.exists()) {
			diretorioBase.mkdirs();
		}

		List<Instituicao> cartorios = instituicaoDAO.getCartorios();
		for (final Instituicao cartorio : cartorios) {
			if (cartorio.getId() == 21 || cartorio.getId() == 114 || cartorio.getId() == 20 || cartorio.getId() == 18 || cartorio.getId() == 14
					|| cartorio.getId() == 13 || cartorio.getId() == 43 || cartorio.getId() == 82 || cartorio.getId() == 2
					|| cartorio.getId() == 24) {
				logger.info("==========================================================");
				logger.info(cartorio.getMunicipio().getNomeMunicipio() + " => [id=" + cartorio.getId() + "] [codigoIbge="
						+ cartorio.getMunicipio().getCodigoIBGE() + "]  deverá ser processado separadamente...");

			} else {
				File arquivo = new File(ConfiguracaoBase.DIRETORIO_BASE + cartorio.getMunicipio().getNomeMunicipio());
				if (arquivo.exists()) {
					logger.info("==========================================================");
					logger.info("Municipio:  " + cartorio.getMunicipio().getNomeMunicipio() + "   -   Arquivo já criado!");
				} else {
					List<LoteCnp> lotes = centralNancionalProtestoDAO.buscarLotesProtesto5Anos(cartorio);

					LoteCnp novoLote = new LoteCnp();
					novoLote.setRegistrosCnp(new ArrayList<RegistroCnp>());
					for (LoteCnp lote : lotes) {
						novoLote.getRegistrosCnp().addAll(lote.getRegistrosCnp());
					}

					RemessaCnpVO remessaCnpVO = new RemessaCnpVO();
					remessaCnpVO.setCabecalhoCnpVO(gerarCabecalhoCnp(cartorio));
					remessaCnpVO.setTitulosCnpVO(new ConversorCnpVO().converterRegistrosCnpParaTitulosCnpVO(novoLote, 2));
					remessaCnpVO.setRodapeCnpVO(getRodapeCnpVO(remessaCnpVO.getTitulosCnpVO().size() + 2));

					if (!novoLote.getRegistrosCnp().isEmpty()) {
						logger.info("==========================================================");
						logger.info("Municipio:  " + cartorio.getMunicipio().getNomeMunicipio() + "  -  Qtd. Títulos:  "
								+ novoLote.getRegistrosCnp().size());

						ArquivoCnpVO arquivoCnpVO = new ArquivoCnpVO();
						arquivoCnpVO.setRemessasCnpVO(new ArrayList<RemessaCnpVO>());
						arquivoCnpVO.getRemessasCnpVO().add(remessaCnpVO);
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

		Instituicao cartorio = instituicaoDAO.getCartorioPeloCodigoMunicipio(municipioParametro);
		if (cartorio == null) {
			logger.info("Municipio com o código " + municipioParametro + " não encontrado!");
		} else {
			List<LoteCnp> lotes = centralNancionalProtestoDAO.buscarLotesPendentesEnvio(cartorio);

			LoteCnp novoLote = new LoteCnp();
			novoLote.setRegistrosCnp(new ArrayList<RegistroCnp>());
			for (LoteCnp lote : lotes) {
				novoLote.getRegistrosCnp().addAll(lote.getRegistrosCnp());
			}

			final CabecalhoCnpVO cabecalho = gerarCabecalhoCnp(cartorio);
			List<TituloCnpVO> titulos = new ConversorCnpVO().converterRegistrosCnpParaTitulosCnpVO(novoLote, 2);
			final RodapeCnpVO rodape = getRodapeCnpVO(titulos.size() + 2);

			if (!novoLote.getRegistrosCnp().isEmpty()) {
				logger.info("==========================================================");
				logger.info("Municipio:  " + cartorio.getMunicipio().getNomeMunicipio() + "  -  Qtd. Títulos:  " + novoLote.getRegistrosCnp().size());

				List<TituloCnpVO> titulosProcessados = new ArrayList<>();
				int total_titulos = 0;
				for (int i = 1; i <= titulos.size(); i++) {
					titulosProcessados.add(titulos.get(i - 1));
					total_titulos++;

					if (titulosProcessados.size() >= 10000 || titulos.size() == total_titulos) {
						RemessaCnpVO remessaCnpVO = new RemessaCnpVO();
						remessaCnpVO.setCabecalhoCnpVO(cabecalho);
						remessaCnpVO.setTitulosCnpVO(titulosProcessados);
						remessaCnpVO.setRodapeCnpVO(rodape);

						ArquivoCnpVO arquivoCnpVO = new ArquivoCnpVO();
						arquivoCnpVO.setRemessasCnpVO(new ArrayList<RemessaCnpVO>());
						arquivoCnpVO.getRemessasCnpVO().add(remessaCnpVO);

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
							File arquivo = new File(ConfiguracaoBase.DIRETORIO_BASE + cartorio.getMunicipio().getNomeMunicipio() + i);
							BufferedWriter bWrite = new BufferedWriter(new FileWriter(arquivo));
							logger.info("Escrevendo os dados no arquivo... " + i);
							bWrite.write(msg);
							bWrite.flush();
							bWrite.close();
							logger.info("  ");
							logger.info("Arquivo 5 Anos gerado com sucesso!" + i);
							logger.info("  ");
						} catch (JAXBException e) {
							logger.error(e.getMessage(), e.getCause());
							new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
						} catch (IOException e) {
							logger.error(e.getMessage(), e.getCause());
							new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
						}
						titulosProcessados = new ArrayList<>();
					}
				}

			}
		}
	}

	private CabecalhoCnpVO gerarCabecalhoCnp(Instituicao instituicao) {
		CabecalhoCnpVO cabecalhoCnpVO = new CabecalhoCnpVO();
		cabecalhoCnpVO.setCodigoRegistro("1");
		cabecalhoCnpVO.setEmBranco2("01");
		cabecalhoCnpVO.setDataMovimento(DataUtil.localDateToStringddMMyyyy(new LocalDate()));
		// instituicao.setMunicipio(municipioDAO.carregarMunicipio(instituicao.getMunicipio()));
		cabecalhoCnpVO.setEmBranco53(instituicao.getMunicipio().getCodigoIBGE());
		cabecalhoCnpVO.setNumeroRemessaArquivo(centralNancionalProtestoDAO.gerarSequencialCnp(instituicao));
		cabecalhoCnpVO.setTipoDocumento("1");
		cabecalhoCnpVO.setIdentificacaoDoArquivo("CENTRAL_NACIONAL_PROTESTO");
		cabecalhoCnpVO.setCodigoRemessa("E");
		cabecalhoCnpVO.setNumeroDDD("0063");
		cabecalhoCnpVO.setNumeroTelefoneInstituicaoInformante(instituicao.getTelefoneSemDDD());
		cabecalhoCnpVO.setNumeroRamalContato("");
		cabecalhoCnpVO.setNomeContatoInstituicaoInformante(RemoverAcentosUtil.removeAcentos(instituicao.getTabeliao()));
		cabecalhoCnpVO.setNumeroVersaoSoftware("1");
		cabecalhoCnpVO.setCodigoEDI("");
		cabecalhoCnpVO.setPeriodicidadeEnvio("D");
		cabecalhoCnpVO.setSequenciaRegistro("1");
		return cabecalhoCnpVO;
	}

	private RodapeCnpVO getRodapeCnpVO(Integer sequencial) {
		RodapeCnpVO rodape = new RodapeCnpVO();
		rodape.setCodigoRegistro(TipoRegistro.RODAPE.getConstante());
		rodape.setSequenciaRegistro(Integer.toString(sequencial));
		return rodape;
	}
}