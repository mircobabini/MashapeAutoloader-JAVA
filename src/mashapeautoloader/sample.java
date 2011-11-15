package mashapeautoloader;

import java.io.IOException;
import java.net.MalformedURLException;
import org.json.JSONException;
import org.json.JSONObject;

public class sample
{
	public static void main (String[] args) throws MalformedURLException, IOException, JSONException
	{
		MashapeAutoloader.auth ("your-public-key", "your-private-key");
		MashapeAutoloader.store ("src/store/");

		JSONObject exec = MashapeAutoloader.exec ("Unshortener", "unshort", "http://wp.me/p1e4Gf-6r");
		if (exec != null)
		{
			String string = exec.getString ("longUrl");
			System.out.println (string);
		}
	}
}
