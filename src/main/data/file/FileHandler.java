package main.data.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import main.presentation.Logger;

public abstract class FileHandler
{
	protected static final String ROOT_PATH = System.getProperty("user.dir") + File.separator;
	protected abstract String getExtension();
	protected abstract String getDataPath();
	
	protected void createDirectory(String directory)
	{
		File newFolder = new File(directory);
		boolean success = newFolder.mkdirs();

		if (!success)
			Logger.error("Could not create data directory for " + directory + "!");
	}

	protected void deleteDirectory(String directory)
	{
		File folder = new File(directory);

		if (folder.listFiles() == null)
		{
			Logger.warn("No directory to delete.");
			return;
		}

		for (File file : folder.listFiles())
		{
			file.delete();
		}

		folder.delete();
	}
	
	protected void copyFileAndCatchException(File file, String newDirectory)
	{
		try
		{
			Files.copy(file.toPath(), Paths.get(newDirectory + File.separator + file.getName()), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e)
		{
			Logger.error(e.toString());
		}
	}

	protected String getFileExtension(File file)
	{
		String name = file.getName();
		try
		{
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e)
		{
			return "";
		}
	}
	
	protected String getFileNameNoExtension(File file)
	{
		String name = file.getName();
		try
		{
			return name.substring(0, name.lastIndexOf("."));
		} catch (Exception e)
		{
			return "";
		}
	}

	public void zipDirectory(String directory, String extension) throws IOException
	{
		byte[] buffer = new byte[1024];

		FileOutputStream fos = new FileOutputStream(directory + extension);
		ZipOutputStream zos = new ZipOutputStream(fos);

		File folder = new File(directory);

		for (File file : folder.listFiles())
		{
			String fileName = file.getAbsoluteFile().toString();
			String zipEntryString = fileName.substring(directory.length() + 1, fileName.length());

			ZipEntry ze = new ZipEntry(zipEntryString);

			zos.putNextEntry(ze);

			FileInputStream in = new FileInputStream(directory + File.separator + zipEntryString);

			int len;
			while ((len = in.read(buffer)) > 0)
			{
				zos.write(buffer, 0, len);
			}

			in.close();
		}

		zos.closeEntry();
		zos.close();
	}

	public void unzipSaveDir(String zipFileName, String extension) throws IOException
	{
		byte[] buffer = new byte[1024];

		// get the zip file content
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFileName + extension));
		// get the zipped file list entry
		ZipEntry ze = zis.getNextEntry();

		while (ze != null)
		{

			String fileName = ze.getName();
			File newFile = new File(zipFileName + File.separator + fileName);

			// create all non exists folders
			// else you will hit FileNotFoundException for compressed folder
			new File(newFile.getParent()).mkdirs();

			FileOutputStream fos = new FileOutputStream(newFile);

			int len;
			while ((len = zis.read(buffer)) > 0)
			{
				fos.write(buffer, 0, len);
			}

			fos.close();
			ze = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();
	}

	protected boolean writeLine(String path, String line)
	{
		PrintWriter out;

		try
		{
			out = new PrintWriter(new FileWriter(path, true));
			out.println(line);
			out.close();
		} catch (IOException e)
		{
			return false;
		}

		return true;
	}

	protected List<String> loadFile(String path)
	{
		List<String> returnLines = new ArrayList<String>();
		BufferedReader in;
		Scanner s;

		try
		{
			in = new BufferedReader(new FileReader(path));
			s = new Scanner(in);

			while (s.hasNextLine())
			{
				returnLines.add(s.nextLine());
			}

			in.close();
			s.close();
		} catch (IOException e)
		{
			Logger.warn("Could not read file " + path);
			return new ArrayList<String>();
		}

		return returnLines;
	}
}
