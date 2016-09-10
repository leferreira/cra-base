package br.com.ieptbto.cra.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import br.com.ieptbto.cra.exception.InfraException;

public class DecoderString {

	/*
	 * Retorna o arquivo caso consiga criar o arquivo a partir da string
	 * passada.
	 * 
	 * @var arquivo Ã© a string referente a um arquivo zipado convertido na
	 * base64.
	 * 
	 * @var diretorio Ã© o path onde o arquivo serÃ¡ salvo.
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

	public String encode(String s) {
		return StringUtils.newStringUtf8(Base64.encodeBase64(s.getBytes()));
	}

	public static byte[] loadFile(File file) {
		byte[] bytes = null;

		try {
			InputStream is = new FileInputStream(file);

			long length = file.length();
			bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			is.close();
		} catch (IOException ex) {
			throw new InfraException("Não foi possível codificar o arquivo.");
		}
		return bytes;
	}

	public void zipFolder(String nomePasta, String diretorioDestino) throws IOException {

		File folder = new File(diretorioDestino + nomePasta);
		FileOutputStream fileWriter = new FileOutputStream(diretorioDestino + nomePasta + ".zip");
		ZipOutputStream zip = new ZipOutputStream(fileWriter);

		for (String fileName : folder.list()) {
			byte[] buffer = new byte[2048];
			ZipEntry ze = new ZipEntry(fileName);
			zip.putNextEntry(ze);
			FileInputStream in = new FileInputStream(folder + "/" + fileName);

			int len;
			while ((len = in.read(buffer)) > 0) {
				zip.write(buffer, 0, len);
			}

			in.close();
		}
		zip.closeEntry();
		zip.close();
	}

	public byte[] zipFiles(String directory) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		byte bytes[] = new byte[2048];

		File folder = new File(directory);

		for (String fileName : folder.list()) {
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
		zos.flush();
		baos.flush();
		zos.close();
		baos.close();

		return baos.toByteArray();
	}

	public void unzip(File zipFile, String diretorio, String nomeUnzip) throws IOException {
		final int BUFFER = 2048;

		if (nomeUnzip == null) {
			nomeUnzip = "";
		}

		try {

			int count;

			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;

			File file = new File(diretorio); // diretorio Ã© uma pasta!
			if (!file.exists()) {
				file.mkdirs(); // mkdir() cria somente um diretÃ³rio, mkdirs()
								// cria diretÃ³rios e subdiretÃ³rios.
			}

			while ((entry = zis.getNextEntry()) != null) {
				byte data[] = new byte[BUFFER];
				nomeUnzip = nomeUnzip.replaceAll(".zip", "");
				String saida = diretorio + "/" + nomeUnzip + "_" + entry.getName();
				FileOutputStream fos = new FileOutputStream(saida);

				// System.out.println(saida);

				dest = new BufferedOutputStream(fos, BUFFER);

				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();

			}
			zis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
