package br.com.ieptbto.cra.conversor.arquivo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoArquivo;
import br.com.ieptbto.cra.entidade.CabecalhoCartorio;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.entidade.RodapeArquivo;
import br.com.ieptbto.cra.entidade.RodapeCartorio;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoCartorioDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.RegistroDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.entidade.vo.RodapeArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeCartorioDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoRegistro;
import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.processador.FabricaRegistro;
import br.com.ieptbto.cra.processador.FabricaRegistroDesistenciaProtesto;
import br.com.ieptbto.cra.validacao.ValidarCabecalhoRemessa;
import br.com.ieptbto.cra.validacao.regra.RegraValidaTipoArquivoTXT;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class FabricaDeArquivoTXT extends AbstractFabricaDeArquivo {

	@Autowired
	InstituicaoMediator instituicaoMediator;
	@Autowired
	private ValidarCabecalhoRemessa validarCabecalhoRemessa;
	@Autowired
	private GeradorDeArquivosTXT geradorDeArquivosTXT;
	@Autowired
	private ConversorArquivoDesistenciaProtesto conversorArquivoDesistenciaProtesto;
	private List<Exception> errosCabecalho;
	private Remessa remessa;
	private List<Remessa> remessas;
	private DesistenciaProtesto desistenciaProtesto;
	private RemessaDesistenciaProtesto remessaDesistenciaProtesto;

	public FabricaDeArquivoTXT fabrica(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		this.arquivoFisico = arquivoFisico;
		this.arquivo = arquivo;
		this.erros = erros;
		this.errosCabecalho = new ArrayList<Exception>();
		validar();
		return this;
	}

	public FabricaDeArquivoTXT fabricaTXT(File remessaTXT, Remessa remessa, List<Exception> erros) {
		this.arquivoFisico = remessaTXT;
		this.arquivo = remessa.getArquivo();
		this.erros = erros;
		this.remessa = remessa;

		validarTXT();

		return this;
	}

	public File fabricaArquivoDesistenciaProtestoTXT(File arquivoFisico, RemessaDesistenciaProtesto remessa, List<Exception> erros) {
		this.arquivoFisico = arquivoFisico;
		this.erros = erros;
		this.remessaDesistenciaProtesto = remessa;
		return gerarArquivoDesistenciaProtesto();
	}

	private File gerarArquivoDesistenciaProtesto() {
		List<Arquivo> arquivos = new ArrayList<Arquivo>();
		arquivos.add(getArquivo());

		return geradorDeArquivosTXT.gerar(conversorArquivoDesistenciaProtesto.converter(this.remessaDesistenciaProtesto),
		        getArquivoFisico());
	}

	public FabricaDeArquivoTXT fabricaArquivoTXT(File arquivoTXT, List<Remessa> remessas, List<Exception> erros) {
		this.arquivoFisico = arquivoTXT;
		this.erros = erros;
		this.remessas = remessas;

		validarTXT();

		return this;
	}

	public void converterParaTXT() {
		RemessaVO remessaVO = new RemessaVO();
		remessaVO.setTitulos(new ArrayList<TituloVO>());
		BigDecimal valorTotalTitulos = BigDecimal.ZERO;
		remessaVO.setCabecalho(new CabecalhoConversor().converter(getRemessa().getCabecalho(), CabecalhoVO.class));
		remessaVO.setRodapes(new RodapeConversor().converter(getRemessa().getRodape(), RodapeVO.class));

		int contSequencial = 2;
		for (Titulo titulo : getRemessa().getTitulos()) {
			TituloVO tituloVO = new TituloVO();
			if (TipoArquivoEnum.REMESSA.equals(getRemessa().getArquivo().getTipoArquivo().getTipoArquivo())) {
				tituloVO = new TituloConversor().converter(TituloRemessa.class.cast(titulo), TituloVO.class);
			} else if (TipoArquivoEnum.CONFIRMACAO.equals(getRemessa().getArquivo().getTipoArquivo().getTipoArquivo())) {
				tituloVO = new ConfirmacaoConversor().converter(Confirmacao.class.cast(titulo), TituloVO.class);
			} else if (TipoArquivoEnum.RETORNO.equals(getRemessa().getArquivo().getTipoArquivo().getTipoArquivo())) {
				tituloVO = new RetornoConversor().converter(Retorno.class.cast(titulo), TituloVO.class);
			} else {
				throw new InfraException("Tipo de Arquivo não identificado");
			}
			tituloVO.setNumeroSequencialArquivo(String.valueOf(contSequencial));
			valorTotalTitulos = valorTotalTitulos.add(titulo.getSaldoTitulo());
			remessaVO.getTitulos().add(tituloVO);
			contSequencial++;
		}
		remessaVO.getCabecalho().setQtdTitulosRemessa(String.valueOf(remessaVO.getTitulos().size()));
		remessaVO.getRodape().setSomatorioQtdRemessa(somatorioSegurancaQuantidadeRemessa(remessaVO));
		remessaVO.getRodape().setSomatorioValorRemessa(new BigDecimalConversor().getValorConvertidoParaString(valorTotalTitulos));
		remessaVO.setIdentificacaoRegistro(getRemessa().getCabecalho().getIdentificacaoRegistro().getConstante());
		remessaVO.setTipoArquivo(getRemessa().getArquivo().getTipoArquivo());
		remessaVO.getRodape().setNumeroSequencialRegistroArquivo(String.valueOf(contSequencial));

		gerarTXT(remessaVO);

	}

	public void converterParaArquivoTXT() {
		List<RemessaVO> remessasVO = new ArrayList<RemessaVO>();

		for (Remessa remessa : getRemessas()) {
			setArquivo(remessa.getArquivo());
			RemessaVO remessaVO = new RemessaVO();
			remessaVO.setTitulos(new ArrayList<TituloVO>());
			BigDecimal valorTotalTitulos = BigDecimal.ZERO;

			remessaVO.setCabecalho(new CabecalhoConversor().converter(remessa.getCabecalho(), CabecalhoVO.class));
			remessaVO.setRodapes(new RodapeConversor().converter(remessa.getRodape(), RodapeVO.class));

			int contSequencial = 2;
			for (Titulo titulo : remessa.getTitulos()) {
				TituloVO tituloVO = new TituloVO();
				if (TipoArquivoEnum.REMESSA.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
					tituloVO = new TituloConversor().converter(TituloRemessa.class.cast(titulo), TituloVO.class);
				} else if (TipoArquivoEnum.CONFIRMACAO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
					tituloVO = new ConfirmacaoConversor().converter(Confirmacao.class.cast(titulo), TituloVO.class);
				} else if (TipoArquivoEnum.RETORNO.equals(remessa.getArquivo().getTipoArquivo().getTipoArquivo())) {
					tituloVO = new RetornoConversor().converter(Retorno.class.cast(titulo), TituloVO.class);
				} else {
					throw new InfraException("Tipo de Arquivo não identificado");
				}
				tituloVO.setNumeroSequencialArquivo(String.valueOf(contSequencial));
				valorTotalTitulos = valorTotalTitulos.add(titulo.getSaldoTitulo());
				remessaVO.getTitulos().add(tituloVO);
				contSequencial++;
			}
			remessaVO.getCabecalho().setQtdTitulosRemessa(String.valueOf(remessaVO.getTitulos().size()));
			remessaVO.getRodape().setSomatorioQtdRemessa(somatorioSegurancaQuantidadeRemessa(remessaVO));
			remessaVO.getRodape().setSomatorioValorRemessa(new BigDecimalConversor().getValorConvertidoParaString(valorTotalTitulos));
			remessaVO.setIdentificacaoRegistro(remessa.getCabecalho().getIdentificacaoRegistro().getConstante());
			remessaVO.setTipoArquivo(remessa.getArquivo().getTipoArquivo());
			remessaVO.getRodape().setNumeroSequencialRegistroArquivo(String.valueOf(contSequencial));

			remessasVO.add(remessaVO);
		}

		gerarTXT(remessasVO);
	}
	
	private String somatorioSegurancaQuantidadeRemessa(RemessaVO remessaVO) {
		int somatorioQtdRemessa = 0;
		if (remessaVO.getCabecalho() != null) {
			somatorioQtdRemessa = Integer.parseInt(remessaVO.getCabecalho().getQtdRegistrosRemessa()) + Integer.parseInt(remessaVO.getCabecalho().getQtdTitulosRemessa()) + 
					Integer.parseInt(remessaVO.getCabecalho().getQtdIndicacoesRemessa()) + Integer.parseInt(remessaVO.getCabecalho().getQtdOriginaisRemessa());
		}
		return Integer.toString(somatorioQtdRemessa);
	}

	private void gerarTXT(RemessaVO remessaVO) {
		geradorDeArquivosTXT.gerar(remessaVO, getArquivoFisico());
	}

	private void gerarTXT(List<RemessaVO> remessasVO) {
		geradorDeArquivosTXT.gerar(remessasVO, getArquivoFisico());
	}

	private void validarTXT() {
		// TODO Auto-generated method stub

	}

	public Arquivo converter() {
		if (TipoArquivoEnum.REMESSA.equals(getArquivo().getTipoArquivo().getTipoArquivo())
		        || TipoArquivoEnum.CONFIRMACAO.equals(getArquivo().getTipoArquivo().getTipoArquivo())
		        || TipoArquivoEnum.RETORNO.equals(getArquivo().getTipoArquivo().getTipoArquivo())) {

			return processarRemessa();
		} else if (TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.equals(getArquivo().getTipoArquivo().getTipoArquivo())) {
			return processarDesistenciaProtesto();

		} else {
			return null;
		}
	}

	private Arquivo processarDesistenciaProtesto() {
		try {
			RemessaDesistenciaProtesto remessa = new RemessaDesistenciaProtesto();
			getArquivo().setRemessaDesistenciaProtesto(remessa);
			remessa.setDesistenciaProtesto(new ArrayList<DesistenciaProtesto>());
			remessa.setArquivo(getArquivo());

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(getArquivoFisico())));
			String linha = "";
			while ((linha = reader.readLine()) != null) {
				setRegistroDesistenciaProtesto(linha, remessa);
				if (remessa.getRodape() != null) {
					remessa = new RemessaDesistenciaProtesto();
					remessa.setDesistenciaProtesto(new ArrayList<DesistenciaProtesto>());
					remessa.setArquivo(getArquivo());
				}
			}
			reader.close();

			return getArquivo();

		} catch (FileNotFoundException e) {
			getErros().add(e);
			new InfraException("arquivoFisico não encontrado");
			logger.error(e.getMessage());
		} catch (IOException e) {
			getErros().add(e);
			new InfraException("arquivoFisico não encontrado");
			logger.error(e.getMessage());
		}

		return null;
	}

	private void setRegistroDesistenciaProtesto(String linha, RemessaDesistenciaProtesto remessa) {
		AbstractArquivoVO registro = FabricaRegistroDesistenciaProtesto.getInstance(linha).criarRegistro();

		if (TipoRegistroDesistenciaProtesto.HEADER_APRESENTANTE.getConstante().equals(registro.getIdentificacaoRegistro())) {
			CabecalhoArquivoDesistenciaProtestoVO cabecalhoVO = CabecalhoArquivoDesistenciaProtestoVO.class.cast(registro);
			CabecalhoArquivo cabecalhoArquivo = new CabecalhoArquivoDesistenciaProtestoConversor().converter(CabecalhoArquivo.class,
			        cabecalhoVO);
			remessa.setCabecalho(cabecalhoArquivo);

		} else if (TipoRegistroDesistenciaProtesto.HEADER_CARTORIO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			desistenciaProtesto = new DesistenciaProtesto();
			desistenciaProtesto.setDesistencias(new ArrayList<PedidoDesistenciaCancelamento>());
			CabecalhoCartorioDesistenciaProtestoVO cabecalhoCartorioVO = CabecalhoCartorioDesistenciaProtestoVO.class.cast(registro);
			CabecalhoCartorio cabecalhoCartorio = new CabecalhoCartorioDesistenciaProtestoConversor().converter(CabecalhoCartorio.class,
			        cabecalhoCartorioVO);
			desistenciaProtesto.setCabecalhoCartorio(cabecalhoCartorio);

		} else if (TipoRegistroDesistenciaProtesto.REGISTRO_PEDIDO_DESISTENCIA.getConstante().equals(registro.getIdentificacaoRegistro())) {
			RegistroDesistenciaProtestoVO tituloDesistenciaProtesto = RegistroDesistenciaProtestoVO.class.cast(registro);
			PedidoDesistenciaCancelamento pedidoDesistencia = new RegistroDesistenciaProtestoConversor()
			        .converter(PedidoDesistenciaCancelamento.class, tituloDesistenciaProtesto);
			desistenciaProtesto.getDesistencias().add(pedidoDesistencia);
			pedidoDesistencia.setDesistenciaProtesto(desistenciaProtesto);

		} else if (TipoRegistroDesistenciaProtesto.TRAILLER_CARTORIO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			RodapeCartorioDesistenciaProtestoVO rodapeCartorioVO = RodapeCartorioDesistenciaProtestoVO.class.cast(registro);
			RodapeCartorio rodapeCartorio = new RodapeCartorioDesistenciaProtestoConversor().converter(RodapeCartorio.class,
			        rodapeCartorioVO);
			desistenciaProtesto.setRodapeCartorio(rodapeCartorio);
			remessa.getDesistenciaProtesto().add(desistenciaProtesto);
			desistenciaProtesto.setRemessaDesistenciaProtesto(remessa);

		} else if (TipoRegistroDesistenciaProtesto.TRAILLER_APRESENTANTE.getConstante().equals(registro.getIdentificacaoRegistro())) {
			RodapeArquivoDesistenciaProtestoVO rodapeArquivoVO = RodapeArquivoDesistenciaProtestoVO.class.cast(registro);
			RodapeArquivo rodapeArquivo = new RodapeArquivoDeistenciaProtestoVOConversor().converter(RodapeArquivo.class, rodapeArquivoVO);
			remessa.setRodape(rodapeArquivo);

		} else {
			getErros().add(new InfraException("O Tipo do registro não foi encontrado: [" + registro.getIdentificacaoRegistro() + " ]"));
			new InfraException("O Tipo do registro não foi encontrado: [" + registro.getIdentificacaoRegistro() + " ]");
			logger.error("O Tipo do registro não foi encontrado: [" + registro.getIdentificacaoRegistro() + " ]");
		}

	}

	private Arquivo processarRemessa() {
		try {
			List<Remessa> remessas = new ArrayList<Remessa>();
			getArquivo().setRemessas(remessas);
			Remessa remessa = new Remessa();
			remessa.setTitulos(new ArrayList<Titulo>());
			remessa.setArquivo(getArquivo());

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(getArquivoFisico())));
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
			getErros().add(e);
			new InfraException("arquivoFisico não encontrado");
			logger.error(e.getMessage());
		} catch (IOException e) {
			getErros().add(e);
			new InfraException("arquivoFisico não encontrado");
			logger.error(e.getMessage());
		}

		return null;

	}

	private void setRegistro(String linha, Remessa remessa) {
		AbstractArquivoVO registro = FabricaRegistro.getInstance(linha).criarRegistro();

		if (TipoRegistro.CABECALHO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			CabecalhoVO cabecalhoVO = CabecalhoVO.class.cast(registro);
			CabecalhoRemessa cabecalho = new CabecalhoConversor().converter(CabecalhoRemessa.class, cabecalhoVO);
			cabecalho.setRemessa(remessa);
			validarCabecalhoRemessa.validar(cabecalho, errosCabecalho);

			if (errosCabecalho.isEmpty()) {
				remessa.setCabecalho(cabecalho);
				remessa.setInstituicaoDestino(getInstituicaoDeDestino(cabecalho));
				remessa.setInstituicaoOrigem(getArquivo().getInstituicaoEnvio());
			} else {
				getErros().addAll(errosCabecalho);
			}

		} else if (TipoRegistro.TITULO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			TituloVO tituloVO = TituloVO.class.cast(registro);
			Titulo titulo;
			if (errosCabecalho.isEmpty()) {
				if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
					titulo = new ConfirmacaoConversor().converter(Confirmacao.class, tituloVO);
				} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
					titulo = new RetornoConversor().converter(Retorno.class, tituloVO);
				} else {
					titulo = new TituloConversor().converter(TituloRemessa.class, tituloVO);
				}
				titulo.setRemessa(remessa);
				remessa.getTitulos().add(titulo);
			}
		} else if (TipoRegistro.RODAPE.getConstante().equals(registro.getIdentificacaoRegistro())) {
			if (errosCabecalho.isEmpty()) {
				RodapeVO rodapeVO = RodapeVO.class.cast(registro);
				Rodape rodape = new RodapeConversor().converter(Rodape.class, rodapeVO);
				remessa.setRodape(rodape);
				rodape.setRemessa(remessa);
			} else {
				errosCabecalho = new ArrayList<Exception>();
			}
		} else {
			getErros().add(new InfraException("O Tipo do registro não foi encontrado: [" + registro.getIdentificacaoRegistro() + " ]"));
			new InfraException("O Tipo do registro não foi encontrado: [" + registro.getIdentificacaoRegistro() + " ]");
			logger.error("O Tipo do registro não foi encontrado: [" + registro.getIdentificacaoRegistro() + " ]");
		}

	}

	private Instituicao getInstituicaoDeDestino(CabecalhoRemessa cabecalho) {
		if (TipoArquivoEnum.CONFIRMACAO.equals(TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getNomeArquivo()))
		        || TipoArquivoEnum.RETORNO.equals(TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getNomeArquivo()))) {
			return instituicaoMediator.getInstituicaoPorCodigoPortador(cabecalho.getNumeroCodigoPortador());
		} else {
			return instituicaoMediator.getCartorioPorCodigoIBGE(cabecalho.getCodigoMunicipio());
		}
	}

	public Remessa getRemessa() {
		return remessa;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	@Override
	public void validar() {
		new RegraValidaTipoArquivoTXT().validar(arquivoFisico, arquivo, arquivo.getUsuarioEnvio(), erros);

	}

	public List<Remessa> getRemessas() {
		return remessas;
	}

}
