package com.ytint.wloaa.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;


public class Crypto {
	/**
	 * Define a hash type enumeration for strong-typing
	 */
	public enum HashType {
		MD5("MD5"), SHA1("SHA-1"), SHA256("SHA-256"), SHA512("SHA-512");
		private String algorithm;

		HashType(String algorithm) {
			this.algorithm = algorithm;
		}

		@Override
		public String toString() {
			return this.algorithm;
		}
	}

	/**
	 * Set-up MD5 as the default hashing algorithm
	 */
	private static final HashType DEFAULT_HASH_TYPE = HashType.SHA256;

	/**
	 * Create a password hash using the default hashing algorithm
	 * 
	 * @param input
	 *            The password
	 * @return The password hash
	 */
	public static String passwordHash(String input) {
		return passwordHash(input, DEFAULT_HASH_TYPE);
	}

	public static String passwordHash(String input, HashType hashType) {
		try {
			MessageDigest m = MessageDigest.getInstance(hashType.toString());
			byte[] out = m.digest(input.getBytes());
			return new String(Base64.encodeBase64(out));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
