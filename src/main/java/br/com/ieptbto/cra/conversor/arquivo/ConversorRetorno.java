package br.com.ieptbto.cra.conversor.arquivo;

import br.com.ieptbto.cra.conversor.AbstractConversorArquivo;
import br.com.ieptbto.cra.conversor.BigDecimalConversor;
import br.com.ieptbto.cra.conversor.CampoArquivo;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.util.CraConstructorUtils;
import br.com.ieptbto.cra.util.DataUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.beans.PropertyDescriptor;

/**
 * 
 * @author Lefer
 *
 */
public class ConversorRetorno extends AbstractConversorArquivo<TituloVO, Retorno> {

    /**
     * Converte um arquivo Entidade em um ArquivoVO
     * 
     * @param entidade
     * @param arquivoVO
     * @return
     */
    public TituloVO converter(Retorno entidade, Class<TituloVO> arquivoVO) {
        BeanWrapper propertyAccessCRA = PropertyAccessorFactory.forBeanPropertyAccess(entidade.getTitulo());
        TituloVO tituloVO = CraConstructorUtils.newInstance(arquivoVO);
        BeanWrapper propertyAccessArquivo = PropertyAccessorFactory.forBeanPropertyAccess(tituloVO);
        PropertyDescriptor[] propertyDescriptors = propertyAccessArquivo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = propertyDescriptor.getName();
            if (propertyAccessCRA.isReadableProperty(propertyName)
                && propertyAccessArquivo.isWritableProperty(propertyName)) {
                Object valor = propertyAccessCRA.getPropertyValue(propertyName);
                if (String.class.isInstance(valor)) {
                    propertyAccessArquivo.setPropertyValue(propertyName, valor);
                } else {
                    propertyAccessArquivo.setPropertyValue(propertyName, converterValor(valor, new CampoArquivo(propertyName, arquivoVO)));
                }
            }
        }

        tituloVO.setNomeCedenteFavorecido(StringUtils.leftPad(" ", 45));
        tituloVO.setNomeSacadorVendedor(StringUtils.leftPad(" ", 45));
        tituloVO.setDocumentoSacador(StringUtils.leftPad(" ", 14));
        tituloVO.setEnderecoSacadorVendedor(StringUtils.leftPad(" ", 45));
        tituloVO.setCepSacadorVendedor(StringUtils.leftPad(" ", 8));
        tituloVO.setCidadeSacadorVendedor(StringUtils.leftPad(" ", 20));
        tituloVO.setUfSacadorVendedor(StringUtils.leftPad(" ", 2));
        tituloVO.setEspecieTitulo(StringUtils.leftPad(" ", 3));
        tituloVO.setNumeroTitulo(StringUtils.leftPad(" ", 11));
        tituloVO.setDataEmissaoTitulo(StringUtils.leftPad(" ", 8));
        tituloVO.setDataVencimentoTitulo(StringUtils.leftPad(" ", 8));
        tituloVO.setPracaProtesto(StringUtils.leftPad(" ", 20));
        tituloVO.setTipoEndoso(StringUtils.leftPad(" ", 1));
        tituloVO.setInformacaoSobreAceite(StringUtils.leftPad(" ", 1));
        tituloVO.setNumeroControleDevedor(StringUtils.leftPad(" ", 1));
        tituloVO.setNomeDevedor(StringUtils.leftPad(" ", 45));
        tituloVO.setTipoIdentificacaoDevedor(StringUtils.leftPad(" ", 3));
        tituloVO.setNumeroIdentificacaoDevedor(StringUtils.leftPad(" ", 14));
        tituloVO.setDocumentoDevedor(StringUtils.leftPad(" ", 11));
        tituloVO.setEnderecoDevedor(StringUtils.leftPad(" ", 45));
        tituloVO.setCepDevedor(StringUtils.leftPad(" ", 8));
        tituloVO.setCidadeDevedor(StringUtils.leftPad(" ", 20));
        tituloVO.setUfDevedor(StringUtils.leftPad(" ", 2));
        tituloVO.setNumeroOperacaoBanco(StringUtils.leftPad(" ", 5));
        tituloVO.setNumeroContratoBanco(StringUtils.leftPad(" ", 15));
        tituloVO.setNumeroParcelaContrato(StringUtils.leftPad(" ", 3));
        tituloVO.setTipoLetraCambio(StringUtils.leftPad(" ", 1));
        tituloVO.setProtestoMotivoFalencia(StringUtils.leftPad(" ", 1));
        tituloVO.setInstrumentoProtesto(StringUtils.leftPad(" ", 1));
        tituloVO.setValorCustaCartorio(new BigDecimalConversor().getValorConvertidoParaString(entidade.getValorCustaCartorio()));
        tituloVO.setTipoOcorrencia(entidade.getTipoOcorrencia());
        tituloVO.setDataOcorrencia(DataUtil.localDateToStringddMMyyyy(entidade.getDataOcorrencia()));
        tituloVO.setCodigoIrregularidade(entidade.getCodigoIrregularidade());
        tituloVO.setCodigoCartorio(entidade.getCodigoCartorio().toString());
        tituloVO.setNumeroProtocoloCartorio(entidade.getNumeroProtocoloCartorio());
        tituloVO.setDataProtocolo(DataUtil.localDateToStringddMMyyyy(entidade.getDataProtocolo()));
        tituloVO.setValorDemaisDespesas(new BigDecimalConversor().getValorConvertidoParaString(entidade.getValorDemaisDespesas()));
        tituloVO.setNumeroSequencialArquivo(entidade.getNumeroSequencialArquivo());
        tituloVO.setDeclaracaoPortador(" ");
        tituloVO.setValorGravacaoEletronica(new BigDecimalConversor().getValorConvertidoParaString(entidade.getValorGravacaoEletronica()));
	    return tituloVO;
    }
}
