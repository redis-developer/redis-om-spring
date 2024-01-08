package com.redis.om.spring.annotations.document.fixtures;

import com.redis.om.spring.repository.RedisDocumentRepository;

public interface ScheduledRepository extends RedisDocumentRepository<Scheduled, String> {
}
