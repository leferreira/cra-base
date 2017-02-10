package br.com.ieptbto.cra.mediator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.FiliadoDAO;
import br.com.ieptbto.cra.dao.MunicipioDAO;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.SetorFiliado;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.CpfCnpjUtil;
import br.com.ieptbto.cra.util.RemoverAcentosUtil;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class FiliadoMediator extends BaseMediator {

    @Autowired
    MunicipioDAO municipioDAO;
    @Autowired
    FiliadoDAO filiadoDAO;

    public Filiado salvarFiliado(Filiado filiado) {
    	return filiadoDAO.salvar(filiado);
    }

    public Filiado alterarFiliado(Filiado filiado) {
    	return filiadoDAO.alterar(filiado);
    }

    public List<Filiado> buscarListaFiliados(Instituicao instituicao) {
    	return filiadoDAO.buscarListaFiliadosPorConvenio(instituicao);
    }

    public List<Filiado> buscarTodosFiliados() {
    	return filiadoDAO.getAll(Filiado.class);
    }

    public void removerSetor(SetorFiliado setor) {
    	filiadoDAO.removerSertorFiliado(setor);
    }

    public List<SetorFiliado> buscarSetoresFiliado(Filiado filiado) {
    	return filiadoDAO.buscarSetoresFiliado(filiado);
    }

    public List<SetorFiliado> buscarSetoresAtivosFiliado(Filiado filiado) {
    	return filiadoDAO.buscarSetoresAtivosFiliado(filiado);
    }

    public SetorFiliado buscarSetorPadraoFiliado(Filiado filiado) {
    	return filiadoDAO.buscarSetorPadraoFiliado(filiado);
    }

	/**
	 * Processador de arquivo de atualiza~~oes de convênios
	 * 
	 * @param uploadedFile
	 */
	public void processarArquivoAtualizacoesEmpresas(Instituicao convenio, FileUpload uploadedFile) {
		logger.info("Início do processamento do arquivo de convênios para atualização das empresas...");;
		
		List<Filiado> filiados = converterArquivoProcessado(uploadedFile);
		for (Filiado filiado : filiados) {
			Filiado registroSalvo = filiadoDAO.buscarFiliadoConvenioPorCpfCnpj(convenio, filiado.getCnpjCpf());
			if (registroSalvo == null) {
				filiado.setInstituicaoConvenio(convenio);
				
				SetorFiliado setorPadraoCra = new SetorFiliado();
				setorPadraoCra.setDescricao("GERAL");
				setorPadraoCra.setSetorPadraoFiliado(true);
				setorPadraoCra.setSituacaoAtivo(true);
				List<SetorFiliado> setoresFiliado = new ArrayList<SetorFiliado>();
				filiado.setSetoresFiliado(setoresFiliado);
				
				filiadoDAO.salvar(filiado);
				logger.info("Salvando novo registro de empresa filiada...");
			} else {
				registroSalvo.setRazaoSocial(filiado.getRazaoSocial());
				registroSalvo.setCnpjCpf(filiado.getCnpjCpf());
				registroSalvo.setEndereco(filiado.getEndereco());
				registroSalvo.setCep(filiado.getCep());
				registroSalvo.setMunicipio(filiado.getMunicipio());
				registroSalvo.setAtivo(filiado.isAtivo());
				filiadoDAO.alterar(registroSalvo);
				logger.info("Alterando registro de empresa filiada...");
			}
		}
		logger.info("Fim do processamento do arquivo de convênios para atualização das empresas...");
	}

	private List<Filiado> converterArquivoProcessado(FileUpload uploadedFile) {
		List<Filiado> filiadosConvenio = new ArrayList<>();
				
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(uploadedFile.getInputStream()));
			
			String linha = "";
			while ((linha = reader.readLine()) != null) {
				if (StringUtils.isNotBlank(linha.trim())) {
					String dados[] = linha.split(Pattern.quote(";"));
					
					if (dados.length == 7) {
						Filiado filiado = new Filiado();
						filiado.setRazaoSocial(RemoverAcentosUtil.removeAcentos(dados[0]));
						filiado.setCnpjCpf(dados[1]);
						
						if (StringUtils.isNotBlank(filiado.getCnpjCpf()) && CpfCnpjUtil.isValidCNPJ(filiado.getCnpjCpf()) 
								|| CpfCnpjUtil.isValidCPF(filiado.getCnpjCpf())) {
							filiado.setEndereco(RemoverAcentosUtil.removeAcentos(dados[2]));
							filiado.setCep(dados[3]);
							Municipio municipio = municipioDAO.buscarMunicipioPorNome(dados[4]);
							filiado.setMunicipio(municipio);
							if (municipio == null) {
								filiado.setMunicipio(municipioDAO.buscarMunicipioPorNome("PALMAS"));	
							}
							filiado.setUf(dados[5]);
							filiado.setAtivo(dados[6] == "1" ? true : false);
							filiadosConvenio.add(filiado);
						} else {
							logger.info("Empresa rejeitada com documento inválido. "
									+ "Razão Social [ " + filiado.getRazaoSocial() +" ] " + " Documento [ " + filiado.getCnpjCpf() +" ];");
						}
					} else {
						logger.info("Linha rejeitada quantidade de campos de tamanho inválido. Linha [ " + linha +" ] ");
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Não foi possível abrir o arquivo de atualizações enviado.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InfraException("Não foi possível converter os dados do arquivo. Verifique as informações...");
		}
		return filiadosConvenio;
	}
}
