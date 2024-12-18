package cn.jarlen.photoedit.net;

/**
 * ZipDownloader
 * 
 * A simple app to demonstrate downloading and unpacking a .zip file
 * as a background task.
 * 
 * Copyright (c) 2011 Michael J. Portuesi (http://www.jotabout.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

/**
 * Unpack ZIP file
 * 
 * Stolen from: http://www.jondev.net/articles/Unzipping_Files_with_Android_(
 * Programmatically)
 * 
 * fixed to: 1) ensure cleanup of resources 2) decompress individual files
 * transactionally 3) buffered I/O to make it work 10x faster
 * 
 * IO Exceptions are rethrown as RuntimeException (not checked).
 */
public class ZipUnPack
{
	private static final int BUFFER_SIZE = 8192;

	private String _zipFile;
	private String _location;
	private byte[] _buffer;

	/**
	 * Constructor.
	 * 
	 * @param zipFile
	 *            Fully-qualified path to .zip file
	 * @param location
	 *            Fully-qualified path to folder where files should be written.
	 *            Path must have a trailing slash.
	 */
	public ZipUnPack(String zipFile, String location)
	{
		_zipFile = zipFile;
		_location = location;
		_buffer = new byte[BUFFER_SIZE];
		dirChecker("");
	}

	public String unzip()
	{
		FileInputStream fin = null;
		ZipInputStream zin = null;
		OutputStream fout = null;

		File outputDir = new File(_location);
		File tmp = null;

		String dirName = null;

		Boolean isSucess = true;
		try
		{
			// fin = new FileInputStream(_zipFile);
			fin = new FileInputStream(new File(_zipFile));
			zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null)
			{

				if (ze.isDirectory())
				{
					dirChecker(ze.getName());
					dirName = outputDir.getAbsolutePath() + "/" + ze.getName();

				} else
				{
					tmp = File.createTempFile("decomp", ".tmp", outputDir);
					fout = new BufferedOutputStream(new FileOutputStream(tmp));
					copyStream(zin, fout, _buffer, BUFFER_SIZE);
					zin.closeEntry();
					fout.close();
					fout = null;
					tmp.renameTo(new File(_location + ze.getName()));
					tmp = null;
				}
			}
			// zin.close();
			// zin = null;
		} catch (IOException e)
		{
			isSucess = false;
			e.printStackTrace();
			Log.i("===", " IOException ");
			return dirName;
			// throw new RuntimeException(e);
		} finally
		{
			if (tmp != null)
			{
				try
				{
					tmp.delete();
				} catch (Exception ignore)
				{

				}
			}
			if (fout != null)
			{
				try
				{
					fout.close();
				} catch (Exception ignore)
				{
					;
				}
			}
			if (zin != null)
			{
				try
				{
					zin.closeEntry();
				} catch (Exception ignore)
				{
					;
				}
			}
			if (fin != null)
			{
				try
				{
					fin.close();
				} catch (Exception ignore)
				{

				}
			}

			isSucess = true;
		}

		return dirName;
	}

	private void dirChecker(String dir)
	{
		File f = new File(_location + dir);

		if (!f.isDirectory())
		{
			f.mkdirs();
		}
	}

	/**
	 * Copy from one stream to another. Throws IOException in the event of error
	 * (for example, SD card is full)
	 * 
	 * @param is
	 *            Input stream.
	 * @param os
	 *            Output stream.
	 * @param buffer
	 *            Temporary buffer to use for copy.
	 * @param bufferSize
	 *            Size of temporary buffer, in bytes.
	 */
	private void copyStream(InputStream is, OutputStream os, byte[] buffer,
			int bufferSize) throws IOException
	{
		try
		{
			for (;;)
			{
				int count = is.read(buffer, 0, bufferSize);
				if (count == -1)
				{
					break;
				}
				os.write(buffer, 0, count);
			}
		} catch (IOException e)
		{
			throw e;
		}
	}
}