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
import org.joda.time.LocalTime;

import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;

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
	private TipoArquivoEnum tipoArquivo;
	private List<PermissaoEnvio> arquivosEnvioPermitido;
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
	public TipoArquivoEnum getTipoArquivo() {
		return tipoArquivo;
	}

	@OneToMany(mappedBy = "tipoArquivo")
	public List<PermissaoEnvio> getArquivosEnvioPermitido() {
		return arquivosEnvioPermitido;
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

	public void setTipoArquivo(TipoArquivoEnum tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public void setArquivosEnvioPermitido(List<PermissaoEnvio> arquivosEnvioPermitido) {
		this.arquivosEnvioPermitido = arquivosEnvioPermitido;
	}

	@Override
	public int compareTo(TipoArquivo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		return tipoArquivo.constante;
	}

}
