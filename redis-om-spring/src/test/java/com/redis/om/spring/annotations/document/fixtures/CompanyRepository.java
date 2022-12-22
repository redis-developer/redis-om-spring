package com.redis.om.spring.annotations.document.fixtures;

import com.redis.om.spring.repository.RedisDocumentRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused") public interface CompanyRepository extends RedisDocumentRepository<Company, String> {
  List<Company> findByName(String companyName);

  boolean existsByEmail(String email);

  List<Company> findByEmployees_name(String name);

  Optional<Company> findFirstByName(String name);

  Optional<Company> findFirstByEmail(String email);

  List<Company> findByPubliclyListed(boolean publiclyListed);

  List<Company> findByTags(Set<String> tags);
}
