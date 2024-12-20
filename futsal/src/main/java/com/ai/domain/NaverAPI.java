package com.ai.domain;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.logging.Logger;

public class NaverAPI {
	private static String redirectURI;
	// callback, 네이버는 로그아웃 제공 안해서 토큰 알아서 가져가고 알아서 처리하라함 나쁜넘들
	public String getAccessToken(String code, HttpSession session, HttpServletRequest request, String clientId, String clientSecret) throws UnsupportedEncodingException {
		if(!CheckDevenv.DEVENV){
			// 서버 배포용
			try {
				redirectURI = URLEncoder.encode("https://www.fsmanager.run" + request.getContextPath() + "/login", "UTF-8");
			} catch (Exception e) {
				System.out.println(e);
			}
		} else {
			// 개발용
			try {
				redirectURI = URLEncoder.encode("http://localhost:8080/login", "UTF-8");
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	    String state = (String)session.getAttribute("state");
	    String apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&";
	    apiURL += "client_id=" + clientId;
	    apiURL += "&client_secret=" + clientSecret;
	    apiURL += "&redirect_uri=" + redirectURI;
	    apiURL += "&code=" + code;
	    apiURL += "&state=" + state;
	    String accessToken = "";
	    String refreshToken = "";
	    BufferedReader br = null;
	    System.out.println("apiURL="+apiURL);
	    try {
	      URL url = new URL(apiURL);
	      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	      conn.setRequestMethod("GET");
	      int responseCode = conn.getResponseCode();
	      
	      System.out.println("responseCode : " + responseCode);
	      if(responseCode == 200) { // 정상 호출
	    	  br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	      } 
	      else {  // 에러 발생
	    	  br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
	      }
	      String line = "";
	      String result = "";
	      
	      while ((line = br.readLine()) != null) {
	    	  result += line;
	      }
	      System.out.println("responseBody : " + result);
	      
	      // 가져온 result parsing
	      JsonParser parser = new JsonParser();
	      JsonElement element = parser.parse(result);
	      
	      // 토큰값 뽑기
	      accessToken = element.getAsJsonObject().get("access_token").getAsString();
	      refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();
	      
	      br.close();
	      
	    } catch (Exception e) {
	      System.out.println(e);
	    }
	    return accessToken;

	}
	
	// https://developers.naver.com/docs/login/profile/profile.md 참조
	// 뽑은 토큰 값으로 유저 정보 조회
	public HashMap<String, Object> getUserInfo(String accesstoken) {
		HashMap<String, Object> userInfo = new HashMap<String, Object>();
		String apiURL = "https://openapi.naver.com/v1/nid/me";
		try {
			// java.net의 URL, HttpURLConnection 객체 사용
			URL url = new URL(apiURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// post 방식 set
			conn.setRequestMethod("GET");
			// Access Token 사용 문서의 Authorization(인가) key - value set
			conn.setRequestProperty("Authorization", "Bearer " + accesstoken);
			// 200 or 401
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode : " + responseCode);
			
			// 예전에 했던 I/O
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String line = "";
			String result = "";
			
			// 읽어올게 없을 때 까지 result 에 더함
			while((line = br.readLine()) != null) {
				result += line;
			}
			// 결과값 확인용(후에 지워도 됨)
			System.out.println("responseBody : " + result);
			
			// Json data parsing (가져온 유저 정보 parsing)
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);
			
			JsonObject response = element.getAsJsonObject().get("response").getAsJsonObject();
			String nickName = response.getAsJsonObject().get("nickname").getAsString();
			String email = response.getAsJsonObject().get("email").getAsString();
			
			userInfo.put("nickName", nickName);
			userInfo.put("email", email);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}
	
	// 토큰 삭제
	public void logout(String accessToken, String clientId, String clientSecret) {
		String apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=delete&";
		String service_provider = "NAVER";
		apiURL += "client_id=" + clientId;
	    apiURL += "&client_secret=" + clientSecret;
		apiURL += "&service_provider=" + service_provider; 
		apiURL += "&access_token=" + accessToken;
		BufferedReader br = null;
		try {
			URL url = new URL(apiURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Bearer " + accessToken);
			
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode : " + responseCode);
			
			if(responseCode == 200) { 
		        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    } 
			else {  
		        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		    }
			
			String line = "";
			String result = "";
			
			while((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("logout : " + result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
