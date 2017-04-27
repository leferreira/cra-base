package br.com.ieptbto.cra.fabrica;

import br.com.ieptbto.cra.conversor.arquivo.*;
import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.vo.*;
import br.com.ieptbto.cra.enumeration.TipoRegistroDesistenciaProtesto;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.FabricaRegistroDesistenciaProtesto;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
			CabecalhoArquivo cabecalhoArquivo = new ConversorCabecalhoArquivoDesistenciaCancelamento().converter(CabecalhoArquivo.class, cabecalhoVO);
			remessa.setCabecalho(cabecalhoArquivo);

		} else if (TipoRegistroDesistenciaProtesto.HEADER_CARTORIO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			desistenciaProtesto = new DesistenciaProtesto();
			desistenciaProtesto.setDesistencias(new ArrayList<PedidoDesistencia>());
			CabecalhoCartorioDesistenciaProtestoVO cabecalhoCartorioVO = CabecalhoCartorioDesistenciaProtestoVO.class.cast(registro);
			CabecalhoCartorio cabecalhoCartorio = new ConversorCabecalhoCartorioDesistenciaCancelamento().converter(CabecalhoCartorio.class, cabecalhoCartorioVO);
			desistenciaProtesto.setCabecalhoCartorio(cabecalhoCartorio);

		} else if (TipoRegistroDesistenciaProtesto.REGISTRO_PEDIDO_DESISTENCIA.getConstante().equals(registro.getIdentificacaoRegistro())) {
			RegistroDesistenciaProtestoVO tituloDesistenciaProtesto = RegistroDesistenciaProtestoVO.class.cast(registro);
			PedidoDesistencia pedidoDesistencia = new ConversorRegistroDesistenciaProtesto().converter(PedidoDesistencia.class, tituloDesistenciaProtesto);
			desistenciaProtesto.getDesistencias().add(pedidoDesistencia);
			pedidoDesistencia.setDesistenciaProtesto(desistenciaProtesto);

		} else if (TipoRegistroDesistenciaProtesto.TRAILLER_CARTORIO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			RodapeCartorioDesistenciaProtestoVO rodapeCartorioVO = RodapeCartorioDesistenciaProtestoVO.class.cast(registro);
			RodapeCartorio rodapeCartorio = new ConversorRodapeCartorioDesistenciaCancelamento().converter(RodapeCartorio.class, rodapeCartorioVO);
			desistenciaProtesto.setRodapeCartorio(rodapeCartorio);
			remessa.getDesistenciaProtesto().add(desistenciaProtesto);
			desistenciaProtesto.setRemessaDesistenciaProtesto(remessa);

		} else if (TipoRegistroDesistenciaProtesto.TRAILLER_APRESENTANTE.getConstante().equals(registro.getIdentificacaoRegistro())) {
			RodapeArquivoDesistenciaProtestoVO rodapeArquivoVO = RodapeArquivoDesistenciaProtestoVO.class.cast(registro);
			RodapeArquivo rodapeArquivo = new ConversorRodapeArquivoDesistenciaCancelamento().converter(RodapeArquivo.class, rodapeArquivoVO);
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