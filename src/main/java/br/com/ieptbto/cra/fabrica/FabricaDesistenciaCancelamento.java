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
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.arquivo.CabecalhoArquivoDesistenciaProtestoConversor;
import br.com.ieptbto.cra.conversor.arquivo.CabecalhoCartorioDesistenciaProtestoConversor;
import br.com.ieptbto.cra.conversor.arquivo.RegistroDesistenciaProtestoConversor;
import br.com.ieptbto.cra.conversor.arquivo.RodapeArquivoDesistenciaProtestoVOConversor;
import br.com.ieptbto.cra.conversor.arquivo.RodapeCartorioDesistenciaProtestoConversor;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoArquivo;
import br.com.ieptbto.cra.entidade.CabecalhoCartorio;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.RodapeArquivo;
import br.com.ieptbto.cra.entidade.RodapeCartorio;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoCartorioDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RegistroDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeArquivoDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeCartorioDesistenciaProtestoVO;
import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.FabricaRegistroDesistenciaProtesto;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class FabricaDesistenciaCancelamento {

	protected static final Logger logger = Logger.getLogger(FabricaDesistenciaCancelamento.class);

	private Arquivo arquivo;
	private List<Exception> erros;
	private DesistenciaProtesto desistenciaProtesto;

	public Arquivo processarDesistenciaProtesto(File file, Arquivo arquivo, List<Exception> erros) {
		this.arquivo = arquivo;
		this.erros = erros;

		try {
			RemessaDesistenciaProtesto remessa = new RemessaDesistenciaProtesto();
			getArquivo().setRemessaDesistenciaProtesto(remessa);
			remessa.setDesistenciaProtesto(new ArrayList<DesistenciaProtesto>());
			remessa.setArquivo(getArquivo());

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
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
			new InfraException("Arquivo não encontrado! ");
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			new InfraException("Arquivo não encontrado! ");
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private void setRegistroDesistenciaProtesto(String linha, RemessaDesistenciaProtesto remessa) {
		AbstractArquivoVO registro = FabricaRegistroDesistenciaProtesto.getInstance(linha).criarRegistro();

		if (TipoRegistroDesistenciaProtesto.HEADER_APRESENTANTE.getConstante().equals(registro.getIdentificacaoRegistro())) {
			CabecalhoArquivoDesistenciaProtestoVO cabecalhoVO = CabecalhoArquivoDesistenciaProtestoVO.class.cast(registro);
			CabecalhoArquivo cabecalhoArquivo = new CabecalhoArquivoDesistenciaProtestoConversor().converter(CabecalhoArquivo.class, cabecalhoVO);
			remessa.setCabecalho(cabecalhoArquivo);

		} else if (TipoRegistroDesistenciaProtesto.HEADER_CARTORIO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			desistenciaProtesto = new DesistenciaProtesto();
			desistenciaProtesto.setDesistencias(new ArrayList<PedidoDesistencia>());
			CabecalhoCartorioDesistenciaProtestoVO cabecalhoCartorioVO = CabecalhoCartorioDesistenciaProtestoVO.class.cast(registro);
			CabecalhoCartorio cabecalhoCartorio =
					new CabecalhoCartorioDesistenciaProtestoConversor().converter(CabecalhoCartorio.class, cabecalhoCartorioVO);
			desistenciaProtesto.setCabecalhoCartorio(cabecalhoCartorio);

		} else if (TipoRegistroDesistenciaProtesto.REGISTRO_PEDIDO_DESISTENCIA.getConstante().equals(registro.getIdentificacaoRegistro())) {
			RegistroDesistenciaProtestoVO tituloDesistenciaProtesto = RegistroDesistenciaProtestoVO.class.cast(registro);
			PedidoDesistencia pedidoDesistencia =
					new RegistroDesistenciaProtestoConversor().converter(PedidoDesistencia.class, tituloDesistenciaProtesto);
			desistenciaProtesto.getDesistencias().add(pedidoDesistencia);
			pedidoDesistencia.setDesistenciaProtesto(desistenciaProtesto);

		} else if (TipoRegistroDesistenciaProtesto.TRAILLER_CARTORIO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			RodapeCartorioDesistenciaProtestoVO rodapeCartorioVO = RodapeCartorioDesistenciaProtestoVO.class.cast(registro);
			RodapeCartorio rodapeCartorio = new RodapeCartorioDesistenciaProtestoConversor().converter(RodapeCartorio.class, rodapeCartorioVO);
			desistenciaProtesto.setRodapeCartorio(rodapeCartorio);
			remessa.getDesistenciaProtesto().add(desistenciaProtesto);
			desistenciaProtesto.setRemessaDesistenciaProtesto(remessa);

		} else if (TipoRegistroDesistenciaProtesto.TRAILLER_APRESENTANTE.getConstante().equals(registro.getIdentificacaoRegistro())) {
			RodapeArquivoDesistenciaProtestoVO rodapeArquivoVO = RodapeArquivoDesistenciaProtestoVO.class.cast(registro);
			RodapeArquivo rodapeArquivo = new RodapeArquivoDesistenciaProtestoVOConversor().converter(RodapeArquivo.class, rodapeArquivoVO);
			remessa.setRodape(rodapeArquivo);

		} else {
			getErros().add(new InfraException("O Tipo do registro não foi encontrado: [" + registro.getIdentificacaoRegistro() + " ]"));
			new InfraException("O Tipo do registro não foi encontrado: [" + registro.getIdentificacaoRegistro() + " ]");
			logger.error("O Tipo do registro não foi encontrado: [" + registro.getIdentificacaoRegistro() + " ]");
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