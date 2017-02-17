package br.com.ieptbto.cra.mediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.convenio.ConversorArquivoFiliado;
import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.LayoutFiliadoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class ArquivoFiliadoMediator extends BaseMediator {

	@Autowired
	TipoArquivoMediator tipoArquivoMediator;
	@Autowired
	ConversorArquivoFiliado conversorArquivoFiliado;
	@Autowired
	ArquivoDAO arquivoDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;
	@Autowired
	LayoutFiliadoDAO layoutFiliadoDao;
	
	private Usuario usuario;
	private Arquivo arquivo;
	private List<Exception> erros;

	public ArquivoFiliadoMediator salvarArquivo(FileUploadField file, Usuario usuario) {
		if (!verificarPermissaoDeEnvio(usuario, arquivo)) {
			logger.error("O usuário " + usuario.getNome() + " não pode enviar arquivos " + file.getInputName());
			throw new InfraException("O usuário " + usuario.getNome() + " não pode enviar arquivos " + file.getInputName());
		}

		if (!verificarSeInstituicaoPossuiLayout(usuario.getInstituicao())) {
			logger.error("Olá " + usuario.getNome() + ", Não existe layout cadastrado para sua instituicao...");
			throw new InfraException("Olá " + usuario.getNome() + ", Não há layout cadastrado para sua instituicao");
		}
		setUsuario(usuario);
		setArquivo(new Arquivo());
		this.arquivo.setNomeArquivo(getNomeArquivo());
		this.arquivo.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoFebraban.REMESSA));
		this.arquivo.setInstituicaoRecebe(instituicaoDAO.buscarInstituicaoPorNomeFantasia(TipoInstituicaoCRA.CRA.toString()));
		this.arquivo.setUsuarioEnvio(usuario);
		this.arquivo.setRemessas(new ArrayList<Remessa>());
		this.arquivo.setHoraEnvio(new LocalTime());
		this.arquivo.setDataEnvio(new LocalDate());
		this.arquivo.setDataRecebimento(new LocalDate().toDate());
		this.arquivo.setInstituicaoEnvio(usuario.getInstituicao());
		this.arquivo.setStatusArquivo(getStatusArquivoEnviado());

		if (verificarSeArquivoJaEnviado(getArquivo())) {

		}
		conversorArquivoFiliado.converter(file, getUsuario(), getArquivo(), getErros());
		setArquivo(arquivoDAO.salvar(getArquivo(), usuario, getErros()));
		return this;
	}

	private boolean verificarSeArquivoJaEnviado(Arquivo arquivo2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 
	 * @param instituicao
	 * @return true se exitir layout para a instituição
	 */
	private boolean verificarSeInstituicaoPossuiLayout(Instituicao instituicao) {
		return layoutFiliadoDao.isLayout(instituicao);
	}

	private String getNomeArquivo() {
		String nome = TipoArquivoFebraban.REMESSA.getConstante();
		nome = nome.concat(getUsuario().getInstituicao().getCodigoCompensacao());
		nome = nome.concat(DataUtil.getDataAtual(new SimpleDateFormat("ddMM")));
		nome = nome.concat("." + DataUtil.getDataAtual(new SimpleDateFormat("YY")) + "1");
		return nome;
	}

	private StatusArquivo getStatusArquivoEnviado() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setStatusDownload(StatusDownload.ENVIADO);
		return status;
	}

	private boolean verificarPermissaoDeEnvio(Usuario user, Arquivo arquivo) {
		if (TipoInstituicaoCRA.CONVENIO.equals(user.getInstituicao().getTipoInstituicao().getTipoInstituicao())
				&& user.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.LAYOUT_PERSONALIZADO_CONVENIOS)) {
			return true;
		}
		return false;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<>();
		}
		return erros;
	}

	public void setErros(List<Exception> erros) {
		this.erros = erros;
	}

}
