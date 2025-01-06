package cloud.simpledoc.controller.api;

import cloud.simpledoc.domain.model.ResultType;
import cloud.simpledoc.domain.model.exception.NotFoundException;
import cloud.simpledoc.domain.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

@RestController
@RequestMapping("/generate")
public class GeneratorController {
    @Autowired
    DocumentService documentService;

    @PostMapping(value = "/pdf/{reference}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_PDF_VALUE)
    public Mono<Resource> getEmployeeById(
            @PathVariable String reference,
            @RequestBody Map<String, Object> initialData) {
        return documentService.generateDocument(initialData, reference, ResultType.PDF)
                .map(ByteArrayResource::new);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> onException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
