package br.com.ieptbto.cra.entidade.vo;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.EspecieTituloEntradaManual;
import br.com.ieptbto.cra.enumeration.regra.TipoIdentificacaoRegistro;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.NONE)
public class TituloConvenioVO extends AbstractArquivoVO {

	@XmlAttribute(name = "t01")
	@IAtributoArquivo(ordem = 1, posicao = 0, formato = " ", tamanho = 11, descricao = "")
	private String numeroTitulo;

	@XmlAttribute(name = "t02")
	@IAtributoArquivo(ordem = 2, posicao = 0, formato = " ", tamanho = 20, descricao = "")
	private String dataEmissao;

	@XmlAttribute(name = "t03")
	@IAtributoArquivo(ordem = 3, posicao = 0, formato = " ", tamanho = 45, descricao = "")
	private String nomeDevedor;

	@XmlAttribute(name = "t04")
	@IAtributoArquivo(ordem = 4, posicao = 0, formato = "0", tamanho = 30, descricao = "")
	private String numeroIdentificacaoDevedor;

	@XmlAttribute(name = "t05")
	@IAtributoArquivo(ordem = 5, posicao = 0, formato = " ", tamanho = 45, descricao = "")
	private String enderecoDevedor;

	@XmlAttribute(name = "t06")
	@IAtributoArquivo(ordem = 6, posicao = 0, formato = " ", tamanho = 20, descricao = "")
	private String cidadeDevedor;

	@XmlAttribute(name = "t07")
	@IAtributoArquivo(ordem = 7, posicao = 0, formato = " ", tamanho = 20, descricao = "")
	private String bairroDevedor;

	@XmlAttribute(name = "t08")
	@IAtributoArquivo(ordem = 8, posicao = 0, formato = "0", tamanho = 8, descricao = "")
	private String cepDevedor;

	@XmlAttribute(name = "t09")
	@IAtributoArquivo(ordem = 9, posicao = 0, formato = " ", tamanho = 11, descricao = "")
	private String documentoDevedor;

	@XmlAttribute(name = "t10")
	@IAtributoArquivo(ordem = 10, posicao = 0, formato = " ", tamanho = 15, descricao = "")
	private String nossoNumero;

	@XmlAttribute(name = "t11")
	@IAtributoArquivo(ordem = 11, posicao = 0, formato = " ", tamanho = 19, descricao = "")
	private String complementoRegistro;

	@XmlAttribute(name = "t12")
	@IAtributoArquivo(ordem = 12, posicao = 0, formato = " ", tamanho = 20, descricao = "")
	private String dataVencimento;

	@XmlAttribute(name = "t13")
	@IAtributoArquivo(ordem = 13, posicao = 0, formato = " ", tamanho = 20, descricao = "")
	private String saldoTitulo;

	private Municipio municipio;

    @Override
    public String getIdentificacaoRegistro() {
        return null;
    }

    public static TituloRemessa createTitulo(Instituicao instituicao, Municipio municipioInsituicao) {
        TituloRemessa titulo = new TituloRemessa();
        titulo.setIdentificacaoRegistro(TipoIdentificacaoRegistro.TITULO);
        titulo.setCodigoPortador(instituicao.getCodigoCompensacao());
        titulo.setAgenciaCodigoCedente(StringUtils.leftPad(instituicao.getCodigoCompensacao() + DataUtil.getDataAtual(new SimpleDateFormat("ddMMyyyy")), 15, "0"));
        titulo.setNomeCedenteFavorecido(RemoverAcentosUtil.removeAcentos(instituicao.getRazaoSocial()).toUpperCase());
        titulo.setNomeSacadorVendedor(RemoverAcentosUtil.removeAcentos(instituicao.getRazaoSocial()).toUpperCase());
        titulo.setDocumentoSacador(instituicao.getCnpj());
        titulo.setEnderecoSacadorVendedor(RemoverAcentosUtil.removeAcentos(instituicao.getEndereco()).toUpperCase());
        titulo.setCidadeSacadorVendedor(RemoverAcentosUtil.removeAcentos(municipioInsituicao.getNomeMunicipio().toUpperCase()));
        titulo.setUfSacadorVendedor(municipioInsituicao.getUf());
        titulo.setEnderecoSacadorVendedor(instituicao.getEndereco());
        titulo.setDataCadastro(new Date());
        titulo.setCepSacadorVendedor("77000000");
        titulo.setNumeroControleDevedor(1);
        titulo.setInformacaoSobreAceite("N");
        titulo.setTipoMoeda("001");
        titulo.setTipoEndoso("M");
        titulo.setCodigoCartorio(1);
        titulo.setEspecieTitulo(EspecieTituloEntradaManual.CDA.getConstante());
        titulo.setDataOcorrencia(new LocalDate());
        return titulo;
    }

