package br.com.ieptbto.cra.conversor.arquivo;

import br.com.ieptbto.cra.conversor.AbstractConversorArquivo;
import br.com.ieptbto.cra.conversor.BigDecimalConversor;
import br.com.ieptbto.cra.conversor.CampoArquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.enumeration.regra.TipoOcorrencia;
import br.com.ieptbto.cra.util.CraConstructorUtils;
import br.com.ieptbto.cra.util.DataUtil;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.beans.PropertyDescriptor;

/**
 * 
 * @author Lefer
 *
 */
public class ConversorConfirmacao extends AbstractConversorArquivo<TituloVO, Confirmacao> {

	/**
	 * Converte um arquivo Entidade em um ArquivoVO
	 * 
	 * @param entidade
	 * @param arquivoVO
	 * @return
	 */
	public TituloVO converter(Confirmacao entidade, Class<TituloVO> arquivoVO) {

		BeanWrapper propertyAccessCRA = PropertyAccessorFactory.forBeanPropertyAccess(entidade.getTitulo());
		TituloVO tituloVO = CraConstructorUtils.newInstance(arquivoVO);
		BeanWrapper propertyAccessArquivo = PropertyAccessorFactory.forBeanPropertyAccess(tituloVO);
		PropertyDescriptor[] propertyDescriptors = propertyAccessArquivo.getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			String propertyName = propertyDescriptor.getName();
			if (propertyAccessCRA.isReadableProperty(propertyName) && propertyAccessArquivo.isWritableProperty(propertyName)) {
				Object valor = propertyAccessCRA.getPropertyValue(propertyName);
				if (String.class.isInstance(valor)) {
					propertyAccessArquivo.setPropertyValue(propertyName, valor);
				} else {
					propertyAccessArquivo.setPropertyValue(propertyName, converterValor(valor, new CampoArquivo(propertyName, arquivoVO)));
				}
			}

		}
		tituloVO.setCodigoCartorio(entidade.getCodigoCartorio().toString());
		tituloVO.setNumeroProtocoloCartorio(entidade.getNumeroProtocoloCartorio());
		tituloVO.setDataProtocolo(DataUtil.localDateToStringddMMyyyy(entidade.getDataProtocolo()));
		tituloVO.setTipoOcorrencia(entidade.getTipoOcorrencia());
		tituloVO.setDataOcorrencia(DataUtil.localDateToStringddMMyyyy(entidade.getDataOcorrencia()));
		tituloVO.setCodigoIrregularidade(entidade.getCodigoIrregularidade());
		
		if (entidade.getTipoOcorrencia() != null) {
			if (entidade.getTipoOcorrencia().trim().equals("") || 
					entidade.getTipoOcorrencia().equals("0")) {
				tituloVO.setDataOcorrencia("00000000");
				tituloVO.setCodigoIrregularidade("00");
			}
		}
		
		if (!entidade.getTipoOcorrencia().equals(TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante())) {
			tituloVO.setValorGravacaoEletronica(new BigDecimalConversor().getValorConvertidoSegundoLayoutFebraban(entidade.getRemessa().getInstituicaoDestino().getValorConfirmacao()));
		}
		return tituloVO;
	}
}
