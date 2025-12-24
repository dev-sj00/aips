package com.portfolio.aips.project.elastic_search.archive.dto;

import lombok.Data;

import java.util.List;
import java.util.Objects;

public record ArchiveSearchLogDocument(
        String pk,
        String queryRaw,
        String queryStat,
        List<String> tokens,
        boolean hasFiltered,
        String userNickName,
        String createdDateTime
) {

    public ArchiveSearchLogDocument {
        Objects.requireNonNull(queryRaw, "queryRaw must not be null");
        Objects.requireNonNull(queryStat, "queryStat must not be null");
        Objects.requireNonNull(tokens, "tokens must not be null");
        Objects.requireNonNull(userNickName, "userNickName must not be null");
        Objects.requireNonNull(createdDateTime, "createdDateTime must not be null");
    }

    // Builder 클래스
    public static class Builder {
        private String pk;
        private String queryRaw;
        private String queryStat;
        private List<String> tokens;
        private boolean hasFiltered;
        private String userNickName;
        private String createdDateTime;

        public Builder pk(String pk) {
            this.pk = pk;
            return this;
        }

        public Builder queryRaw(String queryRaw) {
            this.queryRaw = queryRaw;
            return this;
        }

        public Builder queryStat(String queryStat) {
            this.queryStat = queryStat;
            return this;
        }

        public Builder tokens(List<String> tokens) {
            this.tokens = tokens;
            return this;
        }

        public Builder hasFiltered(boolean hasFiltered) {
            this.hasFiltered = hasFiltered;
            return this;
        }

        public Builder userNickName(String userNickName) {
            this.userNickName = userNickName;
            return this;
        }

        public Builder createdDateTime(String createdDateTime) {
            this.createdDateTime = createdDateTime;
            return this;
        }

        public ArchiveSearchLogDocument build() {
            // null 체크
            Objects.requireNonNull(queryRaw, "queryRaw must not be null");
            Objects.requireNonNull(queryStat, "queryStat must not be null");
            Objects.requireNonNull(tokens, "tokens must not be null");
            Objects.requireNonNull(userNickName, "userNickName must not be null");
            Objects.requireNonNull(createdDateTime, "createdDateTime must not be null");

            return new ArchiveSearchLogDocument(
                    pk,
                    queryRaw,
                    queryStat,
                    tokens,
                    hasFiltered,
                    userNickName,
                    createdDateTime
            );
        }
    }

    // Builder 시작 메서드
    public static Builder builder() {
        return new Builder();
    }
}
