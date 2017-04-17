package br.com.ieptbto.cra.fabrica;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.AbstractFabricaDeArquivo;
import br.com.ieptbto.cra.conversor.BigDecimalConversor;
import br.com.ieptbto.cra.conversor.arquivo.ConversorCabecalho;
import br.com.ieptbto.cra.conversor.arquivo.ConversorCancelamentoProtesto;
import br.com.ieptbto.cra.conversor.arquivo.ConversorConfirmacao;
import br.com.ieptbto.cra.conversor.arquivo.ConversorDesistenciaCancelamento;
import br.com.ieptbto.cra.conversor.arquivo.ConversorRetorno;
import br.com.ieptbto.cra.conversor.arquivo.ConversorRodape;
import br.com.ieptbto.cra.conversor.arquivo.ConversorTitulo;
import br.com.ieptbto.cra.dao.BatimentoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaCancelamentoProtesto;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.entidade.vo.retornoEmpresa.ArquivoRecebimentoEmpresaVO;
import br.com.ieptbto.cra.entidade.vo.retornoEmpresa.ConversorHeaderEmpresa;
import br.com.ieptbto.cra.entidade.vo.retornoEmpresa.ConversorRegistroEmpresa;
import br.com.ieptbto.cra.entidade.vo.retornoEmpresa.HeaderRetornoRecebimentoVO;
import br.com.ieptbto.cra.entidade.vo.retornoEmpresa.RegistroRetornoRecebimentoVO;
import br.com.ieptbto.cra.entidade.vo.retornoEmpresa.TraillerRetornoRecebimentoVO;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.enumeration.regra.TipoOcorrencia;
import br.com.ieptbto.cra.gerador.GeradorDeArquivosTXT;

@SuppressWarnings("rawtypes")
@Service
public class FabricaDeArquivoTXT extends AbstractFabricaDeArquivo {

	@Autowired
	FabricaRemessaConfirmacaoRetorno fabricaRemessaConfirmacaoRetorno;
	@Autowired
	FabricaDesistenciaCancelamento fabricaDesistenciaCancelamento;
	@Autowired
	GeradorDeArquivosTXT geradorDeArquivosTXT;
	@Autowired
	ConversorDesistenciaCancelamento conversorDesistenciaCancelamento;
	@Autowired
	ConversorCancelamentoProtesto conversorCancelamentoProtesto;
	@Autowired
	BatimentoDAO batimentoDAO;

	private static final String PRIMEIRO_DEVEDOR = "1";

