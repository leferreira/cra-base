package br.com.ieptbto.cra.regra.entrada;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RegraDuplicidade extends RegraEntrada {

	@Autowired
	private InstituicaoMediator instituicaoMediator;
	@Autowired
	private ArquivoDAO arquivoDAO;

	private Instituicao instituicaoEnvio;

	@Override
	public void validar(File file, Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.file = file;
		this.usuario = usuario;
		this.erros = erros;
		this.arquivo = arquivo;

		executar();
	}

	@Override
	protected void executar() {
		verificarDuplicidade();
	}

	private void verificarDuplicidade() {
		if (this.file != null) {

			TipoArquivoEnum tipoArquivo = null;

			if (arquivo.getNomeArquivo().length() == 12) {
				tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(arquivo.getNomeArquivo().substring(0, 1));
			} else if (arquivo.getNomeArquivo().length() == 13) {
				tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(arquivo.getNomeArquivo().substring(0, 2));
			} else {
				throw new InfraException("Não foi possível identificar o tipo do arquivo ! Verifique o nome do arquivo !");
			}

			this.instituicaoEnvio = buscarInstituicaoEnvioArquivo(tipoArquivo);
			verificarExistencia();
		}
	}

	private Instituicao buscarInstituicaoEnvioArquivo(TipoArquivoEnum tipoArquivo) {

		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
			return instituicaoMediator.getInstituicaoPorCodigoPortador(arquivo.getNomeArquivo().substring(1, 4));
		} else if (tipoArquivo.equals(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO) || tipoArquivo.equals(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO)
				|| tipoArquivo.equals(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO)) {
			return instituicaoMediator.getInstituicaoPorCodigoPortador(arquivo.getNomeArquivo().substring(2, 5));
		} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO) || tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
			return instituicaoMediator.getCartorioPorCodigoIBGE(arquivo.getRemessas().get(0).getCabecalho().getCodigoMunicipio());
		} else {
			throw new InfraException("Não foi possível validar a duplicidade do arquivo !");
		}
	}

	private void verificarExistencia() {
		if (arquivoDAO.buscarArquivosPorNomeArquivoInstituicaoEnvio(instituicaoEnvio, arquivo.getNomeArquivo()) != null) {
			throw new InfraException("Não foi possível importar, pois, o arquivo [ " + arquivo.getNomeArquivo() + " ] já foi enviado para a CRA !");
		}
	}
}