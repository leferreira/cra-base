package br.com.ieptbto.cra.conversor.arquivo.filiado;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.LayoutFiliado;
import br.com.ieptbto.cra.enumeration.CampoLayout;
import br.com.ieptbto.cra.exception.InfraException;

public class TemplateLayoutEmpresa {

	private static final Logger logger = Logger.getLogger(TemplateLayoutEmpresa.class);

	private String valor;
	private CampoLayout campo;

	public TemplateLayoutEmpresa(String valor, CampoLayout campo) {
		this.valor = valor;
		this.campo = campo;
	}

	public String getValor() {
		return valor;
	}

	public CampoLayout getCampo() {
		return campo;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public void setCampo(CampoLayout campo) {
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
					if (listaLayout.getCampo().equals(CampoLayout.CIDADEDEVEDOR) && StringUtils.isNotBlank(dados[i])) {
						cidade = dados[i];
					} else if (listaLayout.getCampo().equals(CampoLayout.CIDADEDEVEDOR) && StringUtils.isBlank(dados[i])) {
						list.add(new InfraException("Cidade não informada na linha " + Arrays.toString(dados)));
						logger.error("cidade não informada. " + Arrays.toString(dados));
					}
					break;
				}
			}
		}
		return new LinhaTemplateLayout(listaCampos, cidade);
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
