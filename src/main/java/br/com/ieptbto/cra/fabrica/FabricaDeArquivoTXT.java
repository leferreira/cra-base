package br.com.ieptbto.cra.fabrica;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.AbstractFabricaDeArquivo;
import br.com.ieptbto.cra.conversor.BigDecimalConversor;
import br.com.ieptbto.cra.conversor.arquivo.CabecalhoConversor;
import br.com.ieptbto.cra.conversor.arquivo.ConfirmacaoConversor;
import br.com.ieptbto.cra.conversor.arquivo.ConversorCancelamentoProtesto;
import br.com.ieptbto.cra.conversor.arquivo.ConversorDesistenciaProtesto;
import br.com.ieptbto.cra.conversor.arquivo.RetornoConversor;
import br.com.ieptbto.cra.conversor.arquivo.RodapeConversor;
import br.com.ieptbto.cra.conversor.arquivo.TituloConversor;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
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
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.gerador.GeradorDeArquivosTXT;

@SuppressWarnings("rawtypes")
@Service
public class FabricaDeArquivoTXT extends AbstractFabricaDeArquivo {

	@Autowired
	private FabricaRemessaConfirmacaoRetorno fabricaRemessaConfirmacaoRetorno;
	@Autowired
	private FabricaDesistenciaCancelamento fabricaDesistenciaCancelamento;

	@Autowired
	private GeradorDeArquivosTXT geradorDeArquivosTXT;
	@Autowired
	private ConversorDesistenciaProtesto conversorDesistenciaProtesto;
	@Autowired
	private ConversorCancelamentoProtesto conversorCancelamentoProtesto;

	public Arquivo converter(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		this.file = arquivoFisico;
		this.arquivo = arquivo;
		this.erros = erros;

		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(arquivo);
		if (TipoArquivoEnum.REMESSA.equals(tipoArquivo) || TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)
				|| TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			return fabricaRemessaConfirmacaoRetorno.processarRemessaConfirmacaoRetorno(getFile(), getArquivo(), getErros());
		} else if (TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)) {
			return fabricaDesistenciaCancelamento.processarDesistenciaProtesto(getFile(), getArquivo(), getErros());
		} else if (TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
			return null;
		} else if (TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO.equals(tipoArquivo)) {
			return null;
		} else {
			return null;
		}
	}

	public File fabricaArquivoCartorioTXT(File file, Remessa remessa) {
		this.file = file;
		this.arquivo = remessa.getArquivo();

		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(remessa.getArquivo());

		RemessaVO remessaVO = new RemessaVO();
		remessaVO.setTitulos(new ArrayList<TituloVO>());
		BigDecimal valorTotalTitulos = BigDecimal.ZERO;
		remessaVO.setCabecalho(new CabecalhoConversor().converter(remessa.getCabecalho(), CabecalhoVO.class));
		remessaVO.setRodapes(new RodapeConversor().converter(remessa.getRodape(), RodapeVO.class));

		int contSequencial = 2;
		for (Titulo titulo : remessa.getTitulos()) {
			TituloVO tituloVO = new TituloVO();
			if (TipoArquivoEnum.REMESSA.equals(tipoArquivo)) {
				tituloVO = new TituloConversor().converter(TituloRemessa.class.cast(titulo), TituloVO.class);
			} else if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)) {
				tituloVO = new ConfirmacaoConversor().converter(Confirmacao.class.cast(titulo), TituloVO.class);
			} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
				tituloVO = new RetornoConversor().converter(Retorno.class.cast(titulo), TituloVO.class);
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

		gerarTXT(file, remessaVO);
		return file;
	}

	public File fabricaArquivoInstituicaoConvenioTXT(File file, List<Remessa> remessas) {
		this.file = file;

		List<RemessaVO> remessasVO = new ArrayList<RemessaVO>();
		for (Remessa remessa : remessas) {
			TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(remessa.getArquivo());

			RemessaVO remessaVO = new RemessaVO();
			remessaVO.setTitulos(new ArrayList<TituloVO>());
			BigDecimal valorTotalTitulos = BigDecimal.ZERO;

			remessaVO.setCabecalho(new CabecalhoConversor().converter(remessa.getCabecalho(), CabecalhoVO.class));
			remessaVO.setRodapes(new RodapeConversor().converter(remessa.getRodape(), RodapeVO.class));

			int contSequencial = 2;
			for (Titulo titulo : remessa.getTitulos()) {
				TituloVO tituloVO = new TituloVO();
				if (TipoArquivoEnum.REMESSA.equals(tipoArquivo)) {
					tituloVO = new TituloConversor().converter(TituloRemessa.class.cast(titulo), TituloVO.class);
				} else if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)) {
					tituloVO = new ConfirmacaoConversor().converter(Confirmacao.class.cast(titulo), TituloVO.class);
				} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
					tituloVO = new RetornoConversor().converter(Retorno.class.cast(titulo), TituloVO.class);
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

	public File fabricaArquivoDesistenciaProtestoTXT(File file, RemessaDesistenciaProtesto remessa) {
		return geradorDeArquivosTXT.gerar(conversorDesistenciaProtesto.converterParaVO(remessa), file);
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
					+ Integer.parseInt(remessaVO.getCabecalho().getQtdTitulosRemessa())
					+ Integer.parseInt(remessaVO.getCabecalho().getQtdIndicacoesRemessa())
					+ Integer.parseInt(remessaVO.getCabecalho().getQtdOriginaisRemessa());
		}
		return Integer.toString(somatorioQtdRemessa);
	}

	private void gerarTXT(File file, RemessaVO remessaVO) {
		geradorDeArquivosTXT.gerar(remessaVO, file);
	}

	private void gerarTXT(File file, List<RemessaVO> remessasVO) {
		geradorDeArquivosTXT.gerar(remessasVO, file);
	}
}