package cloud.simpledoc.domain.service;

import cloud.simpledoc.domain.external.ConfigStorageInterface;
import cloud.simpledoc.domain.external.TemplateServiceInterface;
import cloud.simpledoc.domain.model.ResultType;
import cloud.simpledoc.domain.model.exception.GenerateFileException;
import cloud.simpledoc.domain.model.exception.NotFoundException;
import cloud.simpledoc.domain.external.TemplateProcessorInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class DocumentService {
    @Autowired
    DataService dataService;
    @Autowired
    ConfigStorageInterface configStorageInterface;
    @Autowired
    TemplateServiceInterface templateService;

    public Mono<byte[]> generateDocument(Map<String, Object> initialData, String configReference, ResultType resultType) {
        return configStorageInterface.getConfigByReference(configReference)
                .switchIfEmpty(Mono.error(new NotFoundException()))
                .flatMap(config -> {
                    Map<String, Object> finalData = dataService.enrichData(initialData, config);
                    try {
                        TemplateProcessorInterface processor = templateService.getTemplateProcessorInterface(config);
                        return Mono.just(processor.processTemplate(finalData, config, resultType));
                    } catch (NotFoundException | GenerateFileException e) {
                        return Mono.error(e);
                    }
                });
    }
}
