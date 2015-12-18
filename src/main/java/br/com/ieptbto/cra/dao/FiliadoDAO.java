package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class FiliadoDAO extends AbstractBaseDAO {

	public Filiado salvar(Filiado filiado) {
		Filiado novoFiliado = new Filiado();
		Transaction transaction = getBeginTransation();

		try {
			novoFiliado = save(filiado);
			novoFiliado.setCodigoFiliado(geradorCodigoCedenteFiliado(novoFiliado));
			update(novoFiliado);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível cadastrar o novo filiado !");
		}
		return novoFiliado;
	}

	public Filiado alterar(Filiado filiado) {
		Filiado novoFiliado = new Filiado();
		Transaction transaction = getBeginTransation();
		
		try {
			novoFiliado = update(filiado);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
		return novoFiliado;
	}
	
	private String geradorCodigoCedenteFiliado(Filiado novoFiliado) {
		String codigoCedente = novoFiliado.getId() + novoFiliado.getInstituicaoConvenio().getCodigoCompensacao();
		while (codigoCedente.length() < 15){
			codigoCedente += "0";
		}
		return codigoCedente;
	}

	@SuppressWarnings("unchecked")
	public List<Filiado> buscarListaFiliadosPorConvenio(Instituicao instituicao) {
		Criteria criteria = getCriteria(Filiado.class);
		criteria.createAlias("municipio", "municipio");
		
		if (instituicao.getId() == 0) {
			return new ArrayList<Filiado>();
		}
		
		if (instituicao.getTipoInstituicao().getTipoInstituicao() != TipoInstituicaoCRA.CRA)
			criteria.add(Restrictions.eq("instituicaoConvenio", instituicao));
		
		criteria.addOrder(Order.asc("razaoSocial"));
		return criteria.list();
	}

}
