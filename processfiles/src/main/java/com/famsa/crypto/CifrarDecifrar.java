package com.famsa.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CifrarDecifrar {

	public static void main(String[] args) {
		
		byte[] resfer = null;
		
		try {
			resfer = cifra("C:/Famsa/Delphi/ProcessFiles/Output%20directory/Win32/Debug/Expedientes/Entrada/065Prom.tif/");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("resfer:"+resfer);
		
		String str = new String(resfer, StandardCharsets.UTF_8);
		
		System.out.println("str:"+str);
		
		
		
		
		String resfer2 = null;
		
		try {
			resfer2=descifra(resfer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("resfer2:"+resfer2);
	}
	
	public static byte[] cifra(String sinCifrar) throws Exception {
		final byte[] bytes = sinCifrar.getBytes("UTF-8");
		final Cipher aes = obtieneCipher(true);
		final byte[] cifrado = aes.doFinal(bytes);
		return cifrado;
	}

	public static String descifra(byte[] cifrado) throws Exception {
		final Cipher aes = obtieneCipher(false);
		final byte[] bytes = aes.doFinal(cifrado);
		final String sinCifrar = new String(bytes, "UTF-8");
		return sinCifrar;
	}

	private static Cipher obtieneCipher(boolean paraCifrar) throws Exception {
		final String frase = "FraseLargaConDiferentesLetrasNumerosYCaracteresEspeciales_áÁéÉíÍóÓúÚüÜñÑ1234567890!#%$&()=%_NO_USAR_ESTA_FRASE!_";
		final MessageDigest digest = MessageDigest.getInstance("SHA");
		digest.update(frase.getBytes("UTF-8"));
		final SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");

		final Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
		if (paraCifrar) {
			aes.init(Cipher.ENCRYPT_MODE, key);
		} else {
			aes.init(Cipher.DECRYPT_MODE, key);
		}

		return aes;
	}
	
}
