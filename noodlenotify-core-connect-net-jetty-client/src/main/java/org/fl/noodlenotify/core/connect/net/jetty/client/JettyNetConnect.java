package org.fl.noodlenotify.core.connect.net.jetty.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.core.connect.net.pojo.MessageResult;

public class JettyNetConnect {

	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory.getLogger(JettyNetConnect.class);
	
	private int timeout;
	
	private String currentUrl;
	
	private URL httpUrl;

	public JettyNetConnect(String ip, int port, String url, int timeout) {
		this.timeout = timeout;
		currentUrl = new StringBuilder("http://").append(ip).append(":").append(port).append(url).toString();
	}
	
	public void connect() throws Exception {
		httpUrl = new URL(currentUrl);
		HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setDoInput(true);
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setUseCaches(false);  
		httpURLConnection.setConnectTimeout(timeout);
		httpURLConnection.setReadTimeout(timeout);             
		httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
		httpURLConnection.setRequestProperty("Connection", "keepalive");
		httpURLConnection.setRequestProperty("Keep-Alive", "30");
		httpURLConnection.connect();
	}
	
	public void close() {
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
			httpURLConnection.disconnect();
		} catch (IOException e) {
		}
	}
	
	public String send(Object object, int readTimeout) throws Exception {
		
		String jMessage = URLEncoder.encode(JsonTranslator.toString(object), "utf-8");
		String request = new StringBuilder().append("input=").append(jMessage).toString();
		
		String jResult = requestTo(request, readTimeout);
		
		MessageResult messageResult = JsonTranslator.fromString(jResult, MessageResult.class);
		
		String uuid = null;
		
		if (messageResult.getResult()) {
			uuid = messageResult.getUuid();
		} else {
			throw (Exception) JsonTranslator.fromStringWithClassName(messageResult.getError());
		}
		
		return uuid;
	}
	
	public String requestTo(String request, int readTimeout) throws Exception {
		
		HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setDoInput(true);
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setUseCaches(false);  
		httpURLConnection.setConnectTimeout(timeout);
		httpURLConnection.setReadTimeout(readTimeout);             
		httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
		httpURLConnection.setRequestProperty("Connection", "keepalive");
		httpURLConnection.setRequestProperty("Keep-Alive", "30");
		httpURLConnection.connect();
		
		PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
		printWriter.print(request);
		printWriter.flush();
		printWriter.close();
        
		String line;
		StringBuilder response = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
		while ((line = bufferedReader.readLine()) != null) {
			response.append(line);
		}
		
		return response.toString();
	}
}
