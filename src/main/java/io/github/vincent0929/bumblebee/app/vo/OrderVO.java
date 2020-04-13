package io.github.vincent0929.bumblebee.app.vo;

import io.github.vincent0929.bumblebee.annotaions.FieldExtension;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderVO extends BaseVO {

  private Long orderId;

  @FieldExtension
  private Long cost;
}
