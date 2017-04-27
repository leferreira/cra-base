package br.com.ieptbto.cra.entidade;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_AUTORIZACAO_CANCELAMENTO")
@org.hibernate.annotations.Table(appliesTo = "TB_AUTORIZACAO_CANCELAMENTO")
public class AutorizacaoCancelamento extends AbstractEntidade<AutorizacaoCancelamento> {

	private static final long serialVersionUID = 1L;
	private int id;
	private CabecalhoCartorio cabecalhoCartorio;
	private List<PedidoAutorizacaoCancelamento> autorizacoesCancelamentos;
	private RodapeCartorio rodapeCartorio;
	private RemessaAutorizacaoCancelamento remessaAutorizacaoCancelamento;
	private Boolean download;

	@Id
	@Column(name = "ID_AUTORIZACAO_CANCELAMENTO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne
	@JoinColumn(name = "CABECALHO_ID")
	public CabecalhoCartorio getCabecalhoCartorio() {
		return cabecalhoCartorio;
	}

	@OneToMany(mappedBy = "autorizacaoCancelamento", fetch = FetchType.LAZY)
	public List<PedidoAutorizacaoCancelamento> getAutorizacoesCancelamentos() {
		return autorizacoesCancelamentos;
	}

	@OneToOne
	@JoinColumn(name = "RODAPE_ID")
	public RodapeCartorio getRodapeCartorio() {
		return rodapeCartorio;
	}

	@ManyToOne
	@JoinColumn(name = "REMESSA_AUTORIZACAO_CANCELAMENTO_PROTESTO_ID")
	public RemessaAutorizacaoCancelamento getRemessaAutorizacaoCancelamento() {
		return remessaAutorizacaoCancelamento;
	}

	@Column(name = "DOWNLOAD_REALIZADO", columnDefinition = "boolean")
	public Boolean getDownload() {
		return download;
	}

	public void setDownload(Boolean download) {
		this.download = download;
	}

	public void setRemessaAutorizacaoCancelamento(RemessaAutorizacaoCancelamento remessaAutorizacaoCancelamento) {
		this.remessaAutorizacaoCancelamento = remessaAutorizacaoCancelamento;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCabecalhoCartorio(CabecalhoCartorio cabecalhoCartorio) {
		this.cabecalhoCartorio = cabecalhoCartorio;
	}

	public void setAutorizacoesCancelamentos(List<PedidoAutorizacaoCancelamento> autorizacoesCancelamentos) {
		this.autorizacoesCancelamentos = autorizacoesCancelamentos;
	}

	public void setRodapeCartorio(RodapeCartorio rodapeCartorio) {
		this.rodapeCartorio = rodapeCartorio;
	}

	@Override
	public int compareTo(AutorizacaoCancelamento entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
