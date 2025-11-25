package com.portfolio.aips.project.elastic_search.archive.repo;

import com.portfolio.aips.project.elastic_search.archive.document.ArchiveDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ArchiveRepositoryTest {



    private final ArchiveRepository archiveRepository;

    @Autowired
    ArchiveRepositoryTest(ArchiveRepository archiveRepository) {
        this.archiveRepository = archiveRepository;
    }


    @Test
    void findById() {
        ArchiveDocument archiveDocument = new ArchiveDocument();
        archiveDocument.setTitle("title");
        archiveDocument.setDescription("description");
        archiveRepository.save(archiveDocument);

        Iterable<ArchiveDocument> result = archiveRepository.findByTitle("title");
        for(ArchiveDocument document : result){
            System.out.println(document);

        }

    }
}