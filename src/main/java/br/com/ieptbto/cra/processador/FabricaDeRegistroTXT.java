package br.com.ieptbto.cra.processador;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import br.com.ieptbto.cra.conversor.AbstractConversorArquivo;
import br.com.ieptbto.cra.conversor.arquivo.CabecalhoArquivoDesistenciaProtestoConversor;
import br.com.ieptbto.cra.conversor.arquivo.CabecalhoCartorioDesistenciaProtestoConversor;
import br.com.ieptbto.cra.conversor.arquivo.CabecalhoConversor;
import br.com.ieptbto.cra.conversor.arquivo.RegistroDesistenciaProtestoConversor;
import br.com.ieptbto.cra.conversor.arquivo.RodapeArquivoDesistenciaProtestoVOConversor;
import br.com.ieptbto.cra.conversor.arquivo.RodapeCartorioDesistenciaProtestoConversor;
import br.com.ieptbto.cra.conversor.arquivo.RodapeConversor;
import br.com.ieptbto.cra.conversor.arquivo.TituloConversor;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;
import br.com.ieptbto.cra.enumeration.TipoRegistro;
import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;
import br.com.ieptbto.cra.util.CraConstructorUtils;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("unchecked")
public class FabricaDeRegistroTXT extends Processador {
	private static final Map<String, Class<? extends AbstractConversorArquivo<?, ?>>> TIPOS_ARQUIVOS;
	static {
		HashMap<String, Class<? extends AbstractConversorArquivo<?, ?>>> map = new HashMap<String, Class<? extends AbstractConversorArquivo<?, ?>>>();
		map.put(TipoRegistro.TITULO.getConstante(), TituloConversor.class);
		map.put(TipoRegistro.CABECALHO.getConstante(), CabecalhoConversor.class);
		map.put(TipoRegistro.RODAPE.getConstante(), RodapeConversor.class);

		TIPOS_ARQUIVOS = Collections.unmodifiableMap(map);
	}

	private static final Map<String, Class<? extends AbstractConversorArquivo<?, ?>>> TIPOS_ARQUIVO_DP;
	static {
		HashMap<String, Class<? extends AbstractConversorArquivo<?, ?>>> map = new HashMap<String, Class<? extends AbstractConversorArquivo<?, ?>>>();
		map.put(TipoRegistroDesistenciaProtesto.HEADER_APRESENTANTE.getConstante(), CabecalhoArquivoDesistenciaProtestoConversor.class);
		map.put(TipoRegistroDesistenciaProtesto.HEADER_CARTORIO.getConstante(), CabecalhoCartorioDesistenciaProtestoConversor.class);
		map.put(TipoRegistroDesistenciaProtesto.REGISTRO_PEDIDO_DESISTENCIA.getConstante(), RegistroDesistenciaProtestoConversor.class);
		map.put(TipoRegistroDesistenciaProtesto.TRAILLER_CARTORIO.getConstante(), RodapeCartorioDesistenciaProtestoConversor.class);
		map.put(TipoRegistroDesistenciaProtesto.TRAILLER_APRESENTANTE.getConstante(), RodapeArquivoDesistenciaProtestoVOConversor.class);

		TIPOS_ARQUIVO_DP = Collections.unmodifiableMap(map);
	}

	public static <T extends AbstractArquivoVO> String getLinha(T arquivoVO) {
		Class<? extends AbstractConversorArquivo<T, ?>> tipo = (Class<? extends AbstractConversorArquivo<T, ?>>) TIPOS_ARQUIVOS
		        .get(TipoRegistro.get(arquivoVO.getIdentificacaoRegistro()).getConstante());
		AbstractConversorArquivo<T, ?> conversor = novoRegistro(tipo);

		return conversor.criarLinhaArquivo(arquivoVO);
	}

	public static <T extends AbstractArquivoVO> String getLinhaDesistenciaProtesto(T arquivoVO) {
		Class<? extends AbstractConversorArquivo<T, ?>> tipo = (Class<? extends AbstractConversorArquivo<T, ?>>) TIPOS_ARQUIVO_DP
		        .get(TipoRegistroDesistenciaProtesto.get(arquivoVO.getIdentificacaoRegistro()).getConstante());
		AbstractConversorArquivo<T, ?> conversor = novoRegistro(tipo);

		return conversor.criarLinhaArquivo(arquivoVO);
	}

	private static <T extends AbstractArquivoVO, R extends AbstractConversorArquivo<T, ?>> R novoRegistro(Class<R> classRegistro) {
		return CraConstructorUtils.newInstance(classRegistro);
	}

}
