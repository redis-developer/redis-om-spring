package com.redis.om.spring.fixtures.document.repository;

import com.redis.om.spring.fixtures.document.model.CustomIndex2Doc;
import com.redis.om.spring.repository.RedisDocumentRepository;

@SuppressWarnings("unused")
public interface CustomIndex2DocRepository extends RedisDocumentRepository<CustomIndex2Doc, String> {
}
