package br.com.ieptbto.cra.conversor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.conversor.arquivo.CabecalhoCnpConversor;
import br.com.ieptbto.cra.conversor.arquivo.RodapeCnpConversor;
import br.com.ieptbto.cra.conversor.arquivo.TituloCnpConversor;
import br.com.ieptbto.cra.entidade.CabecalhoCnp;
import br.com.ieptbto.cra.entidade.RemessaCnp;
import br.com.ieptbto.cra.entidade.RodapeCnp;
import br.com.ieptbto.cra.entidade.TituloCnp;
import br.com.ieptbto.cra.entidade.vo.ArquivoCnpVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoCnpVO;
import br.com.ieptbto.cra.entidade.vo.RemessaCnpVO;
import br.com.ieptbto.cra.entidade.vo.RodapeCnpVO;
import br.com.ieptbto.cra.entidade.vo.TituloCnpVO;
import br.com.ieptbto.cra.enumeration.TipoRegistro;
import br.com.ieptbto.cra.util.CpfCnpjUtil;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;

/**
 * @author Thasso Araújo
 *
 */
public class ConversorArquivoCnpVO {

	public static List<RemessaCnp> converterParaRemessaCnp(ArquivoCnpVO arquivoVo) {
		List<RemessaCnp> remessas = new ArrayList<RemessaCnp>();

		for (RemessaCnpVO remessaVO : arquivoVo.getRemessasCnpVO()) {
			RemessaCnp remessa = new RemessaCnp();
			remessa.setCabecalho(new CabecalhoCnpConversor().converter(CabecalhoCnp.class, remessaVO.getCabecalhoCnpVO()));

			remessa.setTitulos(new ArrayList<TituloCnp>());
			for (TituloCnpVO tituloVO : remessaVO.getTitulosCnpVO()) {
				remessa.getTitulos().add(new TituloCnpConversor().converter(TituloCnp.class, tituloVO));
			}

			remessa.setRodape(new RodapeCnpConversor().converter(RodapeCnp.class, remessaVO.getRodapeCnpVO()));
			remessas.add(remessa);
		}
		return remessas;
	}

	public static List<RemessaCnpVO> converterParaRemessaCnpVO(List<RemessaCnp> remessasCnp) {
		List<RemessaCnpVO> remessasVO = new ArrayList<RemessaCnpVO>();

		for (RemessaCnp remessa : remessasCnp) {
			RemessaCnpVO remessaVO = new RemessaCnpVO();
			remessaVO.setCabecalhoCnpVO(new CabecalhoCnpConversor().converter(remessa.getCabecalho(), CabecalhoCnpVO.class));

			remessaVO.setTitulosCnpVO(new ArrayList<TituloCnpVO>());
			for (TituloCnp titulo : remessa.getTitulos()) {
				TituloCnpVO tituloVO = new TituloCnpConversor().converter(titulo, TituloCnpVO.class);
				if (titulo.getTipoInformacao() != null) {
					if (StringUtils.isNotBlank(titulo.getTipoInformacao().trim()) && StringUtils.isNotEmpty(titulo.getTipoInformacao().trim())) {
						if (titulo.getTipoInformacao().trim().equals("C")) {
							tituloVO.setCodigoOperacao("E");
						}
					}
				}
				if (titulo.getNumeroDocumentoDevedor() != null) {
					if (titulo.getNumeroDocumentoDevedor().length() > 11) {
						tituloVO.setTipoDocumentoDevedor("1");
						tituloVO.setTipoPessoaDevedor("J");
					} else {
						tituloVO.setTipoDocumentoDevedor("2");
						tituloVO.setTipoPessoaDevedor("F");
					}
				}
				if (titulo.getNumeroDocumentoCredor() != null) {
					if (titulo.getNumeroDocumentoCredor().length() > 11) {
						tituloVO.setTipoDocumentoCredor("1");
						tituloVO.setTipoPessoaCredor("J");
					} else {
						tituloVO.setTipoDocumentoCredor("2");
						tituloVO.setTipoPessoaCredor("F");
					}
				}
				System.out.println("=========================================================");
				System.out.println("Documento Credor: " + titulo.getNumeroDocumentoCredor());
				tituloVO.setNumeroDocumentoCredor(CpfCnpjUtil.buscarNumeroDocumento(titulo.getNumeroDocumentoCredor()));
				tituloVO.setComplementoDocumentoCredor(CpfCnpjUtil.buscarComplementoDocumento(titulo.getNumeroDocumentoCredor()));
				tituloVO.setDigitoControleDocumentoCredor(CpfCnpjUtil.calcularDigitoControle(titulo.getNumeroDocumentoCredor()));

				System.out.println("Documento Devedor: " + titulo.getNumeroDocumentoDevedor());
				tituloVO.setNumeroDocumentoDevedor(CpfCnpjUtil.buscarNumeroDocumento(titulo.getNumeroDocumentoDevedor()));
				tituloVO.setComplementoDocumentoDevedor(CpfCnpjUtil.buscarComplementoDocumento(titulo.getNumeroDocumentoDevedor()));
				tituloVO.setDigitoControleDocumentoDevedor(CpfCnpjUtil.calcularDigitoControle(titulo.getNumeroDocumentoDevedor()));

				tituloVO.setCidadeCredor(RemoverAcentosUtil.removeAcentos(titulo.getCidadeCredor()));
				tituloVO.setMunicipioEnderecoCredor(RemoverAcentosUtil.removeAcentos(titulo.getMunicipioEnderecoCredor()));
				tituloVO.setEnderecoCredor(RemoverAcentosUtil.removeAcentos(titulo.getEnderecoCredor()));
				tituloVO.setNomeCredor(RemoverAcentosUtil.removeAcentos(titulo.getNomeCredor()));

				tituloVO.setCidadeDevedor(RemoverAcentosUtil.removeAcentos(titulo.getCidadeDevedor()));
				tituloVO.setEnderecoDevedor(RemoverAcentosUtil.removeAcentos(titulo.getEnderecoDevedor()));
				tituloVO.setNomeDevedor(RemoverAcentosUtil.removeAcentos(titulo.getNomeDevedor()));
				remessaVO.getTitulosCnpVO().add(tituloVO);
			}
			remessaVO.setRodapeCnpVO(new RodapeCnpConversor().converter(remessa.getRodape(), RodapeCnpVO.class));
			remessasVO.add(remessaVO);
		}
		return remessasVO;
	}