	public Arquivo converter(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		this.file = arquivoFisico;
		this.arquivo = arquivo;
		this.erros = erros;

		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoFebraban(arquivo);
		if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo) || TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo) || TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
			return fabricaRemessaConfirmacaoRetorno.processarRemessaConfirmacaoRetorno(getFile(), getArquivo(), getErros());
		} else if (TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)) {
			return fabricaDesistenciaCancelamento.processarDesistenciaProtesto(getFile(), getArquivo(), getErros());
		} else if (TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
			return null;
		} else if (TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.equals(tipoArquivo)) {
			return null;
		} else {
			return null;
		}
	}

	public File fabricaArquivoCartorioTXT(File file, Remessa remessa) {
		this.file = file;
		this.arquivo = remessa.getArquivo();

		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoFebraban(remessa.getArquivo());

		RemessaVO remessaVO = new RemessaVO();
		remessaVO.setTitulos(new ArrayList<TituloVO>());
		BigDecimal valorTotalTitulos = BigDecimal.ZERO;
		remessaVO.setCabecalho(new ConversorCabecalho().converter(remessa.getCabecalho(), CabecalhoVO.class));
		remessaVO.setRodapes(new ConversorRodape().converter(remessa.getRodape(), RodapeVO.class));

		int contSequencial = 2;
		int quantidadeTitulos = 0;
		for (Titulo titulo : remessa.getTitulos()) {
			TituloVO tituloVO = new TituloVO();
			if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo)) {
				tituloVO = new ConversorTitulo().converter(TituloRemessa.class.cast(titulo), TituloVO.class);
			} else if (TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo)) {
				tituloVO = new ConversorConfirmacao().converter(Confirmacao.class.cast(titulo), TituloVO.class);
			} else if (TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
				tituloVO = new ConversorRetorno().converter(Retorno.class.cast(titulo), TituloVO.class);
			}
			if (tituloVO.getNumeroControleDevedor() != null && tituloVO.getNumeroControleDevedor().trim().equals(PRIMEIRO_DEVEDOR)) {
				quantidadeTitulos++;
			}
			tituloVO.setNumeroSequencialArquivo(String.valueOf(contSequencial));
			valorTotalTitulos = valorTotalTitulos.add(titulo.getSaldoTitulo());
			remessaVO.getTitulos().add(tituloVO);
			contSequencial++;
		}
		remessaVO.getCabecalho().setQtdRegistrosRemessa(String.valueOf(remessaVO.getTitulos().size()));
		remessaVO.getCabecalho().setQtdTitulosRemessa(String.valueOf(quantidadeTitulos));
		remessaVO.getRodape().setSomatorioQtdRemessa(somatorioSegurancaQuantidadeRemessa(remessaVO));
		remessaVO.getRodape().setSomatorioValorRemessa(new BigDecimalConversor().getValorConvertidoParaString(valorTotalTitulos));
		remessaVO.setIdentificacaoRegistro(remessa.getCabecalho().getIdentificacaoRegistro().getConstante());
		remessaVO.setTipoArquivo(remessa.getArquivo().getTipoArquivo());
		remessaVO.getRodape().setNumeroSequencialRegistroArquivo(String.valueOf(contSequencial));

		gerarTXT(file, remessaVO);
		return file;
	}

	public File fabricaArquivoInstituicaoConvenioTXT(File file, List<Remessa> remessas) {
		this.file = file;

		List<RemessaVO> remessasVO = new ArrayList<RemessaVO>();
		for (Remessa remessa : remessas) {
			TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoFebraban(remessa.getArquivo());

			RemessaVO remessaVO = new RemessaVO();
			remessaVO.setTitulos(new ArrayList<TituloVO>());
			BigDecimal valorTotalTitulos = BigDecimal.ZERO;

			remessaVO.setCabecalho(new ConversorCabecalho().converter(remessa.getCabecalho(), CabecalhoVO.class));
			remessaVO.setRodapes(new ConversorRodape().converter(remessa.getRodape(), RodapeVO.class));

			int contSequencial = 2;
			for (Titulo titulo : remessa.getTitulos()) {
				TituloVO tituloVO = new TituloVO();
				if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo)) {
					tituloVO = new ConversorTitulo().converter(TituloRemessa.class.cast(titulo), TituloVO.class);
				} else if (TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo)) {
					tituloVO = new ConversorConfirmacao().converter(Confirmacao.class.cast(titulo), TituloVO.class);
				} else if (TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
					tituloVO = new ConversorRetorno().converter(Retorno.class.cast(titulo), TituloVO.class);
				}
				tituloVO.setNumeroSequencialArquivo(String.valueOf(contSequencial));
				valorTotalTitulos = valorTotalTitulos.add(titulo.getSaldoTitulo());
				remessaVO.getTitulos().add(tituloVO);
				contSequencial++;
			}
			remessaVO.getCabecalho().setQtdTitulosRemessa(String.valueOf(remessaVO.getTitulos().size()));
			remessaVO.getRodape().setSomatorioQtdRemessa(somatorioSegurancaQuantidadeRemessa(remessaVO));
			remessaVO.getRodape().setSomatorioValorRemessa(new BigDecimalConversor().getValorConvertidoParaString(valorTotalTitulos));
			remessaVO.setIdentificacaoRegistro(remessa.getCabecalho().getIdentificacaoRegistro().getConstante());
			remessaVO.setTipoArquivo(remessa.getArquivo().getTipoArquivo());
			remessaVO.getRodape().setNumeroSequencialRegistroArquivo(String.valueOf(contSequencial));

			remessasVO.add(remessaVO);
		}
		gerarTXT(file, remessasVO);
		return file;
	}
	
	public File baixarRetornoRecebimentoEmpresaTXT(File file, List<Remessa> remessas, Instituicao instituicao, LocalDate dataGeracao, Integer sequencialArquivo) {
		this.file = file;
		BigDecimal valorTotalTitulos = BigDecimal.ZERO;
		
		ArquivoRecebimentoEmpresaVO arquivoVO = new ArquivoRecebimentoEmpresaVO();  
		arquivoVO.setHeaderEmpresaVO(new ConversorHeaderEmpresa().converter(new HeaderRetornoRecebimentoVO(), instituicao, dataGeracao, sequencialArquivo));
		
		List<RegistroRetornoRecebimentoVO> registros = new ArrayList<RegistroRetornoRecebimentoVO>();
		for (Remessa remessa : remessas) {
			Batimento batimento = batimentoDAO.buscarBatimentoDoRetorno(remessa);
			for (Titulo titulo : remessa.getTitulos()) {
				Retorno retorno = Retorno.class.cast(titulo);
				TipoOcorrencia tipoOcorrencia = TipoOcorrencia.getTipoOcorrencia(retorno.getTipoOcorrencia());
				
				if (TipoOcorrencia.PAGO == tipoOcorrencia) {
					registros.add(new ConversorRegistroEmpresa().converter(new RegistroRetornoRecebimentoVO(), retorno, batimento, registros.size() + 1));
				}
			}
		}
		if (registros.isEmpty()) {
			return null;
		}
		arquivoVO.setRegistrosEmpresaVO(registros);
		
		TraillerRetornoRecebimentoVO trailler = new TraillerRetornoRecebimentoVO();
		trailler.setValorTotalRecebidoRegistros(new BigDecimalConversor().getValorConvertidoParaString(valorTotalTitulos));
		trailler.setTotalRegistrosArquivo(Integer.toString(registros.size()));
		arquivoVO.setTraillerEmpresaVO(trailler);
		
		gerarTXT(file, arquivoVO);
		return file;
	}

	public File fabricaArquivoDesistenciaProtestoTXT(File file, RemessaDesistenciaProtesto remessa) {
		return geradorDeArquivosTXT.gerar(conversorDesistenciaCancelamento.converterParaVO(remessa), file);
	}

	public File fabricaArquivoCancelamentoProtestoTXT(File file, RemessaCancelamentoProtesto remessa) {
		return geradorDeArquivosTXT.gerar(conversorCancelamentoProtesto.converter(remessa), file);
	}

	public File fabricaArquivoAutorizacaoCancelamentoTXT(File file, RemessaAutorizacaoCancelamento remessa) {
		return geradorDeArquivosTXT.gerar(conversorCancelamentoProtesto.converter(remessa), file);
	}

	private String somatorioSegurancaQuantidadeRemessa(RemessaVO remessaVO) {
		int somatorioQtdRemessa = 0;
		if (remessaVO.getCabecalho() != null) {
			somatorioQtdRemessa = Integer.parseInt(remessaVO.getCabecalho().getQtdRegistrosRemessa())
					+ Integer.parseInt(remessaVO.getCabecalho().getQtdTitulosRemessa()) + Integer.parseInt(remessaVO.getCabecalho().getQtdIndicacoesRemessa())
					+ Integer.parseInt(remessaVO.getCabecalho().getQtdOriginaisRemessa());
		}
		return Integer.toString(somatorioQtdRemessa);
	}

	/**
	 * @param file
	 * @param remessaVO
	 */
	private void gerarTXT(File file, RemessaVO remessaVO) {
		geradorDeArquivosTXT.gerar(remessaVO, file);
	}

	/**
	 * @param file
	 * @param remessasVO
	 */
	private void gerarTXT(File file, List<RemessaVO> remessasVO) {
		geradorDeArquivosTXT.gerar(remessasVO, file);
	}
	
	/**
	 * @param file
	 * @param remessasVO
	 */
	private void gerarTXT(File file, ArquivoRecebimentoEmpresaVO arquivoCnab240) {
		geradorDeArquivosTXT.gerar(arquivoCnab240, file);
	}
}