package br.com.ieptbto.cra.regra;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.RegistroCnp;
import br.com.ieptbto.cra.util.CpfCnpjUtil;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class FabricaRegraValidacaoCNP {

	public boolean validarProtesto(RegistroCnp registro) {
		if (registro.getNumeroDocumentoCredor() == null) {
			return false;
		}
		registro.setNumeroDocumentoCredor(registro.getNumeroDocumentoCredor().replace(" ", ""));
		if (registro.getNumeroDocumentoCredor().trim().isEmpty()) {
			return false;
		}
		if (Integer.valueOf(registro.getNumeroDocumentoCredor()) == 0) {
			return false;
		}
		String numeroDocumentoCredor = registro.getNumeroDocumentoCredor().trim() + registro.getComplementoDocumentoCredor().trim()
				+ registro.getDigitoControleDocumentoCredor().trim();
		if (!CpfCnpjUtil.isValidCNPJ(numeroDocumentoCredor) && !CpfCnpjUtil.isValidCPF(numeroDocumentoCredor)) {
			return false;
		}
		if (registro.getNumeroDocumentoDevedor() == null) {
			return false;
		}
		registro.setNumeroDocumentoDevedor(registro.getNumeroDocumentoDevedor().replace(" ", ""));
		if (registro.getNumeroDocumentoDevedor().trim().isEmpty()) {
			return false;
		}
		if (Integer.valueOf(registro.getNumeroDocumentoDevedor()) == 0) {
			return false;
		}
		String numeroDocumentoDevedor = registro.getNumeroDocumentoDevedor().trim() + registro.getComplementoDocumentoDevedor().trim()
				+ registro.getDigitoControleDocumentoDevedor().trim();
		if (!CpfCnpjUtil.isValidCNPJ(numeroDocumentoDevedor) && !CpfCnpjUtil.isValidCPF(numeroDocumentoDevedor)) {
			return false;
		}
		if (registro.getDataProtesto() == null) {
			return false;
		}
		if (registro.getDataProtesto().before(DataUtil.stringToLocalDate("01/01/2011").toDate())) {
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
		if (Integer.valueOf(registro.getNumeroDocumentoCredor()) == 0) {
			return false;
		}
		String numeroDocumentoCredor = registro.getNumeroDocumentoCredor().trim() + registro.getComplementoDocumentoCredor().trim()
				+ registro.getDigitoControleDocumentoCredor().trim();
		if (!CpfCnpjUtil.isValidCNPJ(numeroDocumentoCredor) && !CpfCnpjUtil.isValidCPF(numeroDocumentoCredor)) {
			return false;
		}
		if (registro.getNumeroDocumentoDevedor() == null) {
			return false;
		}
		registro.setNumeroDocumentoDevedor(registro.getNumeroDocumentoDevedor().replace(" ", ""));
		if (registro.getNumeroDocumentoDevedor().trim().isEmpty()) {
			return false;
		}
		String numeroDocumentoDevedor = registro.getNumeroDocumentoDevedor().trim() + registro.getComplementoDocumentoDevedor().trim()
				+ registro.getDigitoControleDocumentoDevedor().trim();
		if (!CpfCnpjUtil.isValidCNPJ(numeroDocumentoDevedor) && !CpfCnpjUtil.isValidCPF(numeroDocumentoDevedor)) {
			return false;
		}
		if (Integer.valueOf(registro.getNumeroDocumentoDevedor()) == 0) {
			return false;
		}
		if (registro.getDataProtesto() == null) {
			return false;
		}
		if (registro.getDataProtesto().before(DataUtil.stringToLocalDate("01/01/2011").toDate())) {
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
		if (registro.getDataCancelamentoProtesto().before(DataUtil.stringToLocalDate("01/01/2011").toDate())) {
			return false;
		}
		return true;
	}
}