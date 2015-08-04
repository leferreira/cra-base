package br.com.ieptbto.cra.processador;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoCartorioDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RegistroDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeCartorioDesistenciaProtestoVO;
import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.util.CraConstructorUtils;

/**
 * 
 * @author Lefer
 *
 */
public class FabricaRegistroDesistenciaProtesto extends Processador {

	private static final Map<String, Class<? extends AbstractArquivoVO>> TIPOS_ARQUIVOS;
	static {
		HashMap<String, Class<? extends AbstractArquivoVO>> map = new HashMap<String, Class<? extends AbstractArquivoVO>>();
		map.put(TipoRegistroDesistenciaProtesto.HEADER_APRESENTANTE.getConstante(), CabecalhoArquivoDesistenciaProtestoVO.class);
		map.put(TipoRegistroDesistenciaProtesto.HEADER_CARTORIO.getConstante(), CabecalhoCartorioDesistenciaProtestoVO.class);
		map.put(TipoRegistroDesistenciaProtesto.REGISTRO_PEDIDO_DESISTENCIA.getConstante(), RegistroDesistenciaProtestoVO.class);
		map.put(TipoRegistroDesistenciaProtesto.TRAILLER_APRESENTANTE.getConstante(), RodapeArquivoDesistenciaProtestoVO.class);
		map.put(TipoRegistroDesistenciaProtesto.TRAILLER_CARTORIO.getConstante(), RodapeCartorioDesistenciaProtestoVO.class);

		TIPOS_ARQUIVOS = Collections.unmodifiableMap(map);
	}

	private String linha;
	private String tipoReg;
	private TipoRegistroDesistenciaProtesto tipoRegistro;

	/**
	 * @param linha
	 */
	public FabricaRegistroDesistenciaProtesto(String linha) {
		if (linha == null) {
			throw new IllegalArgumentException("linha não pode ser null");
		}
		validarTamanhoLinha(linha);
		this.tipoReg = StringUtils.left(linha, 1);
		this.linha = linha;
		this.tipoRegistro = TipoRegistroDesistenciaProtesto.get(linha);
	}

	/**
	 * Retorna uma instância da fábrica.
	 * 
	 * @param linha
	 * @return
	 */
	public static FabricaRegistroDesistenciaProtesto getInstance(String linha) {
		return new FabricaRegistroDesistenciaProtesto(linha);
	}

	/**
	 * Cria um registro de acordo com o tipo
	 * 
	 * @return
	 */
	public AbstractArquivoVO criarRegistro() {
		Class<? extends AbstractArquivoVO> type = TIPOS_ARQUIVOS.get(tipoReg);
		if (type == null) {
			throw new InfraException("Tipo de registro desconhecido ");
		}
		return criarRegistro(type);
	}

	/**
	 * Criar um registro do tipo header ou tipo trailer.
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	private <T extends AbstractArquivoVO> T criarRegistro(Class<T> type) {
		T novoRegistro = novoRegistro(type);
		new GerenciadorArquivo(linha, novoRegistro);
		return novoRegistro;
	}

	private void validarTamanhoLinha(String linha) {
		int tamLinha = linha.length();
		if (tamLinha != ConfiguracaoBase.TAMANHO_PADRAO_LINHA_DESISTENCIA_PROTESTO) {
			throw new InfraException("tamanho inválido de linha do arquivo. Esperado: ["
			        + ConfiguracaoBase.TAMANHO_PADRAO_LINHA_DESISTENCIA_PROTESTO + "], Recebido:[" + tamLinha
			        + ConfiguracaoBase.FECHA_CHAVE);
		}
	}

	private <T> T novoRegistro(Class<T> classRegistro) {
		return CraConstructorUtils.newInstance(classRegistro);
	}

}
