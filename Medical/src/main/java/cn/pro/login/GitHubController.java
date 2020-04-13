package cn.pro.login;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/github")
public class GitHubController {
    @Value("${server.url2}")
    private String serverUrl;
    @Value("${client.id2}")
    private String clientId;
    @Value("${client.secret2}")
    private String clientSecret;
    @Value("${redirect.uri2}")
    private String redirectUri;

    /**
     * 第一步，获取code(授权码)
     */
    @GetMapping("/gethubcode")
    public void getcode(HttpServletResponse response) {
        try {
             String url = serverUrl + "/login/oauth/authorize?client_id="+clientId;
            response.sendRedirect(url);
        } catch (Exception e) {

            log.error("获取授权码失败:", e);
        }
    }


    /**
     * 第二步，根据code换取access_token(自动回调的地址,要在第三方平台上登记)
     */
     @SuppressWarnings("unchecked")
    @GetMapping("/login")
    public String callback(String code) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClientBuilder.create().build();

            HttpPost httpPost = new HttpPost(serverUrl + "/login/oauth/access_token");
            //构造post参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            //list.add(new BasicNameValuePair("grant_type", "authorization_code"));
            list.add(new BasicNameValuePair("client_id", clientId));
            //list.add(new BasicNameValuePair("redirect_uri", redirectUri));
            list.add(new BasicNameValuePair("client_secret", clientSecret));
            list.add(new BasicNameValuePair("code", code));

            HttpEntity entity = new UrlEncodedFormEntity(list);
            httpPost.setEntity(entity);
            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
            //发送请求，获取响应
            response = httpClient.execute(httpPost);
            String result =EntityUtils.toString(response.getEntity());
            log.info("响应结果:{}",result);
            //根据http状态码判断是否成功
            if(response.getStatusLine().getStatusCode()==200) {
                Map<String, String> tokenObject = new HashMap<String, String>();
                ObjectMapper om = new ObjectMapper();
                tokenObject = om.readValue(result, Map.class);
                log.info(tokenObject.toString());
                String access_token = tokenObject.get("access_token");
                return "redirect:/github/getuser?access_token=" + access_token;
            } else {
                log.error("获取access_token失败:");
            }
        } catch (Exception e) {
            log.error("获取access_token失败:", e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
        return "error";
    }

/**
 * 第三步，根据access_token获取服务器上授权开放的资源，这里是获取码云的用户信息
 *
 * @param access_token
 * @param model
 * @return
 */
@SuppressWarnings("unchecked")
	@GetMapping("/getuser")
	public String getuser(String access_token, Model model) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = HttpClientBuilder.create().build();

			HttpGet httpGet = new HttpGet("https://api.github.com/user?access_token=" + access_token);
			httpGet.addHeader("Content-Type", "application/json");
			httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
			//发送请求，获取响应
			response = httpClient.execute(httpGet);
			String result =EntityUtils.toString(response.getEntity());
			log.info("响应结果:{}",result);
			//根据http状态码判断是否成功
			if(response.getStatusLine().getStatusCode()==200) {
				Map<String, String> userObject = new HashMap<String, String>();
				ObjectMapper om = new ObjectMapper();
				userObject = om.readValue(result, Map.class);
				log.info(userObject.toString());
				model.addAttribute("data", userObject);
			} else {
				log.error("获取用户信息失败:");
			}
		} catch (Exception e) {
			log.error("获取用户信息失败:", e);
		} finally {
			HttpClientUtils.closeQuietly(response);
			HttpClientUtils.closeQuietly(httpClient);
		}
		return "home";
	}
}
