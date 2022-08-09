package com.redis.om.spring.id;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.f4b6a3.ulid.Ulid;
import com.redis.om.spring.AbstractBaseEnhancedRedisTest;
import com.redis.om.spring.annotations.hash.fixtures.Person;
import com.redis.om.spring.annotations.hash.fixtures.PersonRepository;

public class ULIDIdentifierTest extends AbstractBaseEnhancedRedisTest {

  @Autowired
  PersonRepository repository;
  
  @Test
  public void testMonotonicallyIncreasingUlidAssignment() {
    Person ofer = Person.of("Ofer Bengal", "ofer@redis.com", "ofer");
    String oferId = repository.save(ofer).getId();
    Person yiftach = Person.of("Yiftach Shoolman", "yiftach@redis.com", "yiftach");
    String yiftachId = repository.save(yiftach).getId();
    // get the Ulid objects from the String ids
    Ulid oferUlid = Ulid.from(oferId);
    Ulid yiftachUlid = Ulid.from(yiftachId);
    assertTrue(oferUlid.getInstant().isBefore(yiftachUlid.getInstant()));
  }

}
