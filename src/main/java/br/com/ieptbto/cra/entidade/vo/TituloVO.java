package br.com.ieptbto.cra.entidade.vo;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.enumeration.PosicaoCampoVazio;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.NONE)
public class TituloVO extends AbstractArquivoVO {

	@XmlAttribute(name = "t52", required = true)
	@IAtributoArquivo(ordem = 52, posicao = 597, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, tamanho = 4,
			descricao = "Número sequêncial do registro do arquivo, independente da quantidade de praças dentro do arquivo.", obrigatoriedade = false)
	private String numeroSequencialArquivo;

	@XmlAttribute(name = "t51", required = true)
	@IAtributoArquivo(ordem = 51, posicao = 578, tamanho = 19, descricao = "Espaço reservado para futuras implementações. Preencher com brancos.",
			obrigatoriedade = false)
	private String complementoRegistro;

	@XmlAttribute(name = "t50", required = true)
	@IAtributoArquivo(ordem = 50, posicao = 568, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, tamanho = 10,
			descricao = "Uso restrito dos cartórios. Preencher com zeros.", obrigatoriedade = false)
	private String valorDemaisDespesas;

	@XmlAttribute(name = "t49", required = true)
	@IAtributoArquivo(ordem = 49, posicao = 567, tamanho = 1,
			descricao = "Informar a letra 'I' para solicitação ao cartório a emissão da 2ª via do Instrumento de Protesto.", obrigatoriedade = false)
	private String instrumentoProtesto;

	@XmlAttribute(name = "t48", required = true)
	@IAtributoArquivo(ordem = 48, posicao = 566, tamanho = 1,
			descricao = "Informar ao cartório se o título será protestado para fins falimentares.Preencher com a letra 'F',caso contrario brancos. ",
			obrigatoriedade = false)
	private String protestoMotivoFalencia;

	@XmlAttribute(name = "t47", required = true)
	@IAtributoArquivo(ordem = 47, posicao = 558, tamanho = 8, descricao = "Uso restrito do serviço de distribuição. Preencher com bancos.",
			obrigatoriedade = false)
	private String complementoCodigoIrregularidade;

	@XmlAttribute(name = "t46", required = true)
	@IAtributoArquivo(ordem = 46, posicao = 557, tamanho = 1,
			descricao = "Identificar o logotipo do banco na letra de câmbio, quando existir mais bancos no conglomerado.", obrigatoriedade = false)
	private String tipoLetraCambio;

	@XmlAttribute(name = "t45", required = true)
	@IAtributoArquivo(ordem = 45, posicao = 554, tamanho = 3,
			descricao = "Identificar o número de parcelas do contrato - exclusivo para protesto de letra de câmbio.", obrigatoriedade = false)
	private String numeroParcelaContrato;

	@XmlAttribute(name = "t44", required = true)
	@IAtributoArquivo(ordem = 44, posicao = 539, tamanho = 15, descricao = "Identificar o número do contrato - exclusivo para protesto de letra de câmbio.",
			obrigatoriedade = false)
	private String numeroContratoBanco;

	@XmlAttribute(name = "t43", required = true)
	@IAtributoArquivo(ordem = 43, posicao = 534, tamanho = 5,
			descricao = "Identificar o número da operação - exclusivo para protesto de letra de câmbio. Os bancos que não usarem devem preencher com zeros.",
			obrigatoriedade = false)
	private String numeroOperacaoBanco;

	@XmlAttribute(name = "t42", required = true)
	@IAtributoArquivo(ordem = 42, posicao = 524, tamanho = 10, descricao = "Uso restrito da Centralizadora de Rmessas de Arquivos (CRA). Preencher com zeros.",
			obrigatoriedade = false, formato = "0000000000")
	private String valorGravacaoEletronica;

	@XmlAttribute(name = "t41", required = true)
	@IAtributoArquivo(ordem = 41, posicao = 518, tamanho = 6, descricao = "Uso restrito do 7º Ofício do Rio de Janeiro. Preencher com zeros.",
			obrigatoriedade = false, formato = "000000")
	private String registroDistribuicao;

