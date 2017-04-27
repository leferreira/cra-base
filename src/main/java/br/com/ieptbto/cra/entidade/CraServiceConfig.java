package br.com.ieptbto.cra.entidade;

import br.com.ieptbto.cra.enumeration.CraServices;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Thasso Aráujo
 *
 */
@Entity
@Audited
@Table(name = "TB_CRA_SERVICE_CONFIG")
@org.hibernate.annotations.Table(appliesTo = "TB_CRA_SERVICE_CONFIG")
public class CraServiceConfig extends AbstractEntidade<CraServiceConfig> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private CraServices craService;
	private boolean status;

	@Override
	@Id
	@Column(name = "ID_CRA_SERVICE_CONFIG", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "CRA_SERVICE", length = 150)
	public CraServices getCraService() {
		return craService;
	}

	@Column(name = "STATUS", length = 150)
	public boolean getStatus() {
		return status;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCraService(CraServices craService) {
		this.craService = craService;
	}

	public void setStatus(boolean status) {
		this.status = status;
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
