package cloud.simpledoc.processor.document;

import cloud.simpledoc.domain.external.TemplateProcessorInterface;
import cloud.simpledoc.domain.model.Config;
import cloud.simpledoc.domain.model.ResultType;
import cloud.simpledoc.domain.model.TemplateType;
import cloud.simpledoc.domain.model.exception.GenerateFileException;
import cloud.simpledoc.processor.document.jod.DocumentConverterGenerator;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class OdtDocumentProcessor implements TemplateProcessorInterface {
    @Autowired
    DocumentConverterGenerator generator;

    @Override
    public byte[] processTemplate(Map<String, Object> data, Config config, ResultType resultType) throws GenerateFileException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            DocumentFormat formatInput = DefaultDocumentFormatRegistry.ODT;
            if (Objects.requireNonNull(config.getTemplateType()) == TemplateType.RTF) {
                formatInput = DefaultDocumentFormatRegistry.RTF;
            }

            DocumentFormat formatOutput = DefaultDocumentFormatRegistry.PDF;
            if (Objects.requireNonNull(resultType) == ResultType.JPG) {
                formatOutput = DefaultDocumentFormatRegistry.JPEG;
            }

            ByteArrayInputStream templateInputStream = new ByteArrayInputStream(config.getTemplateOdtConfig().getTemplateFile());
            generator.getLocalDocumentConverter(data)
                    .convert(templateInputStream).as(formatInput)
                    .to(out).as(formatOutput)
                    .execute();
            return out.toByteArray();
        } catch (OfficeException | IOException e) {
            throw new GenerateFileException(0, "", e);
        }
    }
}
