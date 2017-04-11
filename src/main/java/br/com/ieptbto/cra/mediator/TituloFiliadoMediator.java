package br.com.ieptbto.cra.mediator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.model.util.ListModel;
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
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DecoderString;
import br.com.ieptbto.cra.util.ZipFile;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class TituloFiliadoMediator extends BaseMediator {

	@Autowired
	private TituloFiliadoDAO tituloFiliadoDAO;
	
	private List<FileUpload> filesUpload;
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
	public TituloFiliado salvarTituloConvenio(Usuario usuario, TituloFiliado titulo, ListModel<FileUpload> uploadedFiles) {
		this.filesUpload = uploadedFiles.getObject();
		this.pathInstituicaoTemp = null;
		this.pathUsuarioTemp = null;

		try {
			if (filesUpload != null && !filesUpload.isEmpty()) {
				File fileTmp = verificarDiretorioECopiarArquivo(usuario, titulo);

				byte[] conteudoArquivo = DecoderString.loadFile(fileTmp);
				titulo.setAnexo(Base64.encodeBase64(conteudoArquivo));
			}
			titulo = tituloFiliadoDAO.salvar(titulo);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Não foi possível salvar os dados do título. Favor entrar em contato com o IEPTB-TO...");
		}
		return titulo;
	}

	private File verificarDiretorioECopiarArquivo(Usuario usuario, TituloFiliado titulo) {
		pathInstituicaoTemp = ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO_TEMP + usuario.getInstituicao().getId();
		pathUsuarioTemp = pathInstituicaoTemp + ConfiguracaoBase.BARRA + usuario.getId();
		File diretorioInstituicaoTemp = new File(pathInstituicaoTemp);
		File diretorioUsuarioTemp = new File(pathUsuarioTemp);

		try {
			if (diretorioInstituicaoTemp.exists()) {
				diretorioInstituicaoTemp.delete();
			}
			diretorioInstituicaoTemp.mkdirs();
			if (diretorioUsuarioTemp.exists()) {
				for (File oldFile : diretorioUsuarioTemp.listFiles()) {
					oldFile.delete();
				}
			} else {
				diretorioUsuarioTemp.mkdirs();
			}

			for (FileUpload file : filesUpload) {
				File fileTmp = new File(pathUsuarioTemp + ConfiguracaoBase.BARRA + file.getClientFileName());
				file.writeTo(fileTmp);
			}
			File zipFile = new File(pathUsuarioTemp + ConfiguracaoBase.BARRA + titulo.getNomeDevedor().replace(" ", "_").replace("/", "") 
					+ ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);
			if (zipFile.exists()) {
				zipFile.delete();
			}
			zipFile.createNewFile();
			
			byte[] zipOutput = ZipFile.zipFiles(pathUsuarioTemp, titulo.getNomeDevedor().replace(" ", "_").replace("/", "") 
					+ ConfiguracaoBase.EXTENSAO_ARQUIVO_ZIP);
            FileOutputStream fout1 = new FileOutputStream(zipFile);
            fout1.write(zipOutput);
            fout1.close();
            return zipFile;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Não foi possível criar arquivo temporário do anexo! Por favor entre em contato com o IEPTB-TO.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Não foi possível criar arquivo temporário do anexo! Por favor entre em contato com o IEPTB-TO.");
		}
	}

	public TituloFiliado buscarTituloFiliadoProcessadoNaCra(String nossoNumero, String numeroTitulo) {
		return tituloFiliadoDAO.buscarTituloFiliadoProcessadoNaCra(nossoNumero, numeroTitulo);
	}

	public TituloRemessa buscarTituloDoConvenioNaCra(TituloFiliado tituloFiliado) {
		return tituloFiliadoDAO.buscarTituloDoConvenioNaCra(tituloFiliado);
	}

	public List<TituloFiliado> buscarTitulosParaEnvio(UsuarioFiliado usuarioFiliado, SetorFiliado setor) {
		if (usuarioFiliado == null) {
			 return new ArrayList<TituloFiliado>();
		}
		return tituloFiliadoDAO.buscarTitulosParaEnvio(usuarioFiliado.getFiliado(), setor);
	}
	
	public List<TituloFiliado> buscarTitulosParaEnvio(Filiado filiado, SetorFiliado setor) {
		return tituloFiliadoDAO.buscarTitulosParaEnvio(filiado, setor);
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

	public List<TituloRemessa> buscarListaTitulos(Usuario user, LocalDate dataInicio, LocalDate dataFim, Instituicao instiuicaoCartorio, String numeroTitulo, 
			String nomeDevedor, String documentoDevedor, String nuumeroProtocolo, String codigoFiliado) {
		return tituloFiliadoDAO.buscarListaTitulos(user, dataInicio, dataFim, instiuicaoCartorio, numeroTitulo, nomeDevedor, documentoDevedor, nuumeroProtocolo, codigoFiliado);
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