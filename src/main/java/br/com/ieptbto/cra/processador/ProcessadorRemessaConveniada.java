package br.com.ieptbto.cra.processador;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
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
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoRegistro;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class ProcessadorRemessaConveniada extends Processador {

	@Autowired
	private InstituicaoMediator instituicaoMediator;
	@Autowired
	private TipoArquivoMediator tipoArquivoMediator;
	private Map<chaveTitulo, TituloFiliado> mapaTitulos;
	private List<TituloFiliado> listTitulosFiliado;
	private Usuario usuario;
	private Arquivo arquivo;
	private List<Remessa> remessas;

	public Arquivo processar(List<TituloFiliado> listaTitulosConvenios, Usuario usuario) {
		this.listTitulosFiliado = listaTitulosConvenios;
		this.usuario = usuario;
		agruparTitulosFiliado();
		gerarRemessas();
		getArquivo().setRemessas(getRemessas());

		return getArquivo();
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
		remessa.setArquivo(criarArquivo(tituloFiliado));
		remessa.setCabecalho(setCabecalho(tituloFiliado));
		remessa.setDataRecebimento(new LocalDate());
		remessa.setRodape(setRodape(tituloFiliado));
		remessa.getRodape().setRemessa(remessa);
		remessa.setInstituicaoDestino(setInstituicaoDestino(tituloFiliado));
		remessa.setTitulos(setTitulosRemessa(listaTitulos, tituloFiliado));
		remessa.setInstituicaoOrigem(setInstituicaoOrigem(tituloFiliado));
		return remessa;
	}

	private Arquivo criarArquivo(TituloFiliado tituloFiliado) {
		setArquivo(new Arquivo());
		getArquivo().setDataEnvio(tituloFiliado.getDataEnvioCRA());
		getArquivo().setInstituicaoEnvio(tituloFiliado.getFiliado().getInstituicaoConvenio());
		getArquivo().setInstituicaoRecebe(instituicaoMediator.buscarCRA());
		getArquivo().setNomeArquivo(montarNomeArquivo(tituloFiliado));
		getArquivo().setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.REMESSA));
		getArquivo().setUsuarioEnvio(getUsuario());
		getArquivo().setStatusArquivo(gerarStatusArquivo());
		return getArquivo();
	}

	private StatusArquivo gerarStatusArquivo() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new Date());
		status.setStatus("AGUARDANDO");
		return status;
	}

	public static void main(String[] args) {
		new ProcessadorRemessaConveniada().montarNomeArquivo(new TituloFiliado());
	}

	private String montarNomeArquivo(TituloFiliado tituloFiliado) {
		String dataDiaMes = DataUtil.getDataAtual().substring(0, 5).replace("/", "");
		String dataAno = DataUtil.getDataAtual().substring(8, 10);
		return "B" + tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao() + dataDiaMes + "." + dataAno + "1";
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
		rodape.setNomePortador(tituloFiliado.getFiliado().getInstituicaoConvenio().getRazaoSocial());
		rodape.setNumeroCodigoPortador(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao());
		rodape.getSomatorioQtdRemessa().add(tituloFiliado.getValorSaldoTitulo());
		rodape.getSomatorioValorRemessa().add(tituloFiliado.getValorSaldoTitulo());
		return rodape;
	}

	private CabecalhoRemessa setCabecalho(TituloFiliado tituloFiliado) {
		CabecalhoRemessa cabecalho = new CabecalhoRemessa();
		cabecalho.setAgenciaCentralizadora(tituloFiliado.getFiliado().getInstituicaoConvenio().getAgenciaCentralizadora());
		cabecalho.setCodigoMunicipio(tituloFiliado.getPracaProtesto().getCodigoIBGE());
		cabecalho.setIdentificacaoRegistro(TipoRegistro.CABECALHO);
		cabecalho.setIdentificacaoTransacaoRemetente("BFO");
		cabecalho.setIdentificacaoTransacaoDestinatario("SDT");
		cabecalho.setIdentificacaoTransacaoTipo("TPR");
		cabecalho.setNomePortador(tituloFiliado.getFiliado().getInstituicaoConvenio().getRazaoSocial());
		cabecalho.setNumeroCodigoPortador(tituloFiliado.getFiliado().getInstituicaoConvenio().getCodigoCompensacao());
		cabecalho.setNumeroSequencialRegistroArquivo("0001");
		cabecalho.setVersaoLayout("043");
		cabecalho.setQtdTitulosRemessa(1);
		cabecalho.setQtdOriginaisRemessa(1);
		cabecalho.setQtdRegistrosRemessa(1);
		cabecalho.setDataMovimento(new LocalDate());

		return cabecalho;
	}

	private void agruparTitulosFiliado() {
		for (TituloFiliado tituloFiliado : getListTitulosFiliado()) {
			getMapaTitulos().put(new chaveTitulo(tituloFiliado.getFiliado().getId(), tituloFiliado.getPracaProtesto().getCodigoIBGE()),
			        tituloFiliado);
		}

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

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public List<Remessa> getRemessas() {
		return remessas;
	}

	public void setRemessas(List<Remessa> remessas) {
		this.remessas = remessas;
	}

}

class chaveTitulo {
	private int idFiliado;
	private String codigoMunicipio;

	public chaveTitulo(int idFiliado, String codigoMunicipio) {
		this.idFiliado = idFiliado;
		this.codigoMunicipio = codigoMunicipio;
	}

	public int getIdFiliado() {
		return idFiliado;
	}

	public String getCodigoMunicipio() {
		return codigoMunicipio;
	}

	public void setIdFiliado(int idFiliado) {
		this.idFiliado = idFiliado;
	}

	public void setCodigoMunicipio(String codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}

	@Override
	public String toString() {
		return String.valueOf(getIdFiliado()).concat(getCodigoMunicipio());
	}
}
