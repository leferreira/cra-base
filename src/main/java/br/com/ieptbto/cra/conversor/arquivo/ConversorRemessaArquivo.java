package br.com.ieptbto.cra.conversor.arquivo;

import br.com.ieptbto.cra.conversor.AbstractFabricaDeArquivo;
import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.enumeration.TipoCampo51;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class ConversorRemessaArquivo {

	protected static final Logger logger = Logger.getLogger(ConversorRemessaArquivo.class);

	@Autowired
	private InstituicaoMediator instituicaoMediator;
	@Autowired
	private TituloMediator tituloMediator;

	private Arquivo arquivo;
	private List<Exception> erros;

	private static final String PRIMEIRO_DEVEDOR = "1";

	public Arquivo converterParaArquivo(List<RemessaVO> remessasVO, Arquivo arquivo, List<Exception> erros) {
		this.arquivo = arquivo;
		this.erros = erros;

		return converter(remessasVO);
	}

	private Arquivo converter(List<RemessaVO> remessasVO) {
		for (RemessaVO remessaVO : remessasVO) {
			Remessa remessa = new Remessa();
			remessa.setArquivo(arquivo);
			remessa.setCabecalho(CabecalhoRemessa.parseCabecalhoVO(remessaVO.getCabecalho()));
			remessa.getCabecalho().setRemessa(remessa);
			remessa.setRodape(Rodape.parseRodapeVO(remessaVO.getRodape()));
			remessa.getRodape().setRemessa(remessa);

			remessa.setArquivo(arquivo);
			remessa.setInstituicaoDestino(getInstituicaoDestino(remessaVO.getCabecalho()));
			remessa.setInstituicaoOrigem(arquivo.getInstituicaoEnvio());
			remessa.setDataRecebimento(new LocalDate());
			remessa.setTitulos(getTitulos(remessaVO.getTitulos(), remessa));
			arquivo.getRemessas().add(remessa);
		}
		return arquivo;
	}

	private Instituicao getInstituicaoDestino(CabecalhoVO cabecalho) {
		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.get(arquivo);
		if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo)) {
			return instituicaoMediator.getCartorioPorCodigoIBGE(cabecalho.getCodigoMunicipio());
		} else if (TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo) || TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
			return instituicaoMediator.getInstituicaoPorCodigoPortador(cabecalho.getNumeroCodigoPortador());
		}
		return null;
	}

	private List<Titulo> getTitulos(List<TituloVO> titulosVO, Remessa remessa) {
		List<Titulo> titulos = new ArrayList<Titulo>();
		Titulo titulo = null;
		for (TituloVO tituloVO : titulosVO) {
			if (TipoArquivoFebraban.REMESSA.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
				titulo = new ConversorTitulo().converter(TituloRemessa.class, tituloVO);
				verificarAnexoComplementoRegistro(remessa.getInstituicaoOrigem(), TituloRemessa.class.cast(titulo), tituloVO);
			} else if (TipoArquivoFebraban.CONFIRMACAO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
				titulo = new ConversorConfirmacao().converter(Confirmacao.class, tituloVO);
			} else if (TipoArquivoFebraban.RETORNO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
				titulo = new ConversorRetorno().converter(Retorno.class, tituloVO);
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

					Anexo anexo = new Anexo();
					anexo.setTitulo(titulo);
					anexo.setDocumentoAnexo(tituloVO.getComplementoRegistro());

					titulo.setAnexos(new ArrayList<Anexo>());
					titulo.getAnexos().add(anexo);
					titulo.setComplementoRegistro(StringUtils.EMPTY);
				}
			}
		}
	}

	/**
	 * Conversor de Arquivo(Entidade) para ArquivoVO de Instituicoes e Convênios
	 * 
	 * @param remessas
	 * @return
	 */
	public List<RemessaVO> converterArquivoVO(List<Remessa> remessas) {
		List<RemessaVO> remessasVO = new ArrayList<RemessaVO>();
		for (Remessa remessa : remessas) {
			RemessaVO remessaVO = new RemessaVO();
			remessaVO.setTitulos(new ArrayList<TituloVO>());
			remessaVO.setCabecalho(CabecalhoVO.parseCabecalho(remessa.getCabecalho()));
			remessaVO.getTitulos().addAll(converterTitulos(remessa.getTitulos()));
			remessaVO.setRodapes(RodapeVO.parseRodape(remessa.getRodape(), remessa.getCabecalho()));
			remessasVO.add(remessaVO);
		}
		return remessasVO;
	}

	/**
	 * Conversor de Remessa(Entidade) para RemessaVO de Cartórios
	 * 
	 * @param remessa
	 * @return
	 */
	public RemessaVO converterRemessaVO(Remessa remessa) {
		RemessaVO remessaVO = new RemessaVO();
		remessaVO.setTitulos(new ArrayList<TituloVO>());
		remessaVO.setCabecalho(CabecalhoVO.parseCabecalho(remessa.getCabecalho()));
		remessaVO.getTitulos().addAll(converterTitulos(remessa.getTitulos()));
		remessaVO.setRodapes(RodapeVO.parseRodape(remessa.getRodape(), remessa.getCabecalho()));
		return remessaVO;
	}

	/**
	 * Conversor de Arquivo de Remessas para Cartórios com documentos anexo
	 * preenchendo o campo 51.
	 * 
	 * @param remessa
	 * @return
	 */
	public RemessaVO converterArquivoXMLRemessaVO(Remessa remessa) {
		RemessaVO remessaVO = new RemessaVO();
		remessaVO.setTitulos(new ArrayList<TituloVO>());
		remessaVO.setCabecalho(CabecalhoVO.parseCabecalho(remessa.getCabecalho()));

		List<TituloVO> titulosVO = new ArrayList<TituloVO>();
		int quantidadeTitulos = 0;
		remessaVO.getCabecalho().setNumeroSequencialRegistroArquivo(Integer.toString(1));
		int sequencial = 2;
		for (Titulo titulo : remessa.getTitulos()) {
			if (titulo instanceof TituloRemessa) {
                TituloRemessa tituloRemessa = TituloRemessa.class.cast(titulo);
                TituloVO tituloVO = AbstractFabricaDeArquivo.converterParaTituloVO(titulo);
				if (tituloVO.getNumeroControleDevedor() != null && tituloVO.getNumeroControleDevedor().trim().equals(PRIMEIRO_DEVEDOR)) {
					quantidadeTitulos++;
				}
				if (remessa.getInstituicaoOrigem().getTipoCampo51().equals(TipoCampo51.DOCUMENTOS_COMPACTADOS)) {
					Anexo anexo = tituloMediator.buscarAnexo(tituloRemessa);
					if (anexo != null) {
						tituloVO.setComplementoRegistro(anexo.getDocumentoAnexo());
					}
				}
				tituloVO.setNumeroSequencialArquivo(Integer.toString(sequencial));
				sequencial++;
				titulosVO.add(tituloVO);
			}
		}
		remessaVO.setTitulos(titulosVO);
		remessaVO.setRodapes(RodapeVO.parseRodape(remessa.getRodape(), remessa.getCabecalho()));
		remessaVO.getCabecalho().setQtdRegistrosRemessa(String.valueOf(remessaVO.getTitulos().size()));
		remessaVO.getCabecalho().setQtdTitulosRemessa(String.valueOf(quantidadeTitulos));
		return remessaVO;
	}

	private List<TituloVO> converterTitulos(List<Titulo> titulos) {
		List<TituloVO> titulosVO = new ArrayList<TituloVO>();
		for (Titulo titulo : titulos) {
            TituloVO tituloVO = AbstractFabricaDeArquivo.converterParaTituloVO(titulo);
			titulosVO.add(tituloVO);
		}
		return titulosVO;
	}

	public List<Exception> getErros() {
		return erros;
	}
}