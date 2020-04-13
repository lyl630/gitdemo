package cn.pro.http;
import javax.servlet.http.HttpServletRequest;

import cn.pro.response.Response;
import cn.pro.response.ResponseEnum;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class HttpClientHelper {
    private HttpServletRequest request;
    private ObjectMapper objectMapper = new ObjectMapper();

    public HttpClientHelper(HttpServletRequest request) {
        this.request = request;
    }

    public Response get(String uri) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        try {
            String ssoCookies = (String) request.getAttribute("ssoCookies");
            HttpGet httpGet = new HttpGet(uri);
            httpGet.addHeader("Cookie", ssoCookies);
            response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            //授权过期,需要用户重新授权
            Header contentType = (Header) response.headerIterator("Content-Type").next();
            if(contentType.getValue().startsWith("text/html")) {//请求授权页面
                return new Response(ResponseEnum.ERROR).setResponseBody("调用目标的授权已经失效，请先重新获取授权<a href='"+uri+"'>授权</a>");
            }
            String result = null;
            if(httpEntity != null) {
                result = EntityUtils.toString(httpEntity);
                if(log.isDebugEnabled()) {
                    log.debug("响应内容：{}",result);
                }
                if(response.getStatusLine().getStatusCode() == 200) {
                    return objectMapper.readValue(result,Response.class);
                }
            }
        } catch(Exception ex) {
            log.error(ex.getMessage(),ex);
        } finally {
            HttpClientUtils.closeQuietly(httpClient);
            HttpClientUtils.closeQuietly(response);
        }
        return new Response(ResponseEnum.ERROR).setResponseBody("出错了");
    }
}
