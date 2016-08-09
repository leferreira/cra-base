package br.com.ieptbto.cra.mediator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.BatimentoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.RetornoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.BatimentoDeposito;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.fabrica.FabricaDeArquivoXML;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RetornoMediator extends BaseMediator {

	private static final int NUMERO_SEQUENCIAL_RETORNO = 1;

	@Autowired
	private InstituicaoDAO instituicaoDAO;
	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	private RetornoDAO retornoDAO;
	@Autowired
	private BatimentoDAO batimentoDAO;
	@Autowired
	private TipoArquivoMediator tipoArquivoMediator;
	@Autowired
	private FabricaDeArquivoXML fabricaDeArquivosXML;
	@Autowired
	private ArquivoDAO arquivoDAO;

	private Instituicao cra;
	private TipoArquivo tipoArquivo;

	public Retorno carregarTituloRetornoPorId(int id) {
		Retorno retorno = new Retorno();
		retorno.setId(id);
		return retornoDAO.carregarTituloRetornoPorId(retorno);
	}

	public List<Remessa> buscarRetornosParaBatimento() {
		return retornoDAO.buscarRetornosParaBatimento();
	}

	public List<Remessa> buscarRetornosAguardandoLiberacao(Instituicao instiuicao, LocalDate dataBatimento, boolean dataComoDataLimite) {
		return retornoDAO.buscarRetornosAguardandoLiberacao(instiuicao, dataBatimento, dataComoDataLimite);
	}

	public List<Remessa> buscarRetornosParaPagamentoInstituicao(LocalDate dataBatimento) {
		return retornoDAO.buscarRetornosParaPagamentoInstituicao(dataBatimento);
	}

	public List<Remessa> buscarRetornosConfirmados() {
		return retornoDAO.buscarRetornosConfirmados();
	}

	public BigDecimal buscarValorDeTitulosPagos(Remessa retorno) {
		return retornoDAO.buscarValorDeTitulosPagos(retorno);
	}

	public BigDecimal buscarSomaValorTitulosPagosRemessas(Instituicao instituicao, LocalDate dataBatimento, boolean dataComoDataLimite) {
		return retornoDAO.buscarSomaValorTitulosPagosRemessas(instituicao, dataBatimento, dataComoDataLimite);
	}

	public BigDecimal buscarValorDeCustasCartorio(Remessa retorno) {
		return retornoDAO.buscarValorDeCustasCartorio(retorno);
	}

	public Boolean verificarArquivoRetornoGeradoCra() {
		this.cra = instituicaoDAO.buscarInstituicaoInicial("CRA");
		return retornoDAO.verificarArquivoRetornoGeradoCra(getCra());
	}

	public void salvarBatimentos(List<Remessa> retornos) {
		Boolean arquivoRetornoGeradoHoje = verificarArquivoRetornoGeradoCra();
		for (Remessa retorno : retornos) {
			Batimento batimento = new Batimento();
			batimento.setData(aplicarRegraDataBatimento(arquivoRetornoGeradoHoje));
			batimento.setRemessa(retorno);
			batimento.setDepositosBatimento(new ArrayList<BatimentoDeposito>());

			for (Deposito depositosIdentificado : retorno.getListaDepositos()) {
				BatimentoDeposito depositosBatimento = new BatimentoDeposito();
				depositosBatimento.setBatimento(batimento);
				depositosBatimento.setDeposito(depositosIdentificado);

				batimento.getDepositosBatimento().add(depositosBatimento);
			}
			retornoDAO.salvarBatimento(batimento);
		}
	}

	public LocalDate aplicarRegraDataBatimento(Boolean arquivoRetornoGeradoHoje) {

		if (arquivoRetornoGeradoHoje.equals(false)) {
			return new LocalDate();
		} else if (arquivoRetornoGeradoHoje.equals(true)) {
			Integer contadorDeDias = 1;
			while (true) {
				LocalDate proximoDiaUtil = new LocalDate().plusDays(contadorDeDias);
				Date date = proximoDiaUtil.toDate();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);

				if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
					return proximoDiaUtil;
				}
				contadorDeDias++;
			}
		}
		return null;
	}

	public void removerBatimento(Remessa retorno) {
		Batimento batimento = batimentoDAO.buscarBatimentoDoRetorno(retorno);

		for (BatimentoDeposito depositosBatimento : batimento.getDepositosBatimento()) {
			Deposito deposito = depositosBatimento.getDeposito();
			List<Batimento> batimentosDoDeposito = batimentoDAO.buscarBatimentosDoDeposito(deposito);
			if (batimentosDoDeposito.size() > 1) {
				throw new InfraException("O arquivo de retorno possui um depósito vínculado a mais de um batimento! Não é possível removê-lo...");
			} else if (!batimentosDoDeposito.isEmpty()) {
				deposito.setSituacaoDeposito(SituacaoDeposito.NAO_IDENTIFICADO);
				batimentoDAO.atualizarDeposito(deposito);
			}
		}
		retornoDAO.removerBatimento(retorno, batimento);
	}

	public void liberarRetornoBatimentoInstituicao(List<Remessa> retornoLiberados) {
		retornoDAO.liberarRetornoBatimento(retornoLiberados);
	}

	public void gerarRetornos(Usuario usuarioAcao, List<Remessa> retornos) {
		HashMap<String, Arquivo> arquivosRetorno = new HashMap<String, Arquivo>();
		this.cra = instituicaoDAO.buscarInstituicaoInicial("CRA");
		this.tipoArquivo = tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.RETORNO);

		Arquivo arquivo = null;
		Instituicao instituicaoDestino = new Instituicao();
		for (Remessa retorno : retornos) {

			retorno = retornoDAO.buscarPorPK(retorno);
			if (arquivo == null || !instituicaoDestino.equals(retorno.getInstituicaoDestino())) {
				instituicaoDestino = retorno.getInstituicaoDestino();
				arquivo = criarNovoArquivoDeRetorno(instituicaoDestino, retorno);

				if (!arquivosRetorno.containsKey(instituicaoDestino.getCodigoCompensacao()) && arquivo != null) {
					List<Remessa> retornosDaInstituicao = retornoDAO.buscarRetornosConfirmadosPorInstituicao(instituicaoDestino);
					arquivo.setRemessas(retornosDaInstituicao);
					arquivosRetorno.put(instituicaoDestino.getCodigoCompensacao(), arquivo);
				}
			}
		}
		List<Arquivo> retornosArquivos = new ArrayList<Arquivo>(arquivosRetorno.values());
		retornoDAO.gerarRetornos(usuarioAcao, retornosArquivos);
	}

	private Arquivo criarNovoArquivoDeRetorno(Instituicao destino, Remessa retorno) {
		Arquivo arquivo = new Arquivo();
		arquivo.setTipoArquivo(getTipoArquivo());
		arquivo.setNomeArquivo(gerarNomeArquivoRetorno(retorno));
		arquivo.setInstituicaoRecebe(destino);
		arquivo.setInstituicaoEnvio(getCra());
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setHoraEnvio(new LocalTime());
		return arquivo;
	}

	private String gerarNomeArquivoRetorno(Remessa retorno) {
		return TipoArquivoEnum.RETORNO.getConstante() + retorno.getCabecalho().getNumeroCodigoPortador() + gerarDataArquivo()
				+ NUMERO_SEQUENCIAL_RETORNO;
	}

	private String gerarDataArquivo() {
		SimpleDateFormat dataPadraArquivo = new SimpleDateFormat("ddMM.yy");
		return dataPadraArquivo.format(new Date()).toString();
	}

	public Instituicao getCra() {
		return cra;
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}
}