package br.com.ieptbto.cra.conversor.arquivo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

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
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoCampo51;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class ConversorRemessaArquivo {

	protected static final Logger logger = Logger.getLogger(ConversorRemessaArquivo.class);

	@SpringBean
	private InstituicaoMediator instituicaoMediator;

	private Arquivo arquivo;
	private List<Exception> erros;

	public Arquivo converterParaArquivo(ArquivoVO arquivoVO, Arquivo arquivo, List<Exception> erros) {
		this.arquivo = arquivo;
		this.erros = erros;

		List<RemessaVO> remessasVO = ConversorArquivoVO.converterParaRemessaVO(arquivoVO);
		return converter(remessasVO);
	}

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
			remessa.setDataRecebimento(getDataRecebimento(remessaVO.getCabecalho().getDataMovimento()));
			remessa.setTitulos(getTitulos(remessaVO.getTitulos(), remessa));
			arquivo.getRemessas().add(remessa);
		}
		return arquivo;
	}

	private LocalDate getDataRecebimento(String dataMovimento) {
		if (dataMovimento.equals("00000000") || dataMovimento == null) {
			return new LocalDate();
		}
		return DataUtil.stringToLocalDate(DataUtil.PADRAO_FORMATACAO_DATA_DDMMYYYY, dataMovimento);
	}

	private Instituicao getInstituicaoDestino(CabecalhoVO cabecalho) {
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(arquivo);
		if (TipoArquivoEnum.REMESSA.equals(tipoArquivo)) {
			return instituicaoMediator.getCartorioPorCodigoIBGE(cabecalho.getCodigoMunicipio());
		} else if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo) || TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			return instituicaoMediator.getInstituicaoPorCodigoPortador(cabecalho.getNumeroCodigoPortador());
		}
		return null;
	}

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

	public List<Exception> getErros() {
		return erros;
	}
}