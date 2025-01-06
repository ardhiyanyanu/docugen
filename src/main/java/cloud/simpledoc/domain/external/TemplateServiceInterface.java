package cloud.simpledoc.domain.external;

import cloud.simpledoc.domain.model.Config;
import cloud.simpledoc.domain.model.exception.NotFoundException;

public interface TemplateServiceInterface {
    TemplateProcessorInterface getTemplateProcessorInterface(Config config) throws NotFoundException;
}