	@XmlAttribute(name = "t40", required = true)
	@IAtributoArquivo(ordem = 40, posicao = 508, tamanho = 10, descricao = "Uso restrito do serviço de distribuição.Preencher com brancos.",
			obrigatoriedade = true)
	private String valorCustasCartorioDistribuidor;

	@XmlAttribute(name = "t39", required = true)
	@IAtributoArquivo(ordem = 39, posicao = 488, tamanho = 20, descricao = "Identiticar o bairro do devedor.", obrigatoriedade = true)
	private String bairroDevedor;

	@XmlAttribute(name = "t38", required = true)
	@IAtributoArquivo(ordem = 38, posicao = 486, tamanho = 2, descricao = "", obrigatoriedade = true)
	private String codigoIrregularidade;

	@XmlAttribute(name = "t37", required = true)
	@IAtributoArquivo(ordem = 37, posicao = 478, tamanho = 8, formato = "0", descricao = "", obrigatoriedade = false)
	private String dataOcorrencia;

	@XmlAttribute(name = "t36", required = true)
	@IAtributoArquivo(ordem = 36, posicao = 477, tamanho = 1, descricao = "", formato = " ", obrigatoriedade = true)
	private String declaracaoPortador;

	@XmlAttribute(name = "t35", required = true)
	@IAtributoArquivo(ordem = 35, posicao = 467, tamanho = 10, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO,
			descricao = "Uso restrito do serviço de distribuição.Preencher com zeros.", obrigatoriedade = true)
	private String valorCustaCartorio;

	@XmlAttribute(name = "t34", required = true)
	@IAtributoArquivo(ordem = 34, posicao = 459, tamanho = 8, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO,
			descricao = "Uso restrito do serviço de distribuição.Preencher com zeros.", obrigatoriedade = false)
	private String dataProtocolo;

	@XmlAttribute(name = "t33", required = true)
	@IAtributoArquivo(ordem = 33, posicao = 458, tamanho = 1, descricao = "Uso restrito do serviço de distribuição.Preencher com brancos.",
			obrigatoriedade = false)
	private String tipoOcorrencia;

	@XmlAttribute(name = "t32", required = true)
	@IAtributoArquivo(ordem = 32, posicao = 448, tamanho = 10, descricao = "Uso restrito do serviço de distribuição.Preencher com brancos.",
			obrigatoriedade = true)
	private String numeroProtocoloCartorio;

	@XmlAttribute(name = "t31", required = true)
	@IAtributoArquivo(ordem = 31, posicao = 446, tamanho = 2, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO,
			descricao = "Uso restrito do serviço de distribuição.Preencher com zeros.", obrigatoriedade = true)
	private String codigoCartorio;

	@XmlAttribute(name = "t30", required = true)
	@IAtributoArquivo(ordem = 30, posicao = 444, tamanho = 2, descricao = "Identificar a Unidade Federal do devedor.", obrigatoriedade = true)
	private String ufDevedor;

	@XmlAttribute(name = "t29", required = true)
	@IAtributoArquivo(ordem = 29, posicao = 424, tamanho = 20, descricao = "Identificar a cidade do devedor.", obrigatoriedade = true)
	private String cidadeDevedor;

	@XmlAttribute(name = "t28", required = true)
	@IAtributoArquivo(ordem = 28, posicao = 416, tamanho = 8, descricao = "Identificar o CEP do devedor.", obrigatoriedade = true)
	private String cepDevedor;

	@XmlAttribute(name = "t27", required = true)
	@IAtributoArquivo(ordem = 27, posicao = 371, tamanho = 45, descricao = "Identificar o endereço do devedor.", obrigatoriedade = true)
	private String enderecoDevedor;

	@XmlAttribute(name = "t26", required = true)
	@IAtributoArquivo(ordem = 26, posicao = 360, tamanho = 11, descricao = "Identificar a data da emissão do título, no formato DDMMAAAA. ",
			obrigatoriedade = true)
	private String documentoDevedor;

