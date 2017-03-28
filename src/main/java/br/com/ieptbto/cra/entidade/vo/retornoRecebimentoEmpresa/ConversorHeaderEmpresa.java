package br.com.ieptbto.cra.entidade.vo.retornoRecebimentoEmpresa;

import org.joda.time.LocalDate;

import br.com.ieptbto.cra.conversor.AbstractConversorArquivo;
import br.com.ieptbto.cra.conversor.DateConversor;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araujo
 *
 */
public class ConversorHeaderEmpresa extends AbstractConversorArquivo<HeaderRetornoRecebimentoVO, CabecalhoRemessa> {

	private static final String CODIGO_REMESSA_RETORNO = "2";
	
	public HeaderRetornoRecebimentoVO converter(HeaderRetornoRecebimentoVO headerEmpresaVO, Instituicao instituicao, LocalDate dataGeracao, Integer sequencialArquivo) {
		headerEmpresaVO.setCodigoRemessa(CODIGO_REMESSA_RETORNO);
		headerEmpresaVO.setCodigoConvenio(instituicao.getCodigoCompensacao());
		headerEmpresaVO.setNomeEmpresaOrgao((instituicao.getNomeFantasia().length() > 19) ? instituicao.getNomeFantasia().substring(0, 19) : instituicao.getNomeFantasia());
		headerEmpresaVO.setCodigoBanco("000");
		headerEmpresaVO.setNomeBanco("INSTITUTO DE ESTUDOS ");
		headerEmpresaVO.setDataGeracaoArquivo(new DateConversor().getValorConvertidoParaString(dataGeracao, DataUtil.PADRAO_FORMATACAO_DATA_YYYYMMDD));
		headerEmpresaVO.setNumeroSequencialArquivo(Integer.toString(sequencialArquivo));
		headerEmpresaVO.setVersaoLayout("04");
		headerEmpresaVO.setCodigoDeBarras("CODIGO DE BARRAS");
		return headerEmpresaVO;
	}
}
