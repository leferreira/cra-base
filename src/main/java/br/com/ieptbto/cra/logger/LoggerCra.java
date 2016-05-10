package br.com.ieptbto.cra.logger;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.LogCra;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoAcaoLog;
import br.com.ieptbto.cra.enumeration.TipoLog;
import br.com.ieptbto.cra.mediator.LoggerMediator;

/**
 * @author Thasso Aráujo
 *
 */
@Service
public class LoggerCra {

	@Autowired
	LoggerMediator loggerMediator;

	public LogCra alert(Usuario user, TipoAcaoLog acao, String descricao) {
		LogCra logCra = new LogCra();
		logCra.setAcao(acao);
		logCra.setDescricao(descricao);
		logCra.setTipoLog(TipoLog.ALERTA);
		logCra.setData(new LocalDate());
		logCra.setHora(new LocalTime());
		logCra.setUsuario(user.getNome());
		logCra.setInstituicao(user.getInstituicao().getNomeFantasia());
		return loggerMediator.salvar(logCra);
	}

	public LogCra error(TipoAcaoLog acao, String descricao) {
		LogCra logCra = new LogCra();
		logCra.setAcao(acao);
		logCra.setDescricao(descricao);
		logCra.setTipoLog(TipoLog.OCORRENCIA_ERRO);
		logCra.setData(new LocalDate());
		logCra.setHora(new LocalTime());
		return loggerMediator.salvar(logCra);
	}

	public LogCra error(Usuario user, TipoAcaoLog acao, String descricao) {
		LogCra logCra = new LogCra();
		logCra.setAcao(acao);
		logCra.setDescricao(descricao);
		logCra.setTipoLog(TipoLog.OCORRENCIA_ERRO);
		logCra.setData(new LocalDate());
		logCra.setHora(new LocalTime());
		logCra.setUsuario(user.getNome());
		logCra.setInstituicao(user.getInstituicao().getNomeFantasia());
		return loggerMediator.salvar(logCra);
	}

	public LogCra error(Usuario user, TipoAcaoLog acao, String descricao, Exception ex) {
		LogCra logCra = new LogCra();
		logCra.setAcao(acao);
		logCra.setDescricao(descricao);
		logCra.setTipoLog(TipoLog.OCORRENCIA_ERRO);
		logCra.setExcecao(ex);
		logCra.setData(new LocalDate());
		logCra.setHora(new LocalTime());
		logCra.setUsuario(user.getNome());
		logCra.setInstituicao(user.getInstituicao().getNomeFantasia());
		return loggerMediator.salvar(logCra);
	}

	public LogCra sucess(Usuario user, TipoAcaoLog acao, String descricao) {
		LogCra logCra = new LogCra();
		logCra.setAcao(acao);
		logCra.setDescricao(descricao);
		logCra.setTipoLog(TipoLog.SUCESSO);
		logCra.setData(new LocalDate());
		logCra.setHora(new LocalTime());
		logCra.setUsuario(user.getNome());
		logCra.setInstituicao(user.getInstituicao().getNomeFantasia());
		return loggerMediator.salvar(logCra);
	}

	public LogCra sucess(Instituicao instituicao, Usuario user, TipoAcaoLog acao, String descricao) {
		LogCra logCra = new LogCra();
		logCra.setAcao(acao);
		logCra.setDescricao(descricao);
		logCra.setTipoLog(TipoLog.SUCESSO);
		logCra.setData(new LocalDate());
		logCra.setHora(new LocalTime());
		logCra.setUsuario(user.getNome());
		logCra.setInstituicao(instituicao.getNomeFantasia());
		return loggerMediator.salvar(logCra);
	}
}
