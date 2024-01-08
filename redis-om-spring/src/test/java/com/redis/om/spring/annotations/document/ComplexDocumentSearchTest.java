package com.redis.om.spring.annotations.document;

import com.google.common.collect.Lists;
import com.redis.om.spring.AbstractBaseDocumentTest;
import com.redis.om.spring.annotations.document.fixtures.*;
import com.redis.om.spring.repository.query.Sort;
import com.redis.om.spring.search.stream.EntityStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("SpellCheckingInspection") class ComplexDocumentSearchTest extends AbstractBaseDocumentTest {
  Permit permit1;
  Permit permit2;
  Permit permit3;

  @Autowired
  PermitRepository repository;

  @Autowired
  ComplexRepository complexRepository;

  @Autowired
  ScheduledRepository scheduledRepository;

  @Autowired
  EntityStream es;

  @BeforeEach
  void setup() {
    repository.deleteAll();
    complexRepository.deleteAll();

    // # Document 1
    Address address1 = Address.of("Lisbon", "25 de Abril");
    Order order1 = Order.of("O11", 1.5);
    Order order2 = Order.of("O12", 5.6);
    Attribute attribute11 = Attribute.of("size","S", Lists.newArrayList(order1));
    Attribute attribute12 = Attribute.of("size","M", Lists.newArrayList(order2));
    List<Attribute> attrList1 = Lists.newArrayList(attribute11, attribute12);
    permit1 = Permit.of( //
            address1, //
            "To construct a single detached house with a front covered veranda.", //
            "single detached house", //
            Set.of("demolition", "reconstruction"), //
            42000L, //
            new Point(38.7635877,-9.2018309), //
            List.of("started", "in_progress", "approved"), //
            attrList1
    );
    permit1.setPermitTimestamp(LocalDateTime.of(2022, 8, 1, 10, 0));

    // # Document 2
    Address address2 = Address.of("Porto", "Av. da Liberdade");
    Order order21 = Order.of("O21", 1.2);
    Order order22 = Order.of("O22", 5.6);
    Attribute attribute21 = Attribute.of("color","red", Lists.newArrayList(order21));
    Attribute attribute22 = Attribute.of("color","blue", Lists.newArrayList(order22));
    List<Attribute> attrList2 = Lists.newArrayList(attribute21, attribute22);
    permit2 = Permit.of( //
            address2, //
            "To construct a loft", //
            "apartment", //
            Set.of("construction"), //
            53000L, //
            new Point(38.7205373,-9.148091), //
            List.of("started", "in_progress", "rejected"), //
            attrList2
    );
    permit2.setPermitTimestamp(LocalDateTime.of(2022, 8, 2, 0, 0));

    // # Document 3
    Address address3 = Address.of("Lagos", "D. João");
    Order order31 = Order.of("ABC", 1.6);
    Order order32 = Order.of("DEF", 1.3);
    Order order33 = Order.of("GHJ", 1.6);
    Order order34 = Order.of("VBN", 1.0);
    Attribute attribute31 = Attribute.of("brand","A", Lists.newArrayList(order31, order33));
    Attribute attribute32 = Attribute.of("brand","B", Lists.newArrayList(order32, order34));
    List<Attribute> attrList3 = Lists.newArrayList(attribute31, attribute32);
    permit3 = Permit.of( //
            address3, //
            "New house build", //
            "house", //
            Set.of("construction", "design"), //
            260000L, //
            new Point(37.0990749,-8.6868258), //
            List.of("started", "in_progress", "postponed"), //
            attrList3
    );
    permit3.setPermitTimestamp(LocalDateTime.of(2022, 8, 25, 0, 0));

    repository.saveAll(List.of(permit1, permit2, permit3));

    // complex deep nested
    Complex complex1 = Complex.of("complex1", List.of(HasAList.of(List.of("Nudiustertian", "Comeuppance", "Yarborough")), HasAList.of(List.of("Sialoquent", "Pauciloquent", "Bloviate"))));
    Complex complex2 = Complex.of("complex2", List.of(HasAList.of(List.of("Quire", "Zoanthropy", "Flibbertigibbet")), HasAList.of(List.of("Taradiddle", "Malarkey", "Comeuppance"))));
    Complex complex3 = Complex.of("complex3", List.of(HasAList.of(List.of("Pandiculation", "Taradiddle", "Ratoon")), HasAList.of(List.of("Yarborough", "Wabbit", "Erinaceous"))));

    complexRepository.saveAll(List.of(complex1, complex2, complex3));
  }

  @Test
  void testFindbyConstructionValue() {
    long value = 42000L;
    Iterable<Permit> permits = repository.findByConstructionValue(value);
    assertThat(permits).containsExactly(permit1);
  }

  @Test
  void testFullTextSearch() {
    String q = "veranda";
    Iterable<Permit> permits = repository.search(q);
    assertThat(permits).containsExactly(permit1);
  }

  @Test
  void testByBuildingType() {
    String type = "detached";
    Iterable<Permit> permits =  repository.findByBuildingType(type);
    assertThat(permits).containsExactly(permit1);
  }

  @Test
  void testByCity() {
    Iterable<Permit> permits =  repository.findByAddress_City("Lisbon");
    assertThat(permits).containsExactly(permit1);

    permits =  repository.findByAddress_City("Porto");
    assertThat(permits).containsExactly(permit2);
  }

  @Test
  void testByTags() {
    Set<String> wts = Set.of("design","construction");
    Iterable<Permit> permits =  repository.findByWorkType(wts);
    assertThat(permits).containsExactlyInAnyOrder(permit2, permit3);
  }

  @Test
  void testByTagsWithNullParams() {
    // # Document 4
    List<Attribute> attrs = List.of();
    Address address4 = Address.of("Coimbra", "R. Serra 14");
    Permit permit4 = Permit.of( //
            address4, //
            "Historical Renovation", //
            "church", //
            Set.of(), //
            260000L, //
            new Point(37.0990749,-8.6868258), //
            List.of(), //
            attrs
    );

    repository.save(permit4);

    Iterable<Permit> permits = repository.findByWorkType(Set.of());
    assertThat(permits).isEmpty();
  }


  @Test
  void testByAllTags() {
    Set<String> wts = Set.of("design","construction");
    Iterable<Permit> permits =  repository.findByWorkTypeContainingAll(wts);
    assertThat(permits).containsExactly(permit3);
  }

  @Test
  void testByBuildingTypeAndDescription() {
    String buildingType = "house";
    String description = "new";
    Iterable<Permit> permits = repository.findByBuildingTypeAndDescription(buildingType, description);
    assertThat(permits).containsExactly(permit3);
  }

  @Test
  void testByCityOrDescription() {
    String city = "Lagos";
    String description = "detached";
    Iterable<Permit> permits = repository.findByAddress_CityOrDescription(city, description);
    assertThat(permits).containsExactlyInAnyOrder(permit1, permit3);
  }

  @Test
  void testFullTextSearchShouldNotEscapeSearchString() {
    String q = "Single detached House";
    Iterable<Permit> permits = repository.search(q);
    assertThat(permits).containsExactly(permit1);
  }

  @Test
  void testFullTextSearchWithExplicitEscaping() {
    String q = "house\\ with\\ a\\ front";
    Iterable<Permit> permits = repository.search(q);
    assertThat(permits).isEmpty();
  }

  @Test
  void testFullTextSearchExplicitEscapeSearchTerm() {
    String q = "\"single detached house\"";
    Iterable<Permit> permits = repository.search(q);
    assertThat(permits).containsExactly(permit1);
  }

  @Test
  void testFullTextSearchExplicitEscapeSearchTermPrefixSearch() {
    String q = "To construct*";
    Iterable<Permit> permits = repository.search(q);
    assertThat(permits).containsExactly(permit1,permit2);
  }

  /**
   * "FT.SEARCH" "com.redis.om.spring.annotations.document.fixtures.PermitIdx" "To construct*" "SORTBY" "constructionValue" "DESC"
   */
  @Test
  void testFullTextSearchWithSortByNumericFieldDesc() {
    String q = "To construct*";
    Pageable pageRequest = PageRequest.of(0, 10).withSort(Sort.by(Direction.DESC, Permit$.CONSTRUCTION_VALUE));
    Page<Permit> result = repository.search(q, pageRequest);

    assertThat(result.getTotalPages()).isEqualTo(1);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).containsExactly(permit2,permit1);
  }

  /**
   * "FT.SEARCH" "com.redis.om.spring.annotations.document.fixtures.PermitIdx" "To construct*" "SORTBY" "constructionValue" "ASC"
   */
  @Test
  void testFullTextSearchWithSortByNumericFieldAsc() {
    String q = "To construct*";
    Pageable pageRequest = PageRequest.of(0, 10).withSort(Sort.by(Direction.ASC, Permit$.CONSTRUCTION_VALUE));
    Page<Permit> result = repository.search(q, pageRequest);

    assertThat(result.getTotalPages()).isEqualTo(1);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).containsExactly(permit1,permit2);
  }

  /**
   * "FT.SEARCH" "com.redis.om.spring.annotations.document.fixtures.PermitIdx" "To construct*" "SORTBY" "buildingType" "DESC"
   */
  @Test
  void testFullTextSearchWithSortByTextFieldDesc() {
    String q = "To construct*";
    Pageable pageRequest = PageRequest.of(0, 10).withSort(Sort.by(Direction.DESC, Permit$.BUILDING_TYPE));
    Page<Permit> result = repository.search(q, pageRequest);

    assertThat(result.getTotalPages()).isEqualTo(1);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).containsExactly(permit1, permit2);
  }

  /**
   * "FT.SEARCH" "com.redis.om.spring.annotations.document.fixtures.PermitIdx" "To construct*" "SORTBY" "buildingType" "ASC"
   */
  @Test
  void testFullTextSearchWithSortByTextFieldAsc() {
    String q = "To construct*";
    Pageable pageRequest = PageRequest.of(0, 10).withSort(Sort.by(Direction.ASC, Permit$.BUILDING_TYPE));
    Page<Permit> result = repository.search(q, pageRequest);

    assertThat(result.getTotalPages()).isEqualTo(1);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).containsExactly(permit2, permit1);
  }

  /**
   * "FT.SEARCH" "com.redis.om.spring.annotations.document.fixtures.PermitIdx" "house|loft" "SORTBY" "permitTimestamp" "DESC"
   */
  @Test
  void testFullTextSearchWithSortByDateFieldDesc() {
    String q = "house|loft";
    Pageable pageRequest = PageRequest.of(0, 10).withSort(Sort.by(Direction.DESC, Permit$.PERMIT_TIMESTAMP));
    Page<Permit> result = repository.search(q, pageRequest);

    assertThat(result.getTotalPages()).isEqualTo(1);
    assertThat(result.getTotalElements()).isEqualTo(3);
    assertThat(result.getContent()).containsExactly(permit3,permit2,permit1);
  }

  /**
   * "FT.SEARCH" "com.redis.om.spring.annotations.document.fixtures.PermitIdx" "house|loft" "SORTBY" "permitTimestamp" "ASC"
   */
  @Test
  void testFullTextSearchWithSortByDateFieldAsc() {
    String q = "house|loft";
    Pageable pageRequest = PageRequest.of(0, 10).withSort(Sort.by(Direction.ASC, Permit$.PERMIT_TIMESTAMP));
    Page<Permit> result = repository.search(q, pageRequest);

    assertThat(result.getTotalPages()).isEqualTo(1);
    assertThat(result.getTotalElements()).isEqualTo(3);
    assertThat(result.getContent()).containsExactly(permit1,permit2,permit3);
  }

  /**
   * "FT.SEARCH" "com.redis.om.spring.annotations.document.fixtures.PermitIdx" "house|loft" "SORTBY" "address_city" "DESC"
   */
  @Test
  void testFullTextSearchWithSortByNestedTextTagFieldDesc() {
    String q = "house|loft";
    Pageable pageRequest = PageRequest.of(0, 10).withSort(Sort.by(Direction.DESC, Permit$.ADDRESS_CITY));
    Page<Permit> result = repository.search(q, pageRequest);

    assertThat(result.getTotalPages()).isEqualTo(1);
    assertThat(result.getTotalElements()).isEqualTo(3);
    assertThat(result.getContent()).containsExactly(permit2,permit1,permit3);
  }

  /**
   * "FT.SEARCH" "com.redis.om.spring.annotations.document.fixtures.PermitIdx" "house|loft" "SORTBY" "address_city" "ASC"
   */
  @Test
  void testFullTextSearchWithSortByNestedTextTagAsc() {
    String q = "house|loft";
    Pageable pageRequest = PageRequest.of(0, 10).withSort(Sort.by(Direction.ASC, Permit$.ADDRESS_CITY));
    Page<Permit> result = repository.search(q, pageRequest);

    assertThat(result.getTotalPages()).isEqualTo(1);
    assertThat(result.getTotalElements()).isEqualTo(3);
    assertThat(result.getContent()).containsExactly(permit3,permit1,permit2);
  }

  @Test
  void testListInsideAListTagSearch() {
    List<Complex> withYarborough = es.of(Complex.class)
        .filter(Complex$.MY_LIST.INNER_LIST.in("Yarborough"))
        .collect(Collectors.toList());

    assertAll( //
        () -> assertThat(withYarborough.size()).isEqualTo(2), //
        () -> assertThat(withYarborough).extracting("id").containsExactlyInAnyOrder("complex1", "complex3") //
    );

    List<Complex> withComeuppance = es.of(Complex.class)
        .filter(Complex$.MY_LIST.INNER_LIST.in("Comeuppance"))
        .collect(Collectors.toList());

    assertAll( //
        () -> assertThat(withComeuppance.size()).isEqualTo(2), //
        () -> assertThat(withComeuppance).extracting("id").containsExactlyInAnyOrder("complex1", "complex2") //
    );

    List<Complex> withTaradiddle = es.of(Complex.class)
        .filter(Complex$.MY_LIST.INNER_LIST.in("Taradiddle"))
        .collect(Collectors.toList());

    assertAll( //
        () -> assertThat(withTaradiddle.size()).isEqualTo(2), //
        () -> assertThat(withTaradiddle).extracting("id").containsExactlyInAnyOrder("complex2", "complex3") //
    );
  }

  @Test
  void testValidFilterMatch() {
    int startOf2022 = 1;
    int endOf2022 = 2;
    int startOf2023 = 3;
    int endOf2023 = 5;
    int mid2023 = 4;

    Scheduled scheduled1 = Scheduled.of(
            "1",
            List.of(
                    Filter.of(
                            1L,
                            startOf2023,
                            endOf2023,
                            "admin",
                            "role"
                    ),
                    Filter.of(
                            2L,
                            startOf2022,
                            endOf2022,
                            "user",
                            "role"
                    )
            ));
    Scheduled scheduled2 = Scheduled.of(
            "2",
            List.of(
                    Filter.of(
                            3L,
                            startOf2023,
                            endOf2023,
                            "user",
                            "role"
                    )
            ));

    scheduledRepository.save(scheduled1);
    scheduledRepository.save(scheduled2);

    List<Scheduled> adminRolesValidAtTime = es.of(Scheduled.class)
            .filter(Scheduled$.FILTERS.VALID_FROM.lt(mid2023))
            .filter(Scheduled$.FILTERS.TYPE.equals("role"))
            .filter(Scheduled$.FILTERS.VALUE.equals("admin"))
            .collect(Collectors.toList());

    assertThat(adminRolesValidAtTime.size()).isEqualTo(1);

  }
}
