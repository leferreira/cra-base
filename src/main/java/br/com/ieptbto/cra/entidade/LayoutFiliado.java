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
	private Filiado filiado;
	private CampoLayout campo;
	private Integer ordem;
	private Integer posicaoInicio;
	private Integer posicaoFim;

	@Override
	@Id
	@Column(name = "ID_LAYOUT_EMPRESA", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	@OneToOne
	@JoinColumn(name = "FILIADO_ID")
	public Filiado getFiliado() {
		return filiado;
	}

	@Column(name = "CAMPO")
	@Enumerated(EnumType.STRING)
	public CampoLayout getCampo() {
		return campo;
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

	public void setFiliado(Filiado filiado) {
		this.filiado = filiado;
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

	@Override
	public int compareTo(LayoutFiliado entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
