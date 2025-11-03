package com.BillardManagement.Controller;

import com.BillardManagement.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class MomoPaymentController {

    @Autowired
    private CustomerService customerService;

    private static final String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
    private static final String partnerCode = "MOMO";
    private static final String accessKey = "F8BBA842ECF85";
    private static final String secretKey = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    private static final String redirectUrl = "http://localhost:8080/api/payment/return";  // Cập nhật domain thật khi deploy
    private static final String ipnUrl = "http://localhost:8080/api/payment/ipn";

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@RequestParam String amount, @RequestParam Integer customerId, @RequestParam String planId) {
        try {
            String orderId = String.valueOf(System.currentTimeMillis());
            String requestId = orderId;
            String orderInfo = "Thanh toan Premium - Plan " + planId;
            String requestType = "captureWallet";
            String extraData = customerId + "|" + planId;  // Embed customerId và planId

            // Tạo rawHash cho signature
            String rawHash = "accessKey=" + accessKey +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = hmacSHA256(rawHash, secretKey);

            // Tạo JSON request
            JSONObject json = new JSONObject();
            json.put("partnerCode", partnerCode);
            json.put("accessKey", accessKey);
            json.put("requestId", requestId);
            json.put("amount", amount);
            json.put("orderId", orderId);
            json.put("orderInfo", orderInfo);
            json.put("redirectUrl", redirectUrl);
            json.put("ipnUrl", ipnUrl);
            json.put("extraData", extraData);
            json.put("requestType", requestType);
            json.put("signature", signature);
            json.put("lang", "vi");

            // Gửi request đến Momo
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            InputStream inputStream = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
            }

            if (responseCode == 200) {
                JSONObject responseJson = new JSONObject(sb.toString());
                String payUrl = responseJson.getString("payUrl");
                return ResponseEntity.ok(payUrl);  // Trả về payUrl để frontend redirect
            } else {
                return ResponseEntity.status(500).body("Lỗi từ Momo: " + sb.toString());
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Exception: " + e.getMessage());
        }
    }

    @PostMapping("/ipn")
    public ResponseEntity<String> handleIpn(@RequestBody String body) {
        try {
            JSONObject json = new JSONObject(body);
            String signature = json.getString("signature");

            // Xây dựng rawHash để verify signature
            String rawHash = "accessKey=" + json.getString("accessKey") +
                    "&amount=" + json.getString("amount") +
                    "&extraData=" + json.getString("extraData") +
                    "&message=" + json.getString("message") +
                    "&orderId=" + json.getString("orderId") +
                    "&orderInfo=" + json.getString("orderInfo") +
                    "&orderType=" + json.getString("orderType") +
                    "&partnerCode=" + json.getString("partnerCode") +
                    "&payType=" + json.getString("payType") +
                    "&requestId=" + json.getString("requestId") +
                    "&responseTime=" + json.getString("responseTime") +
                    "&resultCode=" + json.getString("resultCode") +
                    "&transId=" + json.getString("transId");

            if (!hmacSHA256(rawHash, secretKey).equals(signature)) {
                return ResponseEntity.status(400).body("Invalid signature");
            }

            if ("0".equals(json.getString("resultCode"))) {
                String extraData = json.getString("extraData");
                String[] parts = extraData.split("\\|");
                int customerId = Integer.parseInt(parts[0]);
                String planId = parts[1];
                customerService.updateExpireDate(customerId, planId);  // Cập nhật expireDate
            }

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("IPN Error: " + e.getMessage());
        }
    }

    @GetMapping("/return")
    public RedirectView handleReturn(@RequestParam Map<String, String> params) {
        String resultCode = params.get("resultCode");
        String message = params.get("message");
        String extraData = params.get("extraData");

        if ("0".equals(resultCode) && extraData != null) {
            try {
                String[] parts = extraData.split("\\|");
                int customerId = Integer.parseInt(parts[0]);
                String planId = parts[1];
                customerService.updateExpireDate(customerId, planId);  // Cập nhật expireDate
            } catch (Exception e) {
                // Handle error if parsing fails
            }
        }

        if ("0".equals(resultCode)) {
            return new RedirectView("http://localhost:3000/dashboard");
        } else {
            String encodedMessage = message != null ? java.net.URLEncoder.encode(message, StandardCharsets.UTF_8) : "Thanh toán thất bại";
            return new RedirectView("http://localhost:3000/premium?error=" + encodedMessage);
        }
    }

    private static String hmacSHA256(String data, String key) throws Exception {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256.init(secretKeySpec);
        byte[] hash = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
