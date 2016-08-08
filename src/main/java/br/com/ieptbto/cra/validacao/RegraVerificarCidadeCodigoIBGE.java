package br.com.ieptbto.cra.validacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.validacao.regra.RegraCabecalho;

@Service
public class RegraVerificarCidadeCodigoIBGE extends RegraCabecalho {

    @Autowired
    private MunicipioMediator municipioMediator;

    public void executar(CabecalhoRemessa cabecalho, List<Exception> erros) {
        setErros(erros);
        setCabecalhoRemessa(cabecalho);
        executar();

    }

    @Override
    protected void executar() {
        Municipio municipio = municipioMediator.buscaMunicipioPorCodigoIBGE(getCabecalhoRemessa().getCodigoMunicipio());
        if (municipio == null) {
            throw new InfraException("Código do Município " + getCabecalhoRemessa().getCodigoMunicipio() + " não encontrado ou inválido!");
        }

        if (!TipoArquivoEnum.REMESSA.equals(getCabecalhoRemessa().getRemessa().getArquivo().getTipoArquivo().getTipoArquivo())) {

            if (getCabecalhoRemessa().getRemessa().getInstituicaoOrigem() != null
                    && getCabecalhoRemessa().getRemessa().getInstituicaoOrigem().getMunicipio() != null) {
                Municipio municipioEnvio = municipioMediator
                        .carregarMunicipio(getCabecalhoRemessa().getRemessa().getInstituicaoOrigem().getMunicipio());

                if (!municipio.equals(municipioEnvio)) {
                    throw new InfraException("Código do Município no cabeçalho não pertence a instituição que está enviando o arquivo!");
                }
            }

            throw new InfraException("Código do Município no cabeçalho não pertence a instituição que está enviando o arquivo!");
        }
    }
}
