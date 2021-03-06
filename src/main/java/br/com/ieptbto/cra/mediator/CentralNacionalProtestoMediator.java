package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.conversor.cnp.ConversorCnpVO;
import br.com.ieptbto.cra.conversor.cnp.RegistroCnpConversor;
import br.com.ieptbto.cra.dao.CentralNancionalProtestoDAO;
import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.vo.*;
import br.com.ieptbto.cra.enumeration.TipoRegistroCnp;
import br.com.ieptbto.cra.enumeration.regra.TipoIdentificacaoRegistro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.regra.FabricaRegraValidacaoCNP;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class CentralNacionalProtestoMediator extends BaseMediator {

	@Autowired
	FabricaRegraValidacaoCNP validarRegistroCnp;
	@Autowired
	CentralNancionalProtestoDAO centralNancionalProtestoDAO;
	@Autowired
	MunicipioMediator municipioMediator;
	@Autowired
	InstituicaoMediator instituicaoMediator;

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<Instituicao> consultarCartoriosCentralNacionalProtesto() {
		return centralNancionalProtestoDAO.consultarCartoriosCentralNacionalProtesto();
	}

	/**
	 * @param instituicao
	 * @param arquivoCnp5Anos
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public LoteCnp processarLote5anos(Instituicao instituicao, ArquivoCnpVO arquivoCnp5Anos) {
		logger.info("Processando lote de protestos 5 anos de " + instituicao.getNomeFantasia());

		LoteCnp loteCnp5anos = new LoteCnp();
		loteCnp5anos.setDataRecebimento(new LocalDate().toDate());
		loteCnp5anos.setInstituicaoOrigem(instituicao);
		loteCnp5anos.setRegistrosCnp(new ConversorCnpVO().converterArquivoCnpVOParaRegistrosCnp(arquivoCnp5Anos));
		loteCnp5anos.setStatus(false);
		loteCnp5anos.setLote5anos(true);
		return centralNancionalProtestoDAO.salvarLote5Anos(loteCnp5anos);
	}

	/**
	 * @param instituicao
	 * @param arquivoCnpVO
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = false)
	public LoteCnp processarLoteDiario(Usuario user, ArquivoCnpVO arquivoCnpVO) {
		LoteCnp lote = new LoteCnp();
		lote.setDataRecebimento(new LocalDate().toDate());
		lote.setInstituicaoOrigem(user.getInstituicao());
		lote.setRegistrosCnp(new ArrayList<RegistroCnp>());
		lote.setLote5anos(false);
		lote.setStatus(false);

		for (RemessaCnpVO remessaCnp : arquivoCnpVO.getRemessasCnpVO()) {
			for (TituloCnpVO tituloCnpVO : remessaCnp.getTitulosCnpVO()) {
				RegistroCnp registro = new RegistroCnpConversor().converter(RegistroCnp.class, tituloCnpVO);
				if (registro.getTipoRegistroCnp().equals(TipoRegistroCnp.PROTESTO)) {
					if (validarRegistroCnp.validarProtesto(registro)) {
						if (!lote.getRegistrosCnp().contains(registro)) {
							lote.getRegistrosCnp().add(registro);
						}
					}
				} else if (registro.getTipoRegistroCnp().equals(TipoRegistroCnp.CANCELAMENTO)) {
					if (validarRegistroCnp.validarCancelamento(registro)) {
						if (!lote.getRegistrosCnp().contains(registro)) {
							lote.getRegistrosCnp().add(registro);
						}
					}
				}
			}
		}
		return centralNancionalProtestoDAO.salvarLoteRegistrosCnp(user, lote);
	}

	/**
	 * @return
	 */
	public ArquivoCnpVO processarLoteNacional() {
		List<Instituicao> cartoriosPendentes = centralNancionalProtestoDAO.buscarCartoriosComLotesPendentesEnvioNacional();

		ArquivoCnpVO arquivoCnpVO = new ArquivoCnpVO();
		arquivoCnpVO.setRemessasCnpVO(new ArrayList<RemessaCnpVO>());
		for (Instituicao cartorio : cartoriosPendentes) {
			List<LoteCnp> lotes = centralNancionalProtestoDAO.buscarLotesPendentesEnvio(cartorio);

			RemessaCnpVO remessaCnpVO = null;
			for (LoteCnp lote : lotes) {
				if (remessaCnpVO == null) {
					remessaCnpVO = criarRemessaCnpVO(lote);
				} else {
					Integer sequencial = remessaCnpVO.getTitulosCnpVO().size() + 1;
					lote.setSequencialLiberacao(Integer.valueOf(remessaCnpVO.getCabecalhoCnpVO().getNumeroRemessaArquivo()));
					remessaCnpVO.getTitulosCnpVO().addAll(new ConversorCnpVO().converterRegistrosCnpParaTitulosCnpVO(lote, sequencial));
					remessaCnpVO.getRodapeCnpVO().setSequenciaRegistro(Integer.toString(remessaCnpVO.getTitulosCnpVO().size() + 2));
				}
				centralNancionalProtestoDAO.salvarLiberacaoLoteCnp(lote);
			}
			if (remessaCnpVO != null && !remessaCnpVO.getTitulosCnpVO().isEmpty()) {
				arquivoCnpVO.getRemessasCnpVO().add(remessaCnpVO);
			}
		}
		return arquivoCnpVO;
	}

	private RemessaCnpVO criarRemessaCnpVO(LoteCnp lote) {
		RemessaCnpVO remessaCnpVO = new RemessaCnpVO();
		remessaCnpVO.setCabecalhoCnpVO(gerarCabecalhoCnp(lote.getInstituicaoOrigem()));
		remessaCnpVO.setTitulosCnpVO(new ConversorCnpVO().converterRegistrosCnpParaTitulosCnpVO(lote, 2));
		remessaCnpVO.setRodapeCnpVO(getRodapeCnpVO(remessaCnpVO.getTitulosCnpVO().size() + 2));
		lote.setSequencialLiberacao(Integer.valueOf(remessaCnpVO.getCabecalhoCnpVO().getNumeroRemessaArquivo()));
		return remessaCnpVO;
	}

	@Transactional(propagation=Propagation.NOT_SUPPORTED, readOnly=true)
	private CabecalhoCnpVO gerarCabecalhoCnp(Instituicao instituicao) {
		Municipio municipio = municipioMediator.buscarMunicipioInstituicao(instituicao);

		CabecalhoCnpVO cabecalhoCnpVO = new CabecalhoCnpVO();
		cabecalhoCnpVO.setCodigoRegistro("1");
		cabecalhoCnpVO.setEmBranco2("01");
		cabecalhoCnpVO.setDataMovimento(DataUtil.localDateToStringddMMyyyy(new LocalDate()));
		cabecalhoCnpVO.setEmBranco53(municipio.getCodigoIBGE());
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

	/**
	 * @param data
	 * @return
	 */
	public ArquivoCnpVO processarLoteNacionalPorData(LocalDate data) {
		List<Instituicao> cartoriosEnviaram = centralNancionalProtestoDAO.buscarCartoriosEviaramLotesNacionalPorData(data);

		ArquivoCnpVO arquivoCnpVO = new ArquivoCnpVO();
		arquivoCnpVO.setRemessasCnpVO(new ArrayList<RemessaCnpVO>());
		for (Instituicao cartorio : cartoriosEnviaram) {
			List<LoteCnp> lotes = centralNancionalProtestoDAO.buscarLotesParaEnvioPorDate(cartorio, data);

			RemessaCnpVO remessaCnpVO = null;
			for (LoteCnp lote : lotes) {
				if (remessaCnpVO == null) {
					remessaCnpVO = reenviarRemessaCnpVO(lote, data);
				} else {
					Integer sequencial = remessaCnpVO.getTitulosCnpVO().size() + 1;
					lote.setSequencialLiberacao(Integer.valueOf(remessaCnpVO.getCabecalhoCnpVO().getNumeroRemessaArquivo()));
					remessaCnpVO.getTitulosCnpVO().addAll(new ConversorCnpVO().converterRegistrosCnpParaTitulosCnpVO(lote, sequencial));
					remessaCnpVO.getRodapeCnpVO().setSequenciaRegistro(Integer.toString(remessaCnpVO.getTitulosCnpVO().size() + 2));
				}
			}
			if (remessaCnpVO != null && !remessaCnpVO.getTitulosCnpVO().isEmpty()) {
				arquivoCnpVO.getRemessasCnpVO().add(remessaCnpVO);
			}
		}
		return arquivoCnpVO;
	}

	private RemessaCnpVO reenviarRemessaCnpVO(LoteCnp lote, LocalDate dataLiberacao) {
		RemessaCnpVO remessaCnpVO = new RemessaCnpVO();
		remessaCnpVO.setCabecalhoCnpVO(gerarCabecalhoCnp(lote.getInstituicaoOrigem(), dataLiberacao));
		remessaCnpVO.setTitulosCnpVO(new ConversorCnpVO().converterRegistrosCnpParaTitulosCnpVO(lote, 2));
		remessaCnpVO.setRodapeCnpVO(getRodapeCnpVO(remessaCnpVO.getTitulosCnpVO().size() + 2));
		return remessaCnpVO;
	}

	private CabecalhoCnpVO gerarCabecalhoCnp(Instituicao instituicao, LocalDate data) {
		Municipio municipio = municipioMediator.buscarMunicipioInstituicao(instituicao);
		
		CabecalhoCnpVO cabecalhoCnpVO = new CabecalhoCnpVO();
		cabecalhoCnpVO.setCodigoRegistro("1");
		cabecalhoCnpVO.setEmBranco2("01");
		cabecalhoCnpVO.setDataMovimento(DataUtil.localDateToStringddMMyyyy(new LocalDate()));
		cabecalhoCnpVO.setEmBranco53(municipio.getCodigoIBGE());
		cabecalhoCnpVO.setNumeroRemessaArquivo(centralNancionalProtestoDAO.gerarSequencialCnp(instituicao, data));
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
		rodape.setCodigoRegistro(TipoIdentificacaoRegistro.RODAPE.getConstante());
		rodape.setSequenciaRegistro(Integer.toString(sequencial));
		return rodape;
	}

	public boolean isLoteLiberadoConsultaPorData(LocalDate localDate) {
		LoteCnp lote = centralNancionalProtestoDAO.isLoteLiberadoConsultaPorData(localDate);
		if (lote != null)
			return true;
		return false;
	}

	public List<String> consultarProtestos(String documentoDevedor) {
		List<String> pracasComProtesto = new ArrayList<String>();
		List<RegistroCnp> protestos = centralNancionalProtestoDAO.consultarProtestos(documentoDevedor);

		for (RegistroCnp titulo : protestos) {
			RegistroCnp cancelamento =
					centralNancionalProtestoDAO.consultarCancelamento(documentoDevedor, titulo.getNumeroProtocoloCartorio());
			if (cancelamento == null) {
				Municipio municipio = new Municipio();
				if (!pracasComProtesto.contains(municipio.getNomeMunicipio().toUpperCase())) {
					pracasComProtesto.add(municipio.getNomeMunicipio().toUpperCase());
				}
			}
		}
		return pracasComProtesto;
	}

	public List<Instituicao> consultarProtestosWs(String documentoDevedor) {
		List<Instituicao> cartorios = new ArrayList<Instituicao>();
		List<RegistroCnp> protestos = centralNancionalProtestoDAO.consultarProtestos(documentoDevedor);

		for (RegistroCnp titulo : protestos) {
			RegistroCnp cancelamento =
					centralNancionalProtestoDAO.consultarCancelamento(documentoDevedor, titulo.getNumeroProtocoloCartorio());

			if (cancelamento == null && !cartorios.contains(titulo.getLoteCnp().getInstituicaoOrigem())) {
				cartorios.add(titulo.getLoteCnp().getInstituicaoOrigem());
			}
		}
		return cartorios;
	}

	public void importarBase5anosCSV(Instituicao instituicao, FileUpload uploadedFile) {
		int numeroLinha = 2;

		try {
			LoteCnp loteCnp = new LoteCnp();
			loteCnp.setDataRecebimento(new LocalDate().toDate());
			loteCnp.setInstituicaoOrigem(instituicao);
			loteCnp.setStatus(false);
			loteCnp.setRegistrosCnp(new ArrayList<RegistroCnp>());

			BufferedReader reader = new BufferedReader(new InputStreamReader(uploadedFile.getInputStream()));
			String linha = reader.readLine();

			while ((linha = reader.readLine()) != null) {
				linha = linha.replace("&amp;", " ");
				linha = RemoverAcentosUtil.removeAcentos(linha);
				String dados[] = linha.split(Pattern.quote(";"));

				RegistroCnp registro = RegistroCnpConversor.converterLinhaCSV(dados);
				if (registro.getTipoRegistroCnp().equals(TipoRegistroCnp.PROTESTO)) {
					if (validarRegistroCnp.validarProtesto(registro)) {
						loteCnp.getRegistrosCnp().add(registro);
					}
				} else if (registro.getTipoRegistroCnp().equals(TipoRegistroCnp.CANCELAMENTO)) {
					if (validarRegistroCnp.validarCancelamento(registro)) {
						RegistroCnp registroProtesto = centralNancionalProtestoDAO.buscarRegistroProtesto(instituicao, registro);
						if (registroProtesto != null) {
							registro.setValorProtesto(registroProtesto.getValorProtesto());
							loteCnp.getRegistrosCnp().add(registro);
						}
					}
				}
				numeroLinha++;
			}
			reader.close();
			if (loteCnp.getRegistrosCnp().isEmpty()) {
				throw new InfraException("O arquivo não pode ser importado, verifique as informações dos títulos se estão válidas!");
			}
			centralNancionalProtestoDAO.salvarLote5Anos(loteCnp);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Não foi possível abrir o arquivo enviado.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InfraException(
					"Não foi possível converter os dados da linha [ Nº " + numeroLinha + " ]. Verifique as informações do arquivo da cnp!");
		}
	}

	public void processarCnpCartorioTXTSerasa(Instituicao instituicao, FileUpload uploadedFile) {
		int numeroLinha = 1;

		try {
			LoteCnp loteCnp = new LoteCnp();
			loteCnp.setDataRecebimento(new LocalDate().toDate());
			loteCnp.setInstituicaoOrigem(instituicao);
			loteCnp.setStatus(false);
			loteCnp.setRegistrosCnp(new ArrayList<RegistroCnp>());

			BufferedReader reader = new BufferedReader(new InputStreamReader(uploadedFile.getInputStream()));
			String linha;
			while ((linha = reader.readLine()) != null) {

				if (linha.substring(0, 1).trim().equals(TipoIdentificacaoRegistro.TITULO.getConstante())) {
					RegistroCnp registro = RegistroCnpConversor.converterLinhaSerasa(linha);
					if (registro.getTipoRegistroCnp().equals(TipoRegistroCnp.PROTESTO)) {
						if (validarRegistroCnp.validarProtesto(registro)) {
							loteCnp.getRegistrosCnp().add(registro);
						}
					} else if (registro.getTipoRegistroCnp().equals(TipoRegistroCnp.CANCELAMENTO)) {
						if (validarRegistroCnp.validarCancelamento(registro)) {
							RegistroCnp registroProtesto = centralNancionalProtestoDAO.buscarRegistroProtesto(instituicao, registro);
							if (registroProtesto != null) {
								registro.setValorProtesto(registroProtesto.getValorProtesto());
								loteCnp.getRegistrosCnp().add(registro);
							}
						}
					}
				}
				numeroLinha++;
			}
			reader.close();
			if (loteCnp.getRegistrosCnp().isEmpty()) {
				throw new InfraException("O arquivo não pode ser importado, verifique as informações dos títulos se estão válidas!");
			}
			// centralNancionalProtestoDAO.salvarLote(loteCnp);
			throw new InfraException("O serviço de atualização da base da Central de Protestos está indiponível!");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Não foi possível abrir o arquivo enviado.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InfraException(
					"Não foi possível converter os dados da linha [ Nº " + numeroLinha + " ]. Verifique as informações do arquivo da cnp!");
		}
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean instituicaoEnviouLote5anos(Instituicao instituicao) {
		LoteCnp lote5anos = centralNancionalProtestoDAO.buscarLote5anosInteituicao(instituicao);
		if (lote5anos != null) {
			return true;
		}
		return false;
	}
}