package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.enumeration.TipoOcorrencia;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_HISTORICO_OCORRENCIA_TITULO")
@org.hibernate.annotations.Table(appliesTo = "TB_HISTORICO_OCORRENCIA_TITULO")
public class HistoricoOcorrenciaTitulo extends AbstractEntidade<HistoricoOcorrenciaTitulo> {

	/***/
	private static final long serialVersionUID = 1L;

	private int id;
	private TituloRemessa titulo;
	private LocalDate dataOcorrencia;
	private TipoOcorrencia tipoOcorrencia;

	@Id
	@Column(name = "ID_HISTORICO_OCORRENCIA_TITULO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "TITULO_ID")
	public TituloRemessa getTitulo() {
		return titulo;
	}

	@Column(name = "DATA_OCORRENCIA")
	public LocalDate getDataOcorrencia() {
		return dataOcorrencia;
	}

	@Column(name = "TIPO_OCORRENCIA")
	@Enumerated(EnumType.STRING)
	public TipoOcorrencia getTipoOcorrencia() {
		return tipoOcorrencia;
	}

	public void setTipoOcorrencia(TipoOcorrencia tipoOcorrencia) {
		this.tipoOcorrencia = tipoOcorrencia;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setTitulo(TituloRemessa titulo) {
		this.titulo = titulo;
	}

	public void setDataOcorrencia(LocalDate dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	@Override
	public int compareTo(HistoricoOcorrenciaTitulo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
