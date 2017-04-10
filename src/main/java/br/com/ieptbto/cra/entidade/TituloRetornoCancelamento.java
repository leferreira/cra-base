package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * 
 * @author Leandro
 *
 */
@SuppressWarnings("serial")
@Entity
@Audited
@Table(name = "TB_TITULO_RETORNO_CANCELAMENTO")
@org.hibernate.annotations.Table(appliesTo = "TB_TITULO_RETORNO_CANCELAMENTO")

public class TituloRetornoCancelamento extends Titulo<TituloRetornoCancelamento> {

    private int id;
    private TituloRemessa titulo;
    private Date dataCadastro;
    private BigDecimal valorTitulo;

    @Override
    @Id
    @Column(name = "ID_TITULO_RETORNO_CANCELAMENTO", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    @OneToOne
    @JoinColumn(name = "TITULO_REMESSA_ID", columnDefinition = "integer")
    public TituloRemessa getTitulo() {
        return titulo;
    }

    @Column(name = "VALOR_TITULO")
    public BigDecimal getValorTitulo() {
        return valorTitulo;
    }

    @Column(name = "DATA_CADASTRO")
    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setTitulo(TituloRemessa titulo) {
        this.titulo = titulo;
    }

    public void setValorTitulo(BigDecimal valorTitulo) {
        this.valorTitulo = valorTitulo;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void parse(Retorno tituloRetorno) {
        this.setAgenciaCodigoCedente(tituloRetorno.getAgenciaCodigoCedente());
        this.setBairroDevedor(tituloRetorno.getBairroDevedor());
        this.setCodigoIrregularidade(tituloRetorno.getCodigoIrregularidade());
        this.setCodigoCartorio(tituloRetorno.getCodigoCartorio());
        this.setCodigoPortador(tituloRetorno.getCodigoPortador());
        this.setComplementoCodigoIrregularidade(tituloRetorno.getComplementoCodigoIrregularidade());
        this.setDataOcorrencia(tituloRetorno.getDataOcorrencia());
        this.setDataProtocolo(tituloRetorno.getDataProtocolo());
        this.setDeclaracaoPortador(tituloRetorno.getDeclaracaoPortador());
        this.setIdentificacaoRegistro(tituloRetorno.getIdentificacaoRegistro());
        this.setNossoNumero(tituloRetorno.getNossoNumero());
        this.setNumeroControleDevedor(tituloRetorno.getNumeroControleDevedor());
        this.setNumeroProtocoloCartorio(tituloRetorno.getNumeroProtocoloCartorio());
        this.setNumeroSequencialArquivo(tituloRetorno.getNumeroSequencialArquivo());
        this.setNumeroTitulo(tituloRetorno.getNumeroTitulo());
        this.setRegistroDistribuicao(tituloRetorno.getRegistroDistribuicao());
        this.setRemessa(tituloRetorno.getRemessa());
        this.setSaldoTitulo(tituloRetorno.getSaldoTitulo());
        this.setTipoOcorrencia(tituloRetorno.getTipoOcorrencia());
        this.setTitulo(tituloRetorno.getTitulo());
        this.setUfDevedor(tituloRetorno.getUfDevedor());
        this.setValorCustaCartorio(tituloRetorno.getValorCustaCartorio());
        this.setValorCustasCartorioDistribuidor(tituloRetorno.getValorCustasCartorioDistribuidor());
        this.setValorDemaisDespesas(tituloRetorno.getValorDemaisDespesas());
        this.setValorGravacaoEletronica(tituloRetorno.getValorGravacaoEletronica());
        this.setValorTitulo(tituloRetorno.getSaldoTitulo());
        this.setDataCadastro(new Date());

    }

}
