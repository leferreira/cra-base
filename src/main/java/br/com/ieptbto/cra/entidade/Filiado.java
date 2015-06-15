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

import org.hibernate.envers.Audited;

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
	private String cidade;
	private String uf;
	private String codigoFiliado;
	private Boolean ativo;
	private Instituicao instituicaoConvenio;
	private List<UsuarioFiliado> usuariosFiliados;

	@Id
	@Column(name = "ID_FILIADO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "RAZAO_SOCIAL", length = 45)
	public String getRazaoSocial() {
		return razaoSocial;
	}

	@Column(name = "CPF_CNPJ", length = 14, unique = true, nullable = false)
	public String getCnpjCpf() {
		return cnpjCpf;
	}

	@Column(name = "ENDERECO", length = 45)
	public String getEndereco() {
		return endereco;
	}

	@Column(name = "CEP", length = 8)
	public String getCep() {
		return cep;
	}

	@Column(name = "CIDADE", length = 20)
	public String getCidade() {
		return cidade;
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

	@Column(name = "ATIVO", nullable = false)
	public Boolean isAtivo() {
		return ativo;
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

	public void setCidade(String cidade) {
		this.cidade = cidade;
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

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public int compareTo(Filiado entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
