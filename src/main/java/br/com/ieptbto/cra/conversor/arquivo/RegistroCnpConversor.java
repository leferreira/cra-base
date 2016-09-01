package br.com.ieptbto.cra.conversor.arquivo;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import br.com.ieptbto.cra.conversor.AbstractConversorArquivo;
import br.com.ieptbto.cra.conversor.BigDecimalConversor;
import br.com.ieptbto.cra.conversor.DateConversor;
import br.com.ieptbto.cra.entidade.RegistroCnp;
import br.com.ieptbto.cra.entidade.vo.TituloCnpVO;
import br.com.ieptbto.cra.enumeration.TipoRegistro;
import br.com.ieptbto.cra.enumeration.TipoRegistroCnp;
import br.com.ieptbto.cra.util.CpfCnpjUtil;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;

/**
 * @author Thasso Araújo
 *
 */
public class RegistroCnpConversor extends AbstractConversorArquivo<TituloCnpVO, RegistroCnp> {

	@Override
	public RegistroCnp converter(Class<RegistroCnp> entidade, TituloCnpVO entidadeVO) {
		RegistroCnp registro = new RegistroCnp();
		registro.setCodigoRegistro(TipoRegistro.TITULO.getConstante());
		registro.setTipoInformacao(entidadeVO.getTipoInformacao());
		if (entidadeVO.getTipoInformacao().trim().equals("P")) {
			registro.setTipoRegistroCnp(TipoRegistroCnp.PROTESTO);
			registro.setCodigoOperacao(TipoRegistroCnp.PROTESTO.getCodigoOperacao());
		} else {
			registro.setTipoRegistroCnp(TipoRegistroCnp.CANCELAMENTO);
			registro.setCodigoOperacao(TipoRegistroCnp.CANCELAMENTO.getCodigoOperacao());
		}
		registro.setCodigoUnidadeFederativa("17");
		registro.setCodigoPracaEmbratel("63");
		if (entidadeVO.getNomeCredor().length() > 45) {
			registro.setNomeCredor(RemoverAcentosUtil.removeAcentos(entidadeVO.getNomeCredor().substring(0, 44)));
		} else {
			registro.setNomeCredor(RemoverAcentosUtil.removeAcentos(entidadeVO.getNomeCredor()));
		}
		if (entidadeVO.getNumeroDocumentoCredor() != null) {
			registro.setNumeroDocumentoCredor(CpfCnpjUtil
					.buscarNumeroDocumento(entidadeVO.getNumeroDocumentoCredor().replace(".", "").replace("-", "").replace("/", "")));
			registro.setComplementoDocumentoCredor(CpfCnpjUtil
					.buscarComplementoDocumento(entidadeVO.getNumeroDocumentoCredor().replace(".", "").replace("-", "").replace("/", "")));
			registro.setDigitoControleDocumentoCredor(CpfCnpjUtil
					.calcularDigitoControle(entidadeVO.getNumeroDocumentoCredor().replace(".", "").replace("-", "").replace("/", "")));
		} else {
			registro.setNumeroDocumentoCredor("");
			registro.setComplementoDocumentoCredor("");
			registro.setDigitoControleDocumentoCredor("");
		}
		if (entidadeVO.getEnderecoCredor().length() > 45) {
			registro.setEnderecoCredor(RemoverAcentosUtil.removeAcentos(entidadeVO.getEnderecoCredor().substring(0, 44)));
		} else {
			registro.setEnderecoCredor(RemoverAcentosUtil.removeAcentos(entidadeVO.getEnderecoCredor()));
		}
		registro.setCepCredor(RemoverAcentosUtil.removeAcentos(entidadeVO.getCepCredor()));
		if (entidadeVO.getCidadeCredor().length() > 20) {
			registro.setCidadeCredor(RemoverAcentosUtil.removeAcentos(entidadeVO.getCidadeCredor().substring(0, 19)));
			registro.setMunicipioEnderecoCredor(RemoverAcentosUtil.removeAcentos(entidadeVO.getCidadeCredor().substring(0, 19)));
		} else {
			registro.setCidadeCredor(RemoverAcentosUtil.removeAcentos(entidadeVO.getCidadeCredor()));
			registro.setMunicipioEnderecoCredor(RemoverAcentosUtil.removeAcentos(entidadeVO.getCidadeCredor()));
		}
		registro.setUfCredor(RemoverAcentosUtil.removeAcentos(entidadeVO.getUfCredor()));
		if (entidadeVO.getNumeroDocumentoCredor() != null) {
			if (entidadeVO.getNumeroDocumentoCredor().replace(" ", "").trim().length() > 11) {
				registro.setTipoPessoaCredor("J");
				registro.setTipoDocumentoCredor("1");
			} else {
				registro.setTipoPessoaCredor("F");
				registro.setTipoDocumentoCredor("2");
			}
		}
		if (entidadeVO.getValorProtesto() != null) {
			try {
				String valorRecebido = entidadeVO.getValorProtesto().trim().replace("\"", "").replace(".", "").replace(",", ".");
				if (valorRecebido.contains("E")) {
					valorRecebido = valorRecebido.replaceAll("E[0-9]*", "");
				}
				BigDecimal valorProtesto = new BigDecimal(valorRecebido);
				registro.setValorProtesto(valorProtesto.divide(new BigDecimal("100")));
			} catch (Exception ex) {
				registro.setValorProtesto(BigDecimal.ZERO);
			}
		}
		if (entidadeVO.getDataProtesto() != null) {
			try {
				registro.setDataProtesto(DataUtil.stringToLocalDate(entidadeVO.getDataProtesto()).toDate());
			} catch (Exception ex) {
				registro.setDataProtesto(null);
			}
		}
		if (entidadeVO.getNumeroDocumentoDevedor() != null) {
			if (entidadeVO.getNumeroDocumentoDevedor().replace(" ", "").trim().length() > 11) {
				registro.setTipoPessoaDevedor("J");
				registro.setTipoDocumentoDevedor("1");
			} else {
				registro.setTipoPessoaDevedor("F");
				registro.setTipoDocumentoDevedor("2");
			}
		}
		registro.setNumeroCoResponsavel("01");
		if (entidadeVO.getNomeDevedor().length() > 45) {
			registro.setNomeDevedor(RemoverAcentosUtil.removeAcentos(entidadeVO.getNomeDevedor().substring(0, 44)));
		} else {
			registro.setNomeDevedor(RemoverAcentosUtil.removeAcentos(entidadeVO.getNomeDevedor()));
		}
		if (entidadeVO.getNumeroDocumentoDevedor() != null) {
			registro.setNumeroDocumentoDevedor(CpfCnpjUtil
					.buscarNumeroDocumento(entidadeVO.getNumeroDocumentoDevedor().replace(".", "").replace("-", "").replace("/", "")));
			registro.setComplementoDocumentoDevedor(CpfCnpjUtil
					.buscarComplementoDocumento(entidadeVO.getNumeroDocumentoDevedor().replace(".", "").replace("-", "").replace("/", "")));
			registro.setDigitoControleDocumentoDevedor(CpfCnpjUtil
					.calcularDigitoControle(entidadeVO.getNumeroDocumentoDevedor().replace(".", "").replace("-", "").replace("/", "")));
		} else {
			registro.setNumeroDocumentoDevedor("");
			registro.setComplementoDocumentoDevedor("");
			registro.setDigitoControleDocumentoDevedor("");
		}
		if (entidadeVO.getEnderecoDevedor().length() > 45) {
			registro.setEnderecoDevedor(RemoverAcentosUtil.removeAcentos(entidadeVO.getEnderecoDevedor().substring(0, 44)));
		} else {
			registro.setEnderecoDevedor(RemoverAcentosUtil.removeAcentos(entidadeVO.getEnderecoDevedor()));
		}
		registro.setCepDevedor(RemoverAcentosUtil.removeAcentos(entidadeVO.getCepDevedor()));
		if (entidadeVO.getCepDevedor().length() > 20) {
			registro.setCidadeDevedor(RemoverAcentosUtil.removeAcentos(entidadeVO.getCidadeDevedor().substring(0, 19)));
		} else {
			registro.setCidadeDevedor(RemoverAcentosUtil.removeAcentos(entidadeVO.getCidadeDevedor()));
		}
		registro.setUfDevedor(RemoverAcentosUtil.removeAcentos(entidadeVO.getUfDevedor()));
		registro.setNumeroCartorio("01");
		registro.setNumeroProtocoloCartorio(RemoverAcentosUtil.removeAcentos(entidadeVO.getNumeroProtocoloCartorio()));
		if (entidadeVO.getDataCancelamentoProtesto() != null) {
			try {
				registro.setDataCancelamentoProtesto(DataUtil.stringToLocalDate(entidadeVO.getDataCancelamentoProtesto()).toDate());
			} catch (Exception ex) {
				registro.setDataCancelamentoProtesto(null);
			}
		}
		registro.setEspecieProtesto("1");
		registro.setCodigoErro3Posicoes("");
		registro.setSequenciaRegistro("1");
		return registro;
	}

