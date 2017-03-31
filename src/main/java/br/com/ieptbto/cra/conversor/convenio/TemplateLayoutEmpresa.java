package br.com.ieptbto.cra.conversor.convenio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.LayoutFiliado;
import br.com.ieptbto.cra.enumeration.CampoLayoutPersonalizado;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.CpfCnpjUtil;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;

public class TemplateLayoutEmpresa {

	private static final Logger logger = Logger.getLogger(TemplateLayoutEmpresa.class);

	private String valor;
	private CampoLayoutPersonalizado campo;

	public TemplateLayoutEmpresa(String valor, CampoLayoutPersonalizado campo) {
		this.valor = valor;
		this.campo = campo;
	}

	public String getValor() {
		if (valor != null) {
			valor = valor.toUpperCase();
		}
		return valor;
	}

	public CampoLayoutPersonalizado getCampo() {
		return campo;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public void setCampo(CampoLayoutPersonalizado campo) {
		this.campo = campo;
	}

	public static LinhaTemplateLayout getTemplate(String[] dados, List<LayoutFiliado> layoutfiliado, List<Exception> list) {
		List<TemplateLayoutEmpresa> listaCampos = new ArrayList<>();
		String cidade = null;
		for (int i = 0; i < dados.length; i++) {
			for (LayoutFiliado listaLayout : layoutfiliado) {
				if (listaLayout.getOrdem() == i + 1) {
					TemplateLayoutEmpresa template = new TemplateLayoutEmpresa(dados[i], listaLayout.getCampo());
					listaCampos.add(template);
					if (listaLayout.getCampo().equals(CampoLayoutPersonalizado.CIDADEDEVEDOR) && StringUtils.isNotBlank(dados[i])) {
						cidade = RemoverAcentosUtil.removeAcentos(dados[i].trim());
					} else if (listaLayout.getCampo().equals(CampoLayoutPersonalizado.CIDADEDEVEDOR) && StringUtils.isBlank(dados[i])) {
						cidade = "Palmas";
						logger.error("cidade não informada. " + Arrays.toString(dados));
					} 
					if (listaLayout.getCampo().equals(CampoLayoutPersonalizado.NUMEROIDENTIFICACAODEVEDOR) && StringUtils.isNotBlank(dados[i])) {
						if (dados[i] != null) {
							dados[i] = dados[i].replace(".", "").replace("-", "").replace("/", "").trim();
						}
						dados[i] = validarCpfCnpj(dados[i], list, listaCampos);
					}
					break;
				}
			}
		}
		return new LinhaTemplateLayout(listaCampos, cidade);
	}

	private static String validarCpfCnpj(String dados, List<Exception> list, List<TemplateLayoutEmpresa> listaCampos) {
		String tipo = null;
		boolean bool = true;

		if (dados.length() <= 11) {
			dados = StringUtils.leftPad(dados, 11, "0");
			bool = CpfCnpjUtil.isValidCPF(dados);
			tipo = "002";
		} else if (dados.length() > 11 && dados.length() <= 14) {
			dados = StringUtils.leftPad(dados, 14, "0");
			bool = CpfCnpjUtil.isValidCNPJ(dados);
			tipo = "001";
		} else {
			list.add(new InfraException("O CPF/CNPJ " + dados + " está com o tamanho incorreto."));
			logger.error("O CPF/CNPJ " + dados + " está com o tamanho incorreto.");
		}
		if (bool == false) {
			System.out.println("Documento inválido = " + dados);
		}
		listaCampos.add(new TemplateLayoutEmpresa(tipo, CampoLayoutPersonalizado.TIPOIDENTIFICACAODEVEDOR));
		return dados;
	}
}

class LinhaTemplateLayout {
	private List<TemplateLayoutEmpresa> campos;
	private String cidade;

	public LinhaTemplateLayout(List<TemplateLayoutEmpresa> campos, String cidade) {
		this.campos = campos;
		this.cidade = cidade;
	}

	public List<TemplateLayoutEmpresa> getCampos() {
		return campos;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCampos(List<TemplateLayoutEmpresa> campos) {
		this.campos = campos;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
}