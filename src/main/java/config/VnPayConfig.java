package config;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VnPayConfig {
    // 1. ĐƯỜNG DẪN MÔ TRƯỜNG TEST (SANDBOX) CỦA VNPAY
    public static final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    // 2. ĐƯỜNG DẪN ĐIỀU HƯỚNG QUAY VỀ WEBSITE LOCAL SAU KHI THANH TOÁN (GIỮ NGUYÊN LOCALHOST)
    public static final String vnp_ReturnUrl = "http://localhost:8080/zzzz_war_exploded/secure/vnpay-callback";

    // 3. ĐÃ CẬP NHẬT: Mã Terminal ID chính chủ từ Email của bạn
    public static final String vnp_TmnCode = "4EDHIVOM";

    // 4. ĐÃ CẬP NHẬT: Chuỗi Secret Key băm Checksum bảo mật từ Email của bạn
    public static final String vnp_HashSecret = "MEK5SSZDY8O1JPIWN1B4YUN7040U6E02";

    public static final String vnp_Version = "2.1.0";
    public static final String vnp_Command = "pay";

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    // Hàm hỗ trợ bóc tách địa chỉ IP Client phục vụ log lịch sử giao dịch bảo mật
    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAdress = "127.0.0.1";
        }
        return ipAdress;
    }
}