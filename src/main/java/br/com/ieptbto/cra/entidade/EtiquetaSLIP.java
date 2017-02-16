package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import br.com.ieptbto.cra.enumeration.regra.RegraBasicaInstrumentoBanco;

/**
 * @author Thasso
 *
 */
@Entity
@Audited
@Table(name = "TB_ETIQUETA_SLIP")
@org.hibernate.annotations.Table(appliesTo = "TB_ETIQUETA_SLIP")
public class EtiquetaSLIP extends AbstractEntidade<EtiquetaSLIP> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private String banco;
	private String agenciaDestino; //
	private String municipioAgenciaDestino; //
	private String ufAgenciaDestino; //
	private String agenciaCodigoCedente;
	private String nomeCedenteFavorecido;
	private String nomeSacadorVendedor;
	private String documentoSacador;
	private String nossoNumero;
	private String numeroTitulo;
	private Date dataProtocolo;
	private Date dataVencimento;
	private BigDecimal valorSaldoTitulo;
	private String numeroProtocoloCartorio;
	private String pracaProtesto;
	private String agenciaCentralizadora;

	private InstrumentoProtesto instrumentoProtesto;
	private EnvelopeSLIP envelope;

	@Id
	@Column(name = "ID_ETIQUETA_SLIP", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "INSTRUMENTO_PROTESTO_ID")
	public InstrumentoProtesto getInstrumentoProtesto() {
		return instrumentoProtesto;
	}

	@ManyToOne
	@JoinColumn(name = "ENVELOPE_SLIP_ID")
	public EnvelopeSLIP getEnvelope() {
		return envelope;
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

	@Column(name = "AGENCIA_CODIGO_CEDENTE", length = 15)
	public String getAgenciaCodigoCedente() {
		return agenciaCodigoCedente;
	}

	@Column(name = "NOME_CEDENTE_FAVORECIDO", length = 45)
	public String getNomeCedenteFavorecido() {
		return nomeCedenteFavorecido;
	}

	@Column(name = "NOME_SACADOR_VENDEDOR", length = 45)
	public String getNomeSacadorVendedor() {
		return nomeSacadorVendedor;
	}

	@Column(name = "DOCUMENTO_SACADOR", length = 14)
	public String getDocumentoSacador() {
		return documentoSacador;
	}

	@Column(name = "NOSSO_NUMERO", length = 15)
	public String getNossoNumero() {
		return nossoNumero;
	}

	@Column(name = "NUMERO_TITULO", length = 11)
	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	@Column(name = "DATA_PROTOCOLO")
	@Type(type = "date")
	public Date getDataProtocolo() {
		return dataProtocolo;
	}

	@Column(name = "DATA_VENCIMENTO")
	@Type(type = "date")
	public Date getDataVencimento() {
		return dataVencimento;
	}

	@Column(name = "VALOR_SALDO_TITULO")
	public BigDecimal getValorSaldoTitulo() {
		return valorSaldoTitulo;
	}

	@Column(name = "NUMERO_PROTOCOLO_CARTORIO", length = 10)
	public String getNumeroProtocoloCartorio() {
		return numeroProtocoloCartorio;
	}

	@Column(name = "PRACA_PROTESTO", length = 30)
	public String getPracaProtesto() {
		return pracaProtesto;
	}

	@Column(name = "AGENCIA_CENTRALIZADORA", length = 20)
	public String getAgenciaCentralizadora() {
		return agenciaCentralizadora;
	}

	public void setId(int id) {
		this.id = id;
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

	public void setAgenciaCodigoCedente(String agenciaCodigoCedente) {
		this.agenciaCodigoCedente = agenciaCodigoCedente;
	}

	public void setNomeCedenteFavorecido(String nomeCedenteFavorecido) {
		this.nomeCedenteFavorecido = nomeCedenteFavorecido;
	}

	public void setNomeSacadorVendedor(String nomeSacadorVendedor) {
		this.nomeSacadorVendedor = nomeSacadorVendedor;
	}

	public void setDocumentoSacador(String documentoSacador) {
		this.documentoSacador = documentoSacador;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setDataProtocolo(Date dataProtocolo) {
		this.dataProtocolo = dataProtocolo;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public void setValorSaldoTitulo(BigDecimal valorSaldoTitulo) {
		this.valorSaldoTitulo = valorSaldoTitulo;
	}

	public void setNumeroProtocoloCartorio(String numeroProtocoloCartorio) {
		this.numeroProtocoloCartorio = numeroProtocoloCartorio;
	}

	public void setPracaProtesto(String pracaProtesto) {
		this.pracaProtesto = pracaProtesto;
	}

	public void setAgenciaCentralizadora(String agenciaCentralizadora) {
		this.agenciaCentralizadora = agenciaCentralizadora;
	}

	public void setInstrumentoProtesto(InstrumentoProtesto instrumentoProtesto) {
		this.instrumentoProtesto = instrumentoProtesto;
	}

	public void setEnvelope(EnvelopeSLIP envelope) {
		this.envelope = envelope;
	}

	public void parseToTitulo(Retorno retorno) {
		this.setBanco(retorno.getRemessa().getInstituicaoDestino().getRazaoSocial().toUpperCase());

		this.setAgenciaCodigoCedente(retorno.getTitulo().getAgenciaCodigoCedente());
		this.setDataProtocolo(retorno.getDataProtocolo().toDate());
		this.setNomeCedenteFavorecido(retorno.getTitulo().getNomeCedenteFavorecido());
		this.setNomeSacadorVendedor(retorno.getTitulo().getNomeDevedor());
		this.setDocumentoSacador(retorno.getTitulo().getNumeroIdentificacaoDevedor());
		if (retorno.getCodigoPortador() == RegraBasicaInstrumentoBanco.BANCO_DO_BRASIL.getCodigoPortador()) {
			this.setNossoNumero(retorno.getTitulo().getAgenciaCodigoCedente().substring(13, 15) + retorno.getTitulo().getNossoNumero());
		} else {
			this.setNossoNumero(retorno.getTitulo().getNossoNumero());
		}
		this.setNumeroTitulo(retorno.getTitulo().getNumeroTitulo());
		this.setDataVencimento(retorno.getTitulo().getDataVencimentoTitulo().toDate());
		this.setValorSaldoTitulo(retorno.getTitulo().getSaldoTitulo());
		this.setNumeroProtocoloCartorio(retorno.getNumeroProtocoloCartorio());
		this.setPracaProtesto(retorno.getTitulo().getPracaProtesto());
		this.setAgenciaCentralizadora(retorno.getRemessa().getCabecalho().getAgenciaCentralizadora());
	}

	@Override
	public int compareTo(EtiquetaSLIP outraEtiqueta) {
		int comparatePortador = this.banco.compareToIgnoreCase(outraEtiqueta.getBanco());
		int comparateMunicipio = this.pracaProtesto.compareToIgnoreCase(outraEtiqueta.getPracaProtesto());

		if (comparatePortador < -1) {
			return -1;
		} else if (comparatePortador > 1) {
			return 1;
		} else if (!this.getBanco().equalsIgnoreCase(outraEtiqueta.getBanco())) {
			String[] apresentantes = new String[] { this.getBanco(), outraEtiqueta.getBanco() };
			Arrays.sort(apresentantes);
			List<String> bancos = Arrays.asList(apresentantes);
			if (this.getBanco().equals(bancos.get(0))) {
				return -1;
			}
			return 1;
		} else if (comparateMunicipio < -1) {
			return -1;
		} else if (comparateMunicipio > 1) {
			return 1;
		} else if (!this.getPracaProtesto().equalsIgnoreCase(outraEtiqueta.getPracaProtesto())) {
			String[] municipios = new String[] { this.getPracaProtesto(), outraEtiqueta.getPracaProtesto() };
			Arrays.sort(municipios);
			List<String> cidades = Arrays.asList(municipios);
			if (this.getPracaProtesto().equals(cidades.get(0))) {
				return -1;
			}
			return 1;
		} else if (Integer.parseInt(this.getNumeroProtocoloCartorio()) < Integer.parseInt(outraEtiqueta.getNumeroProtocoloCartorio())) {
			return -1;
		} else if (Integer.parseInt(this.getNumeroProtocoloCartorio()) > Integer.parseInt(outraEtiqueta.getNumeroProtocoloCartorio())) {
			return 1;
		}
		return 0;
	}
}
