package cloud.simpledoc.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Config {
    String reference;
    TemplateType templateType;

    TemplateOdtConfig templateOdtConfig;
    TemplateHtmlConfig templateHtmlConfig;
}
