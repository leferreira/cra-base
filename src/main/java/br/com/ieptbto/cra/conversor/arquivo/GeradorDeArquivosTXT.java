package br.com.ieptbto.cra.conversor.arquivo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.DesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RegistroDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaDesistenciaProtestoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.FabricaDeRegistroTXT;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class GeradorDeArquivosTXT extends Gerador {

	public File gerar(RemessaDesistenciaProtestoVO remessaVO, File arquivoFisico) {
		try {
			BufferedWriter bWrite = new BufferedWriter(new FileWriter(arquivoFisico));
			Integer linha = 1;

			// Cabecalho do Arquivo DP
			remessaVO.getCabecalhoArquivo().setSequencialRegistro(linha.toString());
			linha++;
			bWrite.write(gerarLinhaDesistenciaProtesto(remessaVO.getCabecalhoArquivo()));
			bWrite.newLine();

			for (DesistenciaProtestoVO pedido : remessaVO.getPedidoDesistencias()) {
				pedido.getCabecalhoCartorio().setSequencialRegistro(linha.toString());
				linha++;
				bWrite.write(gerarLinhaDesistenciaProtesto(pedido.getCabecalhoCartorio()));
				bWrite.newLine();

				for (RegistroDesistenciaProtestoVO registro : pedido.getRegistroDesistenciaProtesto()) {
					registro.setSequenciaRegistro(linha.toString());
					linha++;
					bWrite.write(gerarLinhaDesistenciaProtesto(registro));
					bWrite.newLine();
				}

				// Rodape do Cartório DP
				pedido.getRodapeCartorio().setSequencialRegistro(linha.toString());
				linha++;
				bWrite.write(gerarLinhaDesistenciaProtesto(pedido.getRodapeCartorio()));
				bWrite.newLine();
			}
			remessaVO.getRodapeArquivo().setSequencialRegistro(linha.toString());
			linha++;
			// Rodapé do Arquivo DP
			bWrite.write(gerarLinhaDesistenciaProtesto(remessaVO.getRodapeArquivo()));
			bWrite.newLine();

			bWrite.flush();
			bWrite.close();

		} catch (IOException e) {
			logger.error(e);
			new InfraException("Não foi possível gerar o arquivo DP físico");
		}
		return arquivoFisico;
	}

	public void gerar(RemessaVO remessaVO, File arquivoTXT) {
		try {
			Map<Integer, String> titulos = new HashMap<Integer, String>();
			BufferedWriter bWrite = new BufferedWriter(new FileWriter(arquivoTXT));

			bWrite.write(gerarLinha(remessaVO.getCabecalho()));
			bWrite.newLine();
			for (TituloVO tituloVO : remessaVO.getTitulos()) {
				titulos.put(Integer.parseInt(tituloVO.getNumeroSequencialArquivo()), gerarLinhaTitulo(tituloVO));
			}

			for (int i = 2; i < titulos.keySet().size() + 2; i++) {
				bWrite.write(titulos.get(i));
				bWrite.newLine();
			}

			bWrite.write(gerarLinhaRodape(remessaVO.getRodape()));
			bWrite.newLine();

			bWrite.flush();
			bWrite.close();

		} catch (IOException e) {
			logger.error(e);
			new InfraException("Não foi possível gerar o arquivo TXT físico");
		}

	}

	public void gerar(List<RemessaVO> remessasVO, File arquivoTXT) {
		try {
			Map<Integer, String> titulos = new HashMap<Integer, String>();
			BufferedWriter bWrite = new BufferedWriter(new FileWriter(arquivoTXT));

			for (RemessaVO remessaVO : remessasVO) {
				bWrite.write(gerarLinha(remessaVO.getCabecalho()));
				bWrite.newLine();
				for (TituloVO tituloVO : remessaVO.getTitulos()) {
					titulos.put(Integer.parseInt(tituloVO.getNumeroSequencialArquivo()), gerarLinhaTitulo(tituloVO));
				}

				for (int i = 2; i < titulos.keySet().size() + 2; i++) {
					bWrite.write(titulos.get(i));
					bWrite.newLine();
				}

				bWrite.write(gerarLinhaRodape(remessaVO.getRodape()));
				bWrite.newLine();
			}

			bWrite.flush();
			bWrite.close();
		} catch (IOException e) {
			logger.error(e);
			new InfraException("Não foi possível gerar o arquivo TXT físico");
		}

	}

	private String gerarLinhaRodape(RodapeVO rodape) {
		return FabricaDeRegistroTXT.getLinha(rodape);
	}

	private String gerarLinhaTitulo(TituloVO tituloVO) {
		return FabricaDeRegistroTXT.getLinha(tituloVO);
	}

	private String gerarLinha(CabecalhoVO cabecalhoVO) {
		return FabricaDeRegistroTXT.getLinha(cabecalhoVO);
	}

	private <T extends AbstractArquivoVO> String gerarLinhaDesistenciaProtesto(T cabecalhoVO) {
		return FabricaDeRegistroTXT.getLinhaDesistenciaProtesto(cabecalhoVO);
	}

}
