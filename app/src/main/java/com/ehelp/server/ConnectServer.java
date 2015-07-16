package com.ehelp.server;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*@author HanksChan
* @version 1.0  
* This class is design to connect to the cloud server
* Cloud server's ip address: 120.24.208.130
* Default port for team1: 1501
*/
public class ConnectServer {

	private String baseURL = "http://127.0.0.1";
	private int defaultPort = 8080;

	public ConnectServer() {}

	/* change baseURL, default 127.0.0.1
	* @params a URL String
	* @return
	*/
	public void changeURL(String url) {
		baseURL = url;
	}

	/* change port, default 8080
	* @params a port number
	* @return
	*/
	public void changePort(int port) {
		defaultPort = port;
	}

	/*  get HttpURLConnection
	* @params an extend url path, like "/account/regist"
	* @return a HttpURLConnection
	*/
	public HttpURLConnection getConn(String urlpath) {
		String finalURL = baseURL + ":" + Integer.toString(defaultPort) + urlpath;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(finalURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true); // permit to use the inputstream
			conn.setDoOutput(true); // permit to use the outputstrem
			conn.setUseCaches(false); // deny to use the cache
			conn.setRequestProperty("Content-Type", "application/json");// set the request Content-Type
		}  catch(MalformedURLException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return conn;
	}
}