	@XmlAttribute(name = "t25", required = true)
	@IAtributoArquivo(ordem = 25, posicao = 346, tamanho = 14, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO,
			descricao = "Identificar o número do documento do devedor. Se o documento for diferente de CNPJ ou CPF, preencher com zeros, sendo obrigatório o campo 26.",
			obrigatoriedade = true)
	private String numeroIdentificacaoDevedor;

	@XmlAttribute(name = "t24", required = true)
	@IAtributoArquivo(ordem = 24, posicao = 343, tamanho = 3, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO,
			descricao = "Identificar o tipo do documento. 001 - CNPJ/002 - CPF", obrigatoriedade = true)
	private String tipoIdentificacaoDevedor;

	@XmlAttribute(name = "t23", required = true)
	@IAtributoArquivo(ordem = 23, posicao = 298, tamanho = 45, descricao = "Identificar o nome do devedor.", obrigatoriedade = true)
	private String nomeDevedor;

	@XmlAttribute(name = "t22", required = true)
	@IAtributoArquivo(ordem = 22, posicao = 297, tamanho = 1,
			descricao = "Identificar a quantidade de devedor(es) ou endereço(s) complementar(es) do título. 1 - Para primeiro devedor/2 - Para segundo devedor ou endereços.",
			obrigatoriedade = true)
	private String numeroControleDevedor;

	@XmlAttribute(name = "t21", required = true)
	@IAtributoArquivo(ordem = 21, posicao = 296, tamanho = 1, descricao = "Informar ao cartótio se o título foi aceito pelo devedor.", obrigatoriedade = true)
	private String informacaoSobreAceite;

	@XmlAttribute(name = "t20", required = true)
	@IAtributoArquivo(ordem = 20, posicao = 295, tamanho = 1,
			descricao = "Identificar o tipo de endosso do título. M - Endosso Mandato/T - Endosso Translativo", obrigatoriedade = true)
	private String tipoEndoso;

	@XmlAttribute(name = "t19", required = true)
	@IAtributoArquivo(ordem = 19, posicao = 275, tamanho = 20, descricao = "Informar a praça em que o título será protestado.", obrigatoriedade = true)
	private String pracaProtesto;

	@XmlAttribute(name = "t18", required = true)
	@IAtributoArquivo(ordem = 18, posicao = 261, tamanho = 14, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO,
			descricao = "Informar ao cartório sobre principais descontos/abatimentos ou pagamentos parciais. Preencher com o valor remanescente.",
			obrigatoriedade = true)
	private String saldoTitulo;

	@XmlAttribute(name = "t17", required = true)
	@IAtributoArquivo(ordem = 17, posicao = 247, tamanho = 14, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO,
			descricao = "Informar o valor do título.", obrigatoriedade = true)
	private String valorTitulo;

	@XmlAttribute(name = "t16", required = true)
	@IAtributoArquivo(ordem = 16, posicao = 244, tamanho = 3, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO,
			descricao = "Identificar o tipo da moeda corrente. 001 - Real", obrigatoriedade = true)
	private String tipoMoeda;

	@XmlAttribute(name = "t15", required = true)
	@IAtributoArquivo(ordem = 15, posicao = 236, tamanho = 8,
			descricao = "Identificar a data do vencimento do título, no formato DDMMAAA. Para vencimentos à vista preencher com 99999999.",
			obrigatoriedade = false)
	private String dataVencimentoTitulo;

	@XmlAttribute(name = "t14", required = true)
	@IAtributoArquivo(ordem = 14, posicao = 228, tamanho = 8, descricao = "Identificar a data da emissão do título, no formato DDMMAAAA. ",
			obrigatoriedade = true)
	private String dataEmissaoTitulo;

	@XmlAttribute(name = "t13", required = true)
	@IAtributoArquivo(ordem = 13, posicao = 217, tamanho = 11, descricao = "Identificar o número do título.", obrigatoriedade = true)
	private String numeroTitulo;

