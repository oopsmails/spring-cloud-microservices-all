package com.oopsmails.spring.cloud.microservices.configserver;

import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
//import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class AsymmetricCryptography {
    private Cipher cipher;

    public AsymmetricCryptography() throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher = Cipher.getInstance("RSA");
    }

    //https://docs.oracle.com/javase/8/docs/api/java/security/spec/PKCS8EncodedKeySpec.html
    public PrivateKey getPrivate(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    //https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
    public PublicKey getPublic(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    /**
     * the decryption should be done with the private key and not with the public key
     * and viceversa with
     * the encryption: it should use the public key to encrypt instead of the private ke
     *
     * @param input
     * @param output
     * @param key
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void encryptFile(byte[] input, File output, PublicKey key) throws IOException, GeneralSecurityException {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        writeToFile(output, this.cipher.doFinal(input));
    }

    public void decryptFile(byte[] input, File output, PrivateKey key) throws IOException, GeneralSecurityException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        writeToFile(output, this.cipher.doFinal(input));
    }

    private void writeToFile(File output, byte[] toWrite)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(toWrite);
        fos.flush();
        fos.close();
    }

    public String encryptText(String msg, PublicKey key) throws NoSuchAlgorithmException, NoSuchPaddingException,
            UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.encodeBase64String(cipher.doFinal(msg.getBytes("UTF-8")));
    }

    public String decryptText(String msg, PrivateKey key)
            throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
    }

    public byte[] getFileInBytes(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        byte[] fbytes = new byte[(int) f.length()];
        fis.read(fbytes);
        fis.close();
        return fbytes;
    }

    public static void main1(String[] args) throws Exception {
        AsymmetricCryptography ac = new AsymmetricCryptography();
        PrivateKey privateKey = ac.getPrivate("KeyPair/privateKey");
        PublicKey publicKey = ac.getPublic("KeyPair/publicKey");

        String msg = "oopsmails@github testing RSA!";
        String encrypted_msg = ac.encryptText(msg, publicKey);
        String decrypted_msg = ac.decryptText(encrypted_msg, privateKey);
        System.out.println("Original Message: " + msg + "\nEncrypted Message: " + encrypted_msg
                + "\nDecrypted Message: " + decrypted_msg);

        if (new File("KeyPair/text.txt").exists()) {
            ac.encryptFile(ac.getFileInBytes(new File("KeyPair/text.txt")), new File("KeyPair/text_encrypted.txt"),
                    publicKey);
            ac.decryptFile(ac.getFileInBytes(new File("KeyPair/text_encrypted.txt")),
                    new File("KeyPair/text_decrypted.txt"), privateKey);
        } else {
            System.out.println("Create a file text.txt under folder KeyPair");
        }
    }

    public static void main(String[] args) throws Exception {
//		AsymmetricCryptography ac = new AsymmetricCryptography();
//		PrivateKey privateKey = ac.getPrivateKeyFromString("KeyPair/privateKey");
//		PublicKey publicKey = ac.getPublicFromString("KeyPair/publicKey");
//
//		String msg = "oopsmails@github testing RSA!";
//		String encrypted_msg = ac.encryptText(msg, publicKey);
//		String decrypted_msg = ac.decryptText(encrypted_msg, privateKey);
//		System.out.println("Original Message: " + msg + "\nEncrypted Message: " + encrypted_msg
//				+ "\nDecrypted Message: " + decrypted_msg);

//        final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
//                new ClassPathResource("oopsmails-config-server.jks"), "oopsmails-storepass".toCharArray()
//        );
//
//        String alias = "oopsmails-config-server-key";
//        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias);
//        System.out.println("alias: " + alias + "\npublic key: " + keyPair.getPublic() + "\nprivate key: " + keyPair.getPrivate());
//
//        Cipher cipher = Cipher.getInstance("RSA");
//        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
//
//        String cipherInput = "testbody";
//        byte[] cipherPublic = cipher.doFinal(cipherInput.getBytes(StandardCharsets.UTF_8));
//
//		String cipherPublicString = new String(cipherPublic, StandardCharsets.ISO_8859_1);
//		System.out.println("cipherPublic: " + cipherPublicString);
//
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
//		dataOutputStream.write(cipherPublic);
//		dataOutputStream.flush();
//		byte[] cipherPublic2 = byteArrayOutputStream.toByteArray();
//		System.out.println("cipherPublic2: " + cipherPublic2);
//
//		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cipher.doFinal(cipherInput.getBytes(StandardCharsets.UTF_8)));
//		ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
//		Object readObject = objectInputStream.readObject();
//		System.out.println("cipherPublic3: " + readObject);
//		objectInputStream.close();

    }

	public PrivateKey getPrivateKeyFromString(String theString) throws Exception {
		byte[] keyBytes = theString.getBytes(StandardCharsets.UTF_8);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	public PublicKey getPublicFromString(String theString) throws Exception {
		byte[] keyBytes = theString.getBytes(StandardCharsets.UTF_8);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}
}
