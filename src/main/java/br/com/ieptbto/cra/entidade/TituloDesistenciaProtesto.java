package br.com.ieptbto.cra.entidade;

import javax.xml.bind.annotation.XmlAttribute;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;

/**
 * 
 * @author Lefer
 *
 */
public class TituloDesistenciaProtesto extends AbstractEntidade<TituloDesistenciaProtesto> {

	private static final long serialVersionUID = 4806576818944343466L;

	private int id;

	@XmlAttribute(name = "t01")
	@IAtributoArquivo(ordem = 1, posicao = 1, tamanho = 1, descricao = "Identificar o Registro Transação no arquivo. Constante 2", obrigatoriedade = true, validacao = "2", tipo = Integer.class)
	private String tipoRegistro;

	@XmlAttribute(name = "t02")
	@IAtributoArquivo(ordem = 2, posicao = 2, tamanho = 10, descricao = "Número do protocolo", obrigatoriedade = true, tipo = String.class)
	private String numeroProtocolo;

	@IAtributoArquivo(ordem = 3, posicao = 12, tamanho = 19, descricao = "data da Protocolagem DDMMAAA", obrigatoriedade = true)
	@XmlAttribute(name = "t03")
	private String dataProtocolagem;

	@XmlAttribute(name = "t04")
	private String numeroTitulo;

	@XmlAttribute(name = "t05")
	private String nomeDevedorPrincipal;

	@XmlAttribute(name = "t06")
	private String valorTitulo;

	@XmlAttribute(name = "t07")
	private String solicitacaoCancelamentoSustacao;

	@XmlAttribute(name = "t08")
	private String agenciaConta;

	@XmlAttribute(name = "t09")
	private String carteiraNossoNumero;

	@XmlAttribute(name = "t10")
	private String reservado;

	@XmlAttribute(name = "t11")
	private String numeroControleRecebimento;

	@XmlAttribute(name = "t12")
	private String sequenciaRegistro;

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public int compareTo(TituloDesistenciaProtesto entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
