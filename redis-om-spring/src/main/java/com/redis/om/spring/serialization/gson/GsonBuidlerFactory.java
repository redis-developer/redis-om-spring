package com.redis.om.spring.serialization.gson;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.data.geo.Point;

import com.github.f4b6a3.ulid.Ulid;
import com.google.gson.GsonBuilder;

public class GsonBuidlerFactory {
  private static GsonBuilder builder = new GsonBuilder();
  static {
    builder.registerTypeAdapter(Point.class, PointTypeAdapter.getInstance());
    builder.registerTypeAdapter(Date.class, DateTypeAdapter.getInstance());
    builder.registerTypeAdapter(LocalDate.class, LocalDateTypeAdapter.getInstance());
    builder.registerTypeAdapter(LocalDateTime.class, LocalDateTimeTypeAdapter.getInstance());
    builder.registerTypeAdapter(Ulid.class, UlidTypeAdapter.getInstance());
    builder.registerTypeAdapter(Instant.class, InstantTypeAdapter.getInstance());
  }

  public static GsonBuilder getBuilder() {
    return builder;
  }

  private GsonBuidlerFactory() {}
}
