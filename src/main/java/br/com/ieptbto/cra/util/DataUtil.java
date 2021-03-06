package br.com.ieptbto.cra.util;

import br.com.ieptbto.cra.exception.InfraException;
import org.apache.commons.lang.StringUtils;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utilitario para datas.
 */
@SuppressWarnings("serial")
public class DataUtil implements Serializable {

	/** {@link DateTimeZone} padrao. */
	public static final DateTimeZone ZONE = DateTimeZone.forID("America/Sao_Paulo");

	/** {@link Locale} padrao. */
	public static final Locale LOCALE = new Locale("Pt", "BR");

	/** Pattern padrao para formatacao de data/hora. */
	public static final String PADRAO_FORMATACAO_DATAHORASEG = "dd/MM/yyyy HH:mm:ss";

	/** Pattern padrao para formatacao de data/hora. */
	public static final String PADRAO_FORMATACAO_DATAHORA = "dd/MM/yyyy HH:mm";

	/** Pattern padrao para formatacao de hora. */
	public static final String PADRAO_FORMATACAO_HORA = "HH:mm";

	/** Pattern padrao para formatacao de data. */
	public static final String PADRAO_FORMATACAO_DATA = "dd/MM/yyyy";

	/** Pattern padrao para formatacao de data. */
	public static final String PADRAO_FORMATACAO_DATA_DDMMYYYY = "ddMMyyyy";
	
	public static final String PADRAO_FORMATACAO_DATA_YYYYMMDD = "yyyyMMdd";

	/** Pattern padrao para formatacao de hora. */
	public static final String PADRAO_FORMATACAO_HORA_HHMM = "HHmm";

	/** Formato padr�o para data */
	public static final String FORMATO_DATA_PADRAO_REGISTRO = PADRAO_FORMATACAO_DATA_DDMMYYYY;

	public static final String DATA_99999999 = "99999999";

	/**
	 * Cria um {@link LocalDate} a partir de uma {@link String} no formato
	 * {@link #PADRAO_FORMATACAO_DATAHORA}.
	 * 
	 * @param dataHoraString
	 *            data/hora no formato esperado
	 * @return {@link LocalDateTime}
	 */
	public static LocalDateTime stringToLocalDateTime(String dataHoraString) {
		return getDateTimeFormatter(PADRAO_FORMATACAO_DATAHORA).parseDateTime(dataHoraString).toLocalDateTime();
	}

	/**
	 * Cria um {@link LocalDateTime} a partir de uma {@link String} no formato
	 * informado.
	 * 
	 * @param formato
	 *            formato da string
	 * @param dataString
	 *            data/hora no formato esperado
	 * @return {@link LocalDateTime}
	 */
	public static LocalDateTime stringToLocalDateTime(String formato, String dataString) {
		return getDateTimeFormatter(formato).parseDateTime(dataString).toLocalDateTime();
	}

	/**
	 * Cria um {@link LocalDate} a partir de uma {@link String} no formato
	 * informado.
	 * 
	 * @param formato
	 *            formato da string
	 * @param dataString
	 *            data no formato esperado
	 * @return {@link LocalDate}
	 */
	public static LocalDate stringToLocalDate(String formato, String dataString) {
		return getDateFormatter(formato).parseDateTime(dataString).toLocalDate();
	}

	/**
	 * Cria um {@link LocalDate} a partir de uma {@link String} no formato
	 * {@link #PADRAO_FORMATACAO_DATA}.
	 * 
	 * @param dataString
	 *            data no formato esperado
	 * @return {@link LocalDate}
	 */
	public static LocalDate stringToLocalDate(String dataString) {
		if (dataString == null) {
			return null;
		}
		return getDateFormatter(PADRAO_FORMATACAO_DATA).parseDateTime(dataString).toLocalDate();
	}

	/**
	 * Converte {@link LocalDate} para {@link String} no formato
	 * {@link #PADRAO_FORMATACAO_DATA}.
	 * 
	 * @param localDate
	 *            data a ser convertida
	 * @return data formatada
	 */
	public static String localDateToString(LocalDate localDate) {
		if (localDate == null) {
			return null;
		}
		return localDate.toString(getDateTimeFormatter(PADRAO_FORMATACAO_DATA));
	}
	
	public static String localDateToString(LocalDate localDate, String format) {
		if (localDate == null) {
			return null;
		}
		return localDate.toString(getDateTimeFormatter(format));
	}

	public static String localTimeToString(LocalTime localTime) {
		if (localTime == null) {
			return null;
		}
		return localTime.toString(getDateTimeFormatter(PADRAO_FORMATACAO_HORA));
	}

	public static String localTimeToString(String format, LocalTime localTime) {
		if (localTime == null) {
			return null;
		}
		return localTime.toString(getDateTimeFormatter(format));
	}

