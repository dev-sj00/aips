package com.portfolio.aips.project.interaction.rating.service.rating_validator;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.interaction.rating.enums.BoardType;
import com.portfolio.aips.project.interaction.rating.service.rating_validator.command.SaveVerifyCommand;
import com.portfolio.aips.project.url_service.archive.repo.ArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingValidatorServiceImpl implements RatingValidatorService {
    private final ArchiveRepository archiveRepository;

    @Override
    public void saveVerify(SaveVerifyCommand command) {
        boolean isExist = true;
        if( command.boardType().equals(BoardType.Archive))
        {
            isExist = archiveRepository.existsByPk(command.boardPk());
        }

        if(!isExist)
        {
            throw new CustomException(ErrorCode.INVALID_RATING_SCORE);
        }
    }


}
