package com.dekses.jersey.docker.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageManipulation {

	public static String covertToString(String filePath) {
		File file = new File(filePath);

		try {
			// Reading a Image file from file system
			FileInputStream imageInFile = new FileInputStream(file);
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);

			// Converting Image byte array into Base64 String
			String imageDataString = encodeImage(imageData);

			// compress code
			// ByteArrayOutputStream os = new ByteArrayOutputStream();
			// GZIPOutputStream gzip = new GZIPOutputStream(os);
			// gzip.write(imageData);
			// gzip.close();
			//
			// byte[] compressed = os.toByteArray();
			// String newImageString = encodeImage(compressed);

			imageInFile.close();

			return imageDataString;
		} catch (FileNotFoundException e) {
			System.out.println("Image not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the Image " + ioe);
		}
		return null;
	}

	public static void covertToImage(String imageDataString, String filepath) {

		File file = new File(filepath);

		try {
			// decompress code
			// final int BUFFER_SIZE = 32;
			// ByteArrayInputStream is = new
			// ByteArrayInputStream(decodeImage(imageDataString));
			// GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
			// ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// byte[] data = new byte[BUFFER_SIZE];
			// int bytesRead;
			// while ((bytesRead = gis.read(data)) != -1) {
			// baos.write(data, 0, bytesRead);
			// }
			// gis.close();

			// Converting a Base64 String into Image byte array
			byte[] imageByteArray = decodeImage(imageDataString);

			// Write a image byte array into file system
			FileOutputStream imageOutFile = new FileOutputStream(file);

			imageOutFile.write(imageByteArray);

			imageOutFile.close();
		} catch (FileNotFoundException e) {
			System.out.println("Image not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the Image " + ioe);
		}
	}

	/**
	 * Encodes the byte array into base64 string
	 *
	 * @param imageByteArray
	 *            - byte array
	 * @return String a {@link java.lang.String}
	 */
	public static String encodeImage(byte[] imageByteArray) {
		return Base64.getEncoder().encodeToString(imageByteArray);
	}

	/**
	 * Decodes the base64 string into byte array
	 *
	 * @param imageDataString
	 *            - a {@link java.lang.String}
	 * @return byte array
	 */
	public static byte[] decodeImage(String imageDataString) {
		return Base64.getDecoder().decode(imageDataString);
	}
}