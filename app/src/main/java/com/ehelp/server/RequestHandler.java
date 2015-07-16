package com.ehelp.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*@author HanksChan
* @version 1.1
* With this class, Client can send the request
* to the Server and get the response from the Server 
*/

public class RequestHandler {

	/*send the request by GET method
	* @param a url path: "url:port/extend_url?params1&params2"
	* 		 for the ehelp project, must provide the url path
	*        like "http://120.24.208.130:1501/account/login..."
	* @return a string that the server respond with json format
	* 		  a string "false"/null indicate some errors happened
	*/
	public static String sendGetRequest(String urlString){
		
		try {
			
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(500);
			conn.setRequestMethod("GET");
			conn.setDoInput(true); // permit to use the inputstream
			conn.setDoOutput(true); // permit to use the outputstrem
			conn.setUseCaches(false); // deny to use the cache
			
			StringBuffer sBuffer = new StringBuffer();
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
				String line;
				InputStream in = conn.getInputStream();
				BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
				while((line = bReader.readLine()) != null) {
					sBuffer.append(line);
				}
				return sBuffer.toString();
			}
			
		} catch (Exception e) {
		
			e.printStackTrace();
			return "false";
		}
		return "false";
	}
	
	/*send the request by POST method
	* @param a url path: "url:port/extend_url?params1&params2"
	* 		 for the ehelp project, must provide the url path
	*        like "http://120.24.208.130:1501/account/login..."
	*        a String with json format
	* @return a string that the server respond with json format
	* 		  a string "false"/null indicate some errors happened
	*/
	public static String sendPostRequest(String urlString, String jsondata){
		
		try {
			
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(500);
			conn.setRequestMethod("POST");
			conn.setDoInput(true); // permit to use the inputstream
			conn.setDoOutput(true); // permit to use the outputstrem
			conn.setUseCaches(false); // deny to use the cache
			conn.setRequestProperty("Content-Type", "application/json");// set the request Content-Type
			
			byte data[] = jsondata.getBytes("UTF-8"); // use utf-8 coding format to transformat string to a byte array
			conn.getOutputStream().write(data);
			
			StringBuffer sBuffer = new StringBuffer();
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
				String line;
				InputStream in = conn.getInputStream();
				BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
				while((line = bReader.readLine()) != null) {
					sBuffer.append(line);
				}
				return sBuffer.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}
		
		return "false";
	}
}
