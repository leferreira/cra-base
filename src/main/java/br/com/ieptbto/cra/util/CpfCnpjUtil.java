package br.com.ieptbto.cra.util;

import org.apache.commons.lang.StringUtils;

public class CpfCnpjUtil {

	private static final int[] pesoCPF = { 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 };
	private static final int[] pesoCNPJ = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };

	private static int calcularDigito(String str, int[] peso) {
		str = verificarCaracterIndevido(str);
		int soma = 0;
		for (int indice = str.length() - 1, digito; indice >= 0; indice--) {
			digito = Integer.parseInt(str.substring(indice, indice + 1));
			soma += digito * peso[peso.length - str.length() + indice];
		}
		soma = 11 - soma % 11;
		return soma > 9 ? 0 : soma;
	}

	public static boolean isValidCPF(String cpf) {
		if ((cpf == null) || (cpf.length() != 11))
			return false;

		if (cpf.equals("00000000000") || cpf.equals("11111111111") || cpf.equals("22222222222") || cpf.equals("33333333333")
				|| cpf.equals("44444444444") || cpf.equals("55555555555") || cpf.equals("66666666666") || cpf.equals("77777777777")
				|| cpf.equals("88888888888") || cpf.equals("99999999999") || (cpf.length() != 11))
			return false;

		Integer digito1 = calcularDigito(cpf.substring(0, 9), pesoCPF);
		Integer digito2 = calcularDigito(cpf.substring(0, 9) + digito1, pesoCPF);
		return cpf.equals(cpf.substring(0, 9) + digito1.toString() + digito2.toString());
	}

	public static boolean isValidCNPJ(String cnpj) {
		if ((cnpj == null) || (cnpj.length() != 14))
			return false;

		if (cnpj.equals("00000000000000") || cnpj.equals("11111111111111") || cnpj.equals("22222222222222") || cnpj.equals("33333333333333")
				|| cnpj.equals("44444444444444") || cnpj.equals("55555555555555") || cnpj.equals("66666666666666")
				|| cnpj.equals("77777777777777") || cnpj.equals("88888888888888") || cnpj.equals("99999999999999") || (cnpj.length() != 14))
			return false;

		Integer digito1 = calcularDigito(cnpj.substring(0, 12), pesoCNPJ);
		Integer digito2 = calcularDigito(cnpj.substring(0, 12) + digito1, pesoCNPJ);
		return cnpj.equals(cnpj.substring(0, 12) + digito1.toString() + digito2.toString());
	}

	public static void main(String[] args) {
		System.out.println(CpfCnpjUtil.isValidCNPJ("02991501000102"));
	}

	public static String buscarComplementoDocumento(String numeroDocumento) {
		numeroDocumento = verificarCaracterIndevido(numeroDocumento);

		String complemento = "";
		if (numeroDocumento != null) {
			if (numeroDocumento.trim().isEmpty()) {
				return complemento;
			}
			numeroDocumento = numeroDocumento.replace(" ", "");
			if (numeroDocumento.length() > 11) {
				complemento = numeroDocumento.substring(8, 12);
			}
		}
		// System.out.println("Complemento = " + complemento);
		return complemento;
	}

	public static String buscarNumeroDocumento(String numeroDocumento) {
		numeroDocumento = verificarCaracterIndevido(numeroDocumento);

		String documento = "";
		if (numeroDocumento != null) {
			if (numeroDocumento.trim().isEmpty()) {
				return documento;
			}
			numeroDocumento = numeroDocumento.replace(" ", "");
			if (numeroDocumento.length() > 11) {
				documento = numeroDocumento.substring(0, 8);
			} else {
				if (numeroDocumento.length() >= 9) {
					documento = numeroDocumento.substring(0, 9);
				} else {
					if (numeroDocumento.length() < 9) {
						numeroDocumento = StringUtils.leftPad(numeroDocumento, 9, "0");
						documento = numeroDocumento.substring(0, 9);
					}
				}
			}
		}
		// System.out.println("Numero Documento = " + documento);
		return documento;
	}

	public static String calcularDigitoControle(String numeroDocumento) {
		numeroDocumento = verificarCaracterIndevido(numeroDocumento);

		String digitoControle = "";
		Integer digito1 = 0;
		Integer digito2 = 0;
		if (numeroDocumento != null) {
			if (numeroDocumento.trim().isEmpty()) {
				return digitoControle;
			}
			numeroDocumento = numeroDocumento.replace(" ", "");
			if (numeroDocumento.length() > 11) {
				digito1 = calcularDigito(numeroDocumento.substring(0, 12), pesoCNPJ);
				digito2 = calcularDigito(numeroDocumento.substring(0, 12) + digito1, pesoCNPJ);
				digitoControle = digito1.toString();
				digitoControle = digitoControle.concat(digito2.toString());
			} else {
				if (numeroDocumento.length() < 9) {
					numeroDocumento = StringUtils.leftPad(numeroDocumento, 9, "0");
				}
				digito1 = calcularDigito(numeroDocumento.substring(0, 9), pesoCPF);
				digito2 = calcularDigito(numeroDocumento.substring(0, 9) + digito1, pesoCPF);
				digitoControle = digito1.toString();
				digitoControle = digitoControle.concat(digito2.toString());
			}
		}
		// System.out.println("Digito COntrole = " + digitoControle);
		return digitoControle;
	}

	private static String verificarCaracterIndevido(String numeroDocumento) {
		return numeroDocumento.replaceAll("([A-z])\\w+", "");
	}
}