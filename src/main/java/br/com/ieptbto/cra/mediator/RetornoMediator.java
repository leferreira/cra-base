package br.com.ieptbto.cra.mediator;

import br.com.ieptbto.cra.dao.*;
import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.view.ViewBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class RetornoMediator extends BaseMediator {

	@Autowired
	InstituicaoDAO instituicaoDAO;
	@Autowired
	TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	TituloDAO tituloDAO;
	@Autowired
	RetornoDAO retornoDAO;
	@Autowired
	BatimentoDAO batimentoDAO;
	@Autowired
	DepositoDAO depositoDAO;

	public Retorno carregarTituloRetornoPorPk(Integer id) {
		return retornoDAO.buscarPorPK(id, Retorno.class);
	}
	
	public Retorno carregarTituloRetornoPorPk(Retorno retorno) {
		return retornoDAO.buscarPorPK(retorno);
	}

	public Retorno buscarRetornoPorTitulo(TituloRemessa titulo) {
		return tituloDAO.buscarRetornoPorTitulo(titulo);
	}
	
	public Retorno carregarTituloRetorno(Retorno retorno) {
		return tituloDAO.buscarPorPK(retorno, Retorno.class);
	}

	public List<Remessa> buscarRetornosConfirmados() {
		return retornoDAO.buscarRetornosConfirmados();
	}

	/**
	 * conusulta a view de Batimento Retorno e traz os arquivos de retorno já confirmados 
	 * juntamente com os arquivos que não contém títulos pagos e já entram como confirmados 
	 * @return
	 */
	public List<ViewBatimentoRetorno> buscarRetornoConfirmados() {
		return retornoDAO.buscarRetornoConfirmados();
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
	
	public List<Remessa> buscarArquivosRetornosVinculadosPorDeposito(Deposito deposito) {
		return retornoDAO.buscarArquivosRetornosVinculadosPorDeposito(deposito);
	}

	/**
	 * Verifica se já foram gerados os arquivos de retorno pela CRA para os bancos
	 * @return
	 */
	public Boolean verificarArquivoRetornoGeradoCra() {
		Instituicao cra = instituicaoDAO.buscarInstituicaoInicial("CRA");
		return retornoDAO.verificarArquivoRetornoGeradoCra(cra);
	}

	/**
	 * Método para geração de arquivos de retorno para os bancos
	 * 
	 * @param usuarioAcao
	 * @param retornos
	 */
	public List<Arquivo> gerarRetornos(Usuario usuarioAcao, List<ViewBatimentoRetorno> arquivos) {
		HashMap<String, Arquivo> arquivosRetorno = new HashMap<String, Arquivo>();
		Instituicao instituicaoDestino = new Instituicao();

		Arquivo arquivo = null;
		for (ViewBatimentoRetorno viewBatimentoRetorno : arquivos) {
			Remessa retorno = retornoDAO.buscarPorPK(viewBatimentoRetorno.getIdRemessa_Remessa(), Remessa.class);
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
		return retornoDAO.gerarRetornos(usuarioAcao, retornosArquivos);
	}

	private Arquivo criarNovoArquivoDeRetorno(Instituicao instituicaoDestino, Remessa retorno) {
		Instituicao instituicaoCra = instituicaoDAO.buscarInstituicaoInicial("CRA");
		TipoArquivo tipoArquivo = tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoFebraban.RETORNO);

		Arquivo arquivo = new Arquivo();
		arquivo.setTipoArquivo(tipoArquivo);
		arquivo.setNomeArquivo(TipoArquivoFebraban.generateNomeArquivoFebraban(TipoArquivoFebraban.RETORNO, instituicaoDestino.getCodigoCompensacao(), ConfiguracaoBase.UM));
		arquivo.setInstituicaoRecebe(instituicaoDestino);
		arquivo.setInstituicaoEnvio(instituicaoCra);
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		arquivo.setHoraEnvio(new LocalTime());
		return arquivo;
	}

	/**
	 * @param retornoLiberados
	 * @return
	 */
	public List<Remessa> liberarRetornoBatimentoInstituicao(List<Remessa> retornoLiberados) {
		return retornoDAO.liberarRetornoBatimento(retornoLiberados);
	}

	/**
	 * Método para retornar a situação do batimento arquivo de retorno
	 * 
	 * @param retorno
	 * @return
	 */
	public Remessa retornarArquivoRetornoParaBatimento(Remessa retorno) {
		Batimento batimento = batimentoDAO.buscarBatimentoDoRetorno(retorno);

		for (BatimentoDeposito depositosBatimento : batimento.getDepositosBatimento()) {
			Deposito deposito = depositosBatimento.getDeposito();
			List<Batimento> batimentosDoDeposito = batimentoDAO.buscarBatimentosDoDeposito(deposito);
			if (batimentosDoDeposito.size() > 1) {
				throw new InfraException("O arquivo de retorno possui um depósito vínculado a mais de um batimento! Não é possível retorná-lo ao batimento...");
			}
			deposito.setSituacaoDeposito(SituacaoDeposito.NAO_IDENTIFICADO);
			depositoDAO.atualizarDeposito(deposito);
		}
		batimentoDAO.removerBatimento(batimento);
		return batimentoDAO.retornarArquivoRetornoParaBatimento(retorno);
	}

	/**
	 * Método para retornar a situação do batimento arquivo de retorno
	 * 
	 * @param retorno
	 * @return
	 */
	public Remessa retornarArquivoRetornoParaAguardandoLiberacao(Remessa retorno) {
		retorno.setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno.AGUARDANDO_LIBERACAO);
		return batimentoDAO.update(retorno);
	}
}