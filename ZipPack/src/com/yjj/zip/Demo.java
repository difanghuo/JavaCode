package com.yjj.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Demo {

	public static final int DAILY_LOG_LENGTH = 14;

	public static void main(String[] args) {
		try {
			doZip("C:/Users/YJJ/Desktop/Log",
					"C:/Users/YJJ/Desktop/Log/2017-07.zip");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File doZip(String sourceDir, String zipFilePath)
			throws IOException {

		File file = new File(sourceDir);
		File zipFile = new File(zipFilePath);
		ZipOutputStream zos = null;
		try {
			// 创建写出流操作
			OutputStream os = new FileOutputStream(zipFile);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			zos = new ZipOutputStream(bos);

			String basePath = null;

			// 获取目录
			if (file.isDirectory()) {
				basePath = file.getPath();
			} else {
				basePath = file.getParent();
			}

			zipFile(file, basePath, zos);
		} finally {
			if (zos != null) {
				zos.closeEntry();
				zos.close();
			}
		}

		return zipFile;
	}

	/**
	 * @param source
	 *            源文件
	 * @param basePath
	 * @param zos
	 */
	private static void zipFile(File source, String basePath,
			ZipOutputStream zos) throws IOException {
		File[] files = null;
		if (source.isDirectory()) {
			files = source.listFiles();
		} else {
			files = new File[1];
			files[0] = source;
		}

		InputStream is = null;
		String pathName;
		byte[] buf = new byte[1024];
		int length = 0;
		try {
			for (File file : files) {
				if (file.isDirectory()) {
					pathName = file.getPath().substring(basePath.length() + 1)
							+ "/";
					zos.putNextEntry(new ZipEntry(pathName));
					zipFile(file, basePath, zos);
				} else {
					pathName = file.getPath().substring(basePath.length() + 1);
					if (pathName.length() == DAILY_LOG_LENGTH) {// 每日log文件名的固定长度
//						System.out.println("pathName:" + pathName);
						is = new FileInputStream(file);
						BufferedInputStream bis = new BufferedInputStream(is);
						zos.putNextEntry(new ZipEntry(pathName));
						while ((length = bis.read(buf)) > 0) {
							zos.write(buf, 0, length);
						}
						bis.close();
						file.delete();
					}

				}
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}

	}
}
