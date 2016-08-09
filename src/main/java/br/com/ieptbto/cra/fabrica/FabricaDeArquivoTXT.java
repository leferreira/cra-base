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
import br.com.ieptbto.cra.exception.InfraException;
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

	private Remessa remessa;
	private List<Remessa> remessas;
	private RemessaDesistenciaProtesto remessaDesistenciaProtesto;
	private RemessaCancelamentoProtesto remessaCancelamentoProtesto;
	private RemessaAutorizacaoCancelamento remessaAutorizacaoCancelamento;

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

	public File fabricaArquivoDesistenciaProtestoTXT(File arquivoFisico, RemessaDesistenciaProtesto remessa, List<Exception> erros) {
		this.file = arquivoFisico;
		this.erros = erros;
		this.remessaDesistenciaProtesto = remessa;

		List<Arquivo> arquivos = new ArrayList<Arquivo>();
		arquivos.add(getArquivo());
		return geradorDeArquivosTXT.gerar(conversorDesistenciaProtesto.converter(this.remessaDesistenciaProtesto), getFile());
	}

	public File fabricaArquivoCancelamentoProtestoTXT(File arquivoFisico, RemessaCancelamentoProtesto remessa, List<Exception> erros) {
		this.file = arquivoFisico;
		this.erros = erros;
		this.remessaCancelamentoProtesto = remessa;

		List<Arquivo> arquivos = new ArrayList<Arquivo>();
		arquivos.add(getArquivo());
		return geradorDeArquivosTXT.gerar(conversorCancelamentoProtesto.converter(this.remessaCancelamentoProtesto), getFile());
	}

	public File fabricaArquivoAutorizacaoCancelamentoTXT(File arquivoFisico, RemessaAutorizacaoCancelamento remessa, List<Exception> erros) {
		this.file = arquivoFisico;
		this.erros = erros;
		this.remessaAutorizacaoCancelamento = remessa;

		List<Arquivo> arquivos = new ArrayList<Arquivo>();
		arquivos.add(getArquivo());
		return geradorDeArquivosTXT.gerar(conversorCancelamentoProtesto.converter(this.remessaAutorizacaoCancelamento), getFile());
	}

	public FabricaDeArquivoTXT fabricaArquivoTXT(File arquivoTXT, List<Remessa> remessas, List<Exception> erros) {
		this.file = arquivoTXT;
		this.erros = erros;
		this.remessas = remessas;

		return this;
	}

	public void converterParaTXT() {
		RemessaVO remessaVO = new RemessaVO();
		remessaVO.setTitulos(new ArrayList<TituloVO>());
		BigDecimal valorTotalTitulos = BigDecimal.ZERO;

		remessaVO.setCabecalho(new CabecalhoConversor().converter(getRemessa().getCabecalho(), CabecalhoVO.class));
		remessaVO.setRodapes(new RodapeConversor().converter(getRemessa().getRodape(), RodapeVO.class));

		int contSequencial = 2;
		for (Titulo titulo : getRemessa().getTitulos()) {
			TituloVO tituloVO = new TituloVO();
			if (TipoArquivoEnum.REMESSA.equals(getRemessa().getArquivo().getTipoArquivo().getTipoArquivo())) {
				tituloVO = new TituloConversor().converter(TituloRemessa.class.cast(titulo), TituloVO.class);
			} else if (TipoArquivoEnum.CONFIRMACAO.equals(getRemessa().getArquivo().getTipoArquivo().getTipoArquivo())) {
				tituloVO = new ConfirmacaoConversor().converter(Confirmacao.class.cast(titulo), TituloVO.class);
			} else if (TipoArquivoEnum.RETORNO.equals(getRemessa().getArquivo().getTipoArquivo().getTipoArquivo())) {
				tituloVO = new RetornoConversor().converter(Retorno.class.cast(titulo), TituloVO.class);
			} else {
				throw new InfraException("Tipo de Arquivo não identificado");
			}
			tituloVO.setNumeroSequencialArquivo(String.valueOf(contSequencial));
			valorTotalTitulos = valorTotalTitulos.add(titulo.getSaldoTitulo());
			remessaVO.getTitulos().add(tituloVO);
			contSequencial++;
		}
		remessaVO.getCabecalho().setQtdTitulosRemessa(String.valueOf(remessaVO.getTitulos().size()));
		remessaVO.getRodape().setSomatorioQtdRemessa(somatorioSegurancaQuantidadeRemessa(remessaVO));
		remessaVO.getRodape().setSomatorioValorRemessa(new BigDecimalConversor().getValorConvertidoParaString(valorTotalTitulos));
		remessaVO.setIdentificacaoRegistro(getRemessa().getCabecalho().getIdentificacaoRegistro().getConstante());
		remessaVO.setTipoArquivo(getRemessa().getArquivo().getTipoArquivo());
		remessaVO.getRodape().setNumeroSequencialRegistroArquivo(String.valueOf(contSequencial));

		gerarTXT(remessaVO);
	}

	public void converterParaArquivoTXT() {
		List<RemessaVO> remessasVO = new ArrayList<RemessaVO>();

		for (Remessa remessa : getRemessas()) {
			this.arquivo = remessa.getArquivo();
			RemessaVO remessaVO = new RemessaVO();
			remessaVO.setTitulos(new ArrayList<TituloVO>());
			BigDecimal valorTotalTitulos = BigDecimal.ZERO;

			remessaVO.setCabecalho(new CabecalhoConversor().converter(remessa.getCabecalho(), CabecalhoVO.class));
			remessaVO.setRodapes(new RodapeConversor().converter(remessa.getRodape(), RodapeVO.class));

			int contSequencial = 2;
			for (Titulo titulo : remessa.getTitulos()) {
				TituloVO tituloVO = new TituloVO();
				if (TipoArquivoEnum.REMESSA.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
					tituloVO = new TituloConversor().converter(TituloRemessa.class.cast(titulo), TituloVO.class);
				} else if (TipoArquivoEnum.CONFIRMACAO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
					tituloVO = new ConfirmacaoConversor().converter(Confirmacao.class.cast(titulo), TituloVO.class);
				} else if (TipoArquivoEnum.RETORNO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
					tituloVO = new RetornoConversor().converter(Retorno.class.cast(titulo), TituloVO.class);
				} else {
					throw new InfraException("Tipo de Arquivo não identificado");
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

		gerarTXT(remessasVO);
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

	private void gerarTXT(RemessaVO remessaVO) {
		geradorDeArquivosTXT.gerar(remessaVO, getFile());
	}

	private void gerarTXT(List<RemessaVO> remessasVO) {
		geradorDeArquivosTXT.gerar(remessasVO, getFile());
	}

	public Remessa getRemessa() {
		return remessa;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	public List<Remessa> getRemessas() {
		return remessas;
	}
}