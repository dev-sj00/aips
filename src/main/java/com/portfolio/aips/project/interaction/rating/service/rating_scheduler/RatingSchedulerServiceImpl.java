package com.portfolio.aips.project.interaction.rating.service.rating_scheduler;


import com.portfolio.aips.project.interaction.rating.repo.RatingRedisRepository;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingSchedulerServiceImpl implements RatingSchedulerService{

    private final RatingRedisRepository ratingRedisRepository;

    @Override
    @Scheduled(cron = "0 */5 * * * *")
    @SchedulerLock(
            name = "updateRatingAndPopularScore",
            lockAtMostFor = "PT10M"
    )
    public void updateRatingAndPopularScore() {
        ratingRedisRepository.findAllWithScan().forEach(entity -> {

        });
    }


}
