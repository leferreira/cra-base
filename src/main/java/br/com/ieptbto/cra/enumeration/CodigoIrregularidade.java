package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso
 *
 */
public enum CodigoIrregularidade {
	
	IRREGULARIDADE_1("01",""),
	IRREGULARIDADE_2("02",""),
	IRREGULARIDADE_3("03",""),
	IRREGULARIDADE_4("04",""),
	IRREGULARIDADE_5("05",""),
	IRREGULARIDADE_6("06",""),
	IRREGULARIDADE_7("07",""),
	IRREGULARIDADE_8("08",""),
	IRREGULARIDADE_9("09",""),
	IRREGULARIDADE_10("10",""),
	IRREGULARIDADE_11("11",""),
	IRREGULARIDADE_12("12",""),
	IRREGULARIDADE_13("13",""),
	IRREGULARIDADE_14("14",""),
	IRREGULARIDADE_15("15",""),
	IRREGULARIDADE_16("16",""),
	IRREGULARIDADE_17("17",""),
	IRREGULARIDADE_18("18",""),
	IRREGULARIDADE_19("19",""),
	IRREGULARIDADE_20("20",""),
	IRREGULARIDADE_21("21",""),
	IRREGULARIDADE_22("22",""),
	IRREGULARIDADE_23("23",""),
	IRREGULARIDADE_24("24",""),
	IRREGULARIDADE_25("25",""),
	IRREGULARIDADE_26("26",""),
	IRREGULARIDADE_27("27",""),
	IRREGULARIDADE_28("28",""),
	IRREGULARIDADE_29("29",""),
	IRREGULARIDADE_30("30",""),
	IRREGULARIDADE_31("31",""),
	IRREGULARIDADE_32("32",""),
	IRREGULARIDADE_33("33",""),
	IRREGULARIDADE_34("34",""),
	IRREGULARIDADE_35("35",""),
	IRREGULARIDADE_36("36",""),
	IRREGULARIDADE_37("37",""),
	IRREGULARIDADE_38("38",""),
	IRREGULARIDADE_39("39",""),
	IRREGULARIDADE_40("40",""),
	IRREGULARIDADE_41("41",""),
	IRREGULARIDADE_42("42",""),
	IRREGULARIDADE_43("43",""),
	IRREGULARIDADE_44("44",""),
	IRREGULARIDADE_45("45",""),
	IRREGULARIDADE_46("46",""),
	IRREGULARIDADE_47("47",""),
	IRREGULARIDADE_48("48",""),
	IRREGULARIDADE_49("49",""),
	IRREGULARIDADE_50("50",""),
	IRREGULARIDADE_51("51",""),
	IRREGULARIDADE_52("52",""),
	IRREGULARIDADE_53("53",""),
	IRREGULARIDADE_54("54",""),
	IRREGULARIDADE_55("55",""),
	IRREGULARIDADE_56("56",""),
	IRREGULARIDADE_57("57",""),
	IRREGULARIDADE_58("58",""),
	IRREGULARIDADE_59("59",""),
	IRREGULARIDADE_60("60",""),
	IRREGULARIDADE_61("61",""),
	IRREGULARIDADE_62("62",""),
	IRREGULARIDADE_63("63",""),
	IRREGULARIDADE_64("64",""),
	IRREGULARIDADE_65("65",""),
	IRREGULARIDADE_66("66",""),
	IRREGULARIDADE_67("67",""),
	IRREGULARIDADE_68("68",""),
	IRREGULARIDADE_69("69","");
	
	private String codigoIrregularidade;
	private String motivo;
	
	private CodigoIrregularidade(String codigoIrregularidade, String motivo) {
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