	@Override
	public TituloCnpVO converter(RegistroCnp entidade, Class<TituloCnpVO> arquivoVO) {
		TituloCnpVO tituloVO = new TituloCnpVO();
		tituloVO.setCodigoRegistro(entidade.getCodigoRegistro());
		tituloVO.setTipoInformacao(entidade.getTipoInformacao());
		tituloVO.setCodigoOperacao(entidade.getCodigoOperacao());
		tituloVO.setCodigoUnidadeFederativa(entidade.getCodigoUnidadeFederativa());
		tituloVO.setCodigoPracaEmbratel(entidade.getCodigoPracaEmbratel());
		tituloVO.setNomeCredor(RemoverAcentosUtil.removeAcentos(entidade.getNomeCredor()));
		tituloVO.setNumeroDocumentoCredor(entidade.getNumeroDocumentoCredor());
		tituloVO.setComplementoDocumentoCredor(entidade.getComplementoDocumentoCredor());
		tituloVO.setDigitoControleDocumentoCredor(entidade.getDigitoControleDocumentoCredor());
		tituloVO.setEnderecoCredor(RemoverAcentosUtil.removeAcentos(entidade.getEnderecoCredor()));
		tituloVO.setCepCredor(entidade.getCepCredor());
		tituloVO.setCidadeCredor(RemoverAcentosUtil.removeAcentos(entidade.getCidadeCredor()));
		tituloVO.setMunicipioEnderecoCredor(RemoverAcentosUtil.removeAcentos(entidade.getCidadeCredor()));
		tituloVO.setUfCredor(RemoverAcentosUtil.removeAcentos(entidade.getUfCredor()));
		tituloVO.setTipoPessoaCredor(entidade.getTipoPessoaCredor());
		tituloVO.setTipoDocumentoCredor(entidade.getTipoDocumentoCredor());
		tituloVO.setValorProtesto(new BigDecimalConversor().getValorConvertidoSegundoLayoutFebraban(entidade.getValorProtesto()));
		if (tituloVO.getValorProtesto().contains("E")) {
			tituloVO.setValorProtesto(tituloVO.getValorProtesto().replaceAll("E[0-9]*", ""));
		}
		tituloVO.setDataProtesto(new DateConversor().getValorConvertidoParaString(new LocalDate(entidade.getDataProtesto())));
		tituloVO.setTipoPessoaDevedor(entidade.getTipoPessoaDevedor());
		tituloVO.setTipoDocumentoDevedor(entidade.getTipoDocumentoDevedor());
		tituloVO.setNumeroCoResponsavel(entidade.getNumeroCoResponsavel());
		tituloVO.setNomeDevedor(RemoverAcentosUtil.removeAcentos(entidade.getNomeDevedor()));
		tituloVO.setNumeroDocumentoDevedor(entidade.getNumeroDocumentoDevedor());
		tituloVO.setComplementoDocumentoDevedor(entidade.getComplementoDocumentoDevedor());
		tituloVO.setDigitoControleDocumentoDevedor(entidade.getDigitoControleDocumentoDevedor());
		tituloVO.setEnderecoDevedor(RemoverAcentosUtil.removeAcentos(entidade.getEnderecoDevedor()));
		tituloVO.setCepDevedor(RemoverAcentosUtil.removeAcentos(entidade.getCepDevedor()));
		tituloVO.setCidadeDevedor(RemoverAcentosUtil.removeAcentos(entidade.getCidadeDevedor()));
		tituloVO.setUfDevedor(RemoverAcentosUtil.removeAcentos(entidade.getUfDevedor()));
		tituloVO.setNumeroCartorio(entidade.getNumeroCartorio());
		tituloVO.setNumeroProtocoloCartorio(RemoverAcentosUtil.removeAcentos(entidade.getNumeroProtocoloCartorio()));
		if (entidade.getDataCancelamentoProtesto() != null) {
			tituloVO.setDataCancelamentoProtesto(
					new DateConversor().getValorConvertidoParaString(new LocalDate(entidade.getDataCancelamentoProtesto())));
		} else {
			tituloVO.setDataCancelamentoProtesto("");
		}
		tituloVO.setEspecieProtesto(entidade.getEspecieProtesto());
		return tituloVO;
	}

