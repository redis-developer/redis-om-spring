package com.redis.om.spring.annotations.document.fixtures;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.redis.om.spring.annotations.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.geo.Point;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Document
public class Company {
  @Id
  private String id;

  @NonNull
  @Searchable(sortable = true)
  private String name;

  @NonNull
  @Indexed
  private Integer yearFounded;

  @NonNull
  @Indexed
  private Point location;

  @Indexed
  private Set<String> tags = new HashSet<String>();

  @NonNull
  @Bloom(name = "bf_company_email", capacity = 100000, errorRate = 0.001)
  private String email;

  private boolean publiclyListed;

  // audit fields

  @CreatedDate
  private Date createdDate;

  @LastModifiedDate
  private Date lastModifiedDate;

  @Indexed
  private Set<Employee> employees;
}
