package io.github.vincent0929.bumblebee.app.controller;

import io.github.vincent0929.bumblebee.annotaions.EnableFiledExtension;
import io.github.vincent0929.bumblebee.app.vo.UserOrderVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@EnableFiledExtension
@RestController
public class TestControllerImpl implements TestController {

    @GetMapping("/get")
    @Override
    public UserOrderVO get() {
        return userOrderVO();
    }

    @GetMapping("/gets")
    @Override
    public List<UserOrderVO> gets() {
        List<UserOrderVO> list = new ArrayList<>();
        list.add(userOrderVO());
        return list;
    }

    private UserOrderVO userOrderVO() {
        UserOrderVO userOrderVO = new UserOrderVO();
        userOrderVO.setId(123L);
        userOrderVO.setName("test");
        userOrderVO.setOrderId(456L);
        userOrderVO.setCost(1000L);
        return userOrderVO;
    }
}