	public static RegistroCnp converterLinhaCSV(String[] dados) {
		RegistroCnp registro = new RegistroCnp();
		registro.setCodigoRegistro(TipoRegistro.TITULO.getConstante());
		registro.setTipoInformacao(RemoverAcentosUtil.removeAcentos(dados[0].replace("\"", "")));
		if (registro.getTipoInformacao().trim().equals("P")) {
			registro.setTipoRegistroCnp(TipoRegistroCnp.PROTESTO);
			registro.setCodigoOperacao(TipoRegistroCnp.PROTESTO.getCodigoOperacao());
		} else {
			registro.setTipoRegistroCnp(TipoRegistroCnp.CANCELAMENTO);
			registro.setCodigoOperacao(TipoRegistroCnp.CANCELAMENTO.getCodigoOperacao());
		}
		registro.setCodigoUnidadeFederativa("17");
		registro.setCodigoPracaEmbratel("63");
		if (dados[2].length() > 45) {
			registro.setNomeCredor(RemoverAcentosUtil.removeAcentos(dados[2].substring(0, 44)));
		} else {
			registro.setNomeCredor(RemoverAcentosUtil.removeAcentos(dados[2]));
		}
		if (dados[3] != null) {
			registro.setNumeroDocumentoCredor(
					CpfCnpjUtil.buscarNumeroDocumento(dados[3].replace(".", "").replace("-", "").replace("/", "")));
			registro.setComplementoDocumentoCredor(
					CpfCnpjUtil.buscarComplementoDocumento(dados[3].replace(".", "").replace("-", "").replace("/", "")));
			registro.setDigitoControleDocumentoCredor(
					CpfCnpjUtil.calcularDigitoControle(dados[3].replace(".", "").replace("-", "").replace("/", "")));
		} else {
			registro.setNumeroDocumentoCredor("");
			registro.setComplementoDocumentoCredor("");
			registro.setDigitoControleDocumentoCredor("");
		}
		if (dados[4].length() > 45) {
			registro.setEnderecoCredor(RemoverAcentosUtil.removeAcentos(dados[4].substring(0, 44)));
		} else {
			registro.setEnderecoCredor(RemoverAcentosUtil.removeAcentos(dados[4]));
		}
		registro.setCepCredor(RemoverAcentosUtil.removeAcentos(dados[5]));
		if (dados[6].length() > 20) {
			registro.setCidadeCredor(RemoverAcentosUtil.removeAcentos(dados[6].substring(0, 19)));
			registro.setMunicipioEnderecoCredor(RemoverAcentosUtil.removeAcentos(dados[6].substring(0, 19)));
		} else {
			registro.setCidadeCredor(RemoverAcentosUtil.removeAcentos(dados[6]));
			registro.setMunicipioEnderecoCredor(RemoverAcentosUtil.removeAcentos(dados[6]));
		}
		registro.setUfCredor(RemoverAcentosUtil.removeAcentos(dados[7]));
		if (dados[3] != null) {
			if (dados[3].replace(" ", "").trim().length() > 11) {
				registro.setTipoPessoaCredor("J");
				registro.setTipoDocumentoCredor("1");
			} else {
				registro.setTipoPessoaCredor("F");
				registro.setTipoDocumentoCredor("2");
			}
		}
		if (dados[23] != null) {
			try {
				registro.setValorProtesto(new BigDecimal(dados[23].trim().replace("\"", "")));

			} catch (Exception ex) {
				registro.setValorProtesto(BigDecimal.ZERO);
			}
		}
		if (dados[22] != null) {
			try {
				registro.setDataProtesto(DataUtil.stringToLocalDate(dados[22]).toDate());

			} catch (Exception ex) {
				registro.setDataProtesto(null);
			}
		}
		if (dados[14] != null) {
			if (dados[14].replace(" ", "").trim().length() > 11) {
				registro.setTipoPessoaDevedor("J");
				registro.setTipoDocumentoDevedor("1");
			} else {
				registro.setTipoPessoaDevedor("F");
				registro.setTipoDocumentoDevedor("2");
			}
		}
		registro.setNumeroCoResponsavel("01");
		if (dados[13].length() > 45) {
			registro.setNomeDevedor(RemoverAcentosUtil.removeAcentos(dados[13].substring(0, 44)));
		} else {
			registro.setNomeDevedor(RemoverAcentosUtil.removeAcentos(dados[13]));
		}
		if (dados[14] != null) {
			registro.setNumeroDocumentoDevedor(
					CpfCnpjUtil.buscarNumeroDocumento(dados[14].replace(".", "").replace("-", "").replace("/", "")));
			registro.setComplementoDocumentoDevedor(
					CpfCnpjUtil.buscarComplementoDocumento(dados[14].replace(".", "").replace("-", "").replace("/", "")));
			registro.setDigitoControleDocumentoDevedor(
					CpfCnpjUtil.calcularDigitoControle(dados[14].replace(".", "").replace("-", "").replace("/", "")));
		} else {
			registro.setNumeroDocumentoDevedor("");
			registro.setComplementoDocumentoDevedor("");
			registro.setDigitoControleDocumentoDevedor("");
		}
		if (dados[15].length() > 45) {
			registro.setEnderecoDevedor(RemoverAcentosUtil.removeAcentos(dados[15].substring(0, 44)));
		} else {
			registro.setEnderecoDevedor(RemoverAcentosUtil.removeAcentos(dados[15]));
		}
		if (dados[16].length() > 8) {
			registro.setCepDevedor(RemoverAcentosUtil.removeAcentos(dados[16].substring(0, 7)));
		} else {
			registro.setCepDevedor(RemoverAcentosUtil.removeAcentos(dados[16]));
		}
		if (dados[17].length() > 20) {
			registro.setCidadeDevedor(RemoverAcentosUtil.removeAcentos(dados[17].substring(0, 19)));
		} else {
			registro.setCidadeDevedor(RemoverAcentosUtil.removeAcentos(dados[17]));
		}
		registro.setUfDevedor(RemoverAcentosUtil.removeAcentos(dados[18]));
		registro.setNumeroCartorio("01");
		registro.setNumeroProtocoloCartorio(RemoverAcentosUtil.removeAcentos(dados[19]));
		if (dados[20] != null) {
			try {
				registro.setDataCancelamentoProtesto(DataUtil.stringToLocalDate(dados[20]).toDate());

			} catch (Exception ex) {
				registro.setDataCancelamentoProtesto(null);
			}
		}
		registro.setEspecieProtesto("1");
		registro.setCodigoErro3Posicoes("");
		registro.setSequenciaRegistro("1");
		return registro;
	}

