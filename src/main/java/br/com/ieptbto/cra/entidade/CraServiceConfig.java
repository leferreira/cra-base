package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.com.ieptbto.cra.enumeration.EnumerationSimNao;
import br.com.ieptbto.cra.enumeration.CraServiceEnum;

/**
 * @author Thasso Aráujo
 *
 */
@Entity
@Audited
@Table(name = "TB_CRA_SERVICE_CONFIG")
@org.hibernate.annotations.Table(appliesTo = "TB_CRA_SERVICE_CONFIG")
public class CraServiceConfig extends AbstractEntidade<CraServiceConfig> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private CraServiceEnum craService;
	private EnumerationSimNao ativo;

	@Override
	@Id
	@Column(name = "ID_CRA_SERVICE_CONFIG", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "CRA_SERVICE", length = 150)
	public CraServiceEnum getCraService() {
		return craService;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "ATIVO", length = 150)
	public EnumerationSimNao getAtivo() {
		return ativo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCraService(CraServiceEnum craService) {
		this.craService = craService;
	}

	public void setAtivo(EnumerationSimNao ativo) {
		this.ativo = ativo;
	}

	@Override
	public int compareTo(CraServiceConfig entidade) {
		return 0;
	}

	@Override
	public boolean equals(Object user) {
		if (getId() != 0 && user instanceof CraServiceConfig) {
			return getId() == ((CraServiceConfig) user).getId();
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (getId() == 0) {
			return 0;
		}
		return getId();
	}
}
