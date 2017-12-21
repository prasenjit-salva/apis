package com.java.customapi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.ant.compress.taskdefs.Unzip;
import org.apache.commons.io.FilenameUtils;

public class CustomZipUtils {

	private final List<File> fileList;
	private Set<String> paths;
	private File zipCustomFile;
	private boolean isOnlyContent = false;
	private static final String ZIP_EXTENSION = ".zip";
	private static final int BUFFER_SIZE = 4096;

	public CustomZipUtils() {
		fileList = new ArrayList<File>();
		paths = new HashSet<String>();
	}

	public void zipFolderContentsOnlyAtSameLocation(File sourceFile)
			throws FileNotFoundException {
		zipCustomFile = new File(getZipFilePath(sourceFile));
		isOnlyContent = true;
		zipFolder(sourceFile);
	}

	public void zipFolderContentsOnlyAtGivenLocation(File sourceFile,
			File destZipFile) throws FileNotFoundException {
		zipCustomFile = new File(getZipFilePath(destZipFile));
		isOnlyContent = true;
		zipFolder(sourceFile);
	}

	public void zipFolderWithRootFolderAtSameLocation(File sourceFile)
			throws FileNotFoundException {
		zipCustomFile = new File(getZipFilePath(sourceFile));
		isOnlyContent = false;
		zipFolder(sourceFile);
	}

	public void zipFolderWithRootFolderAtGivenLocation(File sourceFile,
			File destZipFile) throws FileNotFoundException {
		zipCustomFile = new File(getZipFilePath(destZipFile));
		isOnlyContent = false;
		zipFolder(sourceFile);
	}

	private void zipFolder(File sourceFile) throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(zipCustomFile);
		ZipOutputStream zos = new ZipOutputStream(fos);

		if (sourceFile.isDirectory()) {
			addFilelistToPath(sourceFile);
		}

		if (!sourceFile.isDirectory()) {
			closeZipOutputStream(zos);
			throw new FileNotFoundException(
					"Given file must be a source folder");
		}
		try {
			for (File file : this.fileList) {
				String path = getUserSpecifiedFolderStructurePath(file
						.getParent().trim(), sourceFile);
				path = addFolderStructureToZipHeirarchy(path, zos);
				String entryName = path + file.getName();
				ZipEntry ze = new ZipEntry(entryName);
				zos.putNextEntry(ze);
				writeToZipOutputstream(zos, file);
				System.out.println("Zip Files Entry" + entryName);
			}
			zos.closeEntry();

		} catch (IOException ex) {
			if (ex.getMessage().contains("duplicate entry")) {
				return;
			}
			Log.log(ex.getMessage());
			return;
		} finally {
			closeZipOutputStream(zos);
		}
	}

	public void unzip(String zipFilePath, String destDirectory) {
		File zipFileCopied = new File(zipFilePath);
		Unzip unzipper = new Unzip();
		unzipper.setSrc(zipFileCopied);
		unzipper.setDest(new File(destDirectory));
		unzipper.execute();
		if (zipFileCopied.exists())
			zipFileCopied.delete();
	}

	

	private void closeZipOutputStream(ZipOutputStream zos) {
		if (zos != null)
			try {
				zos.close();
			} catch (IOException e) {
				Log.log(e.getMessage());
			}
	}

	private String addFolderStructureToZipHeirarchy(String path,
			ZipOutputStream zos) throws IOException {
		if (path.length() > 0) {
			if (!paths.contains(path)) {
				paths.add(path);
				ZipEntry ze = new ZipEntry(path + "/");
				zos.putNextEntry(ze);
				zos.closeEntry();
			}
			path += "/";
		}
		return path;
	}

	private void writeToZipOutputstream(ZipOutputStream zos, File file)
			throws IOException {
		byte[] buffer = new byte[1024];
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
		} finally {
			in.close();
		}
	}

	private String getUserSpecifiedFolderStructurePath(String path,
			File sourceFile) {
		String sourcePath = sourceFile.getParentFile().getPath();
		if (isOnlyContent) {
			String root = sourceFile.getName();
			path = path.substring(sourcePath.length() + root.length() + 1);
		} else {
			path = path.substring(sourcePath.length());
		}
		return (path.startsWith(File.separator)) ? path.substring(1) : path;
	}

	private String getPathWithoutExtension(File file) {
		return FilenameUtils.removeExtension(file.getAbsolutePath());
	}

	private String getZipFilePath(File sourceFile) {
		return getPathWithoutExtension(sourceFile) + ZIP_EXTENSION;
	}

	private void addFilelistToPath(File node) {
		if (node.isFile()) {
			fileList.add(node);
		}
		if (node.isDirectory()) {
			File[] subNote = node.listFiles();
			for (File filename : subNote) {
				addFilelistToPath(filename);
			}
		}
	}
}
