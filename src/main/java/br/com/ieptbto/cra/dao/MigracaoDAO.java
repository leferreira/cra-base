package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.PedidoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings( "rawtypes" )
@Repository
public class MigracaoDAO extends AbstractBaseDAO {

	@Autowired
	TituloDAO tituloDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;
	@Autowired
	RemessaDAO remessaDAO;
	
	public Arquivo salvar(Arquivo arquivo, Usuario usuarioAcao) {
		Arquivo arquivoSalvo = new Arquivo();
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		BigDecimal valorTotalSaldo = BigDecimal.ZERO;
		try {
			arquivo.setStatusArquivo(save(arquivo.getStatusArquivo()));
			verificaInstituicaoRecebe(arquivo);
			arquivoSalvo = save(arquivo);

			if (!arquivo.getRemessas().isEmpty()) {
				for (Remessa remessa : arquivo.getRemessas()) {
					remessa.setArquivo(arquivoSalvo);
					remessa.setCabecalho(save(remessa.getCabecalho()));
					remessa.setRodape(save(remessa.getRodape()));
					remessa.setArquivoGeradoProBanco(arquivoSalvo);
					remessa.setDataRecebimento(remessa.getCabecalho().getDataMovimento());
					remessa.setInstituicaoOrigem(arquivo.getInstituicaoEnvio());
					setStatusRemessa(arquivo.getInstituicaoEnvio().getTipoInstituicao(), remessa);
					setSituacaoRemessa(arquivo, remessa);
					save(remessa);
					for (Titulo titulo : remessa.getTitulos()) {
						titulo.setRemessa(remessa);
						if (Retorno.class.isInstance(titulo)) {
							Retorno.class.cast(titulo).setCabecalho(remessa.getCabecalho());
						}
						TituloRemessa tituloSalvo = tituloDAO.salvar(titulo, transaction);

						Historico historico = new Historico();
						if (tituloSalvo != null) {
							historico.setDataOcorrencia(new LocalDateTime());
							historico.setRemessa(remessa);
							historico.setTitulo(tituloSalvo);
							historico.setUsuarioAcao(usuarioAcao);
							save(historico);
						} else {
							titulo.setSaldoTitulo(BigDecimal.ZERO);
							remessa.getTitulos().remove(titulo);
						}

						valorTotalSaldo = valorTotalSaldo.add(titulo.getSaldoTitulo());
						remessa.getCabecalho().setQtdTitulosRemessa(remessa.getTitulos().size());
						remessa.getRodape().setSomatorioValorRemessa(valorTotalSaldo);

						remessa.setCabecalho(save(remessa.getCabecalho()));
						remessa.setRodape(save(remessa.getRodape()));
					}
				}
			} else if (arquivo.getRemessaDesistenciaProtesto() != null) {
				arquivo.getRemessaDesistenciaProtesto().setCabecalho(save(arquivo.getRemessaDesistenciaProtesto().getCabecalho()));
				arquivo.getRemessaDesistenciaProtesto().setRodape(save(arquivo.getRemessaDesistenciaProtesto().getRodape()));
				save(arquivo.getRemessaDesistenciaProtesto());

				for (DesistenciaProtesto desistenciaProtestos : arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto()) {
					desistenciaProtestos.setCabecalhoCartorio(save(desistenciaProtestos.getCabecalhoCartorio()));
					desistenciaProtestos.setRodapeCartorio(save(desistenciaProtestos.getRodapeCartorio()));
					List<PedidoDesistenciaCancelamento> pedidosDesistencia = desistenciaProtestos.getDesistencias();
					desistenciaProtestos.setDesistencias(new ArrayList<PedidoDesistenciaCancelamento>());
					for (PedidoDesistenciaCancelamento pedido : pedidosDesistencia) {
						desistenciaProtestos.getDesistencias().add(save(pedido));
					}
					save(desistenciaProtestos);
				}

			}
			transaction.commit();
			logger.info("O arquivo " + arquivo.getNomeArquivo() + "enviado pelo usuário " + arquivo.getUsuarioEnvio().getLogin()
			        + " foi inserido na base ");
		} catch (InfraException ex) {
			logger.error(ex.getMessage());
			throw new InfraException(ex.getMessage(), ex.getCause());

		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esse arquivo na base de dados.");
		}
		return arquivoSalvo;

	}

	private void verificaInstituicaoRecebe(Arquivo arquivo) {
		if (arquivo.getInstituicaoRecebe() == null) {
			arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicao("CRA"));
		}

	}

	private void setStatusRemessa(TipoInstituicao tipoInstituicao, Remessa remessa) {
		if (tipoInstituicao.getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)
		        || tipoInstituicao.getTipoInstituicao().equals(TipoInstituicaoCRA.CONVENIO)) {
			remessa.setStatusRemessa(StatusRemessa.AGUARDANDO);
		} else if (tipoInstituicao.getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
			remessa.setStatusRemessa(StatusRemessa.ENVIADO);
		}
	}

	private void setSituacaoRemessa(Arquivo arquivo, Remessa remessa) {
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)
		        || arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
			remessa.setSituacao(false);
			if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
				remessa.setSituacaoBatimento(false);
			}
		}
	}
}
