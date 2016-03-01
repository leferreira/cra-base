package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.Audited;

import br.com.ieptbto.cra.util.RemoverAcentosUtil;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_FILIADO")
@org.hibernate.annotations.Table(appliesTo = "TB_FILIADO")
public class Filiado extends AbstractEntidade<Filiado> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private String razaoSocial;
	private String cnpjCpf;
	private String endereco;
	private String cep;
	private Municipio municipio;
	private String uf;
	private String codigoFiliado;
	private Boolean ativo;
	private Instituicao instituicaoConvenio;
	private List<UsuarioFiliado> usuariosFiliados;
	private List<TituloFiliado> titulosFiliado;
	private List<SetorFiliado> setoresFiliado;

	@Id
	@Column(name = "ID_FILIADO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "RAZAO_SOCIAL", length = 45)
	public String getRazaoSocial() {
		if (razaoSocial == null){
			return StringUtils.EMPTY;
		}
		return razaoSocial;
	}

	@Column(name = "CPF_CNPJ", length = 18, nullable = false)
	public String getCnpjCpf() {
		if (cnpjCpf == null){
			cnpjCpf = StringUtils.EMPTY;
		}
		return cnpjCpf.replace(".", "").replace("-", "").replace("/", "").trim();
	}

	@Column(name = "ENDERECO", length = 45)
	public String getEndereco() {
		if (endereco != null) {
			endereco = RemoverAcentosUtil.removeAcentos(endereco);
		}
		return endereco;
	}

	@Column(name = "CEP", length = 10)
	public String getCep() {
		if (cep == null){
			cep = StringUtils.EMPTY;
		}
		return cep.replace(".", "").replace("-", "").trim();
	}
	
	@ManyToOne
	@JoinColumn(name = "MUNICIPIO_ID")
	public Municipio getMunicipio() {
		return municipio;
	}

	@Column(name = "UF", length = 2)
	public String getUf() {
		return uf;
	}

	@Column(name = "CODIGO_FILIADO", length = 15)
	public String getCodigoFiliado() {
		return codigoFiliado;
	}
	
	@ManyToOne
	@JoinColumn(name = "INSTITUICAO_ID")
	public Instituicao getInstituicaoConvenio() {
		return instituicaoConvenio;
	}

	@OneToMany(mappedBy = "filiado")
	public List<UsuarioFiliado> getUsuariosFiliados() {
		return usuariosFiliados;
	}

	@OneToMany(mappedBy = "filiado")
	public List<TituloFiliado> getTitulosFiliado() {
		return titulosFiliado;
	}

	@Column(name = "ATIVO", nullable = false)
	public Boolean isAtivo() {
		return ativo;
	}
	
	@OneToMany(mappedBy = "filiado")
	public List<SetorFiliado> getSetoresFiliado() {
		return setoresFiliado;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public void setCnpjCpf(String cnpjCpf) {
		this.cnpjCpf = cnpjCpf;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}
	
	public void setSetoresFiliado(List<SetorFiliado> setoresFiliado) {
		this.setoresFiliado = setoresFiliado;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public void setCodigoFiliado(String codigoFiliado) {
		this.codigoFiliado = codigoFiliado;
	}

	public void setInstituicaoConvenio(Instituicao instituicaoConvenio) {
		this.instituicaoConvenio = instituicaoConvenio;
	}

	public void setUsuariosFiliados(List<UsuarioFiliado> usuariosFiliados) {
		this.usuariosFiliados = usuariosFiliados;
	}

	public void setTitulosFiliado(List<TituloFiliado> titulosFiliado) {
		this.titulosFiliado = titulosFiliado;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	@Transient
	public String getSituacao() {
		if (isAtivo() == null || isAtivo() !=null && isAtivo() == false) {
			return "Não Ativo";
		} 
		return "Ativo";
	}

	public void setSituacao(String status) {
		if (status.equals("Sim")) {
			setAtivo(true);
		} else {
			setAtivo(false);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Filiado) {
			Filiado modalidade = Filiado.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getRazaoSocial(), modalidade.getRazaoSocial());
			equalsBuilder.append(this.getCnpjCpf(), modalidade.getCnpjCpf());
			return equalsBuilder.isEquals();
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
	
	@Override
	public int compareTo(Filiado entidade) {
		CompareToBuilder compareTo = new CompareToBuilder();
		return compareTo.toComparison();
	}
}