	public static RegistroCnp converterLinhaSerasa(String linha) {
		RegistroCnp registro = new RegistroCnp();
		registro.setCodigoRegistro(TipoRegistro.TITULO.getConstante());
		registro.setTipoInformacao(RemoverAcentosUtil.removeAcentos(linha.substring(1, 2).replace("\"", "")));
		if (registro.getTipoInformacao().trim().equals("P")) {
			registro.setTipoRegistroCnp(TipoRegistroCnp.PROTESTO);
			registro.setCodigoOperacao(TipoRegistroCnp.PROTESTO.getCodigoOperacao());
		} else {
			registro.setTipoRegistroCnp(TipoRegistroCnp.CANCELAMENTO);
			registro.setCodigoOperacao(TipoRegistroCnp.CANCELAMENTO.getCodigoOperacao());
		}
		registro.setCodigoUnidadeFederativa("17");
		registro.setCodigoPracaEmbratel("63");
		if (linha.substring(64, 109).length() > 45) {
			registro.setNomeCredor(RemoverAcentosUtil.removeAcentos(linha.substring(64, 109).substring(0, 44)));
		} else {
			registro.setNomeCredor(RemoverAcentosUtil.removeAcentos(linha.substring(64, 109)));
		}
		if (linha.substring(109, 122) != null) {
			registro.setNumeroDocumentoCredor(
					CpfCnpjUtil.buscarNumeroDocumento(linha.substring(109, 122).replace(".", "").replace("-", "").replace("/", "")));
			registro.setComplementoDocumentoCredor(
					CpfCnpjUtil.buscarComplementoDocumento(linha.substring(109, 122).replace(".", "").replace("-", "").replace("/", "")));
			registro.setDigitoControleDocumentoCredor(
					CpfCnpjUtil.calcularDigitoControle(linha.substring(109, 122).replace(".", "").replace("-", "").replace("/", "")));
		} else {
			registro.setNumeroDocumentoCredor("");
			registro.setComplementoDocumentoCredor("");
			registro.setDigitoControleDocumentoCredor("");
		}
		if (linha.substring(124, 168).length() > 45) {
			registro.setEnderecoCredor(RemoverAcentosUtil.removeAcentos(linha.substring(124, 168).substring(0, 44)));
		} else {
			registro.setEnderecoCredor(RemoverAcentosUtil.removeAcentos(linha.substring(124, 168)));
		}
		registro.setCepCredor(RemoverAcentosUtil.removeAcentos(linha.substring(168, 176)));
		if (linha.substring(176, 196).length() > 20) {
			registro.setCidadeCredor(RemoverAcentosUtil.removeAcentos(linha.substring(176, 196).substring(0, 19)));
			registro.setMunicipioEnderecoCredor(RemoverAcentosUtil.removeAcentos(linha.substring(176, 196).substring(0, 19)));
		} else {
			registro.setCidadeCredor(RemoverAcentosUtil.removeAcentos(linha.substring(176, 196)));
			registro.setMunicipioEnderecoCredor(RemoverAcentosUtil.removeAcentos(linha.substring(176, 196)));
		}
		registro.setUfCredor(RemoverAcentosUtil.removeAcentos(linha.substring(196, 198)));
		if (linha.substring(109, 122) != null) {
			if (linha.substring(109, 122).replace(" ", "").trim().length() > 11) {
				registro.setTipoPessoaCredor("J");
				registro.setTipoDocumentoCredor("1");
			} else {
				registro.setTipoPessoaCredor("F");
				registro.setTipoDocumentoCredor("2");
			}
		}
		if (linha.substring(246, 260) != null) {
			try {
				registro.setValorProtesto(new BigDecimal(linha.substring(246, 260).trim().replace("\"", "")));

			} catch (Exception ex) {
				registro.setValorProtesto(BigDecimal.ZERO);
			}
		}
		if (linha.substring(260, 268) != null) {
			try {
				registro.setDataProtesto(
						DataUtil.stringToLocalDate(DataUtil.PADRAO_FORMATACAO_DATA_DDMMYYYY, linha.substring(260, 268)).toDate());

			} catch (Exception ex) {
				registro.setDataProtesto(null);
			}
		}
		if (linha.substring(345, 358) != null) {
			if (linha.substring(345, 358).replace(" ", "").trim().length() > 11) {
				registro.setTipoPessoaDevedor("J");
				registro.setTipoDocumentoDevedor("1");
			} else {
				registro.setTipoPessoaDevedor("F");
				registro.setTipoDocumentoDevedor("2");
			}
		}
		registro.setNumeroCoResponsavel("01");
		if (linha.substring(297, 342).length() > 45) {
			registro.setNomeDevedor(RemoverAcentosUtil.removeAcentos(linha.substring(297, 342).substring(0, 44)));
		} else {
			registro.setNomeDevedor(RemoverAcentosUtil.removeAcentos(linha.substring(297, 342)));
		}
		if (linha.substring(345, 358) != null) {
			registro.setNumeroDocumentoDevedor(
					CpfCnpjUtil.buscarNumeroDocumento(linha.substring(345, 358).replace(".", "").replace("-", "").replace("/", "")));
			registro.setComplementoDocumentoDevedor(
					CpfCnpjUtil.buscarComplementoDocumento(linha.substring(345, 358).replace(".", "").replace("-", "").replace("/", "")));
			registro.setDigitoControleDocumentoDevedor(
					CpfCnpjUtil.calcularDigitoControle(linha.substring(345, 358).replace(".", "").replace("-", "").replace("/", "")));
		} else {
			registro.setNumeroDocumentoDevedor("");
			registro.setComplementoDocumentoDevedor("");
			registro.setDigitoControleDocumentoDevedor("");
		}
		if (linha.substring(370, 415).length() > 45) {
			registro.setEnderecoDevedor(RemoverAcentosUtil.removeAcentos(linha.substring(370, 415).substring(0, 44)));
		} else {
			registro.setEnderecoDevedor(RemoverAcentosUtil.removeAcentos(linha.substring(370, 415)));
		}
		registro.setCepDevedor(RemoverAcentosUtil.removeAcentos(linha.substring(415, 423)));
		if (linha.substring(423, 443).length() > 20) {
			registro.setCidadeDevedor(RemoverAcentosUtil.removeAcentos(linha.substring(423, 443).substring(0, 19)));
		} else {
			registro.setCidadeDevedor(RemoverAcentosUtil.removeAcentos(linha.substring(423, 443)));
		}
		registro.setUfDevedor(RemoverAcentosUtil.removeAcentos(linha.substring(443, 445)));
		registro.setNumeroCartorio("01");
		registro.setNumeroProtocoloCartorio(RemoverAcentosUtil.removeAcentos(linha.substring(447, 457)));
		if (linha.substring(477, 485) != null) {
			try {
				registro.setDataCancelamentoProtesto(
						DataUtil.stringToLocalDate(DataUtil.PADRAO_FORMATACAO_DATA_DDMMYYYY, linha.substring(477, 485)).toDate());

			} catch (Exception ex) {
				registro.setDataCancelamentoProtesto(null);
			}
		}
		registro.setEspecieProtesto("1");
		registro.setCodigoErro3Posicoes("");
		registro.setSequenciaRegistro("1");

		return registro;
	}
}