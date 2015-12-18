package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

/**
 * 
 * @author Lefer
 *
 */
@Entity
@Audited
@Table(name = "TB_CABECALHO_CNP")
@org.hibernate.annotations.Table(appliesTo = "TB_CABECALHO_CNP")
public class CabecalhoCnp extends AbstractEntidade<CabecalhoCnp> {

	private static final long serialVersionUID = 1L;
	private int id;
	private RemessaCnp remessa;
	private String codigoRegistro;
	private LocalDate dataMovimento;
	private String numeroRemessaArquivo;
	private int tipoDocumento;
	private String numeroCnpjCpfResponsavelCartorio;
	private String identificacaoDoArquivo;
	private String codigoRemessa;
	private String numeroDDD;
	private String numeroTelefoneInstituicaoInformante;
	private String numeroRamalContato;
	private String nomeContatoInstituicaoInformante;
	private String numeroVersaoSoftware;
	private String codigoEDI;
	private String periodicidadeEnvio;

	@Override
	@Id
	@Column(name = "ID_CABECALHO_CNP", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	@OneToOne(mappedBy = "cabecalho")
	public RemessaCnp getRemessa() {
		return remessa;
	}

	@Column(name = "CODIGO_REGISTRO")
	public String getCodigoRegistro() {
		return codigoRegistro;
	}

	@Column(name = "DATA_MOVIMENTO")
	public LocalDate getDataMovimento() {
		return dataMovimento;
	}

	@Column(name = "NUMERO_REMESSA_ARQUIVO")
	public String getNumeroRemessaArquivo() {
		return numeroRemessaArquivo;
	}

	@Column(name = "TIPO_DOCUMENTO")
	public int getTipoDocumento() {
		return tipoDocumento;
	}

	@Column(name = "CPF_CNPJ_RESPONSAVEL_CARTORIO")
	public String getNumeroCnpjCpfResponsavelCartorio() {
		return numeroCnpjCpfResponsavelCartorio;
	}

	@Column(name = "IDENTIFICACAO_ARQUIVO")
	public String getIdentificacaoDoArquivo() {
		return identificacaoDoArquivo;
	}

	@Column(name = "CODIGO_REMESSA")
	public String getCodigoRemessa() {
		return codigoRemessa;
	}

	@Column(name = "DDD")
	public String getNumeroDDD() {
		return numeroDDD;
	}

	@Column(name = "NUMERO_TELEFONE_INSTITUICAO")
	public String getNumeroTelefoneInstituicaoInformante() {
		return numeroTelefoneInstituicaoInformante;
	}

	@Column(name = "NUMERO_RAMAL_CONTATO")
	public String getNumeroRamalContato() {
		return numeroRamalContato;
	}

	@Column(name = "NOME_CONTATO_INSTITUICAO")
	public String getNomeContatoInstituicaoInformante() {
		return nomeContatoInstituicaoInformante;
	}

	@Column(name = "NUMERO_VERSAO_SOFTWARE")
	public String getNumeroVersaoSoftware() {
		return numeroVersaoSoftware;
	}

	@Column(name = "CODIGO_EDI")
	public String getCodigoEDI() {
		return codigoEDI;
	}

	@Column(name = "PERIODICIDADE_ENVIO")
	public String getPeriodicidadeEnvio() {
		return periodicidadeEnvio;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRemessa(RemessaCnp remessa) {
		this.remessa = remessa;
	}

	public void setCodigoRegistro(String codigoRegistro) {
		this.codigoRegistro = codigoRegistro;
	}

	public void setDataMovimento(LocalDate dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public void setNumeroRemessaArquivo(String numeroRemessaArquivo) {
		this.numeroRemessaArquivo = numeroRemessaArquivo;
	}

	public void setTipoDocumento(int tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public void setNumeroCnpjCpfResponsavelCartorio(String numeroCnpjCpfResponsavelCartorio) {
		this.numeroCnpjCpfResponsavelCartorio = numeroCnpjCpfResponsavelCartorio;
	}

	public void setIdentificacaoDoArquivo(String identificacaoDoArquivo) {
		this.identificacaoDoArquivo = identificacaoDoArquivo;
	}

	public void setCodigoRemessa(String codigoRemessa) {
		this.codigoRemessa = codigoRemessa;
	}

	public void setNumeroDDD(String numeroDDD) {
		this.numeroDDD = numeroDDD;
	}

	public void setNumeroTelefoneInstituicaoInformante(String numeroTelefoneInstituicaoInformante) {
		this.numeroTelefoneInstituicaoInformante = numeroTelefoneInstituicaoInformante;
	}

	public void setNumeroRamalContato(String numeroRamalContato) {
		this.numeroRamalContato = numeroRamalContato;
	}

	public void setNomeContatoInstituicaoInformante(String nomeContatoInstituicaoInformante) {
		this.nomeContatoInstituicaoInformante = nomeContatoInstituicaoInformante;
	}

	public void setNumeroVersaoSoftware(String numeroVersaoSoftware) {
		this.numeroVersaoSoftware = numeroVersaoSoftware;
	}

	public void setCodigoEDI(String codigoEDI) {
		this.codigoEDI = codigoEDI;
	}

	public void setPeriodicidadeEnvio(String periodicidadeEnvio) {
		this.periodicidadeEnvio = periodicidadeEnvio;
	}

	@Override
	public int compareTo(CabecalhoCnp entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
