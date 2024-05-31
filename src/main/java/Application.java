import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.commons.codec.Charsets;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zack.zang
 * @date 2024/5/21
 */
public class Application {

    public static final String APP_KEY = "I3Vs0ll3gGU3iOpE";
    public static final String APP_SECRET = "QLNBwqZQXuJ78sgfbmmDfHXu";
    public static final String URL_PREFIX = "http://open.caifbigdata.com";



    public static final MessageDigest MD5;// 生成一个MD5加密计算摘要

    public static final String URL_LAW_EXECUTEE = "/api/ht/getBePerformedInfo?v=1";  //被执行人信息

    public static final String URL_LAW_COURT_ANNOUNCEMENT = "/api/ht/getCourtAnnouncementInfo?v=1";  //法院公告信息

    public static final String URL_LAW_FINAL_CASE = "/api/ht/getFinalCaseInfo?v=1";  //终审判决信息

    public static final String URL_LAW_JUDGEMENT_DETAIL = "/api/ht/getExecuteJudgmentDetailInfo?v=1";  //执行裁判文书信息详情

    public static final String URL_LAW_JUDGEMENT_LIST = "/api/ht/getExecuteJudgmentInfo?v=1";  //执行裁判文书信息列表

    public static final String URL_LAW_LITIGATION_LIST = "/api/litigation/ent/unioninfo?v=1";  //涉诉企业

    public static final String URL_LAW_LITIGATION_DETAIL = "/api/litigation/person/unioninfo?v=1";  //涉诉个人





    static {
        try {
            MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        Long timestamp = System.currentTimeMillis();
//        String ticket = "1ACB718B-0795-4101-B43D-9A5DC2178632";
        String ticket = java.util.UUID.randomUUID().toString().toUpperCase();
        System.out.println(ticket);
        Map<String, Object> params = new LinkedHashMap<>();
//        params.put("entName", "乐视网信息技术（北京）股份有限公司");
//        params.put("pageNo", 1);
//        params.put("pageSize", 20);
        params.put("name", "乐视网信息技术（北京）股份有限公司");
        String requestBody = buildRequestBody(params, ticket, timestamp);
        String response = doPost(URL_PREFIX + URL_LAW_LITIGATION_LIST, requestBody, "application/json;charset=UTF-8");
        prettyPrint(response);
    }

    private static void prettyPrint(String json) {
        Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(gson.fromJson(json, JsonElement.class)));
    }

    private static String sign(String ticket, Long timestamp){
        String preSign = String.format("appKey=%s&appSecret=%s&ticket=%s&timestamp=%s&version=%s", APP_KEY, APP_SECRET, ticket, timestamp, "1");
//        System.out.println(preSign);
        String sign =Md5Utils.md5(preSign).toUpperCase();
//        System.out.println("sign:"+sign);
//        System.out.println("demo sign:"+Md5Utils.md5("appKey=111111&appSecret=222222&ticket=A8076D47-F0A8-4D63-838DB011AD001162&timestamp=1602677762324&version=1").toUpperCase());
//        System.out.println("ticket:"+ticket);
//        System.out.println("timestamp:"+timestamp);
        return sign;
    }

    public static String doPost(String url, String requestBody, String contentType) {

//        Stopwatch stopwatch = Stopwatch.createStarted();
        // 创建 HttpClient 实例
        HttpClient httpClient = HttpClientBuilder.create().build();

        // 创建 HTTP POST 请求
        HttpPost httpPost = new HttpPost(url);

        try {
            // 设置请求参数
            StringEntity entity = new StringEntity(requestBody, Charsets.UTF_8);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", contentType);

            // 发送请求并获取响应
            org.apache.http.HttpResponse response = httpClient.execute(httpPost);

            // 获取响应内容
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String buildRequestBody(Map<String, Object> params, String ticket, Long timestamp){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("appKey", APP_KEY);

        body.put("body", params);
        body.put("sign", sign(ticket, timestamp));
        body.put("ticket", ticket);
        body.put("timestamp", timestamp);
        body.put("version", "1");
        Gson gson = new Gson();
        return gson.toJson(body);
    }
}
