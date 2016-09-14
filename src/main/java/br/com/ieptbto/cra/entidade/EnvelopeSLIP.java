package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * @author Thasso
 *
 */
@Entity
@Audited
@Table(name = "TB_ENVELOPE_SLIP")
@org.hibernate.annotations.Table(appliesTo = "TB_ENVELOPE_SLIP")
public class EnvelopeSLIP extends AbstractEntidade<EnvelopeSLIP> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private String banco;
	private String agenciaDestino; //
	private String municipioAgenciaDestino; //
	private String ufAgenciaDestino; //
	private Integer quantidadeInstrumentos;
	private String codeBar;
	private LocalTime horaGeracao;
	private LocalDate dataGeracao;
	private LocalDate dataLiberacao;
	private boolean liberado;

	private List<EtiquetaSLIP> etiquetas;

	@Id
	@Column(name = "ID_ENVELOPE_SLIP", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "BANCO", length = 45)
	public String getBanco() {
		return banco;
	}

	@Column(name = "AGENCIA_DESTINO", length = 4)
	public String getAgenciaDestino() {
		return agenciaDestino;
	}

	@Column(name = "MUNICIPIO_AGENCIA_DESTINO", length = 45)
	public String getMunicipioAgenciaDestino() {
		if (municipioAgenciaDestino != null) {
			municipioAgenciaDestino = municipioAgenciaDestino.trim();
		}
		return municipioAgenciaDestino;
	}

	@Column(name = "UF_AGENCIA_DESTINO", length = 2)
	public String getUfAgenciaDestino() {
		return ufAgenciaDestino;
	}

	@Column(name = "CODIGO_DE_BARRA", length = 20)
	public String getCodeBar() {
		return codeBar;
	}

	@Column(name = "DATA_GERACAO")
	public LocalDate getDataGeracao() {
		return dataGeracao;
	}

	@Column(name = "HORA_GERACAO")
	public LocalTime getHoraGeracao() {
		if (horaGeracao == null) {
			horaGeracao = new LocalTime();
		}
		return horaGeracao;
	}

	@Column(name = "DATA_LIBERACAO")
	public LocalDate getDataLiberacao() {
		return dataLiberacao;
	}

	@Column(name = "LIBERADO")
	public boolean isLiberado() {
		return liberado;
	}

	@OneToMany(mappedBy = "envelope")
	public List<EtiquetaSLIP> getEtiquetas() {
		return etiquetas;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setHoraGeracao(LocalTime horaGeracao) {
		this.horaGeracao = horaGeracao;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public void setAgenciaDestino(String agenciaDestino) {
		this.agenciaDestino = agenciaDestino;
	}

	public void setMunicipioAgenciaDestino(String municipioAgenciaDestino) {
		this.municipioAgenciaDestino = municipioAgenciaDestino;
	}

	public void setUfAgenciaDestino(String ufAgenciaDestino) {
		this.ufAgenciaDestino = ufAgenciaDestino;
	}

	public void setCodeBar(String codeBar) {
		this.codeBar = codeBar;
	}

	public void setDataGeracao(LocalDate dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public void setDataLiberacao(LocalDate dataLiberacao) {
		this.dataLiberacao = dataLiberacao;
	}

	public void setLiberado(boolean liberado) {
		this.liberado = liberado;
	}

	public void setEtiquetas(List<EtiquetaSLIP> etiquetas) {
		this.etiquetas = etiquetas;
	}

	@Column(name = "QUANTIDADE_INSTRUMENTOS")
	public Integer getQuantidadeInstrumentos() {
		return quantidadeInstrumentos;
	}

	public void setQuantidadeInstrumentos(Integer quantidadeInstrumentos) {
		this.quantidadeInstrumentos = quantidadeInstrumentos;
	}

	@Override
	public int compareTo(EnvelopeSLIP entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