	@XmlAttribute(name = "t12", required = true)
	@IAtributoArquivo(ordem = 12, posicao = 214, tamanho = 3, descricao = "Identificar a sigla de identificação da espécie do título.", obrigatoriedade = true)
	private String especieTitulo;

	@XmlAttribute(name = "t11", required = true)
	@IAtributoArquivo(ordem = 11, posicao = 199, tamanho = 15, descricao = "Identificar o título do cedente.", obrigatoriedade = true)
	private String nossoNumero;

	@XmlAttribute(name = "t10", required = true)
	@IAtributoArquivo(ordem = 10, posicao = 197, tamanho = 2,
			descricao = "Identificar a Unidade da Federeção do Sacador/Vendedor. Informar a UF do cedente se não houver sacador.", obrigatoriedade = true)
	private String ufSacadorVendedor;

	@XmlAttribute(name = "t09", required = true)
	@IAtributoArquivo(ordem = 9, posicao = 177, tamanho = 20,
			descricao = "Identificar a cidade do Sacador/Devedor. Informar a cidade do cedente se não houver sacador.", obrigatoriedade = true)
	private String cidadeSacadorVendedor;

	@XmlAttribute(name = "t08", required = true)
	@IAtributoArquivo(ordem = 8, posicao = 169, tamanho = 8,
			descricao = "Identificar o CEP do Sacador/Vendedor. Informar o CEP do cedente se não houver sacador.", obrigatoriedade = true)
	private String cepSacadorVendedor;

	@XmlAttribute(name = "t07", required = true)
	@IAtributoArquivo(ordem = 7, posicao = 124, tamanho = 45,
			descricao = "Identificar o endereço do Sacador/Vendedor. Informar o endereço do cedente se não houver sacador.", obrigatoriedade = true)
	private String enderecoSacadorVendedor;

	@XmlAttribute(name = "t06", required = true)
	@IAtributoArquivo(ordem = 6, posicao = 110, tamanho = 14,
			descricao = "Identificar o número do documento do Sacador. Informar o número do documento do cedente, se não houver sacador.",
			obrigatoriedade = true)
	private String documentoSacador;

	@XmlAttribute(name = "t05", required = true)
	@IAtributoArquivo(ordem = 5, posicao = 65, tamanho = 45, descricao = "Identificar o Sacador/Vendedor. Repetir o nome do cedente se não houver sacador.",
			obrigatoriedade = true)
	private String nomeSacadorVendedor;

	@XmlAttribute(name = "t04", required = true)
	@IAtributoArquivo(ordem = 4, posicao = 20, tamanho = 45, descricao = "Identificar o Cedente/Favorecido", obrigatoriedade = true)
	private String nomeCedenteFavorecido;

	@XmlAttribute(name = "t03", required = true)
	@IAtributoArquivo(ordem = 3, posicao = 5, tamanho = 15, descricao = "Identificar a Agência e Código do Cedente do Título/Cliente", obrigatoriedade = true)
	private String agenciaCodigoCedente;

	@XmlAttribute(name = "t02", required = true)
	@IAtributoArquivo(ordem = 2, posicao = 2, tamanho = 3,
			descricao = "Identificar o código do banco/portador.Preencher com o código de compensação do Banco ou o número de identificação do portador",
			obrigatoriedade = true)
	private String codigoPortador;

	@XmlAttribute(name = "t01", required = true)
	@IAtributoArquivo(ordem = 1, posicao = 1, tamanho = 1, descricao = "Identificar o Registro Transação no arquivo. Constante 1", obrigatoriedade = true,
			validacao = "1", tipo = Integer.class)
	private String identificacaoRegistro;

	public String getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	public String getCodigoPortador() {
		return codigoPortador;
	}

	public String getAgenciaCodigoCedente() {
		return agenciaCodigoCedente;
	}

	public String getNomeCedenteFavorecido() {
		if (nomeCedenteFavorecido != null) {
			return RemoverAcentosUtil.removeAcentos(nomeCedenteFavorecido);
		}
		return nomeCedenteFavorecido;
	}

