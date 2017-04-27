package br.com.ieptbto.cra.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 
 * @author Leandro
 *
 */
public class ZipFile {

    public static final int BUFFER = 2048;

    /**
     * Retorna o arquivo caso consiga criar o arquivo a partir da string
     * passada.
     * 
     * @var arquivo é a string referente a um arquivo zipado convertido na
     *      base64.
     * 
     * @var diretorio é o path onde o arquivo será salvo.
     * 
     */
    public File decode(String arquivo, String diretorio, String zip) throws FileNotFoundException, IOException {

        byte[] arquivoDecodificado = Base64.decodeBase64(arquivo);

        File dir = new File(diretorio);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(diretorio + zip);

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(arquivoDecodificado);
        bos.close();

        return file;
    }

    public static String encode(String s) {
        return StringUtils.newStringUtf8(Base64.encodeBase64(s.getBytes()));
    }

    public static String decode(String s) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(s));
    }

    /**
     * 
     * compacta uma string e retorna um array de bytes compactados
     * 
     * @param file
     * @param dados
     * @return
     * @throws IOException
     */
    public static byte[] zipFile(String fileName, String dados) throws IOException {

        if (org.apache.commons.lang.StringUtils.isEmpty(dados)) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        byte bytes[] = new byte[2048];

        zos.putNextEntry(new ZipEntry(fileName));
        bytes = dados.getBytes();
        zos.write(bytes, 0, bytes.length);

        zos.closeEntry();
        zos.flush();
        zos.close();
        baos.flush();
        baos.close();

        return baos.toByteArray();
    }

    public static byte[] zipFiles(String directory, String nameFile) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        byte bytes[] = new byte[2048];

        File folder = new File(directory);

        for (String fileName : folder.list()) {
        	if (!fileName.equals(nameFile)) {
        		FileInputStream fis = new FileInputStream(directory + "/" + fileName);
        		BufferedInputStream bis = new BufferedInputStream(fis);
        		
        		zos.putNextEntry(new ZipEntry(fileName));
        		
        		int bytesRead;
        		while ((bytesRead = bis.read(bytes)) != -1) {
        			zos.write(bytes, 0, bytesRead);
        		}
        		zos.closeEntry();
        		bis.close();
        		fis.close();
        	}
        }
        zos.flush();
        baos.flush();
        zos.close();
        baos.close();

        return baos.toByteArray();
    }

    /**
     * Converte um array de bytes zipado em um array de bytes descompactado
     * 
     * @param byteIn
     * @return
     * @throws IOException
     */
    public static byte[] unzip(byte[] byteIn) throws IOException {
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(byteIn));

        while ((zis.getNextEntry()) != null) {
            final byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = zis.read(buffer)) > 0) {
                dest.write(buffer, 0, length);
            }

            dest.flush();
            dest.close();
            zis.close();

            return dest.toString().getBytes();

        }

        return null;
    }

    public static void main(String[] args) {

        try {
            byte[] baos = ZipFile.zipFile("D:\\Projetos\\CRA\\MyZip.xml",
                    "Temos uma disputa. E o Galo está nela! Com a quinta vitória seguida no Campeonato Brasileiro, a equipe alvinegra entrou de vez na briga pelo título da competição, justamente ao final do primeiro turno. O bom momento mostra que Marcelo Oliveira encontrou a forma ideal de o Atlético-MG atuar e brigar no returno pelo segundo título do campeonato nacional da sua história. Mas coloca uma dúvida sobre seus planos: voltar ou não com os laterais considerados titulares?");

            // System.out.println(baos);

            File arquivo = new File("D:\\Projetos\\CRA\\MyTeste.zip");
            FileOutputStream fout = new FileOutputStream(arquivo);
            fout.write(baos);
            fout.close();

            byte[] unzip = ZipFile.unzip(baos);
            File arquivo1 = new File("D:\\Projetos\\CRA\\MyTeste1.txt");
            FileOutputStream fout1 = new FileOutputStream(arquivo1);
            fout1.write(unzip);
            fout1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
