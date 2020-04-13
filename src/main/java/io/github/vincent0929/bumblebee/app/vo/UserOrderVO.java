package io.github.vincent0929.bumblebee.app.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserOrderVO extends OrderVO {

  private String name;

}