	public String getNomeSacadorVendedor() {
		if (nomeSacadorVendedor != null) {
			return RemoverAcentosUtil.removeAcentos(nomeSacadorVendedor);
		}
		return nomeSacadorVendedor;
	}

	public String getDocumentoSacador() {
		return documentoSacador;
	}

	public String getEnderecoSacadorVendedor() {
		if (enderecoSacadorVendedor != null) {
			if (enderecoSacadorVendedor.length() > 45) {
				enderecoSacadorVendedor = enderecoSacadorVendedor.substring(0, 44);
			}
			enderecoSacadorVendedor = RemoverAcentosUtil.removeAcentos(enderecoSacadorVendedor);
		}
		return enderecoSacadorVendedor;
	}

	public String getCepSacadorVendedor() {
		return cepSacadorVendedor;
	}

	public String getCidadeSacadorVendedor() {
		if (cidadeSacadorVendedor != null) {
			return RemoverAcentosUtil.removeAcentos(cidadeSacadorVendedor);
		}
		return cidadeSacadorVendedor;
	}

	public String getUfSacadorVendedor() {
		return ufSacadorVendedor;
	}

	public String getNossoNumero() {
		return nossoNumero;
	}

	public String getEspecieTitulo() {
		return especieTitulo;
	}

	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	public String getDataEmissaoTitulo() {
		return dataEmissaoTitulo;
	}

	public String getDataVencimentoTitulo() {
		if (dataVencimentoTitulo.equals(DataUtil.DATA_99999999)) {
			dataVencimentoTitulo = dataEmissaoTitulo;
		}
		return dataVencimentoTitulo;
	}

	public String getTipoMoeda() {
		return tipoMoeda;
	}

	public String getValorTitulo() {
		return valorTitulo;
	}

	public String getSaldoTitulo() {
		return saldoTitulo;
	}

	public String getPracaProtesto() {
		return pracaProtesto;
	}

	public String getTipoEndoso() {
		if (tipoEndoso == null) {
			tipoEndoso = "M";
		} else if (StringUtils.isEmpty(tipoEndoso.trim())) {
			tipoEndoso = "M";
		}
		return tipoEndoso;
	}

	public String getInformacaoSobreAceite() {
		return informacaoSobreAceite;
	}

	public String getNumeroControleDevedor() {
		return numeroControleDevedor;
	}

	public String getNomeDevedor() {
		if (nomeDevedor != null) {
			if (nomeDevedor.length() > 45) {
				nomeDevedor = nomeDevedor.substring(0, 44);
			}
			return RemoverAcentosUtil.removeAcentos(nomeDevedor);
		}
		return nomeDevedor;
	}

	public String getTipoIdentificacaoDevedor() {
		return tipoIdentificacaoDevedor;
	}

	public String getNumeroIdentificacaoDevedor() {
		return numeroIdentificacaoDevedor;
	}

	public String getDocumentoDevedor() {
		return documentoDevedor;
	}

	public String getEnderecoDevedor() {
		if (enderecoDevedor != null) {
			return RemoverAcentosUtil.removeAcentos(enderecoDevedor);
		}
		return enderecoDevedor;
	}

	public String getCepDevedor() {
		return cepDevedor;
	}

	public String getCidadeDevedor() {
		if (cidadeDevedor != null) {
			return RemoverAcentosUtil.removeAcentos(cidadeDevedor);
		}
		return cidadeDevedor;
	}

	public String getUfDevedor() {
		return ufDevedor;
	}

	public String getCodigoCartorio() {
		return codigoCartorio;
	}

	public String getNumeroProtocoloCartorio() {
		return numeroProtocoloCartorio;
	}

	public String getTipoOcorrencia() {
		return tipoOcorrencia;
	}

	public String getDataProtocolo() {
		return dataProtocolo;
	}

	public String getValorCustaCartorio() {
		return valorCustaCartorio;
	}

