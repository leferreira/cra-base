package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.SetorFiliado;
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

			for (SetorFiliado setor : filiado.getSetoresFiliado()) {
				setor.setFiliado(novoFiliado);
				save(setor);
			}

			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível cadastrar o novo filiado !");
		}
		return novoFiliado;
	}

	public Filiado alterar(Filiado filiado) {
		Transaction transaction = getBeginTransation();

		try {
			filiado = update(filiado);

			for (SetorFiliado setor : filiado.getSetoresFiliado()) {
				if (setor.getId() == 0) {
					setor.setFiliado(filiado);
					save(setor);
				} else {
					update(setor);
				}
			}

			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
		return filiado;
	}

	private String geradorCodigoCedenteFiliado(Filiado novoFiliado) {
		String codigoCedente = novoFiliado.getId() + novoFiliado.getInstituicaoConvenio().getCodigoCompensacao();
		while (codigoCedente.length() < 15) {
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
		if (instituicao.getTipoInstituicao().getTipoInstituicao() != TipoInstituicaoCRA.CRA) {
			criteria.add(Restrictions.eq("instituicaoConvenio", instituicao));
		}
		criteria.addOrder(Order.asc("razaoSocial"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<SetorFiliado> buscarSetoresFiliado(Filiado filiado) {
		Criteria criteria = getCriteria(SetorFiliado.class);
		criteria.add(Restrictions.eq("filiado", filiado));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<SetorFiliado> buscarSetoresAtivosFiliado(Filiado filiado) {
		Criteria criteria = getCriteria(SetorFiliado.class);
		criteria.add(Restrictions.eq("filiado", filiado));
		criteria.add(Restrictions.eq("situacaoAtivo", true));
		return criteria.list();
	}

	public void removerSertorFiliado(SetorFiliado setor) {

		try {
			Query query = createSQLQuery("DELETE FROM tb_setor_filiado WHERE id_setor_filiado=" + setor.getId());
			query.executeUpdate();

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	public SetorFiliado buscarSetorPadraoFiliado(Filiado filiado) {
		Criteria criteria = getCriteria(SetorFiliado.class);
		criteria.add(Restrictions.eq("filiado", filiado));
		criteria.add(Restrictions.eq("setorPadraoFiliado", true));
		return SetorFiliado.class.cast(criteria.uniqueResult());
	}

	public Filiado buscarFiliadoConvenioPorCpfCnpj(Instituicao convenio, String cnpjCpf) {
		Criteria criteria = getCriteria(Filiado.class);
		criteria.add(Restrictions.eq("instituicaoConvenio", convenio));
		criteria.add(Restrictions.eq("cnpjCpf", cnpjCpf));
		criteria.setMaxResults(1);
		return Filiado.class.cast(criteria.uniqueResult());
	}
}
