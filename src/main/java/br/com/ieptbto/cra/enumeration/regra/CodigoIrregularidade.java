package br.com.ieptbto.cra.enumeration.regra;

/**
 * @author Thasso
 *
 */
public enum CodigoIrregularidade {

									IRREGULARIDADE_0("00", "Título Sem Irregularidade"),
									IRREGULARIDADE_1("01", "Data da apresentação inferior à data de vencimento"),
									IRREGULARIDADE_2("02", "Falta de comprovante da prestação de serviço"),
									IRREGULARIDADE_3("03", "Nome do sacado incompleto/incorreto"),
									IRREGULARIDADE_4("04", "Nome do cedente incompleto/incorreto"),
									IRREGULARIDADE_5("05", "Nome do sacador incompleto/incorreto"),
									IRREGULARIDADE_6("06", "Endereço do sacado insuficiente"),
									IRREGULARIDADE_7("07", "CNPJ/CPF do sacado inválido/incorreto"),
									IRREGULARIDADE_8("08", "CNPJ/CPF incompatível c/ o nome do sacado/sacador/avalista."),
									IRREGULARIDADE_9("09", "CNPJ/CPF do sacado incompatível com o tipo de documento"),
									IRREGULARIDADE_10("10", "CNPJ/CPF do sacador incompatível com a espécie"),
									IRREGULARIDADE_11("11", "Título aceito sem a assinatura do sacado"),
									IRREGULARIDADE_12("12", "Título aceito rasurado ou rasgado"),
									IRREGULARIDADE_13("13", "Título aceito – falta título (ag ced: enviar)"),
									IRREGULARIDADE_14("14", "CEP incorreto"),
									IRREGULARIDADE_15("15", "Praça de pagamento incompatível com endereço"),
									IRREGULARIDADE_16("16", "Falta número do título"),
									IRREGULARIDADE_17("17", "Título sem endosso do cedente ou irregular."),
									IRREGULARIDADE_18("18", "Falta data de emissão do título."),
									IRREGULARIDADE_19("19", "Título aceito: valor por extenso diferente do valor por numérico."),
									IRREGULARIDADE_20("20", "Data de emissão posterior ao vencimento"),
									IRREGULARIDADE_21("21", "Espécie inválida para protesto"),
									IRREGULARIDADE_22("22", "CEP do sacado incompatível com a praça de protesto"),
									IRREGULARIDADE_23("23", "Falta espécie do título"),
									IRREGULARIDADE_24("24", "Saldo maior que o valor do título"),
									IRREGULARIDADE_25("25", "Tipo de endosso inválido"),
									IRREGULARIDADE_26("26", "Devolvido por ordem judicial"),
									IRREGULARIDADE_27("27", "Dados do título não conferem com disquete"),
									IRREGULARIDADE_28("28", "Sacado e Sacador/Avalista são a mesma pessoa"),
									IRREGULARIDADE_29("29", "Corrigir a espécie do título"),
									IRREGULARIDADE_30("30", "Aguardar um dia útil após o vencimento para protestar"),
									IRREGULARIDADE_31("31", "Data do vencimento rasurada"),
									IRREGULARIDADE_32("32", "Vencimento – extenso não confere com número"),
									IRREGULARIDADE_33("33", "Falta data de vencimento no título"),
									IRREGULARIDADE_34("34", "DM/DMI sem comprovante autenticado ou declaração"),
									IRREGULARIDADE_35("35", "Comprovante ilegível para conferência e microfilmagem"),
									IRREGULARIDADE_36("36", "Nome solicitado não confere com emitente ou sacado"),
									IRREGULARIDADE_37("37", "Confirmar se são 2 emitentesSe sim, indicar os dados dos 2"),
									IRREGULARIDADE_38("38", "Endereço do sacado igual ao do sacador ou do portador"),
									IRREGULARIDADE_39("39", "Endereço do apresentante incompleto ou não informado"),
									IRREGULARIDADE_40("40", "Rua / Número inexistente no endereço"),
									IRREGULARIDADE_41("41", "Informar a qualidade do endosso (M ou T)"),
									IRREGULARIDADE_42("42", "Falta endosso do favorecido para o apresentante"),
									IRREGULARIDADE_43("43", "Data da emissão rasurada"),
									IRREGULARIDADE_44("44", "Protesto de cheque proibido – motivo 20/25/28/30 ou 35"),
									IRREGULARIDADE_45("45", "Falta assinatura do emitente no cheque"),
									IRREGULARIDADE_46("46", "Endereço do emitente no cheque igual ao do banco sacado"),
									IRREGULARIDADE_47("47", "Falta o motivo da devolução no cheque ou motivo ilegível"),
									IRREGULARIDADE_48("48", "Falta assinatura do sacador no título"),
									IRREGULARIDADE_49("49", "Nome do apresentante não informado/incompleto/incorreto"),
									IRREGULARIDADE_50("50", "Erro de preenchimento do título"),
									IRREGULARIDADE_51("51", "Título com direito de regresso vencido"),
									IRREGULARIDADE_52("52", "Título apresentado em duplicidade"),
									IRREGULARIDADE_53("53", "Título já protestado"),
									IRREGULARIDADE_54("54", "Letra de Câmbio vencida – falta aceite do sacado"),
									IRREGULARIDADE_55("55", "Título – falta tradução por tradutor público"),
									IRREGULARIDADE_56("56", "Falta declaração de saldo assinada no título"),
									IRREGULARIDADE_57("57", "Contrato de Câmbio – falta conta gráfica"),
									IRREGULARIDADE_58("58", "Ausência do Documento Físico"),
									IRREGULARIDADE_59("59", "Sacado Falecido"),
									IRREGULARIDADE_60("60", "Sacado Apresentou Quitação do Título"),
									IRREGULARIDADE_61("61", "Título de outra jurisdição territorial"),
									IRREGULARIDADE_62("62", "Título com emissão anterior à concordata do sacado"),
									IRREGULARIDADE_63("63", "Sacado consta na lista de falência"),
									IRREGULARIDADE_64("64", "Apresentante não aceita publicação de edital"),
									IRREGULARIDADE_65("65", "Dados do sacador em branco ou inválido"),
									IRREGULARIDADE_66("66", "Titulo sem autorização para protesto por edital."),
									IRREGULARIDADE_67("67", "Valor divergente entre título e comprovante"),
									IRREGULARIDADE_68("68", "Condomínio não pode ser protestado para fins falimentares"),
									IRREGULARIDADE_69("69", "Vedada a intimação por edital para protesto falimentar"),
									IRREGULARIDADE_70("70", "Dados do Cedente em branco ou inválido"),
									IRREGULARIDADE_CONVENIO("99", "Título devolvido por demora na apresentação do documento"),;

	private String codigoIrregularidade;
	private String motivo;

	CodigoIrregularidade(String codigoIrregularidade, String motivo) {
		this.codigoIrregularidade = codigoIrregularidade;
		this.motivo = motivo;
	}

	/**
	 * retorna o tipo irregularidade de acordo com o codigo
	 * 
	 * @param codigoIrregularidade
	 * @return tipo ocorrencia
	 */
	public static CodigoIrregularidade getIrregularidade(String valor) {
		if (valor == null) {
			return null;
		}
		if (valor.trim().equals("00") || valor.trim().equals("0")) {
			return CodigoIrregularidade.IRREGULARIDADE_0;
		}
		CodigoIrregularidade[] values = CodigoIrregularidade.values();
		for (CodigoIrregularidade irregularidade : values) {
			if (valor.startsWith(irregularidade.getCodigoIrregularidade())) {
				return irregularidade;
			}
		}
		return null;
	}

	public String getCodigoIrregularidade() {
		return codigoIrregularidade;
	}

	public String getMotivo() {
		return motivo;
	}
}
