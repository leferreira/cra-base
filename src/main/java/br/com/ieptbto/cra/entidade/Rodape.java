package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.bytecode.internal.javassist.FieldHandled;
import org.hibernate.bytecode.internal.javassist.FieldHandler;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.enumeration.regra.TipoIdentificacaoRegistro;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
@Entity
@Audited
@Table(name = "TB_RODAPE")
@org.hibernate.annotations.Table(appliesTo = "TB_RODAPE")
public class Rodape extends AbstractEntidade<Rodape> implements FieldHandled {

	/** **/
	private static final long serialVersionUID = 1L;
	private int id;
	private Remessa remessa;
	private TipoIdentificacaoRegistro identificacaoRegistro;
	private String numeroCodigoPortador;
	private String nomePortador;
	private LocalDate dataMovimento;
	private BigDecimal somatorioQtdRemessa;
	private BigDecimal somatorioValorRemessa;
	private String complementoRegistro;
	private String numeroSequencialRegistroArquivo;
	private FieldHandler handler;

	@Override
	public int compareTo(Rodape entidade) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(this.getId(), entidade.getId());
		return compareToBuilder.toComparison();
	}

	@Id
	@Column(name = "ID_RODAPE", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne(mappedBy = "rodape", fetch = FetchType.LAZY)
	@LazyToOne(LazyToOneOption.NO_PROXY)
	public Remessa getRemessa() {
		if (this.handler != null) {
			return (Remessa) this.handler.readObject(this, "remessa", remessa);
		}
		return remessa;
	}

	@Column(name = "IDENTIFICACAO_REGISTRO")
	public TipoIdentificacaoRegistro getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	@Column(name = "CODIGO_PORTADOR")
	public String getNumeroCodigoPortador() {
		if (numeroCodigoPortador == null) {
			numeroCodigoPortador = StringUtils.EMPTY;
		}
		return numeroCodigoPortador.trim();
	}

	@Column(name = "NOME_PORTADOR")
	public String getNomePortador() {
		if (nomePortador == null) {
			nomePortador = StringUtils.EMPTY;
		}
		return nomePortador.trim();
	}

	@Column(name = "DATA_MOVIMENTO")
	public LocalDate getDataMovimento() {
		return dataMovimento;
	}

	@Column(name = "QTD_REMESSA")
	public BigDecimal getSomatorioQtdRemessa() {
		if (somatorioQtdRemessa == null) {
			somatorioQtdRemessa = BigDecimal.ZERO;
		}

		if (this.remessa != null) {
			if (this.remessa.getCabecalho() != null) {
				somatorioQtdRemessa =
						new BigDecimal(this.remessa.getCabecalho().getQtdRegistrosRemessa() + this.remessa.getCabecalho().getQtdTitulosRemessa()
								+ this.remessa.getCabecalho().getQtdIndicacoesRemessa() + this.remessa.getCabecalho().getQtdOriginaisRemessa());
			}
		}
		return somatorioQtdRemessa;
	}

	@Column(name = "SOMATORIO_VLR_REMESSA")
	public BigDecimal getSomatorioValorRemessa() {
		if (somatorioValorRemessa == null) {
			somatorioValorRemessa = BigDecimal.ZERO;
		}
		return somatorioValorRemessa;
	}

	@Column(name = "COMPLEMENTO_REGISTRO")
	public String getComplementoRegistro() {
		if (complementoRegistro == null) {
			complementoRegistro = StringUtils.EMPTY;
		}
		return complementoRegistro.trim();
	}

	@Column(name = "NUMERO_SEQUENCIAL")
	public String getNumeroSequencialRegistroArquivo() {
		if (numeroSequencialRegistroArquivo == null) {
			numeroSequencialRegistroArquivo = StringUtils.EMPTY;
		}
		return numeroSequencialRegistroArquivo.trim();
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRemessa(Remessa remessa) {
		if (this.handler != null) {
			this.remessa = (Remessa) this.handler.writeObject(this, "remessa", this.remessa, remessa);
		}
		this.remessa = remessa;
	}

	public void setIdentificacaoRegistro(TipoIdentificacaoRegistro identificacaoRegistro) {
		this.identificacaoRegistro = identificacaoRegistro;
	}

	public void setNumeroCodigoPortador(String numeroCodigoPortador) {
		this.numeroCodigoPortador = numeroCodigoPortador;
	}

	public void setNomePortador(String nomePortador) {
		this.nomePortador = nomePortador;
	}

	public void setDataMovimento(LocalDate dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public void setSomatorioQtdRemessa(BigDecimal somatorioQtdRemessa) {
		this.somatorioQtdRemessa = somatorioQtdRemessa;
	}

	public void setSomatorioValorRemessa(BigDecimal somatorioValorRemessa) {
		this.somatorioValorRemessa = somatorioValorRemessa;
	}

	public void setComplementoRegistro(String complementoRegistro) {
		this.complementoRegistro = complementoRegistro;
	}

	public void setNumeroSequencialRegistroArquivo(String numeroSequencialRegistroArquivo) {
		this.numeroSequencialRegistroArquivo = numeroSequencialRegistroArquivo;
	}

	public static Rodape parseRodapeVO(RodapeVO rodapeVO) {
		Rodape rodape = new Rodape();
		rodape.setComplementoRegistro(rodapeVO.getComplementoRegistro());
		rodape.setDataMovimento(verificaDataMovimento(rodapeVO.getDataMovimento()));
		rodape.setIdentificacaoRegistro(TipoIdentificacaoRegistro.get(rodapeVO.getIdentificacaoRegistro()));
		rodape.setNomePortador(rodapeVO.getNomePortador());
		rodape.setNumeroCodigoPortador(rodapeVO.getNumeroCodigoPortador());
		rodape.setNumeroSequencialRegistroArquivo(rodapeVO.getNumeroSequencialRegistroArquivo());
		rodape.setSomatorioQtdRemessa(new BigDecimal(rodapeVO.getSomatorioQtdRemessa()));
		rodape.setSomatorioValorRemessa(new BigDecimal(getValorTotalTitulos(rodapeVO.getSomatorioValorRemessa())));

		return rodape;
	}

	private static Integer getValorTotalTitulos(String valor) {
		if (valor != null) {
			if (valor.contains(".")) {
				valor = valor.replace(".", "");
			}
			if (valor.contains(",")) {
				valor = valor.replace(",", "");
			}
		}
		return Integer.parseInt(valor) / 100;
	}

	private static LocalDate verificaDataMovimento(String dataMovimento) {
		if (dataMovimento.equals("00000000") || dataMovimento == null) {
			return new LocalDate();
		}
		return DataUtil.stringToLocalDate(DataUtil.PADRAO_FORMATACAO_DATA_DDMMYYYY, dataMovimento);
	}

	@Override
	public void setFieldHandler(FieldHandler handler) {
		this.handler = handler;

	}

	@Override
	public FieldHandler getFieldHandler() {
		return this.handler;
	}
}
