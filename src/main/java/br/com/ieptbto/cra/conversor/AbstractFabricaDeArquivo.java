package br.com.ieptbto.cra.conversor;

import br.com.ieptbto.cra.conversor.arquivo.ConversorConfirmacao;
import br.com.ieptbto.cra.conversor.arquivo.ConversorRetorno;
import br.com.ieptbto.cra.conversor.arquivo.ConversorRetornoCancelamento;
import br.com.ieptbto.cra.conversor.arquivo.ConversorTitulo;
import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
public abstract class AbstractFabricaDeArquivo {

	protected static final Logger logger = Logger.getLogger(AbstractFabricaDeArquivo.class);
	protected File file;
	protected Arquivo arquivo;
	protected List<Exception> erros;

	public abstract Arquivo converter(File arquivoFisico, Arquivo arquivo, List<Exception> erros);

	public File getFile() {
		return file;
	}

	public void setFile(File arquivoFisico) {
		this.file = arquivoFisico;
	}

	public Arquivo getArquivo() {
		if (arquivo == null) {
			arquivo = new Arquivo();
		}
		return arquivo;
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}


    /**
     * Converte uma instancia de título em um objeto da herança
     *
     * @param titulo
     * @return
     */
    public static TituloVO converterParaTituloVO(Titulo titulo) {
        TituloVO tituloVO = new TituloVO();
        if (TituloRemessa.class.isInstance(titulo)) {
            tituloVO = new ConversorTitulo().converter(TituloRemessa.class.cast(titulo), TituloVO.class);
        } else if (Confirmacao.class.isInstance(titulo)) {
            tituloVO = new ConversorConfirmacao().converter(Confirmacao.class.cast(titulo), TituloVO.class);
        } else if (Retorno.class.isInstance(titulo)) {
            tituloVO = new ConversorRetorno().converter(Retorno.class.cast(titulo), TituloVO.class);
        } else if (RetornoCancelamento.class.isInstance(titulo)) {
            tituloVO = new ConversorRetornoCancelamento().converter(RetornoCancelamento.class.cast(titulo), TituloVO.class);
        }
        return tituloVO;
    }

}