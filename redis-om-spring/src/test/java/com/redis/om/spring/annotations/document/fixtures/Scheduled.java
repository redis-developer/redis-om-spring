package com.redis.om.spring.annotations.document.fixtures;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Document
@Data
@RequiredArgsConstructor(staticName = "of")
public class Scheduled {

    @Id
    @NonNull
    private String id;

    @Indexed
    @NonNull
    private List<Filter> filters;

}
