package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
public enum TipoAlineaCheque  implements AbstractCraEnum {

	CHEQUE_SEM_FUNDOS_1("11","11 - Cheque sem fundos - 1ª apresentação."),                                                                                                                                                  
	CHEQUE_SEM_FUNDOS_2("12","12 - Cheque sem fundos - 2ª apresentação."),
	CONTA_ENCERRADA("13","13 - Conta encerrada."),
	PRATICA_ESPURIA("14","14 - Prática espúria."),
//	CHEQUE_SUSTADO_REVOGADO_ROUBO("20","20 - Cheque sustado ou revogado em virtude de roubo, furto ou extravio."),
	CHEQUE_SUSTADO_REVOGADO("21","21 - Cheque sustado ou revogado."),
	DIVERGENCIA_OU_INSUFICIENCIA_DE_ASSINATURAS("22","22 - Divergência ou insuficiência de assinatura."),
	CHEQUES_EMITIDOS_POR_ADMINISTRACAO_PUBLICA("23","23 - Cheques emitidos por entidades e órgãos da administração pública federal."),
	BLOQUEIO_JUDICIAL("24","24 - Bloqueio judicial ou determinação do Banco Central do Brasil."),
//	CANCELAMENTO_DE_TALONARIO("25","25 - Cancelamento de talonário pelo participante destinatário."),
	INOPERANCIA_TEMPORARIA_DE_TRANSPORTE("26","26 - Inoperância temporária de transporte."),
	FERIADO_MUNICIPAL_NAO_PREVISTO("27","27 - Feriado municipal não previsto."),
//	CHEQUE_SUSTADO_OU_REVOGADO_EXTRAVIO("28","28 - Cheque sustado ou revogado em virtude de roubo, furto ou extravio."),
	BLOQUEADO_POR_FALTA_DE_CONFIRMACAO_RECEBIMENTO("29","29 - Cheque bloqueado por falta de confirmação de recebimento."),
//	FURTO_OU_ROUBO_CHEQUE("30","30 - Furto ou roubo de cheque."),
	ERRO_FORMAL("31","31 - Erro formal."),
	DIVERGÊNCIA_DE_ENDOSSO("33","33 - Divergência de endosso."),
	APRESENTADO_POR_PARTICIPANTE_NAO_O_INDICADO("34","34 - Cheque apresentado por participante não o indicado."),
//	CHEQUE_FRAUDADO("35","35 - Cheque fraudado, emitido sem prévio controle.."),
	REGISTRO_INCONSISTENTE("37","37 - Registro inconsistente."),
	ASSINATURA_DIGITAL_INVALIDA("38","38 - Assinatura digital ausente ou inválida."),
	IMAGEM_FORA_DO_PADRAO("39","39 - Imagem fora do padrão."),
	MOEDA_INVALIDA("40","40 - Moeda inválida."),
	APRESENTADO_A_PARTICIPANTE_QUE_NAO_O_DESTINATARIO("41","41 - Cheque apresentado a participante que não o destinatário."),
	CHEQUE_NAO_COMPENSAVEL("42","42 - Cheque não compensável na sessão ou sistema."),
	CHEQUE_DEVOLVIDO("43","43 - Cheque, devolvido anteriormente."),
	CHEQUE_PRESCRITO("44","44 - Cheque prescrito."),
	CHEQUE_EMITIDO_POR_ENTIDADE_OBRIGADA("45","45 - Cheque emitido por entidade obrigada a realizar movimentação."),
	VALOR_SUPERIOR_A_CEM("48","48 - Cheque de valor superior a cem reais."),
	REMESSA_NULA("49","49 - Remessa nula, caracterizada pela reapresentação de cheque devolvido."),
	INFORMACAO_ESSENCIAL_FALTANTE("59","59 - Informação essencial faltante ou inconsistente."),
	INSTRUMENTO_INADEQUADO("60","60 - Instrumento inadequado para a finalidade."),
	ITEM_NAO_COMPENSAVEL("61","61 - Item não compensável."),
	ARQUIVO_LOGICO_NAO_PROCESSADO("64","64 - Arquivo lógico não processado / processado parcialmente."),
//	SUSTACAO_OU_REVOGACAO_PROVISORIA("70","70 - Sustação ou revogação provisória."),
	INADIMPLEMENTO_CONTRATUAL_DA_COOPERATIVA("71","71 - Inadimplemento contratual da cooperativa de crédito."),
	CONTRATO_DE_COMPENSACAO_ENCERRADO("72","72 - Contrato de compensação encerrado.");
	
	private String constante;
	private String label;
	
	private TipoAlineaCheque(String constante, String label) {
		this.constante = constante;
		this.label = label;
	}

	@Override
	public String getConstante() {
		return constante;
	}

	@Override
	public String getLabel() {
		return label;
	}
	
	/**
	 * retorna o tipo de alinea dependendo do tipo informado
	 * 
	 * @param valor
	 * @return tipo arquivo
	 */
	public static TipoAlineaCheque getTipoArquivoEnum(String codigo) {
		TipoAlineaCheque[] values = TipoAlineaCheque.values();
		for (TipoAlineaCheque tipoAlinea : values) {
			if (codigo.startsWith(tipoAlinea.getConstante())) {
				return tipoAlinea;
			}
		}
		throw new InfraException("Não foi possível encontrar o tipo de alínea específico !");
	}
}
