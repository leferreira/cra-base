package br.com.ieptbto.cra.entidade;

import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.Audited;

import br.com.ieptbto.cra.util.ZipFile;

/**
 * 
 * @author Leandro
 *
 */

@Entity
@Audited
@Table(name = "TB_DADOS_ARQUIVO_RECEBIDO", schema = "ARQUIVO_RECEBIDO")
@org.hibernate.annotations.Table(appliesTo = "TB_DADOS_ARQUIVO_RECEBIDO")
public class DadosArquivoRecebido extends AbstractEntidade<DadosArquivoRecebido> {

    /****/
    private static final long serialVersionUID = 1L;

    private int id;
    private String nomeArquivo;
    private String login;
    private String senha;
    private String servico;
    private byte[] dados;
    private Date dataRecebimento;

    @Override
    @Id
    @Column(name = "ID_DADOS_ARQUIVO_RECEBIDO", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return this.id;
    }

    @Column(name = "NOME_ARQUIVO", length = 100)
    public String getNomeArquivo() {
        return nomeArquivo;
    }

    @Column(name = "LOGIN", length = 100)
    public String getLogin() {
        return login;
    }

    @Column(name = "SENHA", length = 100)
    public String getSenha() {
        return senha;
    }

    @Column(name = "DADOS")
    public byte[] getDados() {
        return dados;
    }

    @Column(name = "SERVICO")
    public String getServico() {
        return servico;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_RECEBIMENTO")
    public Date getDataRecebimento() {
        return dataRecebimento;
    }

    public void setDataRecebimento(Date dataRecebimento) {
        this.dataRecebimento = dataRecebimento;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setDados(byte[] dados) {
        this.dados = dados;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(DadosArquivoRecebido entidade) {
        CompareToBuilder compareTo = new CompareToBuilder();
        compareTo.append(this.getId(), entidade.getId());
        return compareTo.toComparison();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DadosArquivoRecebido) {
            DadosArquivoRecebido dadosArquivoRecebido = DadosArquivoRecebido.class.cast(obj);
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(this.getId(), dadosArquivoRecebido.getId());
            return equalsBuilder.isEquals();
        }
        return false;
    }

    public static DadosArquivoRecebido set(String login, String senha, String nomeArquivo, String dados, String servico, Date data)
            throws IOException {
        DadosArquivoRecebido dadosArquivoRecebido = new DadosArquivoRecebido();
        dadosArquivoRecebido.setLogin(login);
        dadosArquivoRecebido.setSenha(senha);
        dadosArquivoRecebido.setNomeArquivo(nomeArquivo);
        dadosArquivoRecebido.setDados(ZipFile.zipFile(nomeArquivo, dados));
        dadosArquivoRecebido.setServico(servico);
        dadosArquivoRecebido.setDataRecebimento(data);

        return dadosArquivoRecebido;
    }

    public static DadosArquivoRecebido set(String login, String senha, String nomeArquivo, String dados, String servico)
            throws IOException {
        return set(login, senha, nomeArquivo, dados, servico, new Date());
    }

}
