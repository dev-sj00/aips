package com.portfolio.aips.project.tags.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "board_tags")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BoardTagsEntity {
    @Id
    @Column(name = "board_tags_pk")

/*    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_tags_seq")
    @SequenceGenerator(
            name = "board_tags_seq",
            sequenceName = "board_tags_seq",
            allocationSize = 50   // Hibernate batch size와 맞추기
    )* 불필요/

 */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tags_pk", nullable = false)
    private TagsEntity tags;


    @Column(name = "board_id",  nullable = false)
    private Long boardId;

    @Column(name = "board_type",  nullable = false)
    private String boardType;


}
