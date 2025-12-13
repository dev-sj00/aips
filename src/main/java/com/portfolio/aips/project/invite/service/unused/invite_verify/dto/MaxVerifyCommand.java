package com.portfolio.aips.project.invite.service.unused.invite_verify.dto;

import com.portfolio.aips.project.invite.enums.InviteType;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;

@Deprecated
public record MaxVerifyCommand(
        EntityPathBase<?> qEntity,
        EntityPathBase<?> qPolicy,
        NumberPath<Long> qPolicyPk,
        NumberPath<Long> ownerUserPkPath,
        Long ownerUserPkValue,
        NumberPath<Integer> maxCountPath,
        EnumPath<InviteType> targetTypePath,
        InviteType targetTypeValue
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EntityPathBase<?> qEntity;
        private EntityPathBase<?> qPolicy;
        private NumberPath<Long> qPolicyPk;
        private NumberPath<Long> ownerUserPkPath;
        private Long ownerUserPkValue;
        private NumberPath<Integer> maxCountPath;
        private EnumPath<InviteType> targetTypePath;
        private InviteType targetTypeValue;

        public Builder qEntity(EntityPathBase<?> qEntity) { this.qEntity = qEntity; return this; }
        public Builder qPolicy(EntityPathBase<?> qPolicy) { this.qPolicy = qPolicy; return this; }
        public Builder qPolicyPk(NumberPath<Long> qPolicyPk) { this.qPolicyPk = qPolicyPk; return this; }
        public Builder ownerUserPkPath(NumberPath<Long> ownerUserPkPath) { this.ownerUserPkPath = ownerUserPkPath; return this; }
        public Builder ownerUserPkValue(Long ownerUserPkValue) { this.ownerUserPkValue = ownerUserPkValue; return this; }
        public Builder maxCountPath(NumberPath<Integer> maxCountPath) { this.maxCountPath = maxCountPath; return this; }
        public Builder targetTypePath(EnumPath<InviteType> targetTypePath) { this.targetTypePath = targetTypePath; return this; }
        public Builder targetTypeValue(InviteType targetTypeValue) { this.targetTypeValue = targetTypeValue; return this; }

        public MaxVerifyCommand build() {
            return new MaxVerifyCommand(
                    qEntity, qPolicy, qPolicyPk,
                    ownerUserPkPath, ownerUserPkValue,
                    maxCountPath, targetTypePath, targetTypeValue
            );
        }
    }
}

