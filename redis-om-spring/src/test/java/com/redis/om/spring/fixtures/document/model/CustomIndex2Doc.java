package com.redis.om.spring.fixtures.document.model;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.IndexCreationMode;
import com.redis.om.spring.annotations.IndexingOptions;
import com.redis.om.spring.annotations.Searchable;
import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(force = true)
@IndexingOptions(indexName = "ci2", creationMode = IndexCreationMode.SKIP_IF_EXIST)
@Document(value = "cp2")
public class CustomIndex2Doc {
  @Id
  private String id;

  @NonNull
  @Searchable(sortable = true)
  private String first;

  @NonNull
  @Searchable(sortable = true)
  private String second;
}
