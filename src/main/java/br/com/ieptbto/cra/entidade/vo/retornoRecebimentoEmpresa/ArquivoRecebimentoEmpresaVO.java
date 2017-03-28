package br.com.ieptbto.cra.entidade.vo.retornoRecebimentoEmpresa;

import java.io.Serializable;
import java.util.List;

/**
 * @author Thasso Araujo
 *
 */
public class ArquivoRecebimentoEmpresaVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private HeaderRetornoRecebimentoVO headerEmpresaVO;
	private List<RegistroRetornoRecebimentoVO> registrosEmpresaVO;
	private TraillerRetornoRecebimentoVO traillerEmpresaVO;

	public HeaderRetornoRecebimentoVO getHeaderEmpresaVO() {
		return headerEmpresaVO;
	}

	public void setHeaderEmpresaVO(HeaderRetornoRecebimentoVO headerCnab240) {
		this.headerEmpresaVO = headerCnab240;
	}

	public List<RegistroRetornoRecebimentoVO> getRegistrosEmpresaVO() {
		return registrosEmpresaVO;
	}

	public void setRegistrosEmpresaVO(List<RegistroRetornoRecebimentoVO> registrosCnab240) {
		this.registrosEmpresaVO = registrosCnab240;
	}

	public TraillerRetornoRecebimentoVO getTraillerEmpresaVO() {
		return traillerEmpresaVO;
	}

	public void setTraillerEmpresaVO(TraillerRetornoRecebimentoVO traillerCnab240) {
		this.traillerEmpresaVO = traillerCnab240;
	}
}