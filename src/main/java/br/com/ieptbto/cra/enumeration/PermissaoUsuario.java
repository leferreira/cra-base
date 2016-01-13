package br.com.ieptbto.cra.enumeration;

/**
 * @author Thasso Araújo
 *
 */
public enum PermissaoUsuario {

	SUPER_ADMINISTRADOR("Super Administrador"), 
	ADMINISTRADOR("Administrador"), 
	USUARIO("Usuário");
	
	private String label;
	
	private PermissaoUsuario(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