	public static List<RemessaCnpVO> converterParaRemessaCnpVO5Anos(List<RemessaCnp> remessasCnp) {
		List<RemessaCnpVO> remessasVO = new ArrayList<RemessaCnpVO>();
		int sequencialRegistro = 71957;
		for (RemessaCnp remessa : remessasCnp) {
			RemessaCnpVO remessaVO = new RemessaCnpVO();
			remessa.getCabecalho().setSequenciaRegistro(Integer.toString(sequencialRegistro));
			remessaVO.setCabecalhoCnpVO(new CabecalhoCnpConversor().converter(remessa.getCabecalho(), CabecalhoCnpVO.class));

			remessaVO.setTitulosCnpVO(new ArrayList<TituloCnpVO>());
			for (TituloCnp titulo : remessa.getTitulos()) {
				sequencialRegistro++;
				titulo.setSequenciaRegistro(Integer.toString(sequencialRegistro));
				TituloCnpVO tituloVO = new TituloCnpConversor().converter(titulo, TituloCnpVO.class);
				if (titulo.getTipoInformacao() != null) {
					if (StringUtils.isNotBlank(titulo.getTipoInformacao().trim()) && StringUtils.isNotEmpty(titulo.getTipoInformacao().trim())) {
						if (titulo.getTipoInformacao().trim().equals("C")) {
							tituloVO.setCodigoOperacao("E");
						}
					}
				}
				if (titulo.getNumeroDocumentoDevedor() != null) {
					if (titulo.getNumeroDocumentoDevedor().length() > 11) {
						tituloVO.setTipoDocumentoDevedor("1");
						tituloVO.setTipoPessoaDevedor("J");
					} else {
						tituloVO.setTipoDocumentoDevedor("2");
						tituloVO.setTipoPessoaDevedor("F");
					}
				}
				if (titulo.getNumeroDocumentoCredor() != null) {
					if (titulo.getNumeroDocumentoCredor().length() > 11) {
						tituloVO.setTipoDocumentoCredor("1");
						tituloVO.setTipoPessoaCredor("J");
					} else {
						tituloVO.setTipoDocumentoCredor("2");
						tituloVO.setTipoPessoaCredor("F");
					}
				}
				tituloVO.setNumeroDocumentoCredor(CpfCnpjUtil.buscarNumeroDocumento(titulo.getNumeroDocumentoCredor()));
				tituloVO.setComplementoDocumentoCredor(CpfCnpjUtil.buscarComplementoDocumento(titulo.getNumeroDocumentoCredor()));
				tituloVO.setDigitoControleDocumentoCredor(CpfCnpjUtil.calcularDigitoControle(titulo.getNumeroDocumentoCredor()));

				tituloVO.setNumeroDocumentoDevedor(CpfCnpjUtil.buscarNumeroDocumento(titulo.getNumeroDocumentoDevedor()));
				tituloVO.setComplementoDocumentoDevedor(CpfCnpjUtil.buscarComplementoDocumento(titulo.getNumeroDocumentoDevedor()));
				tituloVO.setDigitoControleDocumentoDevedor(CpfCnpjUtil.calcularDigitoControle(titulo.getNumeroDocumentoDevedor()));

				tituloVO.setCidadeCredor(RemoverAcentosUtil.removeAcentos(titulo.getCidadeCredor()));
				tituloVO.setMunicipioEnderecoCredor(RemoverAcentosUtil.removeAcentos(titulo.getMunicipioEnderecoCredor()));
				tituloVO.setEnderecoCredor(RemoverAcentosUtil.removeAcentos(titulo.getEnderecoCredor()));
				tituloVO.setNomeCredor(RemoverAcentosUtil.removeAcentos(titulo.getNomeCredor()));

				tituloVO.setCidadeDevedor(RemoverAcentosUtil.removeAcentos(titulo.getCidadeDevedor()));
				tituloVO.setEnderecoDevedor(RemoverAcentosUtil.removeAcentos(titulo.getEnderecoDevedor()));
				tituloVO.setNomeDevedor(RemoverAcentosUtil.removeAcentos(titulo.getNomeDevedor()));
				remessaVO.getTitulosCnpVO().add(tituloVO);
			}
			sequencialRegistro++;
			RodapeCnp rodape = new RodapeCnp();
			rodape.setCodigoRegistro(TipoRegistro.RODAPE.getConstante());
			remessa.setRodape(rodape);
			remessaVO.setRodapeCnpVO(new RodapeCnpConversor().converter(remessa.getRodape(), RodapeCnpVO.class));
			remessaVO.getRodapeCnpVO().setSequenciaRegistro(Integer.toString(sequencialRegistro));
			remessasVO.add(remessaVO);
		}
		return remessasVO;
	}
}