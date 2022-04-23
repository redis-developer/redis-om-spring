package com.redis.om.spring;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.util.ClassTypeInformation;

import com.google.gson.annotations.JsonAdapter;
import com.redis.om.spring.annotations.Bloom;
import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.DocumentScore;
import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import com.redis.om.spring.annotations.EnableRedisEnhancedRepositories;
import com.redis.om.spring.annotations.GeoIndexed;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.NumericIndexed;
import com.redis.om.spring.annotations.Searchable;
import com.redis.om.spring.annotations.TagIndexed;
import com.redis.om.spring.annotations.TextIndexed;
import com.redis.om.spring.client.RedisModulesClient;
import com.redis.om.spring.ops.RedisModulesOperations;
import com.redis.om.spring.ops.json.JSONOperations;
import com.redis.om.spring.ops.pds.BloomOperations;
import com.redis.om.spring.ops.search.SearchOperations;
import com.redis.om.spring.search.stream.EntityStream;
import com.redis.om.spring.search.stream.EntityStreamImpl;

import io.redisearch.FieldName;
import io.redisearch.Schema;
import io.redisearch.Schema.Field;
import io.redisearch.Schema.FieldType;
import io.redisearch.Schema.TagField;
import io.redisearch.Schema.TextField;
import io.redisearch.client.Client;
import io.redisearch.client.Client.IndexOptions;
import io.redisearch.client.IndexDefinition;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RedisProperties.class)
@EnableAspectJAutoProxy
@ComponentScan("com.redis.om.spring.bloom")
@ComponentScan("com.redis.om.spring.autocomplete")
@ComponentScan("com.redis.om.spring.metamodel")
public class RedisModulesConfiguration extends CachingConfigurerSupport {

  private static final Log logger = LogFactory.getLog(RedisModulesConfiguration.class);

  @Bean(name = "redisModulesClient")
  RedisModulesClient redisModulesClient(JedisConnectionFactory jedisConnectionFactory) {
    return new RedisModulesClient(jedisConnectionFactory);
  }

  @Bean(name = "redisModulesOperations")
  @Primary
  @ConditionalOnMissingBean
  RedisModulesOperations<?> redisModulesOperations(RedisModulesClient rmc, RedisTemplate<?, ?> template) {
    return new RedisModulesOperations<>(rmc, template);
  }

  @Bean(name = "redisJSONOperations")
  JSONOperations<?> redisJSONOperations(RedisModulesOperations<?> redisModulesOperations) {
    return redisModulesOperations.opsForJSON();
  }

  @Bean(name = "redisBloomOperations")
  BloomOperations<?> redisBloomOperations(RedisModulesOperations<?> redisModulesOperations) {
    return redisModulesOperations.opsForBloom();
  }

  @Bean(name = "redisTemplate")
  @Primary
  public RedisTemplate<?, ?> redisTemplate(JedisConnectionFactory connectionFactory) {
    RedisTemplate<?, ?> template = new RedisTemplate<>();
    template.setKeySerializer(new StringRedisSerializer());
    template.setDefaultSerializer(new StringRedisSerializer());
    template.setConnectionFactory(connectionFactory);

    return template;
  }

  @Bean(name = "redisJSONKeyValueAdapter")
  RedisJSONKeyValueAdapter getRedisJSONKeyValueAdapter(RedisOperations<?, ?> redisOps,
      JSONOperations<?> redisJSONOperations) {
    return new RedisJSONKeyValueAdapter(redisOps, redisJSONOperations);
  }

  @Bean(name = "redisJSONKeyValueTemplate")
  public CustomRedisKeyValueTemplate getRedisJSONKeyValueTemplate(RedisOperations<?, ?> redisOps,
      JSONOperations<?> redisJSONOperations) {
    RedisMappingContext mappingContext = new RedisMappingContext();
    return new CustomRedisKeyValueTemplate(getRedisJSONKeyValueAdapter(redisOps, redisJSONOperations), mappingContext);
  }

  @Bean(name = "redisCustomKeyValueTemplate")
  public CustomRedisKeyValueTemplate getKeyValueTemplate(RedisOperations<?, ?> redisOps,
      JSONOperations<?> redisJSONOperations) {
    RedisMappingContext mappingContext = new RedisMappingContext();
    return new CustomRedisKeyValueTemplate(new RedisEnhancedKeyValueAdapter(redisOps), mappingContext);
  }

  @Bean(name = "streamingQueryBuilder")
  EntityStream streamingQueryBuilder(RedisModulesOperations<?> redisModulesOperations) {
    return new EntityStreamImpl(redisModulesOperations);
  }

  @EventListener(ContextRefreshedEvent.class)
  public void ensureIndexesAreCreated(ContextRefreshedEvent cre) {
    logger.info("Creating Indexes......");

    ApplicationContext ac = cre.getApplicationContext();
    createIndicesFor(Document.class, ac);
    createIndicesFor(RedisHash.class, ac);
  }

  @EventListener(ContextRefreshedEvent.class)
  public void processBloom(ContextRefreshedEvent cre) {
    ApplicationContext ac = cre.getApplicationContext();
    @SuppressWarnings("unchecked")
    RedisModulesOperations<String> rmo = (RedisModulesOperations<String>) ac
        .getBean("redisModulesOperations");

    Set<BeanDefinition> beanDefs = getBeanDefinitionsFor(ac, Document.class, RedisHash.class);

    for (BeanDefinition beanDef : beanDefs) {
      try {
        Class<?> cl = Class.forName(beanDef.getBeanClassName());
        for (java.lang.reflect.Field field : cl.getDeclaredFields()) {
          // Text
          if (field.isAnnotationPresent(Bloom.class)) {
            Bloom bloom = field.getAnnotation(Bloom.class);
            BloomOperations<String> ops = rmo.opsForBloom();
            String filterName = !ObjectUtils.isEmpty(bloom.name()) ? bloom.name()
                : String.format("bf:%s:%s", cl.getSimpleName(), field.getName());
            ops.createFilter(filterName, bloom.capacity(), bloom.errorRate());
          }
        }
      } catch (Exception e) {
        logger.debug("Error during processing of @Bloom annotation: ", e);
      }
    }
  }

  private void createIndicesFor(Class<?> cls, ApplicationContext ac) {
    @SuppressWarnings("unchecked")
    RedisModulesOperations<String> rmo = (RedisModulesOperations<String>) ac
        .getBean("redisModulesOperations");

    Set<BeanDefinition> beanDefs = new HashSet<BeanDefinition>();
    beanDefs.addAll(getBeanDefinitionsFor(ac, cls));

    logger.info(String.format("Found %s @%s annotated Beans...", beanDefs.size(), cls.getSimpleName()));

    for (BeanDefinition beanDef : beanDefs) {
      String indexName = "";
      String scoreField = null;
      try {
        Class<?> cl = Class.forName(beanDef.getBeanClassName());
        indexName = cl.getName() + "Idx";
        logger.info(String.format("Found @%s annotated class: %s", cls.getSimpleName(), cl.getName()));

        List<Field> fields = new ArrayList<Field>();

        for (java.lang.reflect.Field field : cl.getDeclaredFields()) {
          fields.addAll(findIndexFields(field, null, cls == Document.class));

          // @DocumentScore
          if (field.isAnnotationPresent(DocumentScore.class)) {
            String fieldPrefix = cls == Document.class ? "$." : "";
            scoreField = fieldPrefix + field.getName();
          }
        }

        if (!fields.isEmpty()) {
          Schema schema = new Schema();
          SearchOperations<String> opsForSearch = rmo.opsForSearch(indexName);
          for (Field field : fields) {
            schema.addField(field);
          }

          IndexDefinition index = new IndexDefinition(
              cls == Document.class ? IndexDefinition.Type.JSON : IndexDefinition.Type.HASH);

          if (cl.isAnnotationPresent(Document.class)) {
            Document document = (Document) cl.getAnnotation(Document.class);
            index.setAsync(document.async());
            if (ObjectUtils.isNotEmpty(document.filter())) {
              index.setFilter(document.filter());
            }
            if (ObjectUtils.isNotEmpty(document.language())) {
              index.setLanguage(document.language());
            }
            if (ObjectUtils.isNotEmpty(document.languageField())) {
              index.setLanguageField(document.languageField());
            }
            index.setScore(document.score());
            if (scoreField != null) {
              index.setScoreFiled(scoreField);
            }
          }

          index.setPrefixes(cl.getName() + ":");
          IndexOptions ops = Client.IndexOptions.defaultOptions().setDefinition(index);
          opsForSearch.createIndex(schema, ops);
        }
      } catch (Exception e) {
        logger.warn(String.format("Skipping index creation for %s because %s", indexName, e.getMessage()));
      }
    }
  }

  private List<Field> findIndexFields(java.lang.reflect.Field field, String prefix, boolean isDocument) {
    List<Field> fields = new ArrayList<Field>();

    if (field.isAnnotationPresent(Indexed.class)) {
      logger.info(String.format("FOUND @Indexed annotation on field of type: %s", field.getType()));

      Indexed indexed = (Indexed) field.getAnnotation(Indexed.class);

      //
      // Any Character class -> Tag Search Field
      //
      if (CharSequence.class.isAssignableFrom(field.getType()) || (field.getType() == Boolean.class)) {
        fields.add(indexAsTagFieldFor(field, isDocument, prefix, indexed.sortable(), indexed.separator(),
            indexed.arrayIndex()));
      }
      //
      // Any Numeric class -> Numeric Search Field
      //
      else if (Number.class.isAssignableFrom(field.getType()) || (field.getType() == LocalDateTime.class)
          || (field.getType() == LocalDate.class) || (field.getType() == Date.class)) {
        fields.add(indexAsNumericFieldFor(field, isDocument, prefix, indexed.sortable(), indexed.noindex()));
      }
      //
      // Set / List
      //
      else if (Set.class.isAssignableFrom(field.getType()) || List.class.isAssignableFrom(field.getType())) {
        Optional<Class<?>> maybeCollectionType = com.redis.om.spring.util.ObjectUtils.getCollectionElementType(field);

        // It is only possible to index an array of strings or booleans in a TAG identifier.
        // https://redis.io/docs/stack/json/indexing_json/#json-arrays-can-only-be-indexed-in-tag-identifiers
        if (maybeCollectionType.isPresent() && (CharSequence.class.isAssignableFrom(maybeCollectionType.get()) || (maybeCollectionType.get() == Boolean.class))) {
          fields.add(indexAsTagFieldFor(field, isDocument, prefix, indexed.sortable(), indexed.separator(),
              indexed.arrayIndex()));
        // Index nested fields
        } else if (isDocument) {
          // Index nested JSON Array field. But the current implementation of RediSearch supports array only for TAG fields, not TEXT fields.
          // https://github.com/RediSearch/RediSearch/issues/2293
          logger.info(String.format("FOUND @Nested annotation on field of type: %s", field.getType()));
          fields.addAll(indexAsNestedFieldFor(field, prefix));
        }
      }
      //
      // Point
      //
      else if (field.getType() == Point.class) {
        fields.add(indexAsGeoFieldFor(field, isDocument, prefix, indexed.sortable(), indexed.noindex()));
      }
      //
      // Recursively explore the fields for Index annotated fields
      //
      else {
        for (java.lang.reflect.Field subfield : field.getType().getDeclaredFields()) {
          fields.addAll(findIndexFields(subfield, field.getName(), isDocument));
        }
      }
    }

    // Searchable - behaves like Text indexed
    else if (field.isAnnotationPresent(Searchable.class)) {
      logger.info(String.format("FOUND @Searchable annotation on field of type: %s", field.getType()));
      Searchable searchable = field.getAnnotation(Searchable.class);
      fields.add(indexAsTextFieldFor(field, isDocument, prefix, searchable));
    }
    // Text
    else if (field.isAnnotationPresent(TextIndexed.class)) {
      TextIndexed ti = field.getAnnotation(TextIndexed.class);
      fields.add(indexAsTextFieldFor(field, isDocument, prefix, ti));
    }
    // Tag
    else if (field.isAnnotationPresent(TagIndexed.class)) {
      TagIndexed ti = field.getAnnotation(TagIndexed.class);
      fields.add(indexAsTagFieldFor(field, isDocument, prefix, ti));
    }
    // Geo
    else if (field.isAnnotationPresent(GeoIndexed.class)) {
      GeoIndexed gi = field.getAnnotation(GeoIndexed.class);
      fields.add(indexAsGeoFieldFor(field, isDocument, prefix, gi));
    }
    // Numeric
    else if (field.isAnnotationPresent(NumericIndexed.class)) {
      NumericIndexed ni = field.getAnnotation(NumericIndexed.class);
      fields.add(indexAsNumericFieldFor(field, isDocument, prefix, ni));
    }

    return fields;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Set<BeanDefinition> getBeanDefinitionsFor(ApplicationContext ac, Class... classes) {
    Map<String, Object> annotatedBeans = ac.getBeansWithAnnotation(SpringBootApplication.class);
    Class<?> app = annotatedBeans.isEmpty() ? null : annotatedBeans.values().toArray()[0].getClass();
    Set<BeanDefinition> beanDefs = new HashSet<>();

    if (app == null) {
      return beanDefs;
    }

    ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
    for (Class cls : classes) {
      provider.addIncludeFilter(new AnnotationTypeFilter(cls));
    }

    if (app.isAnnotationPresent(EnableRedisDocumentRepositories.class)) {
      EnableRedisDocumentRepositories edr = (EnableRedisDocumentRepositories) app
          .getAnnotation(EnableRedisDocumentRepositories.class);
      if (edr.basePackages().length > 0) {
        for (String pkg : edr.basePackages()) {
          beanDefs.addAll(provider.findCandidateComponents(pkg));
        }
      } else if (edr.basePackageClasses().length > 0) {
        for (Class<?> pkg : edr.basePackageClasses()) {
          beanDefs.addAll(provider.findCandidateComponents(pkg.getPackageName()));
        }
      } else {
        beanDefs.addAll(provider.findCandidateComponents(app.getPackageName()));
      }
    }

    if (app.isAnnotationPresent(EnableRedisEnhancedRepositories.class)) {
      EnableRedisEnhancedRepositories er = (EnableRedisEnhancedRepositories) app
          .getAnnotation(EnableRedisEnhancedRepositories.class);
      if (er.basePackages().length > 0) {
        for (String pkg : er.basePackages()) {
          beanDefs.addAll(provider.findCandidateComponents(pkg));
        }
      } else if (er.basePackageClasses().length > 0) {
        for (Class<?> pkg : er.basePackageClasses()) {
          beanDefs.addAll(provider.findCandidateComponents(pkg.getPackageName()));
        }
      } else {
        beanDefs.addAll(provider.findCandidateComponents(app.getPackageName()));
      }
    }

    return beanDefs;
  }

  private Field indexAsTagFieldFor(java.lang.reflect.Field field, boolean isDocument, String prefix, TagIndexed ti) {
    ClassTypeInformation<?> typeInfo = ClassTypeInformation.from(field.getType());
    String chain = (prefix == null || prefix.isBlank()) ? "" : prefix + ".";
    String fieldPrefix = isDocument ? "$." + chain : chain;
    String fieldPostfix = (isDocument && typeInfo.isCollectionLike() && !field.isAnnotationPresent(JsonAdapter.class))
        ? "[*]"
        : "";
    FieldName fieldName = FieldName.of(fieldPrefix + field.getName() + fieldPostfix);

    if (!ObjectUtils.isEmpty(ti.alias())) {
      fieldName = fieldName.as(ti.alias());
    } else if (prefix != null && !prefix.isBlank()) {
      fieldName = fieldName.as(prefix.replace(".", "_") + "_" + field.getName());
    } else {
      fieldName = fieldName.as(field.getName());
    }

    return new Field(fieldName, FieldType.Tag, false, ti.noindex());
  }

  private Field indexAsTagFieldFor(java.lang.reflect.Field field, boolean isDocument, String prefix, boolean sortable,
      String separator, int arrayIndex) {
    ClassTypeInformation<?> typeInfo = ClassTypeInformation.from(field.getType());
    String chain = (prefix == null || prefix.isBlank()) ? "" : prefix + ".";
    String fieldPrefix = isDocument ? "$." + chain : chain;
    String index = (arrayIndex != Integer.MIN_VALUE) ? ".[" + arrayIndex + "]" : "[*]";
    String fieldPostfix = (isDocument && typeInfo.isCollectionLike() && !field.isAnnotationPresent(JsonAdapter.class))
        ? index
        : "";
    FieldName fieldName = FieldName.of(fieldPrefix + field.getName() + fieldPostfix);

    if (prefix != null && !prefix.isBlank()) {
      fieldName = fieldName.as(prefix.replace(".", "_") + "_" + field.getName());
    } else {
      fieldName = fieldName.as(field.getName());
    }

    return new TagField(fieldName, separator.isBlank() ? null : separator, sortable);
  }

  private Field indexAsTextFieldFor(java.lang.reflect.Field field, boolean isDocument, String prefix, TextIndexed ti) {
    String chain = (prefix == null || prefix.isBlank()) ? "" : prefix + ".";
    String fieldPrefix = isDocument ? "$." + chain : chain;
    FieldName fieldName = FieldName.of(fieldPrefix + field.getName());

    if (!ObjectUtils.isEmpty(ti.alias())) {
      fieldName = fieldName.as(ti.alias());
    } else if (prefix != null && !prefix.isBlank()) {
      fieldName = fieldName.as(prefix.replace(".", "_") + "_" + field.getName());
    } else {
      fieldName = fieldName.as(field.getName());
    }

    String phonetic = ObjectUtils.isEmpty(ti.phonetic()) ? null : ti.phonetic();

    return new TextField(fieldName, ti.weight(), ti.sortable(), ti.nostem(), ti.noindex(), phonetic);
  }

  private Field indexAsTextFieldFor(java.lang.reflect.Field field, boolean isDocument, String prefix, Searchable ti) {
    String chain = (prefix == null || prefix.isBlank()) ? "" : prefix + ".";
    String fieldPrefix = isDocument ? "$." + chain : chain;
    FieldName fieldName = FieldName.of(fieldPrefix + field.getName());

    if (!ObjectUtils.isEmpty(ti.alias())) {
      fieldName = fieldName.as(ti.alias());
    } else if (prefix != null && !prefix.isBlank()) {
      fieldName = fieldName.as(prefix.replace(".", "_") + "_" + field.getName());
    } else {
      fieldName = fieldName.as(field.getName());
    }
    String phonetic = ObjectUtils.isEmpty(ti.phonetic()) ? null : ti.phonetic();

    return new TextField(fieldName, ti.weight(), ti.sortable(), ti.nostem(), ti.noindex(), phonetic);
  }

  private Field indexAsGeoFieldFor(java.lang.reflect.Field field, boolean isDocument, String prefix, GeoIndexed gi) {
    String chain = (prefix == null || prefix.isBlank()) ? "" : prefix + ".";
    String fieldPrefix = isDocument ? "$." + chain : chain;
    FieldName fieldName = FieldName.of(fieldPrefix + field.getName());

    if (!ObjectUtils.isEmpty(gi.alias())) {
      fieldName = fieldName.as(gi.alias());
    } else if (prefix != null && !prefix.isBlank()) {
      fieldName = fieldName.as(prefix.replace(".", "_") + "_" + field.getName());
    } else {
      fieldName = fieldName.as(field.getName());
    }

    return new Field(fieldName, FieldType.Geo);
  }

  private Field indexAsNumericFieldFor(java.lang.reflect.Field field, boolean isDocument, String prefix,
      NumericIndexed ni) {
    String chain = (prefix == null || prefix.isBlank()) ? "" : prefix + ".";
    String fieldPrefix = isDocument ? "$." + chain : chain;
    FieldName fieldName = FieldName.of(fieldPrefix + field.getName());

    if (!ObjectUtils.isEmpty(ni.alias())) {
      fieldName = fieldName.as(ni.alias());
    } else if (prefix != null && !prefix.isBlank()) {
      fieldName = fieldName.as(prefix.replace(".", "_") + "_" + field.getName());
    } else {
      fieldName = fieldName.as(field.getName());
    }

    return new Field(fieldName, FieldType.Numeric);
  }

  private Field indexAsNumericFieldFor(java.lang.reflect.Field field, boolean isDocument, String prefix,
      boolean sortable, boolean noIndex) {
    String chain = (prefix == null || prefix.isBlank()) ? "" : prefix + ".";
    String fieldPrefix = isDocument ? "$." + chain : chain;
    FieldName fieldName = FieldName.of(fieldPrefix + field.getName());

    fieldName = fieldName.as(field.getName());

    if (prefix != null && !prefix.isBlank()) {
      fieldName = fieldName.as(prefix.replace(".", "_") + "_" + field.getName());
    } else {
      fieldName = fieldName.as(field.getName());
    }

    return new Field(fieldName, FieldType.Numeric, sortable, noIndex);
  }

  private Field indexAsGeoFieldFor(java.lang.reflect.Field field, boolean isDocument, String prefix, boolean sortable,
      boolean noIndex) {
    String chain = (prefix == null || prefix.isBlank()) ? "" : prefix + ".";
    String fieldPrefix = isDocument ? "$." + chain : chain;
    FieldName fieldName = FieldName.of(fieldPrefix + field.getName());

    if (prefix != null && !prefix.isBlank()) {
      fieldName = fieldName.as(prefix.replace(".", "_") + "_" + field.getName());
    } else {
      fieldName = fieldName.as(field.getName());
    }

    return new Field(fieldName, FieldType.Geo);
  }

  private List<Field> indexAsNestedFieldFor(java.lang.reflect.Field field, String prefix) {
    String chain = (prefix == null || prefix.isBlank()) ? "" : prefix + ".";
    String fieldPrefix = "$." + chain;
    return getNestedField(fieldPrefix, field, prefix, null);
  }

  private List<Field> getNestedField(String fieldPrefix, java.lang.reflect.Field field, String prefix, List<Field> fieldList) {
    if (fieldList == null) {
      fieldList = new ArrayList<>();
    }
    Type genericType = field.getGenericType();
    if (genericType instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) genericType;
      Class<?> actualTypeArgument = (Class<?>)pt.getActualTypeArguments()[0];
      java.lang.reflect.Field[] subDeclaredFields = actualTypeArgument.getDeclaredFields();
      String tempPrefix = "";
      if (prefix == null) {
        prefix = field.getName();
      } else {
        prefix += "." + field.getName();
      }
      for (java.lang.reflect.Field subField : subDeclaredFields) {

        Optional<Class<?>> maybeCollectionType = com.redis.om.spring.util.ObjectUtils.getCollectionElementType(subField);

        if (subField.isAnnotationPresent(TagIndexed.class)) {
          TagIndexed ti = subField.getAnnotation(TagIndexed.class);
          tempPrefix = field.getName() + "[0:].";

          FieldName fieldName = FieldName.of(fieldPrefix + tempPrefix + subField.getName());
          fieldName = fieldName.as(prefix.replace(".", "_") + "_" + subField.getName());

          logger.info(String.format("Creating nested relationships: %s -> %s", field.getName(), subField.getName()));
          fieldList.add(new TagField(fieldName, ti.separator(), false));
          continue;
        } else if (subField.isAnnotationPresent(Indexed.class)) {
          System.out.println(">>> Found Indexed SUBFIELD....");
          boolean subFieldIsTagField = ((subField
              .isAnnotationPresent(Indexed.class)
              && ((CharSequence.class.isAssignableFrom(subField.getType()) || (subField.getType() == Boolean.class)
                  || (maybeCollectionType.isPresent() && (CharSequence.class.isAssignableFrom(maybeCollectionType.get())
                      || (maybeCollectionType.get() == Boolean.class)))))));
          System.out.println(">>> subFieldIsTagField ==> " + subFieldIsTagField);
          if (subFieldIsTagField) {

            Indexed indexed = subField.getAnnotation(Indexed.class);
            tempPrefix = field.getName() + "[0:].";

            FieldName fieldName = FieldName.of(fieldPrefix + tempPrefix + subField.getName());
            fieldName = fieldName.as(prefix.replace(".", "_") + "_" + subField.getName());

            logger.info(String.format("Creating nested relationships: %s -> %s", field.getName(), subField.getName()));
            fieldList.add(new TagField(fieldName, indexed.separator(), false));
            continue;
          }
        }
        fieldPrefix += tempPrefix;
        getNestedField(fieldPrefix, subField, prefix, fieldList);
      }
    }
    return fieldList;
  }

}
