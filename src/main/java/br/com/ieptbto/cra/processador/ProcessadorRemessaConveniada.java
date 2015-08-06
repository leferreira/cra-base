package br.com.ieptbto.cra.processador;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoRegistro;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;
import br.com.ieptbto.cra.util.RemoveAcentosUtil;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class ProcessadorRemessaConveniada extends Processador {

	private static final int NUMERO_SEQUENCIAL_REMESSA = 1;
	
	@Autowired
	private InstituicaoMediator instituicaoMediator;
	@Autowired
	private TipoArquivoMediator tipoArquivoMediator;
	@Autowired
	private RemessaMediator remessaMediator;
	private Map<chaveTitulo, TituloFiliado> mapaTitulos;
	private Map<String, Arquivo> mapaArquivos;
	private List<TituloFiliado> listTitulosFiliado;
	private Usuario usuario;
	private List<Remessa> remessas;
	private List<Arquivo> arquivos;

	public List<Arquivo> processar(List<TituloFiliado> listaTitulosConvenios, Usuario usuario) {
		this.listTitulosFiliado = listaTitulosConvenios;
		this.usuario = usuario;
		agruparTitulosFiliado();
		gerarRemessas();
		criarArquivos();

		return getArquivos();
	}
	
	private void criarArquivos() {
		for (Remessa remessa: getRemessas()){
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
		remessa.setRodape(setRodape(tituloFiliado));
		remessa.getCabecalho().setRemessa(remessa);
		remessa.getRodape().setRemessa(remessa);
		remessa.setStatusRemessa(StatusRemessa.AGUARDANDO);
		return remessa;
	}

	private Arquivo criarArquivo(Remessa remessa) {
		Arquivo arquivo = new Arquivo();
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setInstituicaoEnvio(remessa.getInstituicaoOrigem());
		arquivo.setInstituicaoRecebe(instituicaoMediator.buscarCRA());
		arquivo.setNomeArquivo(gerarNomeArquivo(remessa.getInstituicaoOrigem()));
		arquivo.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.REMESSA));
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
		status.setSituacaoArquivo(SituacaoArquivo.ENVIADO);
		return status;
	}

	private String gerarNomeArquivo(Instituicao instituicao) {
		
		return TipoArquivoEnum.REMESSA.getConstante() 
			+ instituicao.getCodigoCompensacao()
			+ gerarDataArquivo()
			+ NUMERO_SEQUENCIAL_REMESSA;
	}
	
	private String gerarDataArquivo(){
		SimpleDateFormat dataPadraArquivo = new SimpleDateFormat("ddMM.yy");
		return dataPadraArquivo.format(new Date()).toString();
	}

	private Instituicao setInstituicaoOrigem(TituloFiliado tituloFiliado) {
		return tituloFiliado.getFiliado().getInstituicaoConvenio();
	}

	private void atualizaRemessa(Remessa remessa, TituloFiliado tituloFiliado) {
		int quantidade = remessa.getCabecalho().getQtdTitulosRemessa() + 1;
		BigDecimal valorSaldo = remessa.getRodape().getSomatorioValorRemessa().add(tituloFiliado.getValorSaldoTitulo());
		remessa.getCabecalho().setQtdIndicacoesRemessa(quantidade);
		remessa.getCabecalho().setQtdOriginaisRemessa(quantidade);
		remessa.getCabecalho().setQtdTitulosRemessa(quantidade);
		remessa.getCabecalho().setQtdRegistrosRemessa(quantidade);
		remessa.getRodape().setSomatorioValorRemessa(valorSaldo);
		remessa.getRodape().setSomatorioQtdRemessa(valorSaldo);

		TituloRemessa titulo = new TituloRemessa();
		titulo.parseTituloFiliado(tituloFiliado);
		remessa.getTitulos().add(titulo);
	}

	private List<Titulo> setTitulosRemessa(List<Titulo> listaTitulos, TituloFiliado tituloFiliado) {
		TituloRemessa tituloRemessa = new TituloRemessa();
		tituloRemessa.parseTituloFiliado(tituloFiliado);
		listaTitulos.add(tituloRemessa);
		return listaTitulos;
	}

	private Instituicao setInstituicaoDestino(TituloFiliado tituloFiliado) {
		return instituicaoMediator.getInstituicaoPorCodigoIBGE(tituloFiliado.getPracaProtesto().getCodigoIBGE());
	}

	private Rodape setRodape(TituloFiliado tituloFiliado) {
		Rodape rodape = new Rodape();
		rodape.setDataMovimento(new LocalDate());
		rodape.setIdentificacaoRegistro(TipoRegistro.RODAPE);
		rodape.setNomePortador(RemoveAcentosUtil.removeAcentos(tituloFiliado.getFiliado().getInstituicaoConvenio().getRazaoSocial()));
		rodape.setNumeroCodigoPortador(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao());
		rodape.getSomatorioQtdRemessa().add(tituloFiliado.getValorSaldoTitulo());
		rodape.getSomatorioValorRemessa().add(tituloFiliado.getValorSaldoTitulo());
		return rodape;
	}

	private CabecalhoRemessa setCabecalho(TituloFiliado tituloFiliado, Instituicao instituicaoDestino) {
		CabecalhoRemessa cabecalho = new CabecalhoRemessa();
		cabecalho.setAgenciaCentralizadora(tituloFiliado.getFiliado().getInstituicaoConvenio().getAgenciaCentralizadora());
		cabecalho.setCodigoMunicipio(tituloFiliado.getPracaProtesto().getCodigoIBGE());
		cabecalho.setIdentificacaoRegistro(TipoRegistro.CABECALHO);
		cabecalho.setIdentificacaoTransacaoRemetente("BFO");
		cabecalho.setIdentificacaoTransacaoDestinatario("SDT");
		cabecalho.setIdentificacaoTransacaoTipo("TPR");
		cabecalho.setNomePortador(RemoveAcentosUtil.removeAcentos(tituloFiliado.getFiliado().getInstituicaoConvenio().getRazaoSocial()));
		cabecalho.setNumeroCodigoPortador(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao());
		cabecalho.setNumeroSequencialRegistroArquivo("0001");
		cabecalho.setNumeroSequencialRemessa(gerarNumeroSequencial(tituloFiliado.getFiliado().getInstituicaoConvenio(), instituicaoDestino));
		cabecalho.setVersaoLayout("043");
		cabecalho.setQtdTitulosRemessa(1);
		cabecalho.setQtdOriginaisRemessa(1);
		cabecalho.setQtdRegistrosRemessa(1);
		cabecalho.setDataMovimento(new LocalDate());

		return cabecalho;
	}

	private void agruparTitulosFiliado() {
		for (TituloFiliado tituloFiliado : getListTitulosFiliado()) {
			getMapaTitulos().put(new chaveTitulo(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao(), tituloFiliado.getPracaProtesto().getCodigoIBGE()),
			        tituloFiliado);
		}
	}
	
	private Integer gerarNumeroSequencial(Instituicao convenio, Instituicao instituicaoDestino) {
		int quantidadeAtual = remessaMediator.getNumeroSequencialConvenio(convenio, instituicaoDestino);
		return Integer.parseInt(StringUtils.leftPad(Integer.toString(quantidadeAtual), 5, "0"));
	}
	
	public Map<chaveTitulo, TituloFiliado> getMapaTitulos() {
		if (mapaTitulos == null) {
			mapaTitulos = new HashMap<chaveTitulo, TituloFiliado>();
		}
		return mapaTitulos;
	}

	public List<TituloFiliado> getListTitulosFiliado() {
		return listTitulosFiliado;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setMapatitulos(Map<chaveTitulo, TituloFiliado> mapatitulos) {
		this.mapaTitulos = mapatitulos;
	}

	public void setListTitulosFiliado(List<TituloFiliado> listTitulosFiliado) {
		this.listTitulosFiliado = listTitulosFiliado;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Remessa> getRemessas() {
		return remessas;
	}

	public void setRemessas(List<Remessa> remessas) {
		this.remessas = remessas;
	}

	public Map<String, Arquivo> getMapaArquivos() {
		if (mapaArquivos == null) {
			mapaArquivos = new HashMap<String, Arquivo>();
		}
		return mapaArquivos;
	}

	public void setMapaArquivos(Map<String, Arquivo> mapaArquivos) {
		this.mapaArquivos = mapaArquivos;
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