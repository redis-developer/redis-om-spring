package com.redis.om.spring.search.stream.actions;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Consumer;

import com.redis.om.spring.ops.json.JSONOperations;
import com.redis.om.spring.util.ObjectUtils;
import com.redislabs.modules.rejson.Path;

public class NumIncrByAction<E> implements TakesJSONOperations, Consumer<E> {
  
  private Field field;
  private JSONOperations<String> json;
  private Long value;

  public NumIncrByAction(Field field, Long value) {
    this.field = field;
    this.value = value;
  }

  @Override
  public void accept(E entity) {
    Optional<?> maybeId = ObjectUtils.getIdFieldForEntity(entity);
    
    if (maybeId.isPresent()) {
      json.numIncrBy(entity.getClass().getName() + ":" + maybeId.get().toString(), Path.of("." + field.getName()), value);
    }
  }

  @Override
  public void setJSONOperations(JSONOperations<String> json) {
    this.json = json;
  }
}
