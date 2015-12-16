package br.com.ieptbto.cra.conversor.arquivo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.ConversorArquivoVO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaCnp;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class ConversorRemessaArquivo {

	protected static final Logger logger = Logger.getLogger(ConversorRemessaArquivo.class);
	@Autowired
	private FabricaDeArquivo fabricaDeArquivo;

	public ArquivoVO converter(Remessa remessa) {
		ArquivoVO arquivo = new ArquivoVO();
		arquivo.setCabecalhos(converterCabecalho(remessa.getCabecalho()));
		arquivo.setRodapes(converterRodape(remessa.getRodape()));
		arquivo.setTitulos(converterTitulos(remessa.getTitulos()));
		arquivo.setIdentificacaoRegistro("0");
		arquivo.setTipoArquivo(remessa.getArquivo().getTipoArquivo());

		return arquivo;
	}

	private List<TituloVO> converterTitulos(List<Titulo> titulos) {
		List<TituloVO> titulosVO = new ArrayList<TituloVO>();
		TituloVO tituloVO = null;
		for (Titulo titulo : titulos) {
			if (titulo instanceof TituloRemessa) {
				tituloVO = TituloVO.parseTitulo(TituloRemessa.class.cast(titulo));
			} else if (titulo instanceof Confirmacao) {
				tituloVO = TituloVO.parseTitulo(Confirmacao.class.cast(titulo));
			} else if (titulo instanceof Retorno) {
				tituloVO = TituloVO.parseTitulo(Retorno.class.cast(titulo));
			}

			titulosVO.add(tituloVO);
		}
		return titulosVO;
	}

	private List<RodapeVO> converterRodape(Rodape rodape) {
		List<RodapeVO> rodapes = new ArrayList<RodapeVO>();
		RodapeVO rodapeVO = RodapeVO.parseRodape(rodape);
		rodapes.add(rodapeVO);
		return rodapes;
	}

	private List<CabecalhoVO> converterCabecalho(CabecalhoRemessa cabecalho) {
		List<CabecalhoVO> cabecalhos = new ArrayList<CabecalhoVO>();
		CabecalhoVO cabecalhoVO = CabecalhoVO.parseCabecalho(cabecalho);
		cabecalhos.add(cabecalhoVO);
		return cabecalhos;
	}

	public Arquivo converter(ArquivoVO arquivoVO, Arquivo arquivo, List<Exception> erros) {
		List<RemessaCnp> remessas = ConversorArquivoVO.converterParaRemessaVO(arquivoVO);
		arquivo.setRemessas(new ArrayList<Remessa>());

		fabricaDeArquivo.processarArquivoXML(remessas, arquivo.getUsuarioEnvio(), arquivo.getNomeArquivo(), arquivo, erros);

		return arquivo;
	}

	public List<RemessaCnp> converter(List<Arquivo> arquivos) {
		List<RemessaCnp> remessasVO = new ArrayList<RemessaCnp>();

		for (Arquivo arquivo : arquivos) {
			remessasVO = converterRemessa(arquivo.getRemessaBanco());
		}
		return remessasVO;
	}

	public List<RemessaCnp> converterRemessa(List<Remessa> remessas) {
		List<RemessaCnp> remessasVO = new ArrayList<RemessaCnp>();
		for (Remessa remessa : remessas) {
			RemessaCnp remessaVO = new RemessaCnp();
			remessaVO.setTitulos(new ArrayList<TituloVO>());
			remessaVO.setCabecalho(CabecalhoVO.parseCabecalho(remessa.getCabecalho()));
			remessaVO.getTitulos().addAll(converterTitulos(remessa.getTitulos()));
			remessaVO.setRodapes(RodapeVO.parseRodape(remessa.getRodape()));
			remessasVO.add(remessaVO);
		}

		return remessasVO;
	}

	public RemessaCnp converterRemessaVO(Remessa remessa) {
		RemessaCnp remessaVO = new RemessaCnp();
		remessaVO.setTitulos(new ArrayList<TituloVO>());
		remessaVO.setCabecalho(CabecalhoVO.parseCabecalho(remessa.getCabecalho()));
		remessaVO.getTitulos().addAll(converterTitulos(remessa.getTitulos()));
		remessaVO.setRodapes(RodapeVO.parseRodape(remessa.getRodape()));

		return remessaVO;
	}

}
