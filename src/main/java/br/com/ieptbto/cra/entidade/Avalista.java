package br.com.ieptbto.cra.entidade;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_AVALISTA")
@org.hibernate.annotations.Table(appliesTo = "TB_AVALISTA")
public class Avalista extends AbstractEntidade<Avalista> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private TituloFiliado tituloFiliado;

	private String nome;
	private String tipoDocumento;
	private String documento;
	private String endereco;
	private String cidade;
	private String cep;
	private String uf;
	private String bairro;

	@Id
	@Column(name = "ID_AVALISTA", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "TITULO_FILIADO_ID")
	public TituloFiliado getTituloFiliado() {
		return tituloFiliado;
	}

	@Column(name = "NOME_")
	public String getNome() {
		return nome;
	}

	@Column(name = "TIPO_DOCUMENTO_")
	public String getTipoDocumento() {
		return tipoDocumento;
	}

	@Column(name = "DOCUMENTO_")
	public String getDocumento() {
		if (documento == null) {
			documento = StringUtils.EMPTY;
		}
		return documento.replace(".", "").replace("-", "").replace("/", "").trim();
	}

	@Column(name = "ENDERECO_")
	public String getEndereco() {
		if (endereco != null) {
			return endereco.replaceAll("\n", "").replaceAll("\t", "").replaceAll("\r", "");
		}
		return endereco;
	}

	@Column(name = "CIDADE_")
	public String getCidade() {
		return cidade;
	}

	@Column(name = "CEP")
	public String getCep() {
		if (cep == null) {
			cep = StringUtils.EMPTY;
		}
		return cep.replace(".", "").replace("-", "").trim();
	}

	public String getUf() {
		return uf;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTituloFiliado(TituloFiliado tituloFiliado) {
		this.tituloFiliado = tituloFiliado;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Avalista) {
			Avalista modalidade = Avalista.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getTituloFiliado(), modalidade.getTituloFiliado());
			equalsBuilder.append(this.getNome(), modalidade.getNome());
			equalsBuilder.append(this.getDocumento(), modalidade.getDocumento());
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
	public int compareTo(Avalista entidade) {
		return 0;
	}

	public String getBairro() {
		if (bairro == null) {
			this.bairro = "CENTRO";
		}
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
}
