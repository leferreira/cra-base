package br.com.ieptbto.cra.conversor.arquivo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
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
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.ConfirmacaoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.entidade.vo.RetornoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoCampo51;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.XmlCraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class FabricaDeArquivoXML extends AbstractFabricaDeArquivo {

    private List<RemessaVO> arquivoVO;
    @Autowired
    private InstituicaoMediator instituicaoMediator;
    private Instituicao instituicaoEnvio;

    public void fabrica(List<RemessaVO> arquivoFisico, Arquivo arquivo, List<Exception> erros) {
	this.arquivoVO = arquivoFisico;
	this.arquivo = arquivo;
	this.erros = erros;
	converter();
    }

    public Arquivo processarConfirmacaoXML(Arquivo arquivo, ConfirmacaoVO confirmacaoVO, List<Exception> erros) {
	this.arquivo = arquivo;
	this.erros = erros;

	RemessaVO remessaVO = new RemessaVO();
	remessaVO.setCabecalho(confirmacaoVO.getCabecalho());
	remessaVO.setRodapes(confirmacaoVO.getRodape());
	remessaVO.setTitulos(new ArrayList<TituloVO>());
	remessaVO.getTitulos().addAll(confirmacaoVO.getTitulos());

	Remessa remessa = new Remessa();
	remessa.setArquivo(arquivo);
	remessa.setCabecalho(getCabecalho(remessaVO.getCabecalho()));
	remessa.getCabecalho().setRemessa(remessa);
	remessa.setRodape(getRodape(remessaVO.getRodape()));
	remessa.getRodape().setRemessa(remessa);
	remessa.setInstituicaoDestino(getInstituicaoDestino(remessaVO.getCabecalho()));
	remessa.setInstituicaoOrigem(getInstituicaoEnvio(remessaVO.getCabecalho()));
	remessa.setDataRecebimento(getDataRecebimento(remessaVO.getCabecalho().getDataMovimento()));
	remessa.setTitulos(getTitulos(remessaVO.getTitulos(), remessa));
	arquivo.getRemessas().add(remessa);

	return arquivo;

    }

    public Arquivo processarRetornoXML(Arquivo arquivo, RetornoVO retornoVO, List<Exception> erros) {
	this.arquivo = arquivo;
	this.erros = erros;

	RemessaVO remessaVO = new RemessaVO();
	remessaVO.setCabecalho(retornoVO.getCabecalho());
	remessaVO.setRodapes(retornoVO.getRodape());
	remessaVO.setTitulos(new ArrayList<TituloVO>());
	remessaVO.getTitulos().addAll(retornoVO.getTitulos());

	Remessa remessa = new Remessa();
	remessa.setArquivo(arquivo);
	remessa.setCabecalho(getCabecalho(remessaVO.getCabecalho()));
	remessa.getCabecalho().setRemessa(remessa);
	remessa.setRodape(getRodape(remessaVO.getRodape()));
	remessa.getRodape().setRemessa(remessa);
	remessa.setInstituicaoDestino(getInstituicaoDestino(remessaVO.getCabecalho()));
	remessa.setInstituicaoOrigem(getInstituicaoEnvio(remessaVO.getCabecalho()));
	remessa.setDataRecebimento(getDataRecebimento(remessaVO.getCabecalho().getDataMovimento()));
	remessa.setTitulos(getTitulos(remessaVO.getTitulos(), remessa));
	arquivo.getRemessas().add(remessa);

	return arquivo;

    }

    public Arquivo converter() {
	for (RemessaVO remessaVO : getArquivoVO()) {
	    if (validar(remessaVO)) {
		Remessa remessa = new Remessa();
		remessa.setArquivo(getArquivo());
		remessa.setCabecalho(getCabecalho(remessaVO.getCabecalho()));
		remessa.getCabecalho().setRemessa(remessa);
		remessa.setRodape(getRodape(remessaVO.getRodape()));
		remessa.getRodape().setRemessa(remessa);
		remessa.setArquivo(getArquivo());
		remessa.setInstituicaoDestino(getInstituicaoDestino(remessaVO.getCabecalho()));
		remessa.setInstituicaoOrigem(getInstituicaoEnvio(remessaVO.getCabecalho()));
		remessa.setDataRecebimento(getDataRecebimento(remessaVO.getCabecalho().getDataMovimento()));
		remessa.setTitulos(getTitulos(remessaVO.getTitulos(), remessa));
		getArquivo().getRemessas().add(remessa);
		getArquivo().setInstituicaoEnvio(getInstituicaoEnvio(remessaVO.getCabecalho()));
	    }
	}
	return getArquivo();
    }

    private boolean validar(RemessaVO remessaVO) {

	validarCodigoMunicipio(remessaVO);
	validarMunicipioAtivo(remessaVO);

	if (getErros().isEmpty()) {
	    return true;
	}
	return false;
    }

    private void validarMunicipioAtivo(RemessaVO remessaVO) {
	try {
	    getInstituicaoDestino(remessaVO.getCabecalho());

	} catch (Exception ex) {
	    logger.error(ex.getMessage(), ex.getCause());
	    getErros().add(new XmlCraException(ex.getMessage(), remessaVO.getCabecalho().getCodigoMunicipio(), remessaVO.getCabecalho().getCodigoMunicipio(), CodigoErro.CRA_MUNICIPIO_NAO_CADASTRADO_NA_CRA));
	}
    }

    private void validarCodigoMunicipio(RemessaVO remessaVO) {
	if (StringUtils.isEmpty(remessaVO.getCabecalho().getCodigoMunicipio())) {
	    logger.error(CodigoErro.CRA_CODIGO_DO_MUNICIPIO_NAO_INFORMADO.getDescricao());
	    getErros().add(new XmlCraException(CodigoErro.CRA_CODIGO_DO_MUNICIPIO_NAO_INFORMADO.getDescricao(), remessaVO.getCabecalho().getCodigoMunicipio(), remessaVO.getCabecalho().getCodigoMunicipio(), CodigoErro.CRA_CODIGO_DO_MUNICIPIO_NAO_INFORMADO));
	}
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
	if (TipoArquivoEnum.CONFIRMACAO.equals(TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getNomeArquivo()))
		|| TipoArquivoEnum.RETORNO.equals(TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getNomeArquivo()))) {
	    return instituicaoMediator.getInstituicaoPorCodigoPortador(cabecalho.getNumeroCodigoPortador());
	} else {
	    return instituicaoMediator.getCartorioPorCodigoIBGE(cabecalho.getCodigoMunicipio());
	}
    }

    private Rodape getRodape(RodapeVO rodapeVO) {
	return Rodape.parseRodapeVO(rodapeVO);
    }

    private CabecalhoRemessa getCabecalho(CabecalhoVO cabecalhoVO) {
	return CabecalhoRemessa.parseCabecalhoVO(cabecalhoVO);
    }

    @Override
    public void validar() {
    }

    public List<RemessaVO> getArquivoVO() {
	return arquivoVO;
    }

    public void setArquivoVO(List<RemessaVO> arquivoVO) {
	this.arquivoVO = arquivoVO;
    }

    public Instituicao getInstituicaoEnvio(CabecalhoVO cabecalho) {
	if (instituicaoEnvio == null) {
	    if (TipoArquivoEnum.CONFIRMACAO.equals(TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getNomeArquivo()))
		    || TipoArquivoEnum.RETORNO.equals(TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getNomeArquivo()))) {
		instituicaoEnvio = instituicaoMediator.getCartorioPorCodigoIBGE(cabecalho.getCodigoMunicipio());
	    } else {
		instituicaoEnvio = instituicaoMediator.getInstituicaoPorCodigoPortador(cabecalho.getNumeroCodigoPortador());
	    }

	}
	return instituicaoEnvio;
    }

    public void setInstituicaoEnvio(Instituicao instituicaoEnvio) {
	this.instituicaoEnvio = instituicaoEnvio;
    }

}
