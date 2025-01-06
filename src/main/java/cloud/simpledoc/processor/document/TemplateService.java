package cloud.simpledoc.processor.document;

import cloud.simpledoc.domain.external.TemplateProcessorInterface;
import cloud.simpledoc.domain.external.TemplateServiceInterface;
import cloud.simpledoc.domain.model.Config;
import cloud.simpledoc.domain.model.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateService implements TemplateServiceInterface {
    @Autowired
    private HtmlDocumentProcessor htmlProcessor;
    @Autowired
    private OdtDocumentProcessor odtProcessor;

    @Override
    public TemplateProcessorInterface getTemplateProcessorInterface(Config config) throws NotFoundException {
        switch (config.getTemplateType()) {
            case ODT, RTF -> {
                return odtProcessor;
            }
            case HTML -> {
                return htmlProcessor;
            }
            default -> {
                throw new NotFoundException();
            }
        }
    }
}
