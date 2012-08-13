package net.sf.mardao.test.aed;

import java.net.URI;
import net.sf.mardao.test.aed.domain.DCategory;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author os
 */
public class PostITest {

    static final String                  BASE_URL       = "http://localhost:8686/DCategory/";

    RestTemplate                         template;
    public PostITest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        template = new RestTemplate();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testUpdate() {
        
        MultiValueMap<String, Object> requestEntity = new LinkedMultiValueMap<String, Object>();
        requestEntity.set("id", "424242");
        requestEntity.set("title", "Highly unlikely");
        
        URI uri = template.postForLocation(BASE_URL + "create.html", 
                requestEntity);
        final String url = uri.toString();
        assertTrue(url.startsWith(BASE_URL));
        
        String itemUrl = url.substring(0, url.lastIndexOf(".html")) + ".json";
        DCategory actual = template.getForObject(itemUrl, DCategory.class);
        assertEquals(Long.valueOf(424242L), actual.getId());
        assertEquals(requestEntity.get("title"), actual.getTitle());
    }

}
