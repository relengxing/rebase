package com.relengxing.rebase.es.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryParam {
    private String indexName;

    private String channel;

    private Map<String, String> queryCondition;
}
