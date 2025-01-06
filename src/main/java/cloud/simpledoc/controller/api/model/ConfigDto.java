package cloud.simpledoc.controller.api.model;

import cloud.simpledoc.domain.model.Config;
import cloud.simpledoc.domain.model.TemplateType;

public record ConfigDto(
        String reference,
        TemplateType templateType
) {
    public static ConfigDto fromConfig(Config config) {
        return new ConfigDto(config.getReference(), config.getTemplateType());
    }
}
