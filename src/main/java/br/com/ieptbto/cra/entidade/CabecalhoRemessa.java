package br.com.ieptbto.cra.entidade;

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

import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.enumeration.regra.TipoIdentificacaoRegistro;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
@Entity
@Audited
@Table(name = "TB_CABECALHO")
@org.hibernate.annotations.Table(appliesTo = "TB_CABECALHO")
public class CabecalhoRemessa extends Cabecalho<CabecalhoRemessa> implements FieldHandled {

	/*** */
	private static final long serialVersionUID = 1L;
	private int id;
	private Remessa remessa;

	private TipoIdentificacaoRegistro identificacaoRegistro;
	private String numeroCodigoPortador;
	private String nomePortador;
	private LocalDate dataMovimento;
	private Integer numeroSequencialRemessa;
	private Integer qtdRegistrosRemessa;
	private Integer qtdTitulosRemessa;;
	private Integer qtdIndicacoesRemessa;
	private Integer qtdOriginaisRemessa;
	private String agenciaCentralizadora;
	private String versaoLayout;
	private String codigoMunicipio;
	private String complementoRegistro;
	private String numeroSequencialRegistroArquivo;
	private FieldHandler handler;

	@Override
	@Id
	@Column(name = "ID_CABECALHO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne(mappedBy = "cabecalho", fetch = FetchType.LAZY)
	@LazyToOne(LazyToOneOption.NO_PROXY)
	public Remessa getRemessa() {
		if (this.handler != null) {
			return (Remessa) this.handler.readObject(this, "remessa", remessa);
		}
		return remessa;
	}

	@Column(name = "TIPO_REGISTRO_ID")
	public TipoIdentificacaoRegistro getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	@Column(name = "NUMERO_CODIGO_PORTADOR", length = 3)
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
		nomePortador.toUpperCase();
		return nomePortador.trim();
	}

	@Column(name = "DATA_MOVIMENTO")
	public LocalDate getDataMovimento() {
		if (dataMovimento == null) {
			dataMovimento = new LocalDate();
		}
		return dataMovimento;
	}

	@Column(name = "NUMERO_SEQUENCIAL_REMESSA")
	public Integer getNumeroSequencialRemessa() {
		if (numeroSequencialRemessa == null) {
			numeroSequencialRemessa = 1;
		}
		return numeroSequencialRemessa;
	}

	@Column(name = "QUANTIDADE_REGISTROS_REMESSA")
	public Integer getQtdRegistrosRemessa() {
		if (qtdOriginaisRemessa == null) {
			qtdOriginaisRemessa = 0;
		}
		return qtdRegistrosRemessa;
	}

	@Column(name = "QUANTIDADE_TITULOS_REMESSA")
	public Integer getQtdTitulosRemessa() {
		if (qtdOriginaisRemessa == null) {
			qtdOriginaisRemessa = 0;
		}
		return qtdTitulosRemessa;
	}

	@Column(name = "QUANTIDADE_INDICACOES_REMESSA")
	public Integer getQtdIndicacoesRemessa() {
		if (qtdIndicacoesRemessa == null) {
			qtdIndicacoesRemessa = 0;
		}
		return qtdIndicacoesRemessa;
	}

	@Column(name = "QUANTIDADE_ORIGINAL_REMESSA")
	public Integer getQtdOriginaisRemessa() {
		if (qtdOriginaisRemessa == null) {
			qtdOriginaisRemessa = 0;
		}
		return qtdOriginaisRemessa;
	}

	@Column(name = "AGENCIA_CENTRALIZADORA")
	public String getAgenciaCentralizadora() {
		if (agenciaCentralizadora == null) {
			agenciaCentralizadora = StringUtils.EMPTY;
		}
		return agenciaCentralizadora.trim();
	}

	@Column(name = "VERSAO_LAYOUT", length = 3)
	public String getVersaoLayout() {
		return versaoLayout;
	}

	@Column(name = "CODIGO_MUNICIPIO")
	public String getCodigoMunicipio() {
		return codigoMunicipio;
	}

