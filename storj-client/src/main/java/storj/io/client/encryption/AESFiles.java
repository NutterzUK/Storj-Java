package storj.io.client.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import storj.io.client.exceptions.StorjFileException;

class AESFiles {

	private byte[] getKeyBytes(final byte[] key) throws Exception {
		byte[] keyBytes = new byte[16];
		System.arraycopy(key, 0, keyBytes, 0, Math.min(key.length, keyBytes.length));
		return keyBytes;
	}

	private Cipher getCipherEncrypt(final byte[] key) throws Exception {
		byte[] keyBytes = getKeyBytes(key);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		return cipher;
	}

	private Cipher getCipherDecrypt(byte[] key) throws Exception {
		byte[] keyBytes = getKeyBytes(key);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
		return cipher;
	}

	public void encrypt(File inputFile, File outputFile, byte[] key) throws Exception {

		checkFreeSpace(inputFile, outputFile);

		Cipher cipher = getCipherEncrypt(key);
		FileOutputStream fos = null;
		CipherOutputStream cos = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(inputFile);
			fos = new FileOutputStream(outputFile);
			cos = new CipherOutputStream(fos, cipher);
			byte[] data = new byte[1024];
			int read = fis.read(data);
			while (read != -1) {
				cos.write(data, 0, read);
				read = fis.read(data);
			}
			cos.flush();
		} finally {
			if (cos != null) {
				cos.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
		System.out.println("Encrypted SIZE " + outputFile.length());

	}

	public void decrypt(File inputFile, File outputFile, byte[] key) throws StorjFileException, Exception {

		checkFreeSpace(inputFile, outputFile);

		long length = inputFile.length();
		System.out.println("Size before decrypt " + length);
		Cipher cipher = getCipherDecrypt(key);
		FileOutputStream fos = null;
		CipherInputStream cis = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(inputFile);
			cis = new CipherInputStream(fis, cipher);
			fos = new FileOutputStream(outputFile);
			byte[] data = new byte[1024];
			int read = cis.read(data);
			while (read != -1) {
				fos.write(data, 0, read);
				read = cis.read(data);
			}
		} finally {
			fos.close();
			cis.close();
			fis.close();
		}
	}

	/**
	 * Checks there is enough free available space to copy the {@code inputFile}
	 * to the {@code outputFile} location. If there is not enough free space a {@link StorjFileException} is thrown.
	 * 
	 * @param inputFile the inputFile
	 * @param outputFile the outputFile
	 * @throws StorjFileException if there is not enough free available space
	 */
	private void checkFreeSpace(final File inputFile, final File outputFile) throws StorjFileException {
		if (inputFile.length() > outputFile.getUsableSpace()) {
			throw new StorjFileException("Error storing file", "Not enough space to store file");
		}
	}
}