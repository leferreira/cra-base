package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.bytecode.internal.javassist.FieldHandled;
import org.hibernate.bytecode.internal.javassist.FieldHandler;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.StatusRemessa;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("rawtypes")
@Entity
@Audited
@Table(name = "TB_REMESSA")
@org.hibernate.annotations.Table(appliesTo = "TB_REMESSA")
public class Remessa extends AbstractRemessa<Remessa> implements FieldHandled {

    /***/
    private static final long serialVersionUID = 1L;

    private int id;
    private Arquivo arquivo;
    private Arquivo arquivoGeradoProBanco;
    private LocalDate dataRecebimento;
    private Instituicao instituicaoOrigem;
    private Instituicao instituicaoDestino;
    private CabecalhoRemessa cabecalho;
    private Rodape rodape;
    private List<Titulo> titulos;
    private Batimento batimento;
    private StatusRemessa statusRemessa;
    private SituacaoBatimentoRetorno situacaoBatimentoRetorno;
    private Boolean situacao;
    private Boolean devolvidoPelaCRA;
    private FieldHandler handler;

    @Id
    @Column(name = "ID_REMESSA", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
	return id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARQUIVO_ID")
    public Arquivo getArquivo() {
	return arquivo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARQUIVO_GERADO_BANCO_ID")
    public Arquivo getArquivoGeradoProBanco() {
	return arquivoGeradoProBanco;
    }

    @Column(name = "DATA_RECEBIMENTO")
    public LocalDate getDataRecebimento() {
	return dataRecebimento;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSTITUICAO_DESTINO_ID")
    public Instituicao getInstituicaoDestino() {
	return instituicaoDestino;
    }

    @OneToOne
    @JoinColumn(name = "CABECALHO_ID")
    public CabecalhoRemessa getCabecalho() {
	return cabecalho;
    }

    @javax.persistence.Transient
    public List<Titulo> getTitulos() {
	return titulos;
    }

    @OneToOne
    @JoinColumn(name = "RODAPE_ID")
    public Rodape getRodape() {
	return rodape;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSTITUICAO_ORIGEM_ID")
    public Instituicao getInstituicaoOrigem() {
	return instituicaoOrigem;
    }

    @Column(name = "SITUACAO_BATIMENTO_RETORNO", length = 50)
    @Enumerated(EnumType.STRING)
    public SituacaoBatimentoRetorno getSituacaoBatimentoRetorno() {
	return situacaoBatimentoRetorno;
    }

    @Column(name = "SITUACAO", nullable = true)
    public Boolean getSituacao() {
	return situacao;
    }

    @OneToOne(mappedBy = "remessa", fetch = FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    public Batimento getBatimento() {
	if (this.handler != null) {
	    return (Batimento) this.handler.readObject(this, "batimento", batimento);
	}
	return batimento;
    }

    public void setBatimento(Batimento batimento) {
	if (this.handler != null) {
	    this.batimento = (Batimento) this.handler.writeObject(this, "batimento", this.batimento, batimento);
	}
	this.batimento = batimento;
    }

    public void setSituacao(Boolean situacao) {
	this.situacao = situacao;
    }

    public void setId(int id) {
	this.id = id;
    }

    public void setArquivo(Arquivo arquivo) {
	this.arquivo = arquivo;
    }

    public void setTitulos(List<Titulo> titulos) {
	this.titulos = titulos;
    }

    public void setDataRecebimento(LocalDate dataRecebimento) {
	this.dataRecebimento = dataRecebimento;
    }

    public void setArquivoGeradoProBanco(Arquivo arquivoGeradoProBanco) {
	this.arquivoGeradoProBanco = arquivoGeradoProBanco;
    }

    public void setInstituicaoDestino(Instituicao instituicaoDestino) {
	this.instituicaoDestino = instituicaoDestino;
    }

    public void setInstituicaoOrigem(Instituicao instituicaoOrigem) {
	this.instituicaoOrigem = instituicaoOrigem;
    }

    public void setSituacaoBatimentoRetorno(SituacaoBatimentoRetorno situacaoBatimentoRetorno) {
	this.situacaoBatimentoRetorno = situacaoBatimentoRetorno;
    }

    @Override
    public int compareTo(Remessa entidade) {
	CompareToBuilder compareToBuilder = new CompareToBuilder();
	compareToBuilder.append(this.getId(), entidade.getId());
	return compareToBuilder.toComparison();
    }

    public void setCabecalho(CabecalhoRemessa cabecalho) {
	this.cabecalho = cabecalho;
    }

    public void setRodape(Rodape rodape) {
	this.rodape = rodape;
    }

    @Column(name = "STATUS_REMESSA")
    @Enumerated(EnumType.STRING)
    public StatusRemessa getStatusRemessa() {
	return statusRemessa;
    }

    private List<Deposito> listaDepositos;

    @javax.persistence.Transient
    public List<Deposito> getListaDepositos() {
	return listaDepositos;
    }

    public void setListaDepositos(List<Deposito> listaDepositos) {
	this.listaDepositos = listaDepositos;
    }

    public void setStatusRemessa(StatusRemessa statusRemessa) {
	this.statusRemessa = statusRemessa;
    }

    public Boolean getDevolvidoPelaCRA() {
	if (devolvidoPelaCRA == null) {
	    devolvidoPelaCRA = false;
	}
	return devolvidoPelaCRA;
    }

    public void setDevolvidoPelaCRA(Boolean devolvidoPelaCRA) {
	this.devolvidoPelaCRA = devolvidoPelaCRA;
    }

    @Override
    public void setFieldHandler(FieldHandler handler) {
	this.handler = handler;

    }

    @Override
    public FieldHandler getFieldHandler() {
	return this.handler;
    }
}