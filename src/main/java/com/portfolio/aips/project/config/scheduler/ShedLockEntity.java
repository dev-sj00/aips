package com.portfolio.aips.project.config.scheduler;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "shedlock")
public class ShedLockEntity {

    // Getter / Setter
    @Id
    @Column(length = 64)
    private String name; // @SchedulerLock(name="...")과 매칭

    @Column(name = "lock_until")
    private LocalDateTime lockUntil;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @Column(name = "locked_by", length = 255)
    private String lockedBy;

    // JPA 기본 생성자
    protected ShedLockEntity() {}

    public ShedLockEntity(String name, LocalDateTime lockUntil, LocalDateTime lockedAt, String lockedBy) {
        this.name = name;
        this.lockUntil = lockUntil;
        this.lockedAt = lockedAt;
        this.lockedBy = lockedBy;
    }

    public void setName(String name) { this.name = name; }

    public void setLockUntil(LocalDateTime lockUntil) { this.lockUntil = lockUntil; }

    public void setLockedAt(LocalDateTime lockedAt) { this.lockedAt = lockedAt; }

    public void setLockedBy(String lockedBy) { this.lockedBy = lockedBy; }
}
