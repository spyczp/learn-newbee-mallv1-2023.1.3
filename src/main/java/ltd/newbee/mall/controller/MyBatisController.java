package ltd.newbee.mall.controller;

import ltd.newbee.mall.dao.UserMapper;
import ltd.newbee.mall.entity.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class MyBatisController {

    @Resource
    private UserMapper userDao;

    @GetMapping("/findAllUser")
    public List<User> findAllUser(){
        return userDao.findAllUser();
    }

    @GetMapping("insertUser")
    public Boolean insertUser(@RequestParam("name") String name, @RequestParam("password") String password){
        if(!StringUtils.hasText(name) || !StringUtils.hasText(password)){
            return false;
        }
        User user = User.builder()
                .name(name)
                .password(password)
                .build();
        return userDao.insertUser(user) > 0;
    }

    @GetMapping("/updateUser")
    public Boolean updateUser(User user){
        if(user.getId() == null
                || user.getId() < 1
                || !StringUtils.hasText(user.getName())
                || !StringUtils.hasText(user.getPassword())){
            return false;
        }
        return userDao.updateUser(user) > 0;
    }

    @GetMapping("/deleteUser")
    public Boolean deleteUser(@RequestParam("id") Integer id){
        if(id == null | id < 1){
            return false;
        }
        return userDao.deleteUser(id) > 0;
    }
}
