package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 */
public enum CampoLayout implements CraEnum {
	IDENTIFICACAOREGISTRO("1", "identificacaoRegistro"), CODIGOPORTADOR("2", "codigoPortador"), //
	AGENCIACODIGOCEDENTE("3", "agenciaCodigoCedente"), NOMECEDENTEFAVORECIDO("4", "nomeCedenteFavorecido"), //
	NOMESACADORVENDEDOR("5", "nomeSacadorVendedor"), DOCUMENTOSACADOR("6", "documentoSacador"), //
	ENDERECOSACADORVENDEDOR("7", "enderecoSacadorVendedor"), CEPSACADORVENDEDOR("8", "cepSacadorVendedor"), //
	CIDADESACADORVENDEDOR("9", "cidadeSacadorVendedor"), UFSACADORVENDEDOR("10", "ufSacadorVendedor"), //
	NOSSONUMERO("11", "nossoNumero"), ESPECIETITULO("12", "especieTitulo"), NUMEROTITULO("13", "numeroTitulo"), //
	DATAEMISSAOTITULO("14", "dataEmissaoTitulo"), DATAVENCIMENTOTITULO("15", "dataVencimentoTitulo"), //
	TIPOMOEDA("16", "tipoMoeda"), VALORTITULO("17", "valorTitulo"), SALDOTITULO("18", "saldoTitulo"), //
	PRACAPROTESTO("19", "pracaProtesto"), TIPOENDOSO("20", "tipoEndoso"), //
	INFORMACAOSOBREACEITE("21", "informacaoSobreAceite"), NUMEROCONTROLEDEVEDOR("22", "numeroControleDevedor"), //
	NOMEDEVEDOR("23", "nomeDevedor"), TIPOIDENTIFICACAODEVEDOR("24", "tipoIdentificacaoDevedor"), //
	NUMEROIDENTIFICACAODEVEDOR("25", "numeroIdentificacaoDevedor"), DOCUMENTODEVEDOR("26", "documentoDevedor"), //
	ENDERECODEVEDOR("27", "enderecoDevedor"), CEPDEVEDOR("28", "cepDevedor"), CIDADEDEVEDOR("29", "cidadeDevedor"), //
	UFDEVEDOR("30", "ufDevedor"), CODIGOCARTORIO("31", "codigoCartorio"), //
	NUMEROPROTOCOLOCARTORIO("32", "numeroProtocoloCartorio"), TIPOOCORRENCIA("33", "tipoOcorrencia"), //
	DATAPROTOCOLO("34", "dataProtocolo"), VALORCUSTACARTORIO("35", "valorCustaCartorio"), //
	DECLARACAOPORTADOR("36", "declaracaoPortador"), DATAOCORRENCIA("37", "dataOcorrencia"), //
	CODIGOIRREGULARIDADE("38", "codigoIrregularidade"), BAIRRODEVEDOR("39", "bairroDevedor"), //
	VALORCUSTASCARTORIODISTRIBUIDOR("40", "valorCustasCartorioDistribuidor"), //
	REGISTRODISTRIBUICAO("41", "registroDistribuicao"), VALORGRAVACAOELETRONICA("42", "valorGravacaoEletronica"), //
	NUMEROOPERACAOBANCO("43", "numeroOperacaoBanco"), NUMEROCONTRATOBANCO("44", "numeroContratoBanco"), //
	NUMEROPARCELACONTRATO("45", "numeroParcelaContrato"), TIPOLETRACAMBIO("46", "tipoLetraCambio"), //
	COMPLEMENTOCODIGOIRREGULARIDADE("47", "complementoCodigoIrregularidade"), //
	PROTESTOMOTIVOFALENCIA("48", "protestoMotivoFalencia"), INSTRUMENTOPROTESTO("49", "instrumentoProtesto"), //
	VALORDEMAISDESPESAS("50", "valorDemaisDespesas"), COMPLEMENTOREGISTRO("51", "complementoRegistro"), //
	NUMEROSEQUENCIALARQUIVO("52", "numeroSequencialArquivo");

	private String constante;
	private String label;

	private CampoLayout(String constante, String label) {
		this.constante = constante;
		this.label = label;
	}

	/**
	 * retorna o tipo de arquivo dependendo do tipo informado
	 * 
	 * @param valor
	 * @return tipo arquivo
	 */
	public static CampoLayout get(String valor) {
		CampoLayout[] values = CampoLayout.values();
		for (CampoLayout especieTitulo : values) {
			if (valor.equals(especieTitulo.getConstante())) {
				return especieTitulo;
			}
		}
		throw new InfraException("Campo " + valor + " do layout não encontrado");
	}

	@Override
	public String getConstante() {
		return this.constante;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public String toString() {
		return this.label;
	}

}
