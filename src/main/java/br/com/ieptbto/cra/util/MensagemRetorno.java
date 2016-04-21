package br.com.ieptbto.cra.util;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

public class MensagemRetorno {

	public String mensagem;
	public String mensagemFormatada;
	public static final String SEM_PROTESTO = "<mensagem>";
	public static final String COM_PROTESTO = "<cartorio>";
	public static final String INICIO_MENSAGEM_SEM_TITULOS = "em>";
	public static final String FIM_MENSAGEM_SEM_TITULOS = "</mensagem>";

	public MensagemRetorno(String msg) {
		this.mensagem = msg;
		this.mensagemFormatada = "";
		this.converterMensagem();
	}

	public void converterMensagem() {
		if (this.mensagem.replaceAll("\\s+", " ").contains(SEM_PROTESTO)) {

			this.mensagemFormatada = this.mensagem.substring(this.mensagem.indexOf(INICIO_MENSAGEM_SEM_TITULOS) + 3,
			        this.mensagem.indexOf(FIM_MENSAGEM_SEM_TITULOS));

		} else if (this.mensagem.replaceAll("\\s+", " ").contains(COM_PROTESTO)) {
			String xmlRecebido = "";
			Scanner scanner = new Scanner(new ByteArrayInputStream(new String(this.mensagem).getBytes()));
			while (scanner.hasNext()) {
				xmlRecebido = xmlRecebido + scanner.nextLine().replaceAll("& ", "&amp;");
				if (xmlRecebido.contains("<municipio>")) {
					this.mensagemFormatada += "Município: "
					        + xmlRecebido.substring(xmlRecebido.indexOf("pio>") + 4, xmlRecebido.indexOf("</municipio>"));
				}
				if (xmlRecebido.contains("<telefone>")) {
					this.mensagemFormatada += " - Telefone: "
					        + xmlRecebido.substring(xmlRecebido.indexOf("one>") + 4, xmlRecebido.indexOf("</telefone>"));
				}
			}
			scanner.close();

		} else {
			this.mensagemFormatada = "Erro na consulta do CPF/CNPJ";
		}
	}
}
