/*
 * Mashape APIs' Autoloader library.
 *
 * Copyright (C) 2011 _lamemind, mirkolofio.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * The authors of this software are mirkolofio, _lamemind.
 * For any question or feedback please
 *		contact me at: mirkolofio(at)gmail(dot)com
 *		or surf my website: http://mirkolofio.net/
 *
 *
 * Some infos about this lib: http://wp.me/p1e4Gf-6r
 */
package mashapeautoloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.json.JSONObject;

public class MashapeAutoloader
{
	private static final String MASHAPE_DOWNLOAD_ROOT =
		"http://www.mashape.com/apis/download-java-client?componentName=";

	private static String apiStore;

	private static String publicKey;
	private static String privateKey;


	
	/**
	 * @param publicKey
	 * @param privateKey
	 *
	 * Set mashape authentication keys
	 */
	public static void auth (String publicKey, String privateKey)
	{
		MashapeAutoloader.publicKey = publicKey;
		MashapeAutoloader.privateKey = privateKey;
	}
	/**
	 * @param apiStore
	 *
	 * Set the place where apis stored
	 */
	public static void store (String apiStore)
	{
		MashapeAutoloader.apiStore = apiStore;
	}



	private static class ClassLoaderExt extends ClassLoader {
		public Class<?> defineClassCallable (String className, byte[] bytes)
		{
			return defineClass (className, bytes, 0, bytes.length);
		}
	}

	public static JSONObject exec (String librayName, String methodName, String... arguments) throws MalformedURLException, IOException
	{
		// check for local api store
		if (MashapeAutoloader.apiStore == null)
			return null;

		// check for library existance
		if (!downloadLib (librayName))
			return null;


		Object libraryInstance;
		Method method;
		Object result;
		JSONObject trueResult;

		try
		{
			// instance .class library
			ClassLoaderExt loader = new ClassLoaderExt ();

			String filePath = new File (apiStore + librayName + ".class").getAbsolutePath ();
			String fileUrl = "file:" + filePath.replace ("\\", "/");
            URL myUrl = new URL (fileUrl);
            URLConnection connection = myUrl.openConnection ();
            InputStream input = connection.getInputStream ();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream ();

			int data = input.read ();
            while (data != -1)
			{
                buffer.write (data);
                data = input.read ();
            }
            input.close();

            byte[] classData = buffer.toByteArray ();
			Class<?> libraryClass = loader.defineClassCallable (librayName, classData);


			// call constructor
			try {
				Constructor<?> constructor = libraryClass.getConstructor (
					String.class, String.class);
				libraryInstance = constructor.newInstance (publicKey, privateKey);
			}
			catch (NoSuchMethodException ex) {
				throw new RuntimeException (ex);
			}
			catch (SecurityException ex) {
				throw new RuntimeException (ex);
			}
			catch (InstantiationException ex) {
				throw new RuntimeException (ex);
			}
			catch (IllegalAccessException ex) {
				throw new RuntimeException (ex);
			}
			catch (IllegalArgumentException ex) {
				throw new RuntimeException (ex);
			}
			catch (InvocationTargetException ex) {
				throw new RuntimeException (ex);
			}


			// invoke method
			try
			{
				switch (arguments.length)
				{
					case 0:
						method = libraryClass.getMethod (methodName);
						result = method.invoke (libraryInstance);
						break;

					case 1:
						method = libraryClass.getMethod (methodName, String.class);
						result = method.invoke (libraryInstance, arguments[0]);
						break;

					case 2:
						method = libraryClass.getMethod (methodName, String.class);
						result = method.invoke (libraryInstance, arguments[0], arguments[1]);
						break;

					case 3:
						method = libraryClass.getMethod (methodName, String.class);
						result = method.invoke (libraryInstance, arguments[0], arguments[1], arguments[2]);
						break;

					case 4:
						method = libraryClass.getMethod (methodName, String.class);
						result = method.invoke (libraryInstance, arguments[0], arguments[1], arguments[2], arguments[3]);
						break;

					case 5:
						method = libraryClass.getMethod (methodName, String.class);
						result = method.invoke (libraryInstance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
						break;

					default:
						throw new NoSuchMethodException ();
				}
			}
			catch (NoSuchMethodException ex) {
				throw new RuntimeException (ex);
			}
			catch (SecurityException ex) {
				throw new RuntimeException (ex);
			}
			catch (IllegalAccessException ex) {
				throw new RuntimeException (ex);
			}
			catch (IllegalArgumentException ex) {
				throw new RuntimeException (ex);
			}
			catch (InvocationTargetException ex) {
				throw new RuntimeException (ex);
			}
		}
		catch (Exception ex) {
			throw new RuntimeException (ex);
		}


		// convert result
		try {
			trueResult = (JSONObject)result;
		}
		catch (ClassCastException ex) {
			throw new RuntimeException (ex);
		}

		
		return trueResult;
	}

	/**
	 * @param libraryName
	 *
	 * Returns false if something wrong, otherwise true (API interface
	 * already exists or just well downloaded)
	 */
	private static boolean downloadLib (String libraryName)
	{
		URL url;
		URLConnection urlConn;

		// check (or make) for apiStore directory
		File apiStoreDir = new File (apiStore);
		if (!apiStoreDir.isDirectory ())
			apiStoreDir.mkdir ();


		String javaFilePath = apiStore + libraryName + ".java";
		File javaFile = new File (javaFilePath);
		
		// check if the API interface exists
		if (javaFile.exists ())
			return true;
		try
		{
			// download the API interface's archive
			url = new URL (MASHAPE_DOWNLOAD_ROOT + libraryName);
			urlConn = url.openConnection ();

			HttpURLConnection httpConn = (HttpURLConnection)urlConn;
			httpConn.setInstanceFollowRedirects (false);

			urlConn.setDoInput (true);
			urlConn.setDoOutput (false);
			urlConn.setUseCaches (false);

			// extract the archive stream
			ZipInputStream zip = new ZipInputStream (urlConn.getInputStream ());
			String expectedEntryName = libraryName + ".java";
			while (true)
			{
				ZipEntry nextEntry = zip.getNextEntry ();
				if (nextEntry == null)
					return false;

				String name = nextEntry.getName ();
				if (name.equals (expectedEntryName))
				{
					// save .java locally
					FileOutputStream javaFileStream = new FileOutputStream (javaFilePath);
					byte[] buf = new byte[1024];
					int n;
					while ((n = zip.read (buf, 0, 1024)) > -1)
						javaFileStream.write (buf, 0, n);

					// compile it into .class file
					JavaCompiler compiler = ToolProvider.getSystemJavaCompiler ();
					int result = compiler.run (null, null, null, javaFilePath);
					System.out.println (result);
					return result == 0;
				}
			}
		}
		catch (Exception ex) {
			throw new RuntimeException (ex);
		}
	}
}
