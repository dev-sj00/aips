package com.portfolio.aips.project.interaction.rating.service.rating.service;

import com.portfolio.aips.project.interaction.rating.dto.RedisSaveRatingsKeyDTO;
import com.portfolio.aips.project.interaction.rating.entity.QRatingEntity;
import com.portfolio.aips.project.interaction.rating.entity.RatingEntity;
import com.portfolio.aips.project.interaction.rating.repo.RatingRedisRepository;
import com.portfolio.aips.project.interaction.rating.repo.RatingRepository;
import com.portfolio.aips.project.interaction.rating.service.rating.command.FindOwnRatings;
import com.portfolio.aips.project.interaction.rating.service.rating.command.SaveCommand;
import com.portfolio.aips.project.interaction.rating.service.rating.result.FindOwnRatingsResult;
import com.portfolio.aips.project.interaction.rating.service.rating_validator.RatingValidatorService;
import com.portfolio.aips.project.interaction.rating.service.rating_validator.command.SaveVerifyCommand;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl  implements RatingService {

    private final RatingRepository ratingRepository;
    private final JPAQueryFactory queryFactory;
    private final RatingValidatorService validatorService;
    private final RatingRedisRepository ratingRedisRepository;

    @Override
    @Transactional
    public void save(SaveCommand command) {
        validatorService.saveVerify(new SaveVerifyCommand(command.boardPk(), command.boardType()));

        QRatingEntity ratingEntity = QRatingEntity.ratingEntity;


        RatingEntity existingEntity = queryFactory.selectFrom(ratingEntity).where(
                ratingEntity.boardPk.eq(command.boardPk())
                .and(ratingEntity.boardType.eq(command.boardType()))
                        .and(ratingEntity.raterUserPk.eq(command.raterUserPk()))
        ).fetchOne();

        if (existingEntity != null) {
            existingEntity.updateScores(
                    command.usefulnessScore(),
                    command.reliabilityScore(),
                    command.funScore()
            );
            return; // flush 시점에 자동 UPDATE
        }

        log.info("rater user Pk : {}", command.raterUserPk());
        RatingEntity newEntity = RatingEntity.builder()
                .boardPk(command.boardPk())
                .boardType(command.boardType())
                .usefulnessScore(command.usefulnessScore())
                .reliabilityScore(command.reliabilityScore())
                .funScore(command.funScore())
                .raterUserPk(command.raterUserPk())
                .build();

        ratingRepository.save(newEntity);

        //스케줄러 처리용
        ratingRedisRepository.saveRatings(new RedisSaveRatingsKeyDTO(command.boardPk(), command.boardType()));


    }

    @Override
    public FindOwnRatingsResult findOwnRatings(FindOwnRatings command) {
        RatingEntity ratingEntity = ratingRepository.findByBoardPkAndBoardTypeAndRaterUserPk(command.boardPk(), command.boardType(), command.ownUserPk());
        return new FindOwnRatingsResult(ratingEntity.getUsefulnessScore(), ratingEntity.getReliabilityScore(), ratingEntity.getFunScore());
    }
}
