package br.com.ieptbto.cra.slip;

import br.com.ieptbto.cra.enumeration.PadraoArquivoDePara;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.util.List;

/**
 * @author Thasso Araújo
 *
 * @param <T>
 */
public abstract class AbstractDePara {

	protected PadraoArquivoDePara padrao;

	public PadraoArquivoDePara getPadrao() {
		return this.padrao;
	}

	public abstract List<?> processar(FileUpload file);
}
