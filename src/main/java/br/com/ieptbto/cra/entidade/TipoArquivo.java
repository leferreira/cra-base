package br.com.ieptbto.cra.entidade;

import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.Audited;
import org.joda.time.LocalTime;

import javax.persistence.*;

/**
 * 
 * @author Lefer
 *
 */
@Entity
@Audited
@Table(name = "TB_TIPO_ARQUIVO")
@org.hibernate.annotations.Table(appliesTo = "TB_TIPO_ARQUIVO")
public class TipoArquivo extends AbstractEntidade<TipoArquivo> {

	/****/
	private static final long serialVersionUID = 1L;

	private int id;
	private TipoArquivoFebraban tipoArquivo;
	private LocalTime horaEnvioInicio;
	private LocalTime horaEnvioFim;

	@Id
	@Column(name = "ID_TIPO_ARQUIVO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "TIPO_ARQUIVO", unique = true)
	@Enumerated(EnumType.STRING)
	public TipoArquivoFebraban getTipoArquivo() {
		return tipoArquivo;
	}

	@Column(name = "HORA_ENVIO_FIM")
	public LocalTime getHoraEnvioFim() {
		return horaEnvioFim;
	}

	@Column(name = "HORA_ENVIO_INICIO")
	public LocalTime getHoraEnvioInicio() {
		return horaEnvioInicio;
	}

	public void setHoraEnvioFim(LocalTime horaEnvioFim) {
		this.horaEnvioFim = horaEnvioFim;
	}

	public void setHoraEnvioInicio(LocalTime horaEnvioInicio) {
		this.horaEnvioInicio = horaEnvioInicio;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTipoArquivo(TipoArquivoFebraban tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TipoArquivo) {
			TipoArquivo modalidade = TipoArquivo.class.cast(obj);
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getId(), modalidade.getId());
			equalsBuilder.append(this.getTipoArquivo(), modalidade.getTipoArquivo());
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
	public int compareTo(TipoArquivo entidade) {
		CompareToBuilder compareTo = new CompareToBuilder();
		return compareTo.toComparison();
	}

	@Override
	public String toString() {
		return tipoArquivo.constante;
	}

}
