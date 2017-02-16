package br.com.ieptbto.cra.fabrica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.arquivo.ConversorCabecalho;
import br.com.ieptbto.cra.conversor.arquivo.ConversorConfirmacao;
import br.com.ieptbto.cra.conversor.arquivo.ConversorRetorno;
import br.com.ieptbto.cra.conversor.arquivo.ConversorRodape;
import br.com.ieptbto.cra.conversor.arquivo.ConversorTitulo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.enumeration.regra.TipoIdentificacaoRegistro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.processador.FabricaRegistro;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class FabricaRemessaConfirmacaoRetorno {

	protected static final Logger logger = Logger.getLogger(FabricaRemessaConfirmacaoRetorno.class);

	@Autowired
	private InstituicaoMediator instituicaoMediator;

	private Arquivo arquivo;
	private List<Exception> erros;

	public Arquivo processarRemessaConfirmacaoRetorno(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		this.arquivo = arquivo;
		this.erros = erros;

		try {
			List<Remessa> remessas = new ArrayList<Remessa>();
			this.arquivo.setRemessas(remessas);

			Remessa remessa = new Remessa();
			remessa.setTitulos(new ArrayList<Titulo>());
			remessa.setArquivo(arquivo);

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoFisico)));
			String linha = "";
			while ((linha = reader.readLine()) != null) {
				setRegistro(linha, remessa);
				if (remessa.getRodape() != null) {
					remessas.add(remessa);
					remessa = new Remessa();
					remessa.setTitulos(new ArrayList<Titulo>());
					remessa.setArquivo(getArquivo());
				}
			}
			reader.close();
			return getArquivo();

		} catch (FileNotFoundException e) {
			new InfraException("Arquivo não encontrado! ");
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			new InfraException("Arquivo não encontrado!");
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private void setRegistro(String linha, Remessa remessa) {
		AbstractArquivoVO registro = FabricaRegistro.getInstance(linha).criarRegistro();

		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoFebraban(remessa.getArquivo());
		if (TipoIdentificacaoRegistro.CABECALHO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			CabecalhoVO cabecalhoVO = CabecalhoVO.class.cast(registro);
			CabecalhoRemessa cabecalho = new ConversorCabecalho().converter(CabecalhoRemessa.class, cabecalhoVO);
			cabecalho.setRemessa(remessa);
			remessa.setCabecalho(cabecalho);
			remessa.setInstituicaoDestino(getInstituicaoDeDestino(cabecalho));
			remessa.setInstituicaoOrigem(getArquivo().getInstituicaoEnvio());

		} else if (TipoIdentificacaoRegistro.TITULO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			TituloVO tituloVO = TituloVO.class.cast(registro);
			Titulo titulo = null;
			if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo)) {
				titulo = new ConversorTitulo().converter(TituloRemessa.class, tituloVO);
			} else if (TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo)) {
				titulo = new ConversorConfirmacao().converter(Confirmacao.class, tituloVO);
			} else if (TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
				titulo = new ConversorRetorno().converter(Retorno.class, tituloVO);
			}
			if (titulo != null) {
				titulo.setRemessa(remessa);
				remessa.getTitulos().add(titulo);
			}

		} else if (TipoIdentificacaoRegistro.RODAPE.getConstante().equals(registro.getIdentificacaoRegistro())) {
			RodapeVO rodapeVO = RodapeVO.class.cast(registro);
			Rodape rodape = new ConversorRodape().converter(Rodape.class, rodapeVO);
			remessa.setRodape(rodape);
			rodape.setRemessa(remessa);

		} else {
			logger.error("O Tipo do registro não foi encontrado: [" + registro.getIdentificacaoRegistro() + " ]");
			throw new InfraException("O Tipo do registro não foi encontrado: [" + registro.getIdentificacaoRegistro() + " ]");
		}
	}

	private Instituicao getInstituicaoDeDestino(CabecalhoRemessa cabecalho) {
		if (TipoArquivoFebraban.CONFIRMACAO.equals(TipoArquivoFebraban.getTipoArquivoFebraban(getArquivo().getNomeArquivo()))
				|| TipoArquivoFebraban.RETORNO.equals(TipoArquivoFebraban.getTipoArquivoFebraban(getArquivo().getNomeArquivo()))) {
			return instituicaoMediator.getInstituicaoPorCodigoPortador(cabecalho.getNumeroCodigoPortador());
		} else {
			return instituicaoMediator.getCartorioPorCodigoIBGE(cabecalho.getCodigoMunicipio());
		}
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}
}