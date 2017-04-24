package br.com.ieptbto.cra.processador;

import br.com.ieptbto.cra.conversor.BigDecimalConversor;
import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.vo.ArquivoRemessaConvenioVO;
import br.com.ieptbto.cra.entidade.vo.TituloConvenioVO;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.regra.TipoIdentificacaoRegistro;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.TituloConvenioException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.util.CpfCnpjUtil;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Thasso Araujo
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class ProcessadorArquivoConvenio extends Processador {
	
	@Autowired
	private InstituicaoMediator instituicaoMediator;
	@Autowired
	private RemessaMediator remessaMediator;
    @Autowired
    private MunicipioMediator municipioMediator;

	private Arquivo arquivo;
    private Usuario usuario;

    /**
     * Processador e conversor de XML para titulos convênios
     *
	 * @param arquivoConvenioVO, arquivo, usuario, erros
	 * @return arquivo
	 */
	public Arquivo processarArquivoWS(ArquivoRemessaConvenioVO arquivoConvenioVO, Arquivo arquivo, Usuario usuario, List<Exception> erros) {
		this.arquivo = arquivo;
		this.usuario = usuario;

		logger.info("Início processamento arquivo via WS " + arquivo.getNomeArquivo() + " do usuário " + usuario.getLogin());
		converterArquivoConvenio(arquivoConvenioVO, erros);
		logger.info("Fim processamento arquivo via WS " + arquivo.getNomeArquivo() + " do usuário " + usuario.getLogin());
		return arquivo;
	}

	private void converterArquivoConvenio(ArquivoRemessaConvenioVO arquivoConvenioVO, List<Exception> erros) {
        Municipio municipio = municipioMediator.carregarMunicipio(usuario.getInstituicao().getMunicipio());

        HashMap<Integer, Remessa> mapaRemessas = new HashMap<Integer, Remessa>();
        for (TituloConvenioVO tituloConvenioVO : arquivoConvenioVO.getTitulosConvenio()) {
            TituloRemessa titulo  = TituloConvenioVO.createTitulo(usuario.getInstituicao(), municipio);
            BeanWrapper propertyAccessEntidadeVO = PropertyAccessorFactory.forBeanPropertyAccess(tituloConvenioVO);
            BeanWrapper propertyAccessTitulo = PropertyAccessorFactory.forBeanPropertyAccess(titulo);

            PropertyDescriptor[] propertyDescriptors = propertyAccessTitulo.getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String propertyName = propertyDescriptor.getName();

                if (propertyAccessEntidadeVO.isReadableProperty(propertyName) && propertyAccessTitulo.isWritableProperty(propertyName)) {
                    Object valor = propertyAccessEntidadeVO.getPropertyValue(propertyName);
                    if (String.class.isInstance(valor)) {
                        valor = String.class.cast(valor);
                    }
                    if (!propertyName.equals("saldoTitulo")) {
                        propertyAccessTitulo.setPropertyValue(propertyName, valor);
                    }
                }
            }
            converterValoresComplexos(titulo, tituloConvenioVO, erros);
            generateNossoNumero(titulo);
            validarTitulo(titulo, erros);
            gerarRemessa(titulo, mapaRemessas, erros);
		}
		this.arquivo.setRemessas(new ArrayList<Remessa>(mapaRemessas.values()));
	}

	private void gerarRemessa(TituloRemessa titulo, HashMap<Integer, Remessa> mapaRemessas, List<Exception> erros) {
       Instituicao instituicaoDestino = instituicaoMediator.buscarInstituicaoPorNomeCidade(titulo.getPracaProtesto());
       if (instituicaoDestino == null) {
           erros.add(new TituloConvenioException(CodigoErro.CONVENIO_MUNICIPIO_INVALIDO, titulo));
           return;
       } else if (!instituicaoDestino.getMunicipio().cepIsValidFromMunicipio(titulo.getCepDevedor())) {
           erros.add(new TituloConvenioException(CodigoErro.CONVENIO_CEP_FORA_DA_FAIXA, titulo));
           return;
       }

       if (mapaRemessas.containsKey(instituicaoDestino.getId())) {
           Remessa remessa = mapaRemessas.get(instituicaoDestino.getId());
           int quantidadeRegistros = remessa.getCabecalho().getQtdRegistrosRemessa();
           int quantidadeTitulos = remessa.getCabecalho().getQtdTitulosRemessa();
           int quantidadeIndicacoes = remessa.getCabecalho().getQtdIndicacoesRemessa();
           int quantidadeOriginais = remessa.getCabecalho().getQtdOriginaisRemessa();

           BigDecimal valorSaldo = remessa.getRodape().getSomatorioValorRemessa().add(titulo.getValorTitulo());
           titulo.setNumeroSequencialArquivo(Integer.toString(remessa.getTitulos().size() + 2));
           titulo.setRemessa(remessa);
           remessa.getTitulos().add(titulo);

           remessa.getCabecalho().setQtdRegistrosRemessa(quantidadeRegistros + 1);
           remessa.getCabecalho().setQtdTitulosRemessa(quantidadeTitulos + 1);
           remessa.getCabecalho().setQtdIndicacoesRemessa(quantidadeIndicacoes);
           remessa.getCabecalho().setQtdOriginaisRemessa(quantidadeOriginais);
           BigDecimal somatorioQtdRemessa = new BigDecimal(quantidadeRegistros + quantidadeTitulos + quantidadeOriginais + quantidadeIndicacoes);
           remessa.getRodape().setSomatorioQtdRemessa(somatorioQtdRemessa);
           remessa.getRodape().setSomatorioValorRemessa(valorSaldo);
           remessa.getRodape().setNumeroSequencialRegistroArquivo(Integer.toString(remessa.getTitulos().size() + 2));

       } else {
           Remessa remessa = new Remessa();
           remessa.setArquivo(arquivo);
           remessa.setDataRecebimento(new LocalDate());
           remessa.setInstituicaoDestino(instituicaoDestino);
           remessa.setInstituicaoOrigem(usuario.getInstituicao());
           remessa.setCabecalho(generateCabecalho(remessa, remessa.getInstituicaoDestino()));
           remessa.setRodape(generateRodape(remessa));
           remessa.getCabecalho().setRemessa(remessa);
           remessa.setTitulos(new ArrayList<Titulo>());
           titulo.setNumeroSequencialArquivo(Integer.toString(remessa.getTitulos().size() + 2));
           titulo.setRemessa(remessa);
           remessa.getTitulos().add(titulo);
           remessa.getRodape().setRemessa(remessa);
           remessa.setStatusDownload(StatusDownload.AGUARDANDO);
           mapaRemessas.put(instituicaoDestino.getId(), remessa);
       }
    }

    private Rodape generateRodape(Remessa remessa) {
        Rodape rodape = new Rodape();
        rodape.setDataMovimento(new LocalDate());
        rodape.setIdentificacaoRegistro(TipoIdentificacaoRegistro.RODAPE);
        rodape.setNomePortador(RemoverAcentosUtil.removeAcentos(usuario.getInstituicao().getNomeFantasia()));
        rodape.setNumeroCodigoPortador(usuario.getInstituicao().getCodigoCompensacao());
        rodape.setSomatorioQtdRemessa(new BigDecimal(0));
        rodape.setSomatorioValorRemessa(new BigDecimal(0));
        rodape.setNumeroSequencialRegistroArquivo("0");
        remessa.setRodape(rodape);
        return rodape;
    }

    private CabecalhoRemessa generateCabecalho(Remessa remessa, Instituicao instituicaoDestino) {
        CabecalhoRemessa cabecalho = new CabecalhoRemessa();
        cabecalho.setDataMovimento(new LocalDate());
        cabecalho.setAgenciaCentralizadora(StringUtils.leftPad(usuario.getInstituicao().getCodigoCompensacao(), 6, "0"));
        cabecalho.setCodigoMunicipio(instituicaoDestino.getMunicipio().getCodigoIBGE());
        cabecalho.setIdentificacaoRegistro(TipoIdentificacaoRegistro.CABECALHO);
        cabecalho.setIdentificacaoTransacaoRemetente("BFO");
        cabecalho.setIdentificacaoTransacaoDestinatario("SDT");
        cabecalho.setIdentificacaoTransacaoTipo("TPR");
        cabecalho.setNomePortador(RemoverAcentosUtil.removeAcentos(usuario.getInstituicao().getNomeFantasia()));
        cabecalho.setNumeroCodigoPortador(usuario.getInstituicao().getCodigoCompensacao());
        cabecalho.setNumeroSequencialRemessa(remessaMediator.getNumeroSequencialConvenio(instituicaoDestino, instituicaoDestino) + 1);
        cabecalho.setVersaoLayout("043");
        cabecalho.setQtdTitulosRemessa(1);
        cabecalho.setQtdRegistrosRemessa(1);
        cabecalho.setQtdIndicacoesRemessa(0);
        cabecalho.setQtdOriginaisRemessa(1);
        cabecalho.setNumeroSequencialRegistroArquivo("0001");
        cabecalho.setRemessa(remessa);
        return cabecalho;
    }

    /**
     * Método para validar os campos do titulo
     *
     * @param titulo, erros
     */
    private void validarTitulo(TituloRemessa titulo, List<Exception> erros) {

        if (StringUtils.isEmpty(titulo.getNumeroTitulo()) || titulo.getNumeroTitulo().length() > 11) {
            erros.add(new TituloConvenioException(CodigoErro.CONVENIO_NUMERO_TITULO_INVALIDA, titulo));
        }
        if (titulo.getDataEmissaoTitulo() != null) {
            if (titulo.getDataEmissaoTitulo().isAfter(new LocalDate()) || titulo.getDataEmissaoTitulo().isEqual(new LocalDate())) {
                erros.add(new TituloConvenioException(CodigoErro.CONVENIO_DATA_EMISSAO_MAIOR_QUE_ATUAL, titulo));
            }
        }
        if (StringUtils.isEmpty(titulo.getNomeDevedor())) {
            erros.add(new TituloConvenioException(CodigoErro.CONVENIO_NOME_DEVEDOR_INVALIDO, titulo));;
        }
        if (StringUtils.isEmpty(titulo.getEnderecoDevedor())) {
            erros.add(new TituloConvenioException(CodigoErro.CONVENIO_ENDERECO_DEVEDOR_INVALIDO, titulo));;
        }
        if (StringUtils.isNotEmpty(titulo.getNumeroIdentificacaoDevedor())) {
            if (!CpfCnpjUtil.isValidCNPJ(titulo.getNumeroIdentificacaoDevedor()) && !CpfCnpjUtil.isValidCPF(titulo.getNumeroIdentificacaoDevedor())) {
                erros.add(new TituloConvenioException(CodigoErro.CONVENIO_DOCUMENTO_DEVEDOR_INVALIDO, titulo));
            } else {
                titulo.setTipoIdentificacaoDevedor(TituloRemessa.getTIpoIdentificacao(titulo.getNumeroIdentificacaoDevedor()));
            }
        }
        if (titulo.getValorTitulo() != null && titulo.getValorTitulo().equals(BigDecimal.ZERO)) {
            erros.add(new TituloConvenioException(CodigoErro.CONVENIO_VALOR_SALDO_INVALIDA, titulo));
        }
        if (titulo.getDataVencimentoTitulo() != null) {
            if (titulo.getDataVencimentoTitulo().isBefore(titulo.getDataEmissaoTitulo()) || titulo.getDataVencimentoTitulo().equals(new LocalDate())) {
                erros.add(new TituloConvenioException(CodigoErro.CONVENIO_DATA_VENCIMENTO_MENOR_QUE_EMISSAO, titulo));
            }
        }
    }

    /**
     * Método para converter e validar os tipos complexos dos campos dos titulos
     *
     * @param titulo, tituloConvenioVO, erros
     */
    private void converterValoresComplexos(TituloRemessa titulo, TituloConvenioVO tituloConvenioVO, List<Exception> erros) {
        LocalDate dataEmissao = null;
        LocalDate dataVencimento = null;
        BigDecimal valorSaldo = null;

        try {
            dataEmissao = DataUtil.stringToLocalDate("ddMMyyyy", tituloConvenioVO.getDataEmissao());

        } catch (Exception ex) {
            erros.add(new TituloConvenioException(CodigoErro.CONVENIO_DATA_EMISSAO_INVALIDA, tituloConvenioVO));
            logger.info("Titulo com a data de emissão inválida [ " + tituloConvenioVO.getDataEmissao() + "] = " + tituloConvenioVO.asString(), ex);
        }

        try {
            dataVencimento = DataUtil.stringToLocalDate("ddMMyyyy", tituloConvenioVO.getDataVencimento());

        } catch (Exception ex) {
            erros.add(new TituloConvenioException(CodigoErro.CONVENIO_DATA_VENCIMENTO_INVALIDA, tituloConvenioVO));
            logger.info("Titulo com a data de vencimento inválida [ " + tituloConvenioVO.getDataVencimento() + "] = " + tituloConvenioVO.asString(), ex);
        }

        try {
            valorSaldo = new BigDecimalConversor().getValorBigDecimal(tituloConvenioVO.getSaldoTitulo());
            if (valorSaldo.equals(new BigDecimal(0))) {
                erros.add(new TituloConvenioException(CodigoErro.CONVENIO_VALOR_SALDO_INVALIDA, tituloConvenioVO));
            }

        } catch (Exception ex) {
            erros.add(new TituloConvenioException(CodigoErro.CONVENIO_VALOR_SALDO_INVALIDA, tituloConvenioVO));
            logger.info("Titulo com valor do saldo inválido [ " + tituloConvenioVO.getSaldoTitulo() + "] = " + tituloConvenioVO.asString(), ex);
        }

        titulo.setDataEmissaoTitulo(dataEmissao);
        titulo.setDataVencimentoTitulo(dataVencimento);
        titulo.setValorTitulo(valorSaldo);
        titulo.setSaldoTitulo(valorSaldo);
    }

    /**
     * Gera o nossoNumero para o titulo caso não tenha sido informado
     *
     * @param titulo
     */
    private void generateNossoNumero(TituloRemessa titulo) {
        if (StringUtils.isBlank(titulo.getNossoNumero())) {
            String nossoNumero = StringUtils.leftPad(titulo.getCodigoPortador() + titulo.getNumeroTitulo(), 15, "0");
            if (nossoNumero.length() > 15) {
                nossoNumero = nossoNumero.substring(0, 14);
            }
            titulo.setNossoNumero(nossoNumero);
        }
    }
}