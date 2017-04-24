package br.com.ieptbto.cra.webservice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "remessa")
@XmlAccessorType(XmlAccessType.FIELD)
public class RelatorioRemessaPendenteVO {

    @XmlElement(name = "arquivo")
    private List<ArquivoRemessaPendente> arquivosRemessaPendente;

    public List<ArquivoRemessaPendente> getArquivosRemessaPendente() {
        return arquivosRemessaPendente;
    }

    public void setArquivoRemessaPendente(List<ArquivoRemessaPendente> arquivosRemessaPendente) {
        this.arquivosRemessaPendente = arquivosRemessaPendente;
    }
}