package cloud.simpledoc.storage.mongo.model;

import cloud.simpledoc.domain.model.Config;
import cloud.simpledoc.domain.model.TemplateHtmlConfig;
import cloud.simpledoc.domain.model.TemplateOdtConfig;
import cloud.simpledoc.domain.model.TemplateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "configuration")
public class ConfigurationEntity {
    @Id
    String reference;
    TemplateType templateType;

    TemplateOdtConfig templateOdtConfig;
    TemplateHtmlConfig templateHtmlConfig;

    public Config toConfig() {
        return new Config(
                this.reference,
                this.templateType,
                this.templateOdtConfig,
                this.templateHtmlConfig
        );
    }

    public static ConfigurationEntity fromConfig(Config config) {
        return new ConfigurationEntity(
                config.getReference(),
                config.getTemplateType(),
                config.getTemplateOdtConfig(),
                config.getTemplateHtmlConfig()
        );
    }
}
