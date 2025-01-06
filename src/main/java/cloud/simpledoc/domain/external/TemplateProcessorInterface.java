package cloud.simpledoc.domain.external;

import cloud.simpledoc.domain.model.Config;
import cloud.simpledoc.domain.model.ResultType;
import cloud.simpledoc.domain.model.exception.GenerateFileException;

import java.util.Map;

public interface TemplateProcessorInterface {
    byte[] processTemplate(Map<String, Object> data, Config config, ResultType resultType) throws GenerateFileException;
}
