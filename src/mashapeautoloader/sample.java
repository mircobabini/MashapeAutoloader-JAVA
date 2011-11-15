package mashapeautoloader;

import java.io.IOException;
import java.net.MalformedURLException;
import org.json.JSONException;
import org.json.JSONObject;

public class sample
{
	public static void main (String[] args) throws MalformedURLException, IOException, JSONException
	{
		MashapeAutoloader.auth ("PUBbxB$sw6HHHxqMaFeiWYlxHu4pHzuS", "PRIY6gfUmS4UHZlQke%dzR79mTrkGpix");
		MashapeAutoloader.store ("src/store/");

		JSONObject exec = MashapeAutoloader.exec ("Unshortener", "unshort", "http://ddg.gg");
		if (exec != null)
		{
			String string = exec.getString ("longUrl");
			System.out.println (string);
		}
	}
}