	public String getDeclaracaoPortador() {
		return declaracaoPortador;
	}

	public String getDataOcorrencia() {
		if (dataOcorrencia == null) {
			dataOcorrencia = StringUtils.EMPTY;
		}
		return dataOcorrencia;
	}

	public String getCodigoIrregularidade() {
		return codigoIrregularidade;
	}

	public String getBairroDevedor() {
		if (bairroDevedor != null) {
			return RemoverAcentosUtil.removeAcentos(bairroDevedor);
		}
		return bairroDevedor;
	}

	public String getValorCustasCartorioDistribuidor() {
		if (valorCustasCartorioDistribuidor == null) {
			valorCustasCartorioDistribuidor = "0";
		}
		return valorCustasCartorioDistribuidor;
	}

	public String getRegistroDistribuicao() {
		return registroDistribuicao;
	}

	public String getValorGravacaoEletronica() {
		return valorGravacaoEletronica;
	}

	public String getNumeroOperacaoBanco() {
		return numeroOperacaoBanco;
	}

	public String getNumeroContratoBanco() {
		return numeroContratoBanco;
	}

	public String getNumeroParcelaContrato() {
		return numeroParcelaContrato;
	}

	public String getTipoLetraCambio() {
		return tipoLetraCambio;
	}

	public String getComplementoCodigoIrregularidade() {
		return complementoCodigoIrregularidade;
	}

	public String getProtestoMotivoFalencia() {
		return protestoMotivoFalencia;
	}

	public String getInstrumentoProtesto() {
		return instrumentoProtesto;
	}

	public String getValorDemaisDespesas() {
		return valorDemaisDespesas;
	}

	public String getComplementoRegistro() {
		return complementoRegistro;
	}

	public String getNumeroSequencialArquivo() {
		return numeroSequencialArquivo;
	}

	public void setIdentificacaoRegistro(String identificacaoRegistro) {
		this.identificacaoRegistro = identificacaoRegistro;
	}

