package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;

@Entity
@Audited
@Table(name = "TB_TIPO_INSTITUICAO")
@org.hibernate.annotations.Table(appliesTo = "TB_TIPO_INSTITUICAO")
public class TipoInstituicao extends AbstractEntidade<TipoInstituicao> {

	private static final long serialVersionUID = 1L;
	private int id;
	private TipoInstituicaoCRA tipoInstituicao;
	private List<Instituicao> listaInstituicoes;
	private List<PermissaoEnvio> arquivosEnvioPermitido;

	@Id
	@Column(name = "ID_TIPO_INSTITUICAO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "TIPO_INSTITUICAO", nullable = false)
	@Enumerated(EnumType.STRING)
	public TipoInstituicaoCRA getTipoInstituicao() {
		return tipoInstituicao;
	}

	@OneToMany(mappedBy = "tipoInstituicao")
	public List<Instituicao> getListaInstituicoes() {
		return listaInstituicoes;
	}

	@OneToMany(mappedBy = "tipoInstituicao")
	public List<PermissaoEnvio> getArquivosEnvioPermitido() {
		return arquivosEnvioPermitido;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTipoInstituicao(TipoInstituicaoCRA tipoInstituicao) {
		this.tipoInstituicao = tipoInstituicao;
	}

	public void setListaInstituicoes(List<Instituicao> listaInstituicoes) {
		this.listaInstituicoes = listaInstituicoes;
	}

	public void setArquivosEnvioPermitido(List<PermissaoEnvio> arquivosEnvioPermitido) {
		this.arquivosEnvioPermitido = arquivosEnvioPermitido;
	}

	@Override
	public int compareTo(TipoInstituicao entidade) {
		return 0;
	}
}