	/**
	 * Converte {@link LocalDate} para {@link Date} no formato
	 * {@link #PADRAO_FORMATACAO_DATA}.
	 * 
	 * @param localDate
	 *            data a ser convertida
	 * @return data formatada
	 */
	public static Date localDateToDate(LocalDate localDate) {
		if (localDate == null) {
			return null;
		}
		return localDate.toDateTimeAtCurrentTime().toDate();
	}

	/**
	 * @param dataHoraString
	 * @return
	 */
	public static DateTime stringToDateTime(String dataHoraString) {
		if (dataHoraString == null) {
			return null;
		}
		return getDateTimeFormatter(PADRAO_FORMATACAO_DATAHORA).parseDateTime(dataHoraString).toDateTime();
	}

	public static DateTime stringToDateTime(String dataHoraString, String formato) {
		if (dataHoraString == null) {
			return null;
		}
		return getDateTimeFormatter(formato).parseDateTime(dataHoraString).toDateTime();
	}

	/**
	 * Cria um {@link String} a partir de uma {@link LocalDate} no formato
	 * {@link #PADRAO_FORMATACAO_DATA_DDMMYYYY}.
	 * 
	 * @param data
	 *            data a ser transformada
	 * @return {@link String} no formato yyyyMMdd
	 */

	public static String localDateToStringddMMyyyy(LocalDate data) {
		if (data == null) {
			return "";
		}
		return data.toString(getDateTimeFormatter(PADRAO_FORMATACAO_DATA_DDMMYYYY));
	}

	public static String localTimeToStringMMmm(LocalTime hora) {
		if (hora == null) {
			return "";
		}
		return hora.toString(getDateTimeFormatter(PADRAO_FORMATACAO_HORA_HHMM));
	}

	/**
	 * Converte {@link LocalDateTime} para {@link String} no formato
	 * {@link #PADRAO_FORMATACAO_DATAHORA}.
	 * 
	 * @param localDateTime
	 *            data/hora a ser convertida
	 * @return data formatada
	 */
	public static String localDateTimeToString(LocalDateTime localDateTime) {
		return localDateTime.toString(getDateTimeFormatter(PADRAO_FORMATACAO_DATAHORA));
	}

	/**
	 * 
	 * Converte {@link Date} para {@link LocalDate}.
	 * 
	 * @param data
	 * @return
	 */
	public static LocalDate dateToLocalDate(Date data) {
		return new LocalDate(data.getTime());
	}

	public static String formatarData(Date data) {
		if (data == null) {
			return StringUtils.EMPTY;
		}
		return new SimpleDateFormat("dd/MM/yyyy").format(data);
	}

	/**
	 * Verifica se a data passada como par�metro est� no per�odo determinado
	 * pela dataInicio e dataFim.
	 * 
	 * @param data
	 *            data a ser verificada
	 * @param dataInicio
	 *            data de in�cio do per�odo
	 * @param dataFim
	 *            data fim do per�odo
	 * @return
	 */
	public static boolean dataEstaNoPeriodo(LocalDate data, LocalDate dataInicio, LocalDate dataFim) {
		Interval periodo = new Interval(dataInicio.toDateTimeAtStartOfDay(), dataFim.plusDays(1).toDateTimeAtStartOfDay());
		return periodo.contains(data.toDateTimeAtStartOfDay());
	}

    /**
     * Verifica se a data inicial é menor ou igual a data final
     *
     * @param dataInicio
     * @param dataFim
     * @return
     */
    public static boolean validarPeriodoDataInicialFinal(Date dataInicio, Date dataFim) {
        if (dataInicio != null && dataFim != null) {
            if (!dataInicio.before(dataFim) && !dataInicio.equals(dataFim)) {
                throw new InfraException("A data de início deve ser anterior ou igual a data fim do período");
            }
            return true;
        }
        throw new InfraException("Os campos do período de datas devem ser preencidos...");
    }

	private static DateTimeFormatter getDateTimeFormatter(String formato) {
		return DateTimeFormat.forPattern(formato).withZone(ZONE).withLocale(LOCALE);
	}

	private static DateTimeFormatter getDateFormatter(String formato) {
		// Necessario timezone UTC para nao dar problema com horario de verao
		return DateTimeFormat.forPattern(formato).withZone(DateTimeZone.UTC);
	}

	public static String getDataAtual() {
		SimpleDateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		return formatData.format(date);
	}

	public static String getDataAtual(SimpleDateFormat formatData) {
		Date date = new Date();
		return formatData.format(date);
	}

	public static String getHoraAtual() {
		SimpleDateFormat formatHora = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		return formatHora.format(date);
	}

	public static String dateToString(Date date) {
		SimpleDateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
		return formatData.format(date);
	}
}
