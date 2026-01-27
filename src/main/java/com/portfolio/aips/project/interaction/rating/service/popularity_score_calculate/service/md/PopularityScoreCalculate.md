# 인기도 점수 계산 알고리즘

## 개요
이 문서는 콘텐츠의 인기도 점수를 계산하는 알고리즘을 설명합니다. 평점, 조회수, 생성 시간을 기반으로 종합적인 인기도 점수를 산출합니다.

## 계산 공식

### 최종 인기도 점수
```
popularityScore = (평균 평점 점수 + 조회수 점수) × 반감기 가중치
```

### 1. 평균 평점 점수 (sumAvgRatingScore)
```
평균 평점 점수 = ((재미 평점 + 신뢰도 평점 + 유용성 평점) / 3 + log(평점 개수 + 1)) × 0.5
```

**구성요소:**
- 재미 평점 평균 (funAvgScore)
- 신뢰도 평점 평균 (reliabilityAvgScore)
- 유용성 평점 평균 (usefulnessAvgScore)
- 평점 개수 (ratingCount)의 로그 변환 값

**가중치:** 0.5

### 2. 조회수 점수 (viewCountsScore)
```
조회수 점수 = log(조회수 개수 + 1) × 0.3
```

**가중치:** 0.3


### 3. 반감기 가중치 (Half-Life Weight)

콘텐츠의 생성 시간에 따라 시간이 지날수록 가중치가 감소합니다.

| 생성 시점 | 가중치 |
|---------|--------|
| 1개월 이내 | 1.0 |
| 1~3개월 | 0.9 |
| 3~6개월 | 0.8 |
| 6개월~1년 | 0.7 |
| 1~3년 | 0.55 |
| 3년 이상 | 0.4 |

## 주요 특징

### 로그 스케일 적용
- 평점 개수와 조회수에 `log1p()` (log(x+1)) 함수를 적용하여 값이 커질수록 증가폭을 완만하게 조정
- 극단적인 값의 영향력을 제한하여 균형잡힌 점수 산출

### 시간 감쇠 효과
- 오래된 콘텐츠일수록 낮은 가중치 적용
- 최신 콘텐츠를 우선적으로 노출하는 효과

### 다차원 평가
- 단일 평점이 아닌 3가지 측면(재미, 신뢰도, 유용성)을 종합적으로 평가

## 입력 데이터 구조

### CalculatePopularityScoreCommand
- `avgRatingInfo`: PopularityScoreElementsResult 타입의 평점 정보

### PopularityScoreElementsResult
- `funAvgScore`: 재미 평점 평균
- `reliabilityAvgScore`: 신뢰도 평점 평균
- `usefulnessAvgScore`: 유용성 평점 평균
- `ratingCount`: 평점 개수
- `createdDateTime`: 콘텐츠 생성 시간

## 출력 데이터 구조

### CalculatePopularityScoreResult
- `avgRatingInfo`: 입력받은 평점 정보 (원본)
- `popularityScore`: 계산된 최종 인기도 점수

