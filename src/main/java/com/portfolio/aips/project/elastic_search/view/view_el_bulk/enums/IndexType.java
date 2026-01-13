package com.portfolio.aips.project.elastic_search.view.view_el_bulk.enums;

import com.portfolio.aips.project.interaction.enums.BoardType;

import java.util.Arrays;

public enum IndexType
{
    Archive(BoardType.Archive, "archive");

    private final BoardType boardType;
    private final String indexName;


    IndexType(BoardType boardType, String indexName) {
        this.boardType = boardType;
        this.indexName = indexName;
    }


    public static String from(BoardType boardType) {
        return Arrays.stream(values())
                .filter(v -> v.boardType == boardType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No index mapped for boardType: " + boardType
                ))
                .indexName;
    }

}
