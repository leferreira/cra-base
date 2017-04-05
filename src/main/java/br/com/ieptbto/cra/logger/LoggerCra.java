package br.com.ieptbto.cra.logger;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.LogCra;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.TipoLog;
import br.com.ieptbto.cra.mediator.LoggerMediator;
import br.com.ieptbto.cra.util.XmlFormatterUtil;

/**
 * @author Thasso Aráujo
 *
 */
@Service
public class LoggerCra {

	@Autowired
	private LoggerMediator loggerMediator;
	protected static final Logger logger = Logger.getLogger(LoggerCra.class);

	public LogCra alert(Usuario user, CraAcao acao, String descricao) {
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

	public LogCra error(CraAcao acao, String descricao) {
		LogCra logCra = new LogCra();
		logCra.setAcao(acao);
		logCra.setDescricao(descricao);
		logCra.setTipoLog(TipoLog.OCORRENCIA_ERRO);
		logCra.setData(new LocalDate());
		logCra.setHora(new LocalTime());
		return loggerMediator.salvar(logCra);
	}

	public LogCra error(Usuario user, CraAcao acao, String descricao) {
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

	public LogCra error(Usuario user, CraAcao acao, String descricao, String dados) {
		LogCra logCra = new LogCra();
		logCra.setAcao(acao);
		logCra.setDescricao(descricao);
		logCra.setTipoLog(TipoLog.OCORRENCIA_ERRO);
		logCra.setData(new LocalDate());
		logCra.setHora(new LocalTime());
		logCra.setUsuario(user.getNome());
		logCra.setInstituicao(user.getInstituicao().getNomeFantasia());
		logger.info(XmlFormatterUtil.format(dados));
		return loggerMediator.salvar(logCra);
	}

	public LogCra error(Usuario user, CraAcao acao, String descricao, Exception ex, String dados) {
		LogCra logCra = new LogCra();
		logCra.setAcao(acao);
		logCra.setDescricao(descricao);
		logCra.setTipoLog(TipoLog.OCORRENCIA_ERRO);
		logCra.setExcecao(ex);
		logCra.setData(new LocalDate());
		logCra.setHora(new LocalTime());
		logCra.setUsuario(user.getNome());
		logCra.setInstituicao(user.getInstituicao().getNomeFantasia());
		logger.info(XmlFormatterUtil.format(dados));
		return loggerMediator.salvar(logCra);
	}

    public LogCra error(Usuario user, CraAcao acao, String descricao, Exception ex) {
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

	public LogCra sucess(Usuario user, CraAcao acao, String descricao) {
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

    public LogCra sucess(Usuario user, CraAcao acao, String relatorio, String descricao) {
        LogCra logCra = new LogCra();
        logCra.setAcao(acao);
        logCra.setDescricao(descricao);
        logCra.setTipoLog(TipoLog.SUCESSO);
        logCra.setData(new LocalDate());
        logCra.setRelatorio(relatorio);
        logCra.setHora(new LocalTime());
        logCra.setUsuario(user.getNome());
        logCra.setInstituicao(user.getInstituicao().getNomeFantasia());
        return loggerMediator.salvar(logCra);
    }
}