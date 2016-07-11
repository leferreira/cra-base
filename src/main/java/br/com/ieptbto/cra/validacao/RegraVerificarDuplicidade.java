package br.com.ieptbto.cra.validacao;

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
import br.com.ieptbto.cra.validacao.regra.RegrasDeEntrada;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RegraVerificarDuplicidade extends RegrasDeEntrada {

	@Autowired
	private InstituicaoMediator instituicaoMediator;
	@Autowired
	private ArquivoDAO arquivoDAO;
	private Usuario usuario;
	private Arquivo arquivo;
	private Instituicao instituicaoEnvio;

	@Override
	public void validar(Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		setUsuario(usuario);
		setErros(erros);
		setArquivo(arquivo);

		executar();
	}

	@Override
	protected void executar() {
		verificarDuplicidade();
	}

	private void verificarDuplicidade() {
		TipoArquivoEnum tipoArquivo = null;

		if (getArquivo().getNomeArquivo().length() == 12) {
			tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getNomeArquivo().substring(0, 1));
		} else if (getArquivo().getNomeArquivo().length() == 13) {
			tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getNomeArquivo().substring(0, 2));
		} else {
			throw new InfraException("Não foi possível identificar o tipo do arquivo ! Verifique o nome do arquivo !");
		}

		setInstituicaoEnvio(buscarInstituicaoEnvioArquivo(tipoArquivo));
		verificarExistencia();
	}

	private Instituicao buscarInstituicaoEnvioArquivo(TipoArquivoEnum tipoArquivo) {

		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
			return instituicaoMediator.getInstituicaoPorCodigoPortador(getArquivo().getNomeArquivo().substring(1, 4));
		} else if (tipoArquivo.equals(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO) || tipoArquivo.equals(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO)) {
			return instituicaoMediator.getInstituicaoPorCodigoPortador(getArquivo().getNomeArquivo().substring(2, 5));
		} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO) || tipoArquivo.equals(TipoArquivoEnum.RETORNO)
				|| tipoArquivo.equals(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO)) {
			return instituicaoMediator.getCartorioPorCodigoIBGE(getArquivo().getRemessas().get(0).getCabecalho().getCodigoMunicipio());
		} else {
			throw new InfraException("Não foi possível validar a duplicidade do arquivo !");
		}
	}

	private void verificarExistencia() {
		if (arquivoDAO.buscarArquivosPorNomeArquivoInstituicaoEnvio(getInstituicaoEnvio(), getArquivo().getNomeArquivo()) != null) {
			throw new InfraException(
					"Não foi possível importar, pois, o arquivo [ " + getArquivo().getNomeArquivo() + " ] já foi enviado para a CRA !");
		}
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Instituicao getInstituicaoEnvio() {
		return instituicaoEnvio;
	}

	public void setInstituicaoEnvio(Instituicao instituicaoEnvio) {
		this.instituicaoEnvio = instituicaoEnvio;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}
}
