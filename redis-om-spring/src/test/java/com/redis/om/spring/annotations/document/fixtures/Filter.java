package com.redis.om.spring.annotations.document.fixtures;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

@Document
@Data
@RequiredArgsConstructor(staticName = "of")
public class Filter {

    @Id
    @NonNull
    private Long id;

    @Indexed
    @NonNull
    private Integer validFrom;

    @Indexed
    @NonNull
    private Integer validTo;

    @Indexed
    @NonNull
    private String value;

    @Indexed
    @NonNull
    private String type;
}
