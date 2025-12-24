package com.portfolio.aips.project.elastic_search.archive.service.archive_el_search_log.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.AnalyzeRequest;
import co.elastic.clients.elasticsearch.indices.AnalyzeResponse;
import co.elastic.clients.elasticsearch.indices.analyze.AnalyzeToken;
import com.portfolio.aips.project.elastic_search.archive.dto.ArchiveSearchLogDocument;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el.ArchiveELService;
import com.portfolio.aips.project.elastic_search.service.ESIndexService;
import com.portfolio.aips.project.users.entity.QUsersEntity;
import com.portfolio.aips.project.users.service.user.UserService;
import com.portfolio.aips.project.utils.DateUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ArchiveELSearchLogServiceImpl implements ArchiveELSearchLogService{
    private final ArchiveELService archiveELService;
    private final ElasticsearchClient client;
    private final UserService userService;

    @Override
    public void save(String queryRaw, Long userPk) throws IOException {

        AnalyzeRequest request = AnalyzeRequest.of(a -> a
                .index("archive_search_log") // index analyzer 사용
                .analyzer("stat_analyzer")  // analyzer 이름
                .text(queryRaw)
        );
        AnalyzeResponse response = client.indices().analyze(request);

        //when
        // 토큰 리스트 추출
        List<String> result = response.tokens().stream()
                .map(AnalyzeToken::token)
                .toList();

        String queryStat = String.join(
                " ",
                response.tokens().stream()
                        .map(AnalyzeToken::token)
                        .toList()
        );

        String userNickName = userService.findUserNickName(userPk);
        ArchiveSearchLogDocument saveDoc = ArchiveSearchLogDocument
                .builder()
                .hasFiltered(false)
                .queryRaw(queryRaw)
                .queryStat(queryStat)
                .userNickName(userNickName)
                .createdDateTime(DateUtils.getDateTimeNow())
                .tokens(result)
                .build();

        archiveELService.save(saveDoc);

    }

    @Override
    public void getLogs(List<ArchiveSearchLogDocument> docs, int pageNo, int pageSize) {

    }

}
