package br.com.ieptbto.cra.validacao;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.RegistroCnp;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ValidarRegistroCnp {

	public boolean validarProtesto(RegistroCnp registro) {
		if (registro.getNumeroDocumentoCredor() == null) {
			return false;
		}
		registro.setNumeroDocumentoCredor(registro.getNumeroDocumentoCredor().replace(" ", ""));
		if (registro.getNumeroDocumentoCredor().trim().isEmpty()) {
			return false;
		}
		if (registro.getNumeroDocumentoDevedor() == null) {
			return false;
		}
		registro.setNumeroDocumentoDevedor(registro.getNumeroDocumentoDevedor().replace(" ", ""));
		if (registro.getNumeroDocumentoDevedor().trim().isEmpty()) {
			return false;
		}
		if (registro.getDataProtesto() == null) {
			return false;
		}
		if (registro.getValorProtesto() == null) {
			return false;
		}
		if (registro.getValorProtesto().equals(new BigDecimal(0.00))) {
			return false;
		}
		if (registro.getNumeroProtocoloCartorio() == null) {
			return false;
		}
		if (registro.getNumeroProtocoloCartorio().trim().isEmpty()) {
			return false;
		}
		return true;
	}

	public boolean validarCancelamento(RegistroCnp registro) {
		if (registro.getNumeroDocumentoCredor() == null) {
			return false;
		}
		registro.setNumeroDocumentoCredor(registro.getNumeroDocumentoCredor().replace(" ", ""));
		if (registro.getNumeroDocumentoCredor().trim().isEmpty()) {
			return false;
		}
		if (registro.getNumeroDocumentoDevedor() == null) {
			return false;
		}
		registro.setNumeroDocumentoDevedor(registro.getNumeroDocumentoDevedor().replace(" ", ""));
		if (registro.getNumeroDocumentoDevedor().trim().isEmpty()) {
			return false;
		}
		if (registro.getDataProtesto() == null) {
			return false;
		}
		if (registro.getValorProtesto() == null) {
			return false;
		}
		if (registro.getValorProtesto().equals(new BigDecimal(0.00))) {
			return false;
		}
		if (registro.getNumeroProtocoloCartorio() == null) {
			return false;
		}
		if (registro.getNumeroProtocoloCartorio().trim().isEmpty()) {
			return false;
		}
		if (registro.getDataCancelamentoProtesto() == null) {
			return false;
		}
		return true;
	}
}
