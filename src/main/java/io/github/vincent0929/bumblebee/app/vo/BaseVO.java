package io.github.vincent0929.bumblebee.app.vo;

import io.github.vincent0929.bumblebee.annotaions.FieldExtension;

import lombok.Data;

@Data
public class BaseVO {

  @FieldExtension
  private Long id;
}
