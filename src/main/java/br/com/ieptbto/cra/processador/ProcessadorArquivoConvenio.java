package br.com.ieptbto.cra.processador;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoRemessaConvenioVO;
import br.com.ieptbto.cra.entidade.vo.TituloConvenioVO;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;
import br.com.ieptbto.cra.util.CraConstructorUtils;

/**
 * @author Thasso Araujo
 *
 */
@Service
public class ProcessadorArquivoConvenio extends Processador {
	
	@Autowired
	private InstituicaoMediator instituicaoMediator;
	@Autowired
	private TipoArquivoMediator tipoArquivoMediator;
	@Autowired
	private RemessaMediator remessaMediator;

	private Arquivo arquivo;
	
	/**
	 * @param titulosConvenios
	 * @param usuario
	 * @return
	 */
	public Arquivo processarArquivoWS(ArquivoRemessaConvenioVO arquivoConvenioVO, Arquivo arquivo, List<Exception> erros) {
		this.arquivo = arquivo;
		Usuario usuario = arquivo.getUsuarioEnvio();
		logger.info("Início processamento arquivo via WS " + arquivo.getNomeArquivo() + " do usuário " + usuario.getLogin());
		
		converterArquivoConvenio(arquivoConvenioVO, erros);
		
		logger.info("Fim processamento arquivo via WS " + arquivo.getNomeArquivo() + " do usuário " + usuario.getLogin());
		return this.arquivo;
	}

	private void converterArquivoConvenio(ArquivoRemessaConvenioVO arquivoConvenioVO, List<Exception> erros) {
		HashMap<String, Remessa> mapaRemessas = new HashMap<String, Remessa>();
		
		for (TituloConvenioVO tituloConvenioVO : arquivoConvenioVO.getTitulosConvenio()) {
			TituloRemessa titulo  = CraConstructorUtils.newInstance(TituloRemessa.class);
			BeanWrapper propertyAccessEntidadeVO = PropertyAccessorFactory.forBeanPropertyAccess(tituloConvenioVO);
			BeanWrapper propertyAccessTitulo = PropertyAccessorFactory.forBeanPropertyAccess(titulo);
			
			PropertyDescriptor[] propertyDescriptors = propertyAccessTitulo.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				String propertyName = propertyDescriptor.getName();
				
				if (propertyAccessEntidadeVO.isReadableProperty(propertyName) && propertyAccessTitulo.isWritableProperty(propertyName)) {
					Object valor = propertyAccessEntidadeVO.getPropertyValue(propertyName);
					
				}
			}
		}
		this.arquivo.setRemessas(new ArrayList<Remessa>(mapaRemessas.values()));
	}	
}