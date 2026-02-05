package com.portfolio.aips.project.visitor.domain.repo.vo;

import com.portfolio.aips.project.visitor.domain.entity.VisitorEntity;

public record SaveVO(Long totalVisitor) {

    public VisitorEntity toEntity() {


        return new VisitorEntity(null, totalVisitor, null);
    }
}
