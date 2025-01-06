package cloud.simpledoc.controller.api;

import cloud.simpledoc.controller.api.model.ConfigDto;
import cloud.simpledoc.controller.api.model.exception.BadRequestException;
import cloud.simpledoc.domain.model.Config;
import cloud.simpledoc.domain.model.TemplateHtmlConfig;
import cloud.simpledoc.domain.model.TemplateOdtConfig;
import cloud.simpledoc.domain.model.TemplateType;
import cloud.simpledoc.domain.service.ConfigurationService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/configuration")
public class ConfigController {
    @Autowired
    ConfigurationService configurationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ConfigDto> createConfiguration(
            @RequestPart("template") FilePart template,
            @RequestPart("reference") String reference
    ) {
        return DataBufferUtils.join(template.content())
                .map(dataBuffer -> dataBuffer.asByteBuffer().array())
                .flatMap(fileByteArray -> {
                    switch (FilenameUtils.getExtension(template.filename().toLowerCase())) {
                        case "html":
                            return Mono.just(new Config(
                                    reference,
                                    TemplateType.HTML,
                                    null,
                                    new TemplateHtmlConfig(fileByteArray)
                            ));
                        case "odt" :
                            return Mono.just(new Config(
                                    reference,
                                    TemplateType.ODT,
                                    new TemplateOdtConfig(fileByteArray),
                                    null
                            ));
                        case "rtf" :
                            return Mono.just(new Config(
                                    reference,
                                    TemplateType.RTF,
                                    new TemplateOdtConfig(fileByteArray),
                                    null
                            ));
                        default: return Mono.error(new BadRequestException(""));
                    }
                })
                .flatMap(configurationService::createConfig)
                .map(ConfigDto::fromConfig);
    }
}
