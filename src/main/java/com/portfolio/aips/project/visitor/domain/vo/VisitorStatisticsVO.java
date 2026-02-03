package com.portfolio.aips.project.visitor.domain.vo;

import com.portfolio.aips.project.visitor.domain.entity.VisitorEntity;

public record VisitorStatisticsVO(Long totalVisitor) {

    public VisitorEntity toEntity() {


        return new VisitorEntity(null, totalVisitor, null);
    }
}
