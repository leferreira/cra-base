package br.com.ieptbto.cra.util;

import java.util.ArrayList;
import java.util.List;

public enum EstadoUtils {

	AC("Acre"),
	AL("Alagoas"),
	AP("Amapá"),
	AM("Amazonas"),
	BA("Bahia"),
	CE("Ceará"),
	DF("Distrito Federal"),
	ES("Espírito Santo"),
	GO("Goiás"),
	MA("Maranhão"),
	MS("Mato Grosso do Sul"),
	MT("Mato Grosso"),
	MG("Minas Gerais"),
	PA("Pará"),
	PB("Paraíba"),
	PR("Paraná"),
	PE("Pernambuco"),
	PI("Píaui"),
	RJ("Rio de Janeiro"),
	RN("Rio Grande do Norte"),
	RS("Rio Grande do Sul"),
	RO("Rondônia"),
	RR("Roraima"),
	SC("Santa Catarina"),
	SP("São Paulo"),
	SE("Sergipe"),
	TO("Tocantins");

	private String nomeEstado;
	
	private EstadoUtils(String nomeEstado){
		this.nomeEstado = nomeEstado;
	}
	
	public String getNomeEstado(){
		return this.nomeEstado;
	}

	public static List<String> getEstadosToList() {
		List<String> estadosList = new ArrayList<String>(); 
		for (EstadoUtils e : EstadoUtils.values()) {
			estadosList.add(e.toString());
		}
		return estadosList;
	}
	
}
