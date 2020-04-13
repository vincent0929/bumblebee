package io.github.vincent0929.bumblebee.app.controller;


import io.github.vincent0929.bumblebee.app.vo.UserOrderVO;

import java.util.List;

public interface TestController {

  UserOrderVO get();

  List<UserOrderVO> gets();
}
