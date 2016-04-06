package br.com.ieptbto.cra.conversor;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import br.com.ieptbto.cra.conversor.arquivo.CabecalhoCnpConversor;
import br.com.ieptbto.cra.conversor.arquivo.DateConversor;
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
				remessaVO.getTitulosCnpVO().add(new TituloCnpConversor().converter(titulo, TituloCnpVO.class));
			}

			remessaVO.setRodapeCnpVO(new RodapeCnpConversor().converter(remessa.getRodape(), RodapeCnpVO.class));
			remessasVO.add(remessaVO);
		}
		return remessasVO;
	}

	public static List<RemessaCnpVO> converterParaRemessaCnpNacionalVO(List<RemessaCnp> remessasCnp) {
		Integer sequencialRegistro = 2;
		RemessaCnpVO remessaVO = new RemessaCnpVO();

		remessaVO.setCabecalhoCnpVO(getCabecalho());
		remessaVO.setTitulosCnpVO(new ArrayList<TituloCnpVO>());
		for (RemessaCnp remessa : remessasCnp) {
			for (TituloCnp titulo : remessa.getTitulos()) {
				TituloCnpVO tituloCnpVO = new TituloCnpConversor().converter(titulo, TituloCnpVO.class);
				tituloCnpVO.setSequenciaRegistro(Integer.toString(sequencialRegistro));
				remessaVO.getTitulosCnpVO().add(tituloCnpVO);
				sequencialRegistro = sequencialRegistro + 1;
			}
		}
		remessaVO.setRodapeCnpVO(getRodape(sequencialRegistro));

		List<RemessaCnpVO> remessasVO = new ArrayList<RemessaCnpVO>();
		remessasVO.add(remessaVO);
		return remessasVO;
	}

	private static CabecalhoCnpVO getCabecalho() {
		CabecalhoCnpVO cabecalho = new CabecalhoCnpVO();
		cabecalho.setCodigoRegistro(TipoRegistro.CABECALHO.getConstante());
		cabecalho.setCodigoRegistro("0");
		cabecalho.setDataMovimento(new DateConversor().getValorConvertidoParaString(new LocalDate()));
		cabecalho.setNumeroRemessaArquivo("1");
		cabecalho.setIdentificacaoDoArquivo("CENTRAL_NACIONAL_PROTESTO");
		cabecalho.setCodigoRemessa("E");
		cabecalho.setNumeroDDD("063");
		cabecalho.setNumeroTelefoneInstituicaoInformante("32120900");
		cabecalho.setNomeContatoInstituicaoInformante("IEPTB_TO");
		cabecalho.setSequenciaRegistro("1");
		cabecalho.setPeriodicidadeEnvio("D");
		return cabecalho;
	}

	private static RodapeCnpVO getRodape(Integer sequencial) {
		RodapeCnpVO rodape = new RodapeCnpVO();
		rodape.setCodigoRegistro(TipoRegistro.RODAPE.getConstante());
		rodape.setSequenciaRegistro(Integer.toString(sequencial));
		return rodape;
	}
}
