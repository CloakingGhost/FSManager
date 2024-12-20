package com.ai.controller;

import com.ai.domain.TeamDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

@Slf4j
@RestController
@Api(tags = {"결제관련 API"})
@RequestMapping(value = "/pay")
public class PayController {

    @Value("${app.pay.merchant.identification.code}")
    private String userCode;
    private final String BASE_URL = "https://service.iamport.kr/payments/ready/" + userCode + "/kakaopay/TC0ONETIME";
    private String merchant_uid = UUID.randomUUID().toString();
    private final String pg = "kakaopay";
    private final String name = "풋살매니저";
    private final String buyer_tel = "010-8223-4716";
    private final String buyer_addr = "서울특별시 양천구 목동";
    private final String buyer_postcode = "01181";



    @RequestMapping(value = "/test", method = RequestMethod.POST)
    @ApiOperation(value = "앱으로 결제 QR보내기")
    @ResponseBody
    public String TestPay (@RequestParam("amount") int amount, @RequestParam("buyer_name") String nickName,
                           @RequestParam("uuid")String uuid, HttpSession session) throws Exception {
        log.info("pay컨트롤러 test들어옴,,,,받은값 : " + amount + "&&" + nickName + "&&" + uuid);
        String buyer_email = session.getAttribute("userId").toString();
        String buyer_name = nickName;
        URL url = new URL(BASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        StringBuilder urlBuilder = new StringBuilder();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

        try {
            urlBuilder.append("?" + URLEncoder.encode("pg", "UTF-8") + "=" + pg);
            urlBuilder.append("&" + URLEncoder.encode("merchant_uid", "UTF-8") + "=" + uuid);
            urlBuilder.append("&" + URLEncoder.encode("name", "UTF-8") + "=" + name);
            urlBuilder.append("&" + URLEncoder.encode("amount", "UTF-8") + "=" + amount);
            urlBuilder.append("&" + URLEncoder.encode("buyer_email", "UTF-8") + "=" + buyer_email);
            urlBuilder.append("&" + URLEncoder.encode("buyer_name", "UTF-8") + "=" + buyer_name);
            urlBuilder.append("&" + URLEncoder.encode("buyer_tel", "UTF-8") + "=" + buyer_tel);
            urlBuilder.append("&" + URLEncoder.encode("buyer_addr", "UTF-8") + "=" + buyer_addr);
            urlBuilder.append("&" + URLEncoder.encode("buyer_postcode", "UTF-8") + "=" + buyer_postcode);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        bw.write(urlBuilder.toString());
        bw.flush();

        int responseCode = conn.getResponseCode();
        System.out.println("responseCode : " + responseCode);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line = "";
        String result = "";

        while((line = br.readLine()) != null) {
            result += line;
        }
        System.out.println("responseBody : " + result);

        bw.close();
        br.close();

        return result;
    }


//    @ResponseBody
//    @RequestMapping(value = "/test1", method = RequestMethod.POST)
//    public String TestPay (@RequestParam("amount") int amount, @RequestParam("buyer_name") String nickName, HttpSession session) throws Exception {
//    }
}