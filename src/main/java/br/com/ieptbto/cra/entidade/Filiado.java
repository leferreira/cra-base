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
	private String nomeCredor;
	private String documentoCredor;
	private String enderecoCredor;
	private String cepCredor;
	private String cidadeCredor;
	private String ufCredor;
	private String agenciaCodigoCredor;
	
	private Instituicao instituicaoConvenio;
	private List<UsuarioFiliado> usuariosFiliados;
	
	@Id
	@Column(name = "ID_FILIADO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "NOME_CREDOR")
	public String getNomeCredor() {
		return nomeCredor;
	}

	@Column(name = "DOCUMENTO_CREDOR")
	public String getDocumentoCredor() {
		return documentoCredor;
	}

	@Column(name = "ENDERECO_CREDOR")
	public String getEnderecoCredor() {
		return enderecoCredor;
	}

	@Column(name = "CEP_CREDOR")
	public String getCepCredor() {
		return cepCredor;
	}

	@Column(name = "CIDADE_CREDOR")
	public String getCidadeCredor() {
		return cidadeCredor;
	}

	@Column(name = "UF_CREDOR")
	public String getUfCredor() {
		return ufCredor;
	}

	@Column(name = "AGENCIA_CODIGO_CREDOR")
	public String getAgenciaCodigoCredor() {
		return agenciaCodigoCredor;
	}

	@ManyToOne
	@JoinColumn(name="INSTITUICAO_CONVENIO_ID")
	public Instituicao getInstituicaoConvenio() {
		return instituicaoConvenio;
	}
	
	@OneToMany(mappedBy="filiado")
	public List<UsuarioFiliado> getUsuariosFiliados() {
		return usuariosFiliados;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNomeCredor(String nomeCredor) {
		this.nomeCredor = nomeCredor;
	}

	public void setDocumentoCredor(String documentoCredor) {
		this.documentoCredor = documentoCredor;
	}

	public void setEnderecoCredor(String enderecoCredor) {
		this.enderecoCredor = enderecoCredor;
	}

	public void setCepCredor(String cepCredor) {
		this.cepCredor = cepCredor;
	}

	public void setCidadeCredor(String cidadeCredor) {
		this.cidadeCredor = cidadeCredor;
	}

	public void setUfCredor(String ufCredor) {
		this.ufCredor = ufCredor;
	}

	public void setAgenciaCodigoCredor(String agenciaCodigoCredor) {
		this.agenciaCodigoCredor = agenciaCodigoCredor;
	}

	public void setInstituicaoConvenio(Instituicao instituicaoConvenio) {
		this.instituicaoConvenio = instituicaoConvenio;
	}

	public void setUsuariosFiliados(List<UsuarioFiliado> usuariosFiliados) {
		this.usuariosFiliados = usuariosFiliados;
	}

	@Override
	public int compareTo(Filiado entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
