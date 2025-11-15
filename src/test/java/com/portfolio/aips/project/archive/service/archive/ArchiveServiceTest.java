package com.portfolio.aips.project.archive.service.archive;

import com.portfolio.aips.project.archive.dto.request.CreateArchiveRequest;
import com.portfolio.aips.project.archive.entity.ArchiveEntity;
import com.portfolio.aips.project.archive.repo.ArchiveRepository;
import com.portfolio.aips.project.archive.service.url_generator.URLGeneratorService;
import com.portfolio.aips.project.archive.service.url_generator.enums.URLGeneratorType;
import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.users.dto.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArchiveServiceTest {

}