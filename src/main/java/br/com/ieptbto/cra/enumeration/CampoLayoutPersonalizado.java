package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 */
public enum CampoLayoutPersonalizado implements AbstractCraEnum {

											AGENCIACODIGOCEDENTE("3", "Agência Código Cedente"),
											NOSSONUMERO("11", "Nosso Número"),
											ESPECIETITULO("12", "Espécie Título"),
											NUMEROTITULO("13", "Numero Título"), //
											DATAEMISSAOTITULO("14", "Data Emissão Título"),
											DATAVENCIMENTOTITULO("15", "Data Vencimento Titulo"), //
											VALORTITULO("17", "Valor Título"),
											SALDOTITULO("18", "Saldo Título"), //
											NUMEROCONTROLEDEVEDOR("22", "Número Controle Devedor"), //
											NOMEDEVEDOR("23", "Nome Devedor"),
											TIPOIDENTIFICACAODEVEDOR("24", "Tipo Identificação Devedor"), //
											NUMEROIDENTIFICACAODEVEDOR("25", "Número Identificação Devedor"),
											DOCUMENTODEVEDOR("26", "Documento Devedor"), //
											ENDERECODEVEDOR("27", "Endereço Devedor"),
											CEPDEVEDOR("28", "Cep Devedor"),
											CIDADEDEVEDOR("29", "Cidade Devedor"), //
											UFDEVEDOR("30", "UF Devedor"),
											DECLARACAOPORTADOR("36", "Declaração Portador"),
											BAIRRODEVEDOR("39", "Bairro Devedor"), //
											NUMEROOPERACAOBANCO("43", "Número Operação Banco"),
											NUMEROCONTRATOBANCO("44", "Número Contrato Banco"), //
											NUMEROPARCELACONTRATO("45", "Número Parcela Contrato"),
											COMPLEMENTOCODIGOIRREGULARIDADE("47", "Complemento Código Irregularidade"), //
											INSTRUMENTOPROTESTO("49", "Instrumento Protesto"), //
											COMPLEMENTOREGISTRO("51", "Complemento Registro"); //

	private String constante;
	private String label;

	private CampoLayoutPersonalizado(String constante, String label) {
		this.constante = constante;
		this.label = label;
	}

	/**
	 * retorna o tipo de arquivo dependendo do tipo informado
	 * 
	 * @param valor
	 * @return tipo arquivo
	 */
	public static CampoLayoutPersonalizado get(String valor) {
		CampoLayoutPersonalizado[] values = CampoLayoutPersonalizado.values();
		for (CampoLayoutPersonalizado especieTitulo : values) {
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
