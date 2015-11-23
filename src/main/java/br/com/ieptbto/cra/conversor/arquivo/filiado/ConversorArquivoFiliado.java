package br.com.ieptbto.cra.conversor.arquivo.filiado;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.joda.time.LocalDate;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.arquivo.AbstractConversor;
import br.com.ieptbto.cra.conversor.arquivo.CampoArquivo;
import br.com.ieptbto.cra.conversor.arquivo.FabricaConversor;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.LayoutFiliado;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.enumeration.TipoRegistro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.LayoutFiliadoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.util.CraConstructorUtils;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class ConversorArquivoFiliado extends ConversorArquivoFiliadoAbstract {

	@Autowired
	LayoutFiliadoMediator layoutFiliadoMediator;
	@Autowired
	InstituicaoMediator instituicaoMediator;
	@Autowired
	MunicipioMediator municipioMediator;
	private Instituicao instituicao;
	private List<LayoutFiliado> layoutfiliado;
	private FileUploadField file;
	private List<Remessa> remessas;
	private Map<String, Municipio> municipios;
	private Map<String, Instituicao> instituicaoDestino;
	private List<Exception> erros;

	public void converter(FileUploadField file, Usuario usuario, Arquivo arquivo, List<Exception> list) {
		setInstituicao(usuario.getInstituicao());
		setFile(file);
		setErros(erros);
		setLayoutfiliado(layoutFiliadoMediator.buscarLayout(instituicao));
		setRemessa(arquivo);

	}

	private void setRemessa(Arquivo arquivo) {
		this.remessas = new ArrayList<>();
		processarArquivoRecebido(arquivo);
	}

	private Rodape getRodapeRemessa(Remessa remessa) {
		Rodape rodape = new Rodape();
		rodape.setDataMovimento(new LocalDate());
		rodape.setIdentificacaoRegistro(TipoRegistro.RODAPE);
		rodape.setNomePortador(getInstituicao().getNomeFantasia());
		rodape.setNumeroCodigoPortador(getInstituicao().getCodigoCompensacao());
		rodape.setRemessa(remessa);
		rodape.setNumeroSequencialRegistroArquivo(String.valueOf(remessa.getTitulos().size() + 1));
		return rodape;
	}

	private CabecalhoRemessa getCabecalhoRemessa(Remessa remessa, String cidade) {
		int totalTitulos = remessa.getTitulos().size();

		CabecalhoRemessa cabecalho = new CabecalhoRemessa();
		cabecalho.setAgenciaCentralizadora(getInstituicao().getAgenciaCentralizadora());
		cabecalho.setCodigoMunicipio(getCodigoMunicipio(cidade));
		cabecalho.setDataMovimento(new LocalDate());
		cabecalho.setIdentificacaoRegistro(TipoRegistro.CABECALHO);
		cabecalho.setIdentificacaoTransacaoDestinatario("SDT");
		cabecalho.setIdentificacaoTransacaoRemetente("BFO");
		cabecalho.setIdentificacaoTransacaoTipo("TPR");
		cabecalho.setNomePortador(getInstituicao().getNomeFantasia());
		cabecalho.setNumeroCodigoPortador(getInstituicao().getCodigoCompensacao());
		cabecalho.setQtdTitulosRemessa(totalTitulos);
		cabecalho.setNumeroSequencialRegistroArquivo(String.valueOf(1));
		cabecalho.setQtdIndicacoesRemessa(totalTitulos);
		cabecalho.setQtdIndicacoesRemessa(totalTitulos);
		cabecalho.setQtdRegistrosRemessa(totalTitulos);
		cabecalho.setRemessa(remessa);
		cabecalho.setVersaoLayout("043");

		return cabecalho;
	}

	private void processarArquivoRecebido(Arquivo arquivo) {
		Map<String, List<List<TemplateLayoutEmpresa>>> listaCampos = new HashMap<>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getFile().getFileUpload().getInputStream()));
			String linha = reader.readLine();
			int cont = 1;
			while ((linha = reader.readLine()) != null) {
				String dados[] = linha.split(Pattern.quote(";"));
				if (dados.length == 12) {
					LinhaTemplateLayout mapaCampos = TemplateLayoutEmpresa.getTemplate(dados, getLayoutfiliado());

					if (listaCampos.containsKey(mapaCampos.getCidade())) {
						listaCampos.get(mapaCampos.getCidade()).add(mapaCampos.getCampos());
					} else {
						List<List<TemplateLayoutEmpresa>> lista = new ArrayList<>();
						lista.add(mapaCampos.getCampos());
						listaCampos.put(mapaCampos.getCidade(), lista);
					}

				} else {
					getErros().add(new InfraException("A Linha " + cont + " contem erros é não foi processada."));
					logger.warn("Linha " + cont + " com problema  = " + linha);
				}
				cont++;
			}
			reader.close();

		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new InfraException("Não foi possível abrir o arquivo enviado.");

		}

		converterTemplate(listaCampos, arquivo);
	}

	private void converterTemplate(Map<String, List<List<TemplateLayoutEmpresa>>> listaRegistros, Arquivo arquivo) {
		TituloRemessa titulo = new TituloRemessa();
		List<TituloRemessa> listaTitulos = new ArrayList<>();
		Remessa remessa = new Remessa();
		BigDecimal valortotalTitulos = BigDecimal.ZERO;
		remessas = new ArrayList<>();

		for (String cidade : listaRegistros.keySet()) {

			valortotalTitulos = BigDecimal.ZERO;
			remessa = new Remessa();
			listaTitulos = new ArrayList<>();
			remessa.setTitulos(new ArrayList<Titulo>());
			remessa.setArquivo(arquivo);
			remessa.setCabecalho(getCabecalhoRemessa(remessa, cidade));
			remessa.setRodape(getRodapeRemessa(remessa));
			remessa.setInstituicaoOrigem(getInstituicao());
			remessa.setInstituicaoDestino(getMunicipioDestino(cidade));

			for (List<TemplateLayoutEmpresa> registro : listaRegistros.get(cidade)) {
				titulo = CraConstructorUtils.newInstance(TituloRemessa.class);
				BeanWrapper propertyAccessTitulo = PropertyAccessorFactory.forBeanPropertyAccess(titulo);
				PropertyDescriptor[] propertyDescriptors = propertyAccessTitulo.getPropertyDescriptors();
				for (TemplateLayoutEmpresa templateLayoutEmpresa : registro) {
					for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
						String propertyName = propertyDescriptor.getName();
						if (propertyName.equals(templateLayoutEmpresa.getCampo().getLabel())) {
							propertyAccessTitulo.setPropertyValue(propertyName, getValorConvertido(templateLayoutEmpresa.getValor(),
							        propertyAccessTitulo.getPropertyType(propertyName), propertyName));
							break;
						}

					}
				}
				valortotalTitulos = valortotalTitulos.add(titulo.getValorTitulo());
				listaTitulos.add(preencherTitulo(titulo, remessa));
			}
			remessa.getTitulos().addAll(listaTitulos);
			remessas.add(remessa);
		}
		arquivo.setRemessas(remessas);

	}

	private Instituicao getMunicipioDestino(String cidade) {
		if (!getInstituicaoDestino().containsKey(cidade)) {
			getInstituicaoDestino().put(cidade, instituicaoMediator.buscarInstituicaoPorNomeCidade(cidade));
		}
		return getInstituicaoDestino().get(cidade);
	}

	private String getCodigoMunicipio(String cidade) {
		try {
			if (!getMunicipios().containsKey(cidade)) {
				getMunicipios().put(cidade, municipioMediator.buscarMunicipio(cidade));
			}
			return getMunicipios().get(cidade).getCodigoIBGE();

		} catch (Exception ex) {
			logger.error(ex.getMessage());
			System.err.println(cidade);
		}
		return null;
	}

	private TituloRemessa preencherTitulo(TituloRemessa titulo, Remessa remessa) {
		titulo.setCodigoPortador(getInstituicao().getCodigoCompensacao());
		titulo.setAgenciaCodigoCedente(getInstituicao().getCodigoCompensacao() + DataUtil.getDataAtual(new SimpleDateFormat("MMyyyy")));
		titulo.setDataCadastro(new Date());
		titulo.setEspecieTitulo("CDA");
		titulo.setPracaProtesto(titulo.getCidadeDevedor());
		titulo.setSaldoTitulo(titulo.getValorTitulo());
		titulo.setIdentificacaoRegistro(TipoRegistro.TITULO);

		titulo.setCidadeSacadorVendedor(getInstituicao().getMunicipio().getNomeMunicipio());
		titulo.setEnderecoSacadorVendedor(getInstituicao().getEndereco());
		titulo.setDocumentoSacador(getInstituicao().getCnpj());
		titulo.setUfSacadorVendedor(getInstituicao().getMunicipio().getUf());

		titulo.setRemessa(remessa);
		return titulo;
	}

	private Object getValorConvertido(String valor, Class<?> propertyType, String nomeCampo) {
		AbstractConversor<?> conversor = FabricaConversor.getConversor(propertyType);
		conversor.setArquivo(new TituloVO());
		conversor.setCampoArquivo(new CampoArquivo(nomeCampo, TituloVO.class));
		return conversor.getValorConvertido(valor);
	}

	public Instituicao getInstituicao() {
		return instituicao;
	}

	public List<LayoutFiliado> getLayoutfiliado() {
		return layoutfiliado;
	}

	public FileUploadField getFile() {
		return file;
	}

	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}

	public void setLayoutfiliado(List<LayoutFiliado> layoutfiliado) {
		this.layoutfiliado = layoutfiliado;
	}

	public void setFile(FileUploadField file) {
		this.file = file;
	}

	public Map<String, Municipio> getMunicipios() {
		if (municipios == null) {
			municipios = new HashMap<String, Municipio>();
		}

		return municipios;
	}

	public void setMunicipios(Map<String, Municipio> municipios) {
		this.municipios = municipios;
	}

	public Map<String, Instituicao> getInstituicaoDestino() {
		if (instituicaoDestino == null) {
			instituicaoDestino = new HashMap<String, Instituicao>();
		}

		return instituicaoDestino;
	}

	public void setInstituicaoDestino(Map<String, Instituicao> instituicaoDestino) {
		this.instituicaoDestino = instituicaoDestino;
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