	public void setCodigoPortador(String codigoPortador) {
		this.codigoPortador = codigoPortador;
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

	public void setEnderecoSacadorVendedor(String enderecoSacadorVendedor) {
		this.enderecoSacadorVendedor = enderecoSacadorVendedor;
	}

	public void setCepSacadorVendedor(String cepSacadorVendedor) {
		this.cepSacadorVendedor = cepSacadorVendedor;
	}

	public void setCidadeSacadorVendedor(String cidadeSacadorVendedor) {
		this.cidadeSacadorVendedor = cidadeSacadorVendedor;
	}

	public void setUfSacadorVendedor(String ufSacadorVendedor) {
		this.ufSacadorVendedor = ufSacadorVendedor;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public void setEspecieTitulo(String especieTitulo) {
		this.especieTitulo = especieTitulo;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setDataEmissaoTitulo(String dataEmissaoTitulo) {
		this.dataEmissaoTitulo = dataEmissaoTitulo;
	}

	public void setDataVencimentoTitulo(String dataVencimentoTitulo) {
		this.dataVencimentoTitulo = dataVencimentoTitulo;
	}

	public void setTipoMoeda(String tipoMoeda) {
		this.tipoMoeda = tipoMoeda;
	}

	public void setValorTitulo(String valorTitulo) {
		this.valorTitulo = valorTitulo;
	}

	public void setSaldoTitulo(String saldoTitulo) {
		this.saldoTitulo = saldoTitulo;
	}

	public void setPracaProtesto(String pracaProtesto) {
		this.pracaProtesto = pracaProtesto;
	}

	public void setTipoEndoso(String tipoEndoso) {
		this.tipoEndoso = tipoEndoso;
	}

	public void setInformacaoSobreAceite(String informacaoSobreAceite) {
		this.informacaoSobreAceite = informacaoSobreAceite;
	}

	public void setNumeroControleDevedor(String numeroControleDevedor) {
		this.numeroControleDevedor = numeroControleDevedor;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public void setTipoIdentificacaoDevedor(String tipoIdentificacaoDevedor) {
		this.tipoIdentificacaoDevedor = tipoIdentificacaoDevedor;
	}

	public void setNumeroIdentificacaoDevedor(String numeroIdentificacaoDevedor) {
		this.numeroIdentificacaoDevedor = numeroIdentificacaoDevedor;
	}

	public void setDocumentoDevedor(String documentoDevedor) {
		this.documentoDevedor = documentoDevedor;
	}

	public void setEnderecoDevedor(String enderecoDevedor) {
		this.enderecoDevedor = enderecoDevedor;
	}

	public void setCepDevedor(String cepDevedor) {
		this.cepDevedor = cepDevedor;
	}

	public void setCidadeDevedor(String cidadeDevedor) {
		this.cidadeDevedor = cidadeDevedor;
	}

	public void setUfDevedor(String ufDevedor) {
		this.ufDevedor = ufDevedor;
	}

	public void setCodigoCartorio(String codigoCartorio) {
		this.codigoCartorio = codigoCartorio;
	}

	public void setNumeroProtocoloCartorio(String numeroProtocoloCartorio) {
		this.numeroProtocoloCartorio = numeroProtocoloCartorio;
	}

	public void setTipoOcorrencia(String tipoOcorrencia) {
		this.tipoOcorrencia = tipoOcorrencia;
	}

	public void setDataProtocolo(String dataProtocolo) {
		this.dataProtocolo = dataProtocolo;
	}

	public void setValorCustaCartorio(String valorCustaCartorio) {
		this.valorCustaCartorio = valorCustaCartorio;
	}

	public void setDeclaracaoPortador(String declaracaoPortador) {
		this.declaracaoPortador = declaracaoPortador;
	}

	public void setDataOcorrencia(String dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	public void setCodigoIrregularidade(String codigoIrregularidade) {
		this.codigoIrregularidade = codigoIrregularidade;
	}

	public void setBairroDevedor(String bairroDevedor) {
		this.bairroDevedor = bairroDevedor;
	}

	public void setValorCustasCartorioDistribuidor(String valorCustasCartorioDistribuidor) {
		this.valorCustasCartorioDistribuidor = valorCustasCartorioDistribuidor;
	}

	public void setRegistroDistribuicao(String registroDistribuicao) {
		this.registroDistribuicao = registroDistribuicao;
	}

	public void setValorGravacaoEletronica(String valorGravacaoEletronica) {
		this.valorGravacaoEletronica = valorGravacaoEletronica;
	}

	public void setNumeroOperacaoBanco(String numeroOperacaoBanco) {
		this.numeroOperacaoBanco = numeroOperacaoBanco;
	}

	public void setNumeroContratoBanco(String numeroContratoBanco) {
		this.numeroContratoBanco = numeroContratoBanco;
	}

	public void setNumeroParcelaContrato(String numeroParcelaContrato) {
		this.numeroParcelaContrato = numeroParcelaContrato;
	}

	public void setTipoLetraCambio(String tipoLetraCambio) {
		this.tipoLetraCambio = tipoLetraCambio;
	}

	public void setComplementoCodigoIrregularidade(String complementoCodigoIrregularidade) {
		this.complementoCodigoIrregularidade = complementoCodigoIrregularidade;
	}

	public void setProtestoMotivoFalencia(String protestoMotivoFalencia) {
		this.protestoMotivoFalencia = protestoMotivoFalencia;
	}

	public void setInstrumentoProtesto(String instrumentoProtesto) {
		this.instrumentoProtesto = instrumentoProtesto;
	}

	public void setValorDemaisDespesas(String valorDemaisDespesas) {
		this.valorDemaisDespesas = valorDemaisDespesas;
	}

	public void setComplementoRegistro(String complementoRegistro) {
		this.complementoRegistro = complementoRegistro;
	}

	public void setNumeroSequencialArquivo(String numeroSequencialArquivo) {
		this.numeroSequencialArquivo = numeroSequencialArquivo;
	}
}