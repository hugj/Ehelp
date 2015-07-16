package com.ehelp.server;

import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;

/*@author HanksChan
* @version 1.0  
* This class is to interactive data between the Client
* and the cloud server
*/

public class JSONTrans {

	public JSONTrans() {}
	
	/*send the request with GET method
	* @param an extend url path, like "/account/regist"
	* @return a string that the server respond, also with json format
	*                a string "false"/null indicate some errors happened
	*/
	public String getJSON(String urlpath) {
		ConnectServer cServer = new ConnectServer();
		try {
			HttpURLConnection conn = cServer.getConn(urlpath);	
			conn.setRequestMethod("GET"); // set the request method to POST
			StringBuffer sBuffer = new StringBuffer();
			if (conn.getResponseCode() == 200) {
				String line = null;
				InputStream in = conn.getInputStream();
				BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
				while((line = bReader.readLine()) != null) {
					sBuffer.append(line);
				}
			}
			return sBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}
	}

	/*send the request to the server with datas which 
	* is a json format
	* @param an extend url path, like "/account/regist"
	*                 a string with json format: "{\"key1\":\"value1\", \"key2\": int_value2}"
	* @return a string that the server respond, also with json format
	*                a string "false"/null indicate some errors happened
	*/
	public String postJSON(String urlpath, String jsonData) {
		ConnectServer cServer = new ConnectServer();
		try {
			HttpURLConnection conn = cServer.getConn(urlpath);	
			conn.setRequestMethod("POST"); // set the request method to POST
			byte data[] = jsonData.getBytes("UTF-8"); // use utf-8 coding format to transformat string to a byte array
			conn.getOutputStream().write(data);
			StringBuffer sBuffer = new StringBuffer();
			if (conn.getResponseCode() == 200) {
				String line = null;
				InputStream in = conn.getInputStream();
				BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
				while((line = bReader.readLine()) != null) {
					sBuffer.append(line);
				}
			}
			return sBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}
	}
}
