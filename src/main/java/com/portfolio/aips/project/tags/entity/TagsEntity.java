package com.portfolio.aips.project.tags.entity;


import jakarta.persistence.*;
import lombok.Getter;

@Entity(name = "tags")
@Getter
public class TagsEntity {

    @Id
    @Column(name = "tags_pk")
/*    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tags_seq")
    @SequenceGenerator(
            name = "tags_seq",
            sequenceName = "tags_seq",
            allocationSize = 50   // Hibernate batch size와 맞춰주는 게 좋음
    )* 소규모 배치 불필요/

 */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    public TagsEntity(String name) {
        this.name = name;
    }

    public TagsEntity() {

    }
}
