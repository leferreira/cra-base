package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.com.ieptbto.cra.enumeration.CampoLayout;
import br.com.ieptbto.cra.enumeration.TipoArquivoLayoutEmpresa;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@Entity
@Audited
@Table(name = "TB_LAYOUT_EMPRESA")
@org.hibernate.annotations.Table(appliesTo = "TB_LAYOUT_EMPRESA")
public class LayoutFiliado extends AbstractEntidade<LayoutFiliado> {

	private int id;
	private Instituicao empresa;
	private CampoLayout campo;
	private Integer ordem;
	private Integer posicaoInicio;
	private Integer posicaoFim;
	private TipoArquivoLayoutEmpresa tipoArquivo;

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

	@Column(name = "CAMPO")
	@Enumerated(EnumType.STRING)
	public CampoLayout getCampo() {
		return campo;
	}

	@Column(name = "TIPO_ARQUIVO")
	@Enumerated(EnumType.STRING)
	public TipoArquivoLayoutEmpresa getTipoArquivo() {
		return tipoArquivo;
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

	public void setPosicaoFim(Integer posicaoFim) {
		this.posicaoFim = posicaoFim;
	}

	public void setCampo(CampoLayout campo) {
		this.campo = campo;
	}

	public void setTipoArquivo(TipoArquivoLayoutEmpresa tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	@Override
	public int compareTo(LayoutFiliado entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
