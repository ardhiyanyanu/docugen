package cloud.simpledoc.domain;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cloud.simpledoc.domain.external.ConfigStorageInterface;
import cloud.simpledoc.domain.external.TemplateServiceInterface;
import cloud.simpledoc.domain.model.Config;
import cloud.simpledoc.domain.model.ResultType;
import cloud.simpledoc.domain.model.TemplateHtmlConfig;
import cloud.simpledoc.domain.model.TemplateType;
import cloud.simpledoc.domain.model.exception.GenerateFileException;
import cloud.simpledoc.domain.model.exception.NotFoundException;
import cloud.simpledoc.domain.service.DataService;
import cloud.simpledoc.domain.service.DocumentService;
import cloud.simpledoc.processor.document.HtmlDocumentProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {
    @InjectMocks
    DocumentService service;

    @Mock
    DataService dataService;

    @Mock
    ConfigStorageInterface configStorageInterface;

    @Mock
    TemplateServiceInterface templateService;

    @Test
    void generatePdfDocument() throws NotFoundException, GenerateFileException {
        String reference = "Ref1";
        Map<String, Object> initial = new HashMap<>();
        Config config = new Config(reference, TemplateType.HTML, null, new TemplateHtmlConfig(new byte[]{}));
        when(configStorageInterface.getConfigByReference(reference)).thenReturn(Mono.just(config));
        when(dataService.enrichData(initial, config)).thenAnswer(param -> param.getArgument(0));
        HtmlDocumentProcessor processor = mock();
        when(templateService.getTemplateProcessorInterface(config)).thenReturn(processor);
        when(processor.processTemplate(initial, config, ResultType.PDF)).thenReturn(new byte[]{1,2,3});

        Mono<byte[]> result = service.generateDocument(initial, reference, ResultType.PDF);
        StepVerifier.create(result)
                .expectNextMatches(response -> Arrays.equals(response, new byte[]{1, 2, 3}))
                .verifyComplete();
    }

    @Test
    void generatePdfDocumentConfigNotFound() {
        String reference = "Ref1";
        when(configStorageInterface.getConfigByReference(reference)).thenReturn(Mono.empty());

        Mono<byte[]> result = service.generateDocument(new HashMap<>(), reference, ResultType.PDF);
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }
}
