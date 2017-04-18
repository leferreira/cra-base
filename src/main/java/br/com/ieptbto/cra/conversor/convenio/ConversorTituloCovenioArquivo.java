package br.com.ieptbto.cra.conversor.convenio;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Avalista;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.EspecieTituloEntradaManual;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.enumeration.regra.TipoIdentificacaoRegistro;
import br.com.ieptbto.cra.mediator.AvalistaMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;
import br.com.ieptbto.cra.processador.Processador;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class ConversorTituloCovenioArquivo extends Processador {

	@Autowired
	private InstituicaoMediator instituicaoMediator;
	@Autowired
	private TipoArquivoMediator tipoArquivoMediator;
	@Autowired
	private RemessaMediator remessaMediator;
	@Autowired
	private AvalistaMediator avalistaMediator;

	private static final int NUMERO_SEQUENCIAL_REMESSA = 1;
	private Usuario usuario;
	private HashMap<chaveTitulo, TituloFiliado> mapaTitulos;
	private HashMap<String, Arquivo> mapaArquivos;
	private List<Remessa> remessas;
	private List<Arquivo> arquivos;
	
	/**
	 * @param titulosConvenios
	 * @param usuario
	 * @return
	 */
	public List<Arquivo> processarRemessa(List<TituloFiliado> titulosConvenios, Usuario usuario) {
		this.usuario = usuario;
		this.mapaTitulos = null;
		this.mapaArquivos = null;
		this.remessas = null;
		this.arquivos = null;

		agruparTitulosFiliado(titulosConvenios);
		gerarRemessas();
		criarArquivos();
		return getArquivos();
	}

	private void criarArquivos() {
		for (Remessa remessa : getRemessas()) {
			if (getMapaArquivos().containsKey(remessa.getInstituicaoOrigem().getCodigoCompensacao())) {
				getMapaArquivos().get(remessa.getInstituicaoOrigem().getCodigoCompensacao()).getRemessas().add(remessa);
			} else {
				getMapaArquivos().put(remessa.getInstituicaoOrigem().getCodigoCompensacao(), criarArquivo(remessa));
			}
		}
	}

	private void gerarRemessas() {
		Map<String, Remessa> mapaRemessa = new HashMap<String, Remessa>();
		for (chaveTitulo key : getMapaTitulos().keySet()) {
			if (mapaRemessa.containsKey(key.toString())) {
				atualizaRemessa(mapaRemessa.get(key.toString()), getMapaTitulos().get(key));
			} else {
				mapaRemessa.put(key.toString(), criarRemessa(getMapaTitulos().get(key)));
			}
		}
		setRemessas(new ArrayList<Remessa>(mapaRemessa.values()));
	}

	private Remessa criarRemessa(TituloFiliado tituloFiliado) {
		List<Titulo> listaTitulos = new ArrayList<Titulo>();
		Remessa remessa = new Remessa();
		remessa.setDataRecebimento(new LocalDate());
		remessa.setInstituicaoDestino(setInstituicaoDestino(tituloFiliado));
		remessa.setTitulos(setTitulosRemessa(listaTitulos, tituloFiliado));
		remessa.setInstituicaoOrigem(setInstituicaoOrigem(tituloFiliado));
		remessa.setCabecalho(setCabecalho(tituloFiliado, remessa.getInstituicaoDestino()));
		remessa.setRodape(setRodape(tituloFiliado, listaTitulos));
		remessa.getCabecalho().setRemessa(remessa);
		remessa.getRodape().setRemessa(remessa);
		remessa.setStatusDownload(StatusDownload.AGUARDANDO);

		int numeroControleDevedor = 2;
		for (Avalista avalista : avalistaMediator.buscarAvalistasPorTitulo(tituloFiliado)) {
			if (avalista.getNome() != null) {
				if (StringUtils.isNotEmpty(avalista.getNome())) {
					incluirAvalistaNaRemessa(remessa, tituloFiliado, avalista, numeroControleDevedor);
					numeroControleDevedor = numeroControleDevedor++;
				}
			}
		}
		return remessa;
	}

	private Arquivo criarArquivo(Remessa remessa) {
		Arquivo arquivo = new Arquivo();
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setHoraEnvio(new LocalTime());
		arquivo.setInstituicaoEnvio(remessa.getInstituicaoOrigem());
		arquivo.setInstituicaoRecebe(instituicaoMediator.buscarCRA());
		arquivo.setNomeArquivo(gerarNomeArquivo(remessa.getInstituicaoOrigem()));
		arquivo.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoFebraban.REMESSA));
		arquivo.setUsuarioEnvio(getUsuario());
		arquivo.setStatusArquivo(gerarStatusArquivo());
		arquivo.setRemessas(new ArrayList<Remessa>());
		arquivo.getRemessas().add(remessa);
		getArquivos().add(arquivo);
		return arquivo;
	}

	private StatusArquivo gerarStatusArquivo() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setStatusDownload(StatusDownload.ENVIADO);
		return status;
	}

	private String gerarNomeArquivo(Instituicao instituicao) {
		return TipoArquivoFebraban.REMESSA.getConstante() + instituicao.getCodigoCompensacao() + gerarDataArquivo() + NUMERO_SEQUENCIAL_REMESSA;
	}

	private String gerarDataArquivo() {
		SimpleDateFormat dataPadraArquivo = new SimpleDateFormat("ddMM.yy");
		return dataPadraArquivo.format(new Date()).toString();
	}

	private Instituicao setInstituicaoOrigem(TituloFiliado tituloFiliado) {
		return tituloFiliado.getFiliado().getInstituicaoConvenio();
	}

	private List<Titulo> setTitulosRemessa(List<Titulo> listaTitulos, TituloFiliado tituloFiliado) {
		TituloRemessa tituloRemessa = new TituloRemessa();
		tituloRemessa.parseTituloFiliado(tituloFiliado);

		if (tituloFiliado.getAnexo() != null) {
			Anexo anexo = new Anexo();
			anexo.setDocumentoAnexo(tituloFiliado.getAnexoAsString());
			anexo.setTitulo(tituloRemessa);

			tituloRemessa.setAnexos(new ArrayList<Anexo>());
			tituloRemessa.getAnexos().add(anexo);
		}
		tituloRemessa.setNumeroSequencialArquivo(Integer.toString(listaTitulos.size() + 2));
		listaTitulos.add(tituloRemessa);
		return listaTitulos;
	}

	private void atualizaRemessa(Remessa remessa, TituloFiliado tituloFiliado) {
		int quantidadeRegistros = remessa.getCabecalho().getQtdRegistrosRemessa();
		int quantidadeTitulos = remessa.getCabecalho().getQtdTitulosRemessa();
		int quantidadeIndicacoes = remessa.getCabecalho().getQtdIndicacoesRemessa();
		int quantidadeOriginais = remessa.getCabecalho().getQtdOriginaisRemessa();
		BigDecimal valorSaldo = remessa.getRodape().getSomatorioValorRemessa().add(tituloFiliado.getValorSaldoTitulo());

		TituloRemessa titulo = new TituloRemessa();
		titulo.parseTituloFiliado(tituloFiliado);
		if (tituloFiliado.getAnexo() != null) {
			Anexo anexo = new Anexo();
			anexo.setDocumentoAnexo(tituloFiliado.getAnexoAsString());
			anexo.setTitulo(titulo);

			titulo.setAnexos(new ArrayList<Anexo>());
			titulo.getAnexos().add(anexo);
		}
		titulo.setNumeroSequencialArquivo(Integer.toString(remessa.getTitulos().size() + 2));
		remessa.getTitulos().add(titulo);

		if (tituloFiliado.getEspecieTitulo().equals(EspecieTituloEntradaManual.DMI)) {
			quantidadeIndicacoes = quantidadeIndicacoes + 1;
		} else {
			quantidadeOriginais = quantidadeOriginais + 1;
		}
		remessa.getCabecalho().setQtdRegistrosRemessa(quantidadeRegistros + 1);
		remessa.getCabecalho().setQtdTitulosRemessa(quantidadeTitulos + 1);
		remessa.getCabecalho().setQtdIndicacoesRemessa(quantidadeIndicacoes);
		remessa.getCabecalho().setQtdOriginaisRemessa(quantidadeOriginais);
		BigDecimal somatorioQtdRemessa =
				new BigDecimal(quantidadeRegistros + quantidadeTitulos + quantidadeOriginais + quantidadeIndicacoes);
		remessa.getRodape().setSomatorioQtdRemessa(somatorioQtdRemessa);
		remessa.getRodape().setSomatorioValorRemessa(valorSaldo);
		remessa.getRodape().setNumeroSequencialRegistroArquivo(
				Integer.toString(Integer.parseInt(remessa.getRodape().getNumeroSequencialRegistroArquivo()) + 1));

		int numeroControleDevedor = 2;
		for (Avalista avalista : avalistaMediator.buscarAvalistasPorTitulo(tituloFiliado)) {
			if (avalista.getNome() != null) {
				if (StringUtils.isNotEmpty(avalista.getNome())) {
					incluirAvalistaNaRemessa(remessa, tituloFiliado, avalista, numeroControleDevedor);
					numeroControleDevedor = numeroControleDevedor++;
				}
			}
		}
	}

	private void incluirAvalistaNaRemessa(Remessa remessa, TituloFiliado tituloFiliado, Avalista avalista, int numeroControleDevedor) {
		int quantidadeRegistros = remessa.getCabecalho().getQtdRegistrosRemessa();
		int quantidadeTitulos = remessa.getCabecalho().getQtdTitulosRemessa();
		int quantidadeIndicacoes = remessa.getCabecalho().getQtdIndicacoesRemessa();
		int quantidadeOriginais = remessa.getCabecalho().getQtdOriginaisRemessa();

		TituloRemessa titulo = new TituloRemessa();
		titulo.parseAvalista(tituloFiliado, avalista);
		titulo.setNumeroControleDevedor(numeroControleDevedor);
		titulo.setNumeroSequencialArquivo(Integer.toString(remessa.getTitulos().size() + 2));
		remessa.getTitulos().add(titulo);

		BigDecimal somatorioQtdRemessa = new BigDecimal(quantidadeRegistros + quantidadeTitulos + quantidadeOriginais + quantidadeIndicacoes);
		remessa.getRodape().setSomatorioQtdRemessa(somatorioQtdRemessa);
		remessa.getRodape().setNumeroSequencialRegistroArquivo(Integer.toString(Integer.parseInt(remessa.getRodape().getNumeroSequencialRegistroArquivo()) + 1));
	}

	private Instituicao setInstituicaoDestino(TituloFiliado tituloFiliado) {
		return instituicaoMediator.getCartorioPorCodigoIBGE(tituloFiliado.getPracaProtesto().getCodigoIBGE());
	}

	private Rodape setRodape(TituloFiliado tituloFiliado, List<Titulo> titulos) {
		Rodape rodape = new Rodape();
		rodape.setDataMovimento(new LocalDate());
		rodape.setIdentificacaoRegistro(TipoIdentificacaoRegistro.RODAPE);
		rodape.setNomePortador(RemoverAcentosUtil.removeAcentos(tituloFiliado.getFiliado().getInstituicaoConvenio().getRazaoSocial()));
		rodape.setNumeroCodigoPortador(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao());
		rodape.getSomatorioQtdRemessa().add(new BigDecimal(3));
		rodape.getSomatorioValorRemessa().add(tituloFiliado.getValorSaldoTitulo());
		rodape.setNumeroSequencialRegistroArquivo(Integer.toString(titulos.size() + 2));
		return rodape;
	}

	private CabecalhoRemessa setCabecalho(TituloFiliado tituloFiliado, Instituicao instituicaoDestino) {
		CabecalhoRemessa cabecalho = new CabecalhoRemessa();
		cabecalho.setDataMovimento(new LocalDate());
		cabecalho.setAgenciaCentralizadora(StringUtils.leftPad(tituloFiliado.getFiliado().getInstituicaoConvenio().getAgenciaCentralizadora(), 6, "0"));
		cabecalho.setCodigoMunicipio(tituloFiliado.getPracaProtesto().getCodigoIBGE());
		cabecalho.setIdentificacaoRegistro(TipoIdentificacaoRegistro.CABECALHO);
		cabecalho.setIdentificacaoTransacaoRemetente("BFO");
		cabecalho.setIdentificacaoTransacaoDestinatario("SDT");
		cabecalho.setIdentificacaoTransacaoTipo("TPR");
		cabecalho.setNomePortador(RemoverAcentosUtil.removeAcentos(tituloFiliado.getFiliado().getInstituicaoConvenio().getRazaoSocial()));
		cabecalho.setNumeroCodigoPortador(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao());
		cabecalho.setNumeroSequencialRegistroArquivo("0001");
		cabecalho.setNumeroSequencialRemessa(gerarNumeroSequencial(tituloFiliado.getFiliado().getInstituicaoConvenio(), instituicaoDestino));
		cabecalho.setVersaoLayout("043");
		cabecalho.setQtdTitulosRemessa(1);
		cabecalho.setQtdRegistrosRemessa(1);
		if (tituloFiliado.getEspecieTitulo().equals(EspecieTituloEntradaManual.DMI)) {
			cabecalho.setQtdIndicacoesRemessa(1);
			cabecalho.setQtdOriginaisRemessa(0);
		} else {
			cabecalho.setQtdIndicacoesRemessa(0);
			cabecalho.setQtdOriginaisRemessa(1);
		}
		return cabecalho;
	}

	private void agruparTitulosFiliado(List<TituloFiliado> titulosFiliados) {
		for (TituloFiliado tituloFiliado : titulosFiliados) {
			getMapaTitulos().put(new chaveTitulo(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao(),
					tituloFiliado.getPracaProtesto().getCodigoIBGE()), tituloFiliado);
		}
	}

	private Integer gerarNumeroSequencial(Instituicao convenio, Instituicao instituicaoDestino) {
		int quantidadeAtual = remessaMediator.getNumeroSequencialConvenio(convenio, instituicaoDestino);
		return Integer.parseInt(StringUtils.leftPad(Integer.toString(quantidadeAtual + 1), 5, "0"));
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setMapatitulos(HashMap<chaveTitulo, TituloFiliado> mapatitulos) {
		this.mapaTitulos = mapatitulos;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setRemessas(List<Remessa> remessas) {
		this.remessas = remessas;
	}

	public void setMapaArquivos(HashMap<String, Arquivo> mapaArquivos) {
		this.mapaArquivos = mapaArquivos;
	}

	public Map<chaveTitulo, TituloFiliado> getMapaTitulos() {
		if (mapaTitulos == null) {
			mapaTitulos = new HashMap<chaveTitulo, TituloFiliado>();
		}
		return mapaTitulos;
	}

	public List<Remessa> getRemessas() {
		if (remessas == null) {
			remessas = new ArrayList<Remessa>();
		}
		return remessas;
	}

	public Map<String, Arquivo> getMapaArquivos() {
		if (mapaArquivos == null) {
			mapaArquivos = new HashMap<String, Arquivo>();
		}
		return mapaArquivos;
	}

	public List<Arquivo> getArquivos() {
		if (arquivos == null) {
			arquivos = new ArrayList<Arquivo>();
		}
		return arquivos;
	}

	public void setArquivos(List<Arquivo> arquivos) {
		this.arquivos = arquivos;
	}
}

class chaveTitulo {
	private String codigoPortador;
	private String codigoMunicipio;

	public chaveTitulo(String codigoPortador, String codigoMunicipio) {
		this.codigoPortador = codigoPortador;
		this.codigoMunicipio = codigoMunicipio;
	}

	public String getCodigoPortador() {
		return codigoPortador;
	}

	public String getCodigoMunicipio() {
		return codigoMunicipio;
	}

	public void setCodigoPortador(String codigoPortador) {
		this.codigoPortador = codigoPortador;
	}

	public void setCodigoMunicipio(String codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}

	@Override
	public String toString() {
		return String.valueOf(getCodigoPortador()).concat(getCodigoMunicipio());
	}
}