package br.com.ieptbto.cra.entidade;

import br.com.ieptbto.cra.enumeration.CampoLayoutPersonalizado;
import br.com.ieptbto.cra.enumeration.LayoutArquivo;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@Entity
@Audited
@Table(name = "TB_LAYOUT_EMPRESA", uniqueConstraints=
	@UniqueConstraint(columnNames={"campo", "layout_arquivo", "instituicao_id"}))
@org.hibernate.annotations.Table(appliesTo = "TB_LAYOUT_EMPRESA")
public class LayoutFiliado extends AbstractEntidade<LayoutFiliado> {

	private int id;
	private Instituicao empresa;
	private CampoLayoutPersonalizado campo;
	private Integer ordem;
	private Integer posicaoInicio;
	private Integer posicaoFim;
	private String descricaoCampo;
	private LayoutArquivo layoutArquivo;

	@Override
	@Id
	@Column(name = "ID_LAYOUT_EMPRESA", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	@OneToOne
	@JoinColumn(name = "INSTITUICAO_ID")
	public Instituicao getEmpresa() {
		return empresa;
	}

	@Column(name = "DESCRICAO_CAMPO", length = 150)
	public String getDescricaoCampo() {
		return descricaoCampo;
	}

	@Column(name = "CAMPO")
	@Enumerated(EnumType.STRING)
	public CampoLayoutPersonalizado getCampo() {
		return campo;
	}

	@Column(name = "LAYOUT_ARQUIVO")
	@Enumerated(EnumType.STRING)
	public LayoutArquivo getLayoutArquivo() {
		return layoutArquivo;
	}

	@Column(name = "ORDEM")
	public Integer getOrdem() {
		return ordem;
	}

	@Column(name = "POSICAO_INICIO")
	public Integer getPosicaoInicio() {
		return posicaoInicio;
	}

	@Column(name = "POSICAO_FIM")
	public Integer getPosicaoFim() {
		return posicaoFim;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setEmpresa(Instituicao empresa) {
		this.empresa = empresa;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public void setPosicaoInicio(Integer posicaoInicio) {
		this.posicaoInicio = posicaoInicio;
	}

	public void setDescricaoCampo(String descricaoCampo) {
		this.descricaoCampo = descricaoCampo;
	}

	public void setPosicaoFim(Integer posicaoFim) {
		this.posicaoFim = posicaoFim;
	}

	public void setCampo(CampoLayoutPersonalizado campo) {
		this.campo = campo;
	}

	public void setLayoutArquivo(LayoutArquivo tipoArquivo) {
		this.layoutArquivo = tipoArquivo;
	}

	@Override
	public int compareTo(LayoutFiliado entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
