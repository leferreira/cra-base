package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Aráujo
 *
 */
public enum TipoLog {

	SUCESSO("Sucesso","ocorrencia-sucesso"),
	OCORRECIA_ERRO("Ocorrência Erro","ocorrencia-erro"),
	ALERTA("Alerta","ocorrencia-alerta");

    private String label;
    private String idHtml;

    private TipoLog(String label, String idHtml) {
	this.label = label;
	this.idHtml = idHtml;
    }

    public String getIdHtml() {
	return idHtml;
    }

    public String getLabel() {
	return label;
    }
}
