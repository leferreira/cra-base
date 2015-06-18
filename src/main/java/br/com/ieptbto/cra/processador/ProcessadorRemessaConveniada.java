package br.com.ieptbto.cra.processador;

import java.util.ArrayList;
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
	private TipoArquivoMediator tipoArquivoMediator;
	private Map<chaveTitulo, TituloFiliado> mapaTitulos;
	private List<TituloFiliado> listTitulosFiliado;
	private Usuario usuario;
	private List<Remessa> remessas;
	private Arquivo arquivo;

	public Arquivo processar(List<TituloFiliado> listaTitulosConvenios, Usuario usuario) {
		this.listTitulosFiliado = listaTitulosConvenios;
		this.usuario = usuario;
		agruparTitulosFiliado();
		gerarRemessas();

		return null;
	}

	private void gerarRemessas() {
		Map<chaveTitulo, Remessa> mapaRemessa = new HashMap<chaveTitulo, Remessa>();
		for (chaveTitulo key : getMapaTitulos().keySet()) {
			if (mapaRemessa.containsKey(key)) {
				atualizaRemessa(getMapaTitulos().get(key));

			} else {
				mapaRemessa.put(key, criarRemessa(getMapaTitulos().get(key)));
			}
		}

	}

	private Remessa criarRemessa(TituloFiliado tituloFiliado) {
		List<Titulo> listaTitulos = new ArrayList<Titulo>();
		Remessa remessa = new Remessa();
		remessa.setArquivo(criarArquivo(tituloFiliado));
		remessa.setCabecalho(setCabecalho(tituloFiliado));
		remessa.setDataRecebimento(new LocalDate());
		remessa.setRodape(setRodape(tituloFiliado));
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
		return getArquivo();
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

	private void atualizaRemessa(TituloFiliado tituloFiliado) {
		// TODO Auto-generated method stub

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
