package cloud.simpledoc.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TemplateHtmlConfig {
    byte[] templateFile;
}