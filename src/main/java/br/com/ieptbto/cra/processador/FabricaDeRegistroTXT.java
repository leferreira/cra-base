package br.com.ieptbto.cra.processador;

import br.com.ieptbto.cra.conversor.AbstractConversorArquivo;
import br.com.ieptbto.cra.conversor.arquivo.*;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;
import br.com.ieptbto.cra.entidade.vo.retornoEmpresa.ConversorHeaderEmpresa;
import br.com.ieptbto.cra.entidade.vo.retornoEmpresa.ConversorRegistroEmpresa;
import br.com.ieptbto.cra.entidade.vo.retornoEmpresa.ConversorTraillerEmpresa;
import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;
import br.com.ieptbto.cra.enumeration.regra.TipoIdentificacaoRegistro;
import br.com.ieptbto.cra.util.CraConstructorUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
		map.put(TipoIdentificacaoRegistro.TITULO.getConstante(), ConversorTitulo.class);
		map.put(TipoIdentificacaoRegistro.CABECALHO.getConstante(), ConversorCabecalho.class);
		map.put(TipoIdentificacaoRegistro.RODAPE.getConstante(), ConversorRodape.class);

		TIPOS_ARQUIVOS = Collections.unmodifiableMap(map);
	}

	private static final Map<String, Class<? extends AbstractConversorArquivo<?, ?>>> TIPOS_ARQUIVO_DP;
	static {
		HashMap<String, Class<? extends AbstractConversorArquivo<?, ?>>> map = new HashMap<String, Class<? extends AbstractConversorArquivo<?, ?>>>();
		map.put(TipoRegistroDesistenciaProtesto.HEADER_APRESENTANTE.getConstante(), ConversorCabecalhoArquivoDesistenciaCancelamento.class);
		map.put(TipoRegistroDesistenciaProtesto.HEADER_CARTORIO.getConstante(), ConversorCabecalhoCartorioDesistenciaCancelamento.class);
		map.put(TipoRegistroDesistenciaProtesto.REGISTRO_PEDIDO_DESISTENCIA.getConstante(), ConversorRegistroDesistenciaProtesto.class);
		map.put(TipoRegistroDesistenciaProtesto.TRAILLER_CARTORIO.getConstante(), ConversorRodapeCartorioDesistenciaCancelamento.class);
		map.put(TipoRegistroDesistenciaProtesto.TRAILLER_APRESENTANTE.getConstante(), ConversorRodapeArquivoDesistenciaCancelamento.class);

		TIPOS_ARQUIVO_DP = Collections.unmodifiableMap(map);
	}

	private static final Map<String, Class<? extends AbstractConversorArquivo<?, ?>>> LAYOUT_RECEBIMENTO_EMPRESA;
	static {
		HashMap<String, Class<? extends AbstractConversorArquivo<?, ?>>> map = new HashMap<String, Class<? extends AbstractConversorArquivo<?, ?>>>();
		map.put(TipoIdentificacaoRegistro.REGISTRO_EMPRESA.getConstante(), ConversorRegistroEmpresa.class);
		map.put(TipoIdentificacaoRegistro.HEADER_EMPRESA.getConstante(), ConversorHeaderEmpresa.class);
		map.put(TipoIdentificacaoRegistro.TRAILLER_EMPRESA.getConstante(), ConversorTraillerEmpresa.class);

		LAYOUT_RECEBIMENTO_EMPRESA = Collections.unmodifiableMap(map);
	}
	
	public static <T extends AbstractArquivoVO> String getLinha(T arquivoVO) {
		Class<? extends AbstractConversorArquivo<T, ?>> tipo = (Class<? extends AbstractConversorArquivo<T, ?>>) TIPOS_ARQUIVOS
		        .get(TipoIdentificacaoRegistro.get(arquivoVO.getIdentificacaoRegistro()).getConstante());
		AbstractConversorArquivo<T, ?> conversor = novoRegistro(tipo);

		return conversor.criarLinhaArquivo(arquivoVO);
	}

	public static <T extends AbstractArquivoVO> String getLinhaDesistenciaProtesto(T arquivoVO) {
		Class<? extends AbstractConversorArquivo<T, ?>> tipo = (Class<? extends AbstractConversorArquivo<T, ?>>) TIPOS_ARQUIVO_DP
		        .get(TipoRegistroDesistenciaProtesto.get(arquivoVO.getIdentificacaoRegistro()).getConstante());
		AbstractConversorArquivo<T, ?> conversor = novoRegistro(tipo);

		return conversor.criarLinhaArquivo(arquivoVO);
	}
	
	public static <T extends AbstractArquivoVO> String getLinhaLayoutEmpresa(T arquivoVO) {
		Class<? extends AbstractConversorArquivo<T, ?>> tipo = (Class<? extends AbstractConversorArquivo<T, ?>>) LAYOUT_RECEBIMENTO_EMPRESA
		        .get(TipoIdentificacaoRegistro.get(arquivoVO.getIdentificacaoRegistro()).getConstante());
		AbstractConversorArquivo<T, ?> conversor = novoRegistro(tipo);

		return conversor.criarLinhaArquivo(arquivoVO);
	}

	private static <T extends AbstractArquivoVO, R extends AbstractConversorArquivo<T, ?>> R novoRegistro(Class<R> classRegistro) {
		return CraConstructorUtils.newInstance(classRegistro);
	}

}
