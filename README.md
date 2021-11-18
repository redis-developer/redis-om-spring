<div align="center">
  <br/>
  <br/>
  <img width="360" src="docs/media/images/logo.svg" alt="Redis OM" />
  <br/>
  <br/>
</div>

<p align="center">
    <p align="center">
        Object Mapping (and more) for Redis!
    </p>
</p>

---

**Redis OM Spring** extends [Spring Data Redis](https://spring.io/projects/spring-data-redis) to take full advantage of the power of Redis.

**Redis OM Spring** | [Redis OM Node.js](https://github.com/redis/redis-om-node) | [Redis OM Python](https://github.com/redis/redis-om-python) | [Redis OM .NET](https://github.com/redis/redis-om-dotnet)

<details>
  <summary><strong>Table of contents</strong></summary>

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

  - [💡 Why Redis OM?](#-why-redis-om)
  - [🍀 Redis OM Spring](#-redis-om-spring)
  - [🏁 Getting Started](#-getting-started)
  - [💻 Maven configuration](#-maven-configuration)
  - [📚 Documentation](#-documentation)
  - [⛏️ Troubleshooting](#-troubleshooting)
  - [✨ So, How Do You Get RediSearch and RedisJSON?](#-so-how-do-you-get-redisearch-and-redisjson)
  - [❤️ Contributing](#-contributing)
  - [📝 License](#-license)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

</details>

## 💡 Why Redis OM?

The Redis OM family of projects aim is to provide high-level abstractions idiomatically implemented for your language/platform of choice. We currently cater to the Node, Python, .Net and Spring communities.

## 🍀 Redis OM Spring

Redis OM Spring provides powerful repository and custom object-mapping abstractions built on top of the powerful Spring Data Redis (SDR) framework.

This **preview** release provides all of SDRs capabilities plus:

* `@Document` annotation to map Spring Data models to Redis JSON documents
* Enhances SDR's `@RedisHash` via `@EnableRedisEnhancedRepositories` to:
  - uses Redis' native search engine (RediSearch) for secondary indexing
  - uses [ULID](https://github.com/ulid/spec) for `@Id` annotated fields
* `RedisDocumentRepository` with automatic implementation of Repository interfaces for complex querying capabilities using `@EnableRedisDocumentRepositories`
* Declarative Search Indices via `@Indexable`
* Full-text Search Indices via `@Searchable`

## 🏁 Getting Started

Here is a quick teaser of an application using Redis OM Spring to map a Spring Data model
using a RedisJSON document.

### The SpringBoot App

Use the `@EnableRedisDocumentRepositories` annotation to scan for `@Document` annotated Spring models,
Inject repositories beans implementing `RedisDocumentRepository` which you can use for CRUD operations and custom queries (all by declaring Spring Data Query Interfaces):

```java
package com.redis.documents;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.geo.Point;

import com.redis.documents.domain.Company;
import com.redis.documents.domain.Person;
import com.redis.documents.repositories.CompanyRepository;
import com.redis.documents.repositories.PersonRepository;
import com.redis.spring.annotations.EnableRedisDocumentRepositories;

@SpringBootApplication
@Configuration
@EnableRedisDocumentRepositories(basePackages = "com.redis.documents.*")
public class RdsDocumentsApplication {

  @Autowired
  CompanyRepository companyRepo;

  @Bean
  CommandLineRunner loadTestData() {
    return args -> {
      companyRepo.deleteAll();
      Company redis = Company.of(
        "Redis", "https://redis.com", new Point(-122.066540, 37.377690), 526, 2011 //
      );
      redis.setTags(Set.of("fast", "scalable", "reliable"));

      Company microsoft = Company.of(
        "Microsoft", "https://microsoft.com", new Point(-122.124500, 47.640160), 182268, 1975 //
      );
      microsoft.setTags(Set.of("innovative", "reliable"));
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(RdsDocumentsApplication.class, args);
  }
}
```

### The Mapped Model

Like many other Spring Data projects, an annotation at the class level determines how instances
of the class are persisted. Redis OM Spring provides the `@Document` annotation to persist models as JSON documents using RedisJSON:

```java
package com.redis.documents;

import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import com.redis.spring.annotations.*;
import lombok.*;

@Data
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Document
public class Company {
  @Id private String id;
  @Searchable private String name;
  @Indexed private Point location;
  @Indexed private Set<String> tags = new HashSet<String>();
  @Indexed private Integer numberOfEmployees;
  @Indexed private Integer yearFounded;
  private String url;
  private boolean publiclyListed;

  // ...
}
```

Redis OM Spring, replaces the conventional `UUID` primary key strategy generation with a `ULID` (Universally Unique Lexicographically Sortable Identifier) which is faster to generate and easier on the eyes.

### The Repository

Redis OM Spring data repository's goal, like other Spring Data repositories, is to significantly reduce the amount of boilerplate code required to implement data access. Simply create a Java interface
that extends `RedisDocumentRepository` that takes the domain class to manage as well as the ID type of the domain class as type arguments. `RedisDocumentRepository` extends Spring Data's `PagingAndSortingRepository`.

Declare query methods on the interface. You can both, expose CRUD methods or create declarations for complex queries that Redis OM Spring will fullfil at runtime:

```java
package com.redis.documents.repositories;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.repository.query.Param;

import com.redis.documents.domain.Company;
import com.redis.spring.annotations.Query;
import com.redis.spring.repository.RedisDocumentRepository;

public interface CompanyRepository extends RedisDocumentRepository<Company, String> {
  // find one by property
  Optional<Company> findOneByName(String name);

  // geospatial query
  Iterable<Company> findByLocationNear(Point point, Distance distance);

  // find by tag field, using JRediSearch "native" annotation
  @Query("@tags:{$tags}")
  Iterable<Company> findByTags(@Param("tags") Set<String> tags);

  // find by numeric property
  Iterable<Company> findByNumberOfEmployees(int noe);

  // find by numeric property range
  Iterable<Company> findByNumberOfEmployeesBetween(int noeGT, int noeLT);

  // starting with/ending with
  Iterable<Company> findByNameStartingWith(String prefix);
}
```

The repository proxy has two ways to derive a store-specific query from the method name:

- By deriving the query from the method name directly.
- By using a manually defined query using the `@Query` or `@Aggregation` annotations.

## 💻 Maven configuration

Add the Maven dependency:

```xml
<dependency>
  <groupId>com.redis.om.spring</groupId>
  <artifactId>redis-om-spring</artifactId>
  <version>${version}</version>
</dependency>
```

**Ready to learn more?** Check out the [getting started](docs/getting_started.md) guide.

## 📚 Documentation

The Redis OM documentation is available [here](docs/index.md).

## Demos

### Basic JSON Mapping and Querying

- **rds-documents**:
  - Simple API example of `@Document` mapping, Spring Repositories and Querying.
  - Run with  `./mvnw install -Dmaven.test.skip && ./mvnw spring-boot:run -pl demos/rds-documents`
- **rds-hashes**:
  - Simple API example of `@RedisHash`, enhanced secondary indices and querying.
  - Run with  `./mvnw install -Dmaven.test.skip && ./mvnw spring-boot:run -pl demos/rds-hashes`

## ⛏️ Troubleshooting

If you run into trouble or have any questions, we're here to help!

First, check the [FAQ](docs/faq.md). If you don't find the answer there,
hit us up on the [Redis Discord Server](http://discord.gg/redis).

## ✨ So How Do You Get RediSearch and RedisJSON?

Some advanced features of Redis OM rely on core features from two source available Redis modules: [RediSearch][redisearch-url] and [RedisJSON][redis-json-url].

You can run these modules in your self-hosted Redis deployment, or you can use [Redis Enterprise][redis-enterprise-url], which includes both modules.

To learn more, read [our documentation](docs/redis_modules.md).

## ❤️ Contributing

We'd love your contributions!

**Bug reports** are especially helpful at this stage of the project. [You can open a bug report on GitHub](https://github.com/redis-om/redis-om-spring/issues/new).

You can also **contribute documentation** -- or just let us know if something needs more detail. [Open an issue on GitHub](https://github.com/redis-om/redis-om-spring/issues/new) to get started.

## 📝 License

Redis OM uses the [BSD 3-Clause license][license-url].

<!-- Badges -->

[ci-url]: https://github.com/redis-developer/redis-om-spring/actions/workflows/ci.yml
[license-image]: http://img.shields.io/badge/license-BSD_3--Clause-green.svg?style=flat-square
[license-url]: LICENSE

<!-- Links -->

[redis-om-website]: https://developer.redis.com
[redis-om-python]: https://github.com/redis-om/redis-om-python
[redis-om-js]: https://github.com/redis-om/redis-om-js
[redis-om-dotnet]: https://github.com/redis-om/redis-om-dotnet
[redisearch-url]: https://oss.redis.com/redisearch/
[redis-json-url]: https://oss.redis.com/redisjson/
[ulid-url]: https://github.com/ulid/spec
[redis-enterprise-url]: https://redis.com/try-free/

