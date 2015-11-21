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

import br.com.ieptbto.cra.conversor.arquivo.filiado.ConversorArquivoFiliado;
import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class ArquivoFiliadoMediator {

	@Autowired
	TipoArquivoMediator tipoArquivoMediator;
	@Autowired
	ConversorArquivoFiliado conversorArquivoFiliado;
	@Autowired
	ArquivoDAO arquivoDAO;

	private Usuario usuario;
	private Arquivo arquivo;
	private List<Exception> erros;

	public ArquivoFiliadoMediator salvarArquivo(FileUploadField file, Usuario usuario) {
		setUsuario(usuario);
		setArquivo(new Arquivo());
		getArquivo().setNomeArquivo(getNomeArquivo());
		getArquivo().setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.REMESSA));
		getArquivo().setInstituicaoEnvio(getUsuario().getInstituicao());
		getArquivo().setUsuarioEnvio(getUsuario());
		getArquivo().setDataEnvio(new LocalDate());
		getArquivo().setHoraEnvio(new LocalTime());
		getArquivo().setStatusArquivo(getStatusArquivoEnviado());

		conversorArquivoFiliado.converter(file, getUsuario(), getArquivo(), getErros());

		setArquivo(arquivoDAO.salvar(getArquivo(), usuario, getErros()));

		return this;
	}

	private String getNomeArquivo() {
		String nome = TipoArquivoEnum.REMESSA.getConstante();
		nome = nome.concat(getUsuario().getInstituicao().getCodigoCompensacao());
		nome = nome.concat(DataUtil.getDataAtual(new SimpleDateFormat("ddMM")));
		nome = nome.concat("." + DataUtil.getDataAtual(new SimpleDateFormat("YY")) + "1");
		return nome;
	}

	private StatusArquivo getStatusArquivoEnviado() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new LocalDateTime());
		status.setSituacaoArquivo(SituacaoArquivo.ENVIADO);
		return status;
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