    public String getNumeroTitulo() {
        return numeroTitulo;
    }

    public void setNumeroTitulo(String numeroTitulo) {
        this.numeroTitulo = numeroTitulo;
    }

    public String getDataEmissao() {
        if (dataEmissao != null) {
            dataEmissao = dataEmissao.replace("/","");
        }
        return dataEmissao;
    }

    public void setDataEmissao(String dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getNomeDevedor() {
        return RemoverAcentosUtil.removeAcentos(nomeDevedor).toUpperCase();
    }

    public void setNomeDevedor(String nomeDevedor) {
        this.nomeDevedor = nomeDevedor;
    }

    public String getNumeroIdentificacaoDevedor() {
        if (!StringUtils.isBlank(numeroIdentificacaoDevedor)) {
            numeroIdentificacaoDevedor = numeroIdentificacaoDevedor.replace("/", "").replace(".", "").replace("-", "");
        }
        return numeroIdentificacaoDevedor;
    }

    public void setNumeroIdentificacaoDevedor(String numeroIdentificacaoDevedor) {
        this.numeroIdentificacaoDevedor = numeroIdentificacaoDevedor;
    }

    public String getEnderecoDevedor() {
        return RemoverAcentosUtil.removeAcentos(enderecoDevedor).toUpperCase();
    }

    public void setEnderecoDevedor(String enderecoDevedor) {
        this.enderecoDevedor = enderecoDevedor;
    }

    public String getCidadeDevedor() {
        return RemoverAcentosUtil.removeAcentos(cidadeDevedor).toUpperCase();
    }

    public void setCidadeDevedor(String cidadeDevedor) {
        this.cidadeDevedor = cidadeDevedor;
    }

    public String getBairroDevedor() {
        return RemoverAcentosUtil.removeAcentos(bairroDevedor).toUpperCase();
    }

    public void setBairroDevedor(String bairroDevedor) {
        this.bairroDevedor = bairroDevedor;
    }

    public String getCepDevedor() {
        if (cepDevedor != null) {
            cepDevedor = cepDevedor.replace("/", "").replace("-", "").replace(".", "");
        }
        return cepDevedor;
    }

    public void setCepDevedor(String cepDevedor) {
        this.cepDevedor = cepDevedor;
    }

    public String getDocumentoDevedor() {
        return RemoverAcentosUtil.removeAcentos(documentoDevedor);
    }

    public void setDocumentoDevedor(String documentoDevedor) {
        this.documentoDevedor = documentoDevedor;
    }

    public String getNossoNumero() {
        if (nossoNumero != null && nossoNumero.length() > 15) {
            nossoNumero = nossoNumero.substring(0, 14);
        }
        return nossoNumero;
    }

    public void setNossoNumero(String nossoNumero) {
        this.nossoNumero = nossoNumero;
    }

    public String getComplementoRegistro() {
        return RemoverAcentosUtil.removeAcentos(complementoRegistro);
    }

    public void setComplementoRegistro(String complementoRegistro) {
        this.complementoRegistro = complementoRegistro;
    }

    public String getDataVencimento() {
        if (dataVencimento != null) {
            dataVencimento = dataVencimento.replace("/","");
        }
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getSaldoTitulo() {
        return saldoTitulo;
    }

    public void setSaldoTitulo(String valorSaldo) {
        this.saldoTitulo = valorSaldo;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public String asString(){
        return nomeDevedor + " - " + numeroTitulo + " - " + saldoTitulo;
    }
}