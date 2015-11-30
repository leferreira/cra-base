package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.CancelamentoException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class CancelamentoDAO extends AbstractBaseDAO {

	@Autowired
	private TituloDAO tituloDAO;
	@Autowired
	private InstituicaoDAO instituicaoDAO;
	
	public Arquivo salvarCancelamentoSerpro(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		Arquivo arquivoSalvo = new Arquivo();
		Transaction transaction = getSession().beginTransaction();

		try {
			arquivo.setStatusArquivo(save(arquivo.getStatusArquivo()));
			arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicao(TipoInstituicaoCRA.CRA.toString()));
			
			arquivoSalvo = save(arquivo);
			if (arquivo.getRemessaCancelamentoProtesto() != null) {
				List<PedidoCancelamento> pedidosCancelamentoComErros = new ArrayList<PedidoCancelamento>();
				List<CancelamentoProtesto> cancelamentosProtesto = new ArrayList<CancelamentoProtesto>();
				BigDecimal valorTotalDesistenciaProtesto = BigDecimal.ZERO;
				int totalCancelamentoProtesto = 0;
				int totalRegistroCancelamentoProtesto = 0;
	
				for (CancelamentoProtesto cancelamento : arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto()) {
					List<PedidoCancelamento> pedidos = new ArrayList<PedidoCancelamento>();
					cancelamento.setRemessaCancelamentoProtesto(arquivo.getRemessaCancelamentoProtesto());
					cancelamento.setDownload(false);
					for (PedidoCancelamento pedido : cancelamento.getCancelamentos()) {
						pedido.setCancelamentoProtesto(cancelamento);;
						pedido.setTitulo(tituloDAO.buscarTituloCancelamentoProtesto(pedido));
						if (pedido.getTitulo() != null) {
							if (pedido.getTitulo().getPedidoDesistencia() == null) {
								pedidos.add(pedido);
								valorTotalDesistenciaProtesto = valorTotalDesistenciaProtesto.add(pedido.getValorTitulo());
								totalRegistroCancelamentoProtesto++;
							} else {
								pedidosCancelamentoComErros.add(pedido);
								erros.add(new InfraException("Linha " + pedido.getSequenciaRegistro() + ": o título de número "+ pedido.getNumeroTitulo() + ", do protocolo " + pedido.getNumeroProtocolo() + " do dia "
								        + DataUtil.localDateToString(pedido.getDataProtocolagem())+ ", já foi enviado anteriormente em outro arquivo de cancelamento!"));
							}
						} else if (pedido.getDataProtocolagem().isAfter(DataUtil.stringToLocalDate("dd/MM/yyyy", "01/12/2015")) ||
								pedido.getDataProtocolagem().equals(DataUtil.stringToLocalDate("dd/MM/yyyy", "01/12/2015"))) {
							pedidosCancelamentoComErros.add(pedido);
							erros.add(new InfraException("Linha " + pedido.getSequenciaRegistro() + ": o título de número "+ pedido.getNumeroTitulo() + ",com o protocolo " + pedido.getNumeroProtocolo() + " do dia "
							        + DataUtil.localDateToString(pedido.getDataProtocolagem())+ ", não foi localizado para a comarca [ "+ pedido.getCancelamentoProtesto().getCabecalhoCartorio().getCodigoMunicipio() +" ]. Verifique os dados do título!"));
						} else {
							pedidos.add(pedido);
							valorTotalDesistenciaProtesto = valorTotalDesistenciaProtesto.add(pedido.getValorTitulo());
							totalRegistroCancelamentoProtesto++;
						}
					}
					if (!pedidos.isEmpty()) {
						cancelamento.getCabecalhoCartorio().setQuantidadeDesistencia(pedidos.size());
						cancelamento.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(pedidos.size());
						cancelamento.setCancelamentos(pedidos);
						cancelamentosProtesto.add(cancelamento);
						totalCancelamentoProtesto++;
					}
				}
				arquivo.getRemessaCancelamentoProtesto().getCabecalho().setQuantidadeDesistencia(totalCancelamentoProtesto);
				arquivo.getRemessaCancelamentoProtesto().getCabecalho().setQuantidadeRegistro(totalRegistroCancelamentoProtesto);
				arquivo.getRemessaCancelamentoProtesto().getRodape().setQuantidadeDesistencia(totalCancelamentoProtesto);
				arquivo.getRemessaCancelamentoProtesto().getRodape().setSomatorioValorTitulo(valorTotalDesistenciaProtesto);
				arquivo.getRemessaCancelamentoProtesto().setCancelamentoProtesto(cancelamentosProtesto);
				arquivo.getRemessaCancelamentoProtesto().setCabecalho(save(arquivo.getRemessaCancelamentoProtesto().getCabecalho()));
				arquivo.getRemessaCancelamentoProtesto().setRodape(save(arquivo.getRemessaCancelamentoProtesto().getRodape()));
				save(arquivo.getRemessaCancelamentoProtesto());
	
				for (CancelamentoProtesto cancelamentoProtestos : cancelamentosProtesto) {
					cancelamentoProtestos.getCabecalhoCartorio().setQuantidadeDesistencia(cancelamentoProtestos.getCancelamentos().size());
					cancelamentoProtestos.getRodapeCartorio().setSomaTotalCancelamentoDesistencia(totalCancelamentoProtesto);
					cancelamentoProtestos.setCabecalhoCartorio(save(cancelamentoProtestos.getCabecalhoCartorio()));
					cancelamentoProtestos.setRodapeCartorio(save(cancelamentoProtestos.getRodapeCartorio()));
					cancelamentoProtestos.setRemessaCancelamentoProtesto(arquivo.getRemessaCancelamentoProtesto());
					save(cancelamentoProtestos);
					for (PedidoCancelamento pedido : cancelamentoProtestos.getCancelamentos()) {
						save(pedido);
					}
				}
				if (!erros.isEmpty()) {
					throw new CancelamentoException("Não foi possível enviar o arquivo de cancelamento! Por favor, corriga os erros no arquivo abaixo...", erros, pedidosCancelamentoComErros);
				}
				transaction.commit();
			}
			logger.info("O arquivo " + arquivo.getNomeArquivo() + "enviado pelo usuário " + arquivo.getUsuarioEnvio().getLogin()
			        + " foi inserido na base ");

		} catch (CancelamentoException ex) {
			transaction.rollback();
			logger.error(ex.getMessage());
			throw new CancelamentoException(ex.getMessage(), ex.getErros(), ex.getPedidosCancelamento());
		} catch (InfraException ex) {
			transaction.rollback();
			logger.error(ex.getMessage());
			throw new InfraException(ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			transaction.rollback();
			throw new InfraException("Não foi possível inserir esse arquivo na base de dados.");
		}
		return arquivoSalvo;
	}
}
