package com.redis.om.spring.search.stream.predicates.numeric;

import com.redis.om.spring.metamodel.SearchFieldAccessor;
import com.redis.om.spring.search.stream.predicates.BaseAbstractPredicate;
import io.redisearch.querybuilder.Node;
import io.redisearch.querybuilder.QueryBuilder;
import io.redisearch.querybuilder.Values;

import java.time.*;
import java.util.Date;

public class EqualPredicate<E, T> extends BaseAbstractPredicate<E, T> {
  private T value;

  public EqualPredicate(SearchFieldAccessor field, T value) {
    super(field);
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  @Override
  public Node apply(Node root) {
    Class<?> cls = getValue().getClass();
    if (cls == LocalDate.class) {
      LocalDate localDate = (LocalDate) getValue();
      Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
      long unixTime = instant.getEpochSecond();
      return QueryBuilder.intersect(root).add(getSearchAlias(), Values.eq(unixTime));
    } else if (cls == Date.class) {
      Date date = (Date) getValue();
      Instant instant = date.toInstant();
      long unixTime = instant.getEpochSecond();
      return QueryBuilder.intersect(root).add(getSearchAlias(), Values.eq(unixTime));
    } else if (cls == LocalDateTime.class) {
      LocalDateTime localDateTime = (LocalDateTime) getValue();
      Instant instant = localDateTime.toInstant(ZoneOffset.of(ZoneId.systemDefault().getId()));
      long unixTime = instant.getEpochSecond();
      return QueryBuilder.intersect(root).add(getSearchAlias(), Values.eq(unixTime));
    } else if (cls == Instant.class) {
      Instant instant = (Instant) getValue();
      long unixTime = instant.getEpochSecond();
      return QueryBuilder.intersect(root).add(getSearchAlias(), Values.eq(unixTime));
    } else if (cls == Integer.class) {
      return QueryBuilder.intersect(root).add(getSearchAlias(), Values.eq(Integer.valueOf(getValue().toString())));
    } else if (cls == Double.class) {
      return QueryBuilder.intersect(root).add(getSearchAlias(), Values.eq(Double.valueOf(getValue().toString())));
    } else {
      return root;
    }
  }

}
