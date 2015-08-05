package com.ehelp.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/*@author HanksChan
* @version 1.2
* With this class, Client can send the request
* to the Server and get the response from the Server 
*/

public class RequestHandler {

	/* send the request by GET method
	 * @param a url path: "url:port/extend_url?params1&params2"
	 * 		  for the ehelp project, must provide the url path
	 *        like "http://120.24.208.130:1501/account/login..."
	 * @return a string that the server respond with json format
	 * 		   a string "false"/null indicate some errors happened
	 */
	public static String sendGetRequest(String urlString){
		
		try {
			
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(1000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true); // permit to use the inputstream
			conn.setDoOutput(true); // permit to use the outputstrem
			conn.setUseCaches(false); // deny to use the cache
			
			StringBuffer sBuffer = new StringBuffer();
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
				String line = null;
				InputStream in = conn.getInputStream();
				BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
				while((line = bReader.readLine()) != null) {
					sBuffer.append(line);
				}
				return sBuffer.toString();
			}
			
		} catch (Exception e) {
		
			e.printStackTrace();
			
		}
		
		return "false";
	}
	
	/* send the request by POST method
	 * @param a url path
	 * 		  for the ehelp project, must provide the url path
	 *        like "http://120.24.208.130:1501/account/login..."
	 *        a String with json format
	 * @return a string that the server respond with json format
	 * 		   a string "false"/null indicate some errors happened
	 */
	public static String sendPostRequest(String urlString, String jsondata){
		
		try {
			
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(1000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true); // permit to use the inputstream
			conn.setDoOutput(true); // permit to use the outputstrem
			conn.setUseCaches(false); // deny to use the cache
			conn.setRequestProperty("Content-Type", "application/json");// set the request Content-Type
			
			byte data[] = jsondata.getBytes("UTF-8"); // use utf-8 coding format to transformat string to a byte array
			conn.getOutputStream().write(data);
			
			StringBuffer sBuffer = new StringBuffer();
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
				String line = null;
				InputStream in = conn.getInputStream();
				BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
				while((line = bReader.readLine()) != null) {
					sBuffer.append(line);
				}
				return sBuffer.toString();
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return e.toString();

		}
		
		return "false";
		
	}
	
	/* send a file(.jpg) to server by POST method
	 * @params a url path
	 * 		   a image file
	 * @return a String "true" if upload successfully,0
	 * 		   "false" otherwise
	 */
	
	public static String uploadFile(String urlString, File file) {
		
		String PREFIX = "--";
		String LINE_END = "\r\n";
		String BOUNDARY = UUID.randomUUID().toString(); // set the boundary
		
		
		try {
			
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(50000);
			conn.setReadTimeout(50000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true); // permit to use the inputstream
			conn.setDoOutput(true); // permit to use the outputstrem
			conn.setUseCaches(false); // deny to use the cache
			conn.setRequestProperty("Charset", "utf-8"); 
			conn.setRequestProperty("Content-Type", "multipart/form-data" + ";boundary="
					+ BOUNDARY);// set the request Content-Type
			conn.setRequestProperty("connection", "keep-alive");
			
			if(file != null) {
				OutputStream outputStream = conn.getOutputStream();
				DataOutputStream doStream = new DataOutputStream(outputStream);
				
				// request's entity body
				StringBuffer sBuffer = new StringBuffer();
				sBuffer.append(PREFIX);
				sBuffer.append(BOUNDARY);
				sBuffer.append(LINE_END);		
				sBuffer.append("Content-Disposition: form-data; name=\"img\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sBuffer.append("Content-Type: application/octet-stream; charset = utf-8" + LINE_END);
				sBuffer.append(LINE_END);
				
				doStream.write(sBuffer.toString().getBytes());
				
				InputStream in = new FileInputStream(file);
				byte[] bytes = new byte[2014];
				int len = 0;
				while((len = in.read(bytes)) != -1) {
					doStream.write(bytes, 0, len);
				}
				in.close();
				doStream.write(LINE_END.getBytes());

				doStream.write(
						(PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes());
				doStream.flush();
				
				if(conn.getResponseCode() == 200) {
					return "true";
				}
				
			}
			
			
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {

			e.printStackTrace();
			
		}
		
		return "false";
		
	}
}
