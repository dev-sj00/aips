# Hibernate Batch Insert & PostgreSQL Sequence 설정 가이드

## 1. 개요

Hibernate에서 대량 Insert를 효율적으로 처리하려면 **배치(insert) 설정**과 **PK 생성 전략(sequence)**을 올바르게 구성해야 합니다.

주요 포인트:

- `hibernate.jdbc.batch_size`로 한 번에 묶어 실행
- `@Id` + `@GeneratedValue(strategy = GenerationType.SEQUENCE)` 사용
- DB에 시퀀스가 존재해야 Hibernate가 올바르게 PK를 가져올 수 있음
- 시퀀스 increment 값과 Hibernate의 allocationSize를 맞춰야 함

---

## 2. Hibernate Batch Insert 설정

`application.yml` 또는 `LocalContainerEntityManagerFactoryBean` 설정에서 지정 가능

```java
Properties properties = new Properties();

// Hibernate batch insert
properties.put("hibernate.jdbc.batch_size", "50"); // 한 번에 묶을 row 개수
properties.put("hibernate.order_inserts", "true"); // insert 순서 정렬
properties.put("hibernate.order_updates", "true"); // update 순서 정렬
properties.put("hibernate.generate_statistics", "true"); // 통계 확인용
```

> ⚠️ `hibernate.jdbc.batch_size`를 50~100 정도로 설정하면 대부분 성능 향상 효과 있음

---

## 3. Entity PK 설정 (@Id + Sequence)

```java
@Entity
@Table(name = "tags")
public class TagsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tags_seq_gen")
    @SequenceGenerator(
        name = "tags_seq_gen",
        sequenceName = "tags_seq", // DB에 생성된 시퀀스 이름
        allocationSize = 50        // Hibernate와 DB increment size 일치 필수
    )
    private Long tagsPk;

    private String name;

    public TagsEntity(String name) {
        this.name = name;
    }
}
```

- `sequenceName`: DB에 존재하는 시퀀스 이름
- `allocationSize`: Hibernate가 한 번에 가져오는 시퀀스 step 수 → DB 시퀀스 increment와 동일하게 설정

---

## 4. PostgreSQL 시퀀스 생성

```sql
-- 기존 테이블에 맞춰 시퀀스 생성
CREATE SEQUENCE tags_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 50;

-- 테이블 컬럼 default 값 지정 (선택 사항)
ALTER TABLE tags
    ALTER COLUMN tags_pk SET DEFAULT nextval('tags_seq');
```

> ⚠️ `INCREMENT BY` 값과 Hibernate `allocationSize`가 다르면 `MappingException` 발생

---

## 5. BoardTagsEntity 등 관계 테이블 PK 설정

```java
@Entity
@Table(name = "board_tags")
public class BoardTagsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_tags_seq_gen")
    @SequenceGenerator(
        name = "board_tags_seq_gen",
        sequenceName = "board_tags_seq",
        allocationSize = 50
    )
    private Long boardTagsPk;

    private Long boardId;
    private String boardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tags_pk")
    private TagsEntity tags;
}
```

- Batch insert 적용 대상 테이블은 모두 PK가 sequence로 생성되어야 batch가 정상 동작
- `ManyToOne` 관계도 lazy 로딩 권장 (insert 성능 영향 최소화)

---

## 6. 주의 사항

1. Hibernate batch insert는 **IDENTITY 전략**과 함께 사용 불가
    - PostgreSQL `SERIAL`이나 `IDENTITY` 컬럼은 자동 commit 시점에 PK 생성 → batch 불가
2. DB 시퀀스 increment 값과 Hibernate `allocationSize` **반드시 동일**
3. `CascadeType.PERSIST`시 단건 단위로 insert가 실행되어 batch 효과가 사라짐
4. `saveAll` 반환값 활용 → DB 재조회 최소화

---

## 7. 요약

- **Hibernate batch insert** → `hibernate.jdbc.batch_size`, `order_inserts`
- **PK 전략** → `@Id + @GeneratedValue(strategy = SEQUENCE) + @SequenceGenerator`
- **DB 시퀀스 생성** → `INCREMENT BY`와 Hibernate `allocationSize` 일치
- **관계 테이블** → 모든 PK 시퀀스 적용, batch insert 지원