	@Column(name = "COMPLEMENTO_REGISTRO")
	public String getComplementoRegistro() {
		if (complementoRegistro == null) {
			complementoRegistro = StringUtils.EMPTY;
		}
		return complementoRegistro.trim();
	}

	@Column(name = "NUMERO_SEQUENCIAL_ARQUIVO")
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

	public void setNumeroSequencialRemessa(Integer numeroSequencialRemessa) {
		this.numeroSequencialRemessa = numeroSequencialRemessa;
	}

	public void setQtdRegistrosRemessa(Integer qtdRegistrosRemessa) {
		this.qtdRegistrosRemessa = qtdRegistrosRemessa;
	}

	public void setQtdTitulosRemessa(Integer qtdTitulosRemessa) {
		this.qtdTitulosRemessa = qtdTitulosRemessa;
	}

	public void setQtdIndicacoesRemessa(Integer qtdIndicacoesRemessa) {
		this.qtdIndicacoesRemessa = qtdIndicacoesRemessa;
	}

	public void setQtdOriginaisRemessa(Integer qtdOriginaisRemessa) {
		this.qtdOriginaisRemessa = qtdOriginaisRemessa;
	}

	public void setAgenciaCentralizadora(String agenciaCentralizadora) {
		this.agenciaCentralizadora = agenciaCentralizadora;
	}

	public void setVersaoLayout(String versaoLayout) {
		this.versaoLayout = versaoLayout;
	}

	public void setCodigoMunicipio(String codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}

	public void setComplementoRegistro(String complementoRegistro) {
		this.complementoRegistro = complementoRegistro;
	}

	public void setNumeroSequencialRegistroArquivo(String numeroSequencialRegistroArquivo) {
		this.numeroSequencialRegistroArquivo = numeroSequencialRegistroArquivo;
	}

	@Override
	public int compareTo(CabecalhoRemessa entidade) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(this.getId(), entidade.getId());
		return compareToBuilder.toComparison();
	}

	public static CabecalhoRemessa parseCabecalhoVO(CabecalhoVO cabecalhoVO) {
		CabecalhoRemessa cabecalho = new CabecalhoRemessa();
		cabecalho.setAgenciaCentralizadora(cabecalhoVO.getAgenciaCentralizadora());
		cabecalho.setCodigoMunicipio(cabecalhoVO.getCodigoMunicipio().trim());
		cabecalho.setComplementoRegistro(cabecalhoVO.getComplementoRegistro());
		cabecalho.setDataMovimento(verificaDataMovimento(cabecalhoVO.getDataMovimento()));
		cabecalho.setIdentificacaoRegistro(TipoIdentificacaoRegistro.get(cabecalhoVO.getIdentificacaoRegistro()));
		cabecalho.setIdentificacaoTransacaoDestinatario(cabecalhoVO.getIdentificacaoTransacaoDestinatario());
		cabecalho.setIdentificacaoTransacaoRemetente(cabecalhoVO.getIdentificacaoTransacaoRemetente());
		cabecalho.setIdentificacaoTransacaoTipo(cabecalhoVO.getIdentificacaoTransacaoTipo());
		cabecalho.setNomePortador(cabecalhoVO.getNomePortador());
		cabecalho.setNumeroCodigoPortador(cabecalhoVO.getNumeroCodigoPortador());
		cabecalho.setNumeroSequencialRemessa(Integer.parseInt(cabecalhoVO.getNumeroSequencialRemessa()));
		cabecalho.setQtdIndicacoesRemessa(Integer.parseInt(cabecalhoVO.getQtdIndicacoesRemessa()));
		cabecalho.setQtdOriginaisRemessa(Integer.parseInt(cabecalhoVO.getQtdOriginaisRemessa()));
		cabecalho.setQtdRegistrosRemessa(Integer.parseInt(cabecalhoVO.getQtdRegistrosRemessa()));
		cabecalho.setQtdTitulosRemessa(Integer.parseInt(cabecalhoVO.getQtdTitulosRemessa()));
		cabecalho.setVersaoLayout(cabecalhoVO.getVersaoLayout());
		cabecalho.setNumeroSequencialRegistroArquivo(cabecalhoVO.getNumeroSequencialRegistroArquivo());

		return cabecalho;
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
