package br.com.ieptbto.cra.mediator;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TituloFiliadoDAO;
import br.com.ieptbto.cra.entidade.Avalista;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.SetorFiliado;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DecoderString;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class TituloFiliadoMediator extends BaseMediator {

	@Autowired
	private TituloFiliadoDAO tituloFiliadoDAO;

	private FileUpload fileUpload;
	private String pathInstituicaoTemp;
	private String pathUsuarioTemp;

	/**
	 * Salvar título convenio
	 * 
	 * @param usuario
	 * @param titulo
	 * @param uploadedFile
	 * @return
	 */
	public TituloFiliado salvarTituloConvenio(Usuario usuario, TituloFiliado titulo, FileUpload uploadedFile) {
		this.fileUpload = uploadedFile;
		this.pathInstituicaoTemp = null;
		this.pathUsuarioTemp = null;

		try {
			if (fileUpload != null) {
				File fileTmp = verificarDiretorioECopiarArquivo(usuario);

				byte[] conteudoArquivo = DecoderString.loadFile(fileTmp);
				titulo.setAnexo(Base64.encodeBase64(conteudoArquivo));
			}
			titulo = tituloFiliadoDAO.salvar(titulo);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return titulo;
	}

	private File verificarDiretorioECopiarArquivo(Usuario usuario) {
		pathInstituicaoTemp = ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO_TEMP + usuario.getInstituicao().getId();
		pathUsuarioTemp = pathInstituicaoTemp + ConfiguracaoBase.BARRA + usuario.getId();
		File diretorioInstituicaoTemp = new File(pathInstituicaoTemp);
		File diretorioUsuarioTemp = new File(pathUsuarioTemp);

		if (diretorioInstituicaoTemp.exists()) {
			diretorioInstituicaoTemp.delete();
		}
		diretorioInstituicaoTemp.mkdirs();
		if (diretorioUsuarioTemp.exists()) {
			diretorioUsuarioTemp.delete();
		}
		diretorioUsuarioTemp.mkdirs();

		File fileTmp = new File(pathUsuarioTemp + ConfiguracaoBase.BARRA + usuario.getId());
		try {
			fileUpload.writeTo(fileTmp);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Não foi possível criar arquivo temporário do anexo! Por favor entre em contato com o IEPTB-TO.");
		}
		return fileTmp;
	}

	public TituloFiliado buscarTituloFiliadoProcessadoNaCra(String nossoNumero, String numeroTitulo) {
		return tituloFiliadoDAO.buscarTituloFiliadoProcessadoNaCra(nossoNumero, numeroTitulo);
	}

	public TituloRemessa buscarTituloDoConvenioNaCra(TituloFiliado tituloFiliado) {
		return tituloFiliadoDAO.buscarTituloDoConvenioNaCra(tituloFiliado);
	}

	public List<TituloFiliado> buscarTitulosParaEnvio(Filiado empresaFiliada, SetorFiliado setor) {
		return tituloFiliadoDAO.buscarTitulosParaEnvio(empresaFiliada, setor);
	}

	public void removerTituloFiliado(TituloFiliado titulo) {
		tituloFiliadoDAO.removerTituloFiliado(titulo);
	}

	public void enviarTitulosPendentes(List<TituloFiliado> listaTitulosFiliado) {
		tituloFiliadoDAO.enviarTitulosPendentes(listaTitulosFiliado);
	}

	public List<Avalista> buscarAvalistasPorTitulo(TituloFiliado titulo) {
		return tituloFiliadoDAO.avalistasTituloFiliado(titulo);
	}

	public List<TituloRemessa> buscarListaTitulos(Usuario user, LocalDate dataInicio, Instituicao instiuicaoCartorio, String numeroTitulo, String nomeDevedor,
			String documentoDevedor, String codigoFiliado) {
		return tituloFiliadoDAO.buscarListaTitulos(user, dataInicio, instiuicaoCartorio, numeroTitulo, nomeDevedor, documentoDevedor, codigoFiliado);
	}

	public List<TituloFiliado> buscarTitulosParaRelatorioFiliado(Filiado filiado, LocalDate dataInicio, LocalDate dataFim,
			SituacaoTituloRelatorio tipoRelatorio, Municipio pracaProtesto) {
		return tituloFiliadoDAO.buscarTitulosParaRelatorioFiliado(filiado, dataInicio, dataFim, tipoRelatorio, pracaProtesto);
	}

	public List<TituloFiliado> buscarTitulosParaRelatorioConvenio(Instituicao convenio, Filiado filiado, LocalDate dataInicio, LocalDate dataFim,
			Municipio pracaProtesto, SituacaoTituloRelatorio tipoRelatorio) {
		return tituloFiliadoDAO.buscarTitulosParaRelatorioConvenio(convenio, filiado, dataInicio, dataFim, pracaProtesto);
	}

	public int quatidadeTitulosPendentesEnvioFiliados(Filiado filiado, LocalDate dataInicio, LocalDate dataFim) {
		return tituloFiliadoDAO.quatidadeTitulosPendentesEnvioFiliados(filiado, dataInicio, dataFim);
	}

	public int quatidadeTitulosEmProcessoFiliados(Filiado filiado, LocalDate dataInicio, LocalDate dataFim) {
		return tituloFiliadoDAO.quatidadeTitulosEmProcessoFiliados(filiado, dataInicio, dataFim);
	}

	public int quatidadeTitulosFinalizadosFiliados(Filiado filiado, LocalDate dataInicio, LocalDate dataFim) {
		return tituloFiliadoDAO.quatidadeTitulosFnalizados(filiado, dataInicio, dataFim);
	}
}