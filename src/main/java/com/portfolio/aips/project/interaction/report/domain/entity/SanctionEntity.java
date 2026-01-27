package com.portfolio.aips.project.interaction.report.domain.entity;

import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.interaction.report.domain.event.ReportStatusCompletedEvent;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "sanction")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Getter
@Setter
public class SanctionEntity extends ReportBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sanction_pk")
    private Long pk;


    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    private String reason; //제재 이유 제재자 알림으로 발송됨

    @Column(name="ban_type")
    private BanType banType;

    @Transient
    private final List<Object> events = new ArrayList<>();



    public boolean StatusIsNotValid()
    {

        return (reportStatus.equals(ReportStatus.COMPLETED) || reportStatus.equals(ReportStatus.CONFIRMED)) && (banType ==null || reason == null);
    }

    public void updateStatus(ReportStatus status)
    {
        this.setReportStatus(status);


    }

    public void updateStatusCompleted()
    {
        if(this.getReportStatus().equals(ReportStatus.COMPLETED))
        {
            events.add(new ReportStatusCompletedEvent(this.getBanType(), this.getTargetUserPk()));
        }
    }


    @DomainEvents
    public Collection<Object> domainEvents() {
        return events;
    }


    @AfterDomainEventPublication
    public void clearEvents() {
        events.clear();
    }







}
