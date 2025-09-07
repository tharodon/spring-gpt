package com.example.springgpt.document;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentIngestionService {

    @Value("classpath:/pdf/test2.txt")
    private Resource resource;
    private final VectorStore vectorStore;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        log.info("Start loading PDF file");
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        TextSplitter textSplitter = TokenTextSplitter.builder()
                .withChunkSize(200)
                .withMinChunkLengthToEmbed(50)
                .withMaxNumChunks(10000)
                .withKeepSeparator(true)
                .build();
        log.info("Ingesting PDF file");
        List<Document> split = textSplitter.split(reader.read());
        vectorStore.accept(split);
        log.info("Completed Ingesting PDF file");
    }
}