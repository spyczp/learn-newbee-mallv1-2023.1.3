package ltd.newbee.mall.controller.test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ThymeleafController {

    @GetMapping("/complex")
    public String complex(ModelMap map){
        map.put("title", "Thymeleaf 语法测试");
        map.put("testString", "Spring Boot 商城");
        map.put("bool", true);
        map.put("testArray", new Integer[]{2021, 2022, 2023, 2024});
        map.put("testList", Arrays.asList("Spring", "Spring Boot", "Thymeleaf", "MyBatis", "Java"));
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("platform", "book");
        testMap.put("title", "Spring Boot 商城项目实战");
        testMap.put("author", "十三");
        map.put("testMap", testMap);
        map.put("testDate", new Date());

        return "complex";
    }

    @GetMapping("/simple")
    public String simple(ModelMap map){
        map.put("thymeleafText", "晴朗的一天");
        map.put("number1", 2021);
        map.put("number2", 2);
        return "simple";
    }

    @GetMapping("/hello")
    public String hello(HttpServletRequest request, @RequestParam("description") String description){
        request.setAttribute("description", description);
        return "thymeleaf";
    }
}
