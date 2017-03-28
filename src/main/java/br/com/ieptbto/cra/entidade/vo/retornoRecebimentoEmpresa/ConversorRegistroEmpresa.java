package br.com.ieptbto.cra.entidade.vo.retornoRecebimentoEmpresa;

import br.com.ieptbto.cra.conversor.AbstractConversorArquivo;
import br.com.ieptbto.cra.conversor.BigDecimalConversor;
import br.com.ieptbto.cra.conversor.DateConversor;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araujo
 *
 */
public class ConversorRegistroEmpresa extends AbstractConversorArquivo<RegistroRetornoRecebimentoVO, Retorno> {
	
	private static final String OUTROS_MEIOS_SEM_FATURA_GUIA_DE_ARRECADACAO = "f";
	private static final String DINHEIRO = "1";
	
	public RegistroRetornoRecebimentoVO converter(RegistroRetornoRecebimentoVO registroVO, Retorno retorno, Batimento batimento, Integer sequenciaRegistro) {
		registroVO.setIdentificacaoAgenciaContaDigitoCreditada(retorno.getNossoNumero());
		registroVO.setDataPagamento(new DateConversor().getValorConvertidoParaString(retorno.getDataOcorrencia(), DataUtil.PADRAO_FORMATACAO_DATA_YYYYMMDD));
		registroVO.setDataCredito(new DateConversor().getValorConvertidoParaString(batimento.getData(), DataUtil.PADRAO_FORMATACAO_DATA_YYYYMMDD));
		registroVO.setCodigoDeBarras("");
		registroVO.setValorRecebido(new BigDecimalConversor().getValorConvertidoParaString(retorno.getSaldoTitulo()));
		registroVO.setValorTarifa(new BigDecimalConversor().getValorConvertidoParaString(retorno.getSaldoTitulo()));
		registroVO.setNumeroSequencialRegistro(Integer.toString(sequenciaRegistro));
		registroVO.setCodigoAgenciaArrecadadora("");
		registroVO.setFormaArrecadacaoCaptura(OUTROS_MEIOS_SEM_FATURA_GUIA_DE_ARRECADACAO);
		registroVO.setNumeroAutenticacaoCaixaCodigotransacao("");
		registroVO.setFormaDePagemento(DINHEIRO);
		return registroVO;
	}
}