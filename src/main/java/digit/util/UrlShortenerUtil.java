package digit.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import digit.config.Configuration;
import static digit.config.ServiceConstants.*;

@Slf4j
@Component
public class UrlShortenerUtil {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Configuration configs;

    /**
     * This method takes a URL and shortens it using a third-party URL shortening service.
     * @param url The original URL that needs to be shortened.
     * @return The shortened URL if the shortening was successful, or the original URL if an error occurred.
     */
    public String getShortenedUrl(String url){

        HashMap<String,String> body = new HashMap<>();
        body.put(URL,url);
        StringBuilder builder = new StringBuilder(configs.getUrlShortnerHost());
        builder.append(configs.getUrlShortnerEndpoint());
        String res = restTemplate.postForObject(builder.toString(), body, String.class);

        if(StringUtils.isEmpty(res)){
            log.error(URL_SHORTENING_ERROR_CODE, URL_SHORTENING_ERROR_MESSAGE + url); ;
            return url;
        }
        else return res;
    }
}