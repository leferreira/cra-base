package br.com.ieptbto.cra.validacao;

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
	private File arquivo;
	private Arquivo arquivoProcessado;
	private Instituicao instituicaoEnvio;
	
	@Override
	public void validar(File arquivo, Arquivo arquivoProcessado, Usuario usuario, List<Exception> erros) {
		setArquivo(arquivo);
		setUsuario(usuario);
		setErros(erros);
		setArquivoProcessado(arquivoProcessado);
		
		executar();
	}

	@Override
	protected void executar() {
		verificarDuplicidade();
	}
	
	private void verificarDuplicidade() {
		TipoArquivoEnum tipoArquivo = null;
		
		if (getArquivo().getName().length() == 12) {
			tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getName().substring(0, 1));
		} else if (getArquivo().getName().length() == 13) { 
			tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getName().substring(0, 2));
		} else {
			throw new InfraException("Não foi possível identificar o tipo do arquivo ! Verifique o nome do arquivo !");
		}
		
		setInstituicaoEnvio(buscarInstituicaoEnvioArquivo(tipoArquivo));
		verificarExistencia();
	}

	private Instituicao buscarInstituicaoEnvioArquivo(TipoArquivoEnum tipoArquivo) {

		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
			return instituicaoMediator.getInstituicaoPorCodigoPortador(getArquivo().getName().substring(1, 4));			
		} else if (tipoArquivo.equals(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO)
				|| tipoArquivo.equals(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO)) {
			return instituicaoMediator.getInstituicaoPorCodigoPortador(getArquivo().getName().substring(2, 5));
		} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO) 
				|| tipoArquivo.equals(TipoArquivoEnum.RETORNO)
				|| tipoArquivo.equals(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO)) {
			return instituicaoMediator.getCartorioPorCodigoIBGE(getArquivoProcessado().getRemessas().get(0).getCabecalho().getCodigoMunicipio());
		} else {
			throw new InfraException("Não foi possível validar a duplicidade do arquivo !");
		}
	}
	
	private void verificarExistencia() {
		if (arquivoDAO.buscarArquivosPorNomeArquivoInstituicaoEnvio(getInstituicaoEnvio(), getArquivo().getName()) != null) {
			throw new InfraException("Não foi possível importar, pois, o arquivo [ "+ getArquivo().getName()
					+" ] já foi enviado para a CRA !");
		}
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public File getArquivo() {
		return arquivo;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setArquivo(File arquivo) {
		this.arquivo = arquivo;
	}

	public Instituicao getInstituicaoEnvio() {
		return instituicaoEnvio;
	}

	public void setInstituicaoEnvio(Instituicao instituicaoEnvio) {
		this.instituicaoEnvio = instituicaoEnvio;
	}

	public Arquivo getArquivoProcessado() {
		return arquivoProcessado;
	}

	public void setArquivoProcessado(Arquivo arquivoProcessado) {
		this.arquivoProcessado = arquivoProcessado;
	}
}
