package cloud.simpledoc.processor.document.jod;

import cloud.simpledoc.processor.document.jod.filter.TemplateFilter;
import com.sun.star.document.UpdateDocMode;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DocumentFormatRegistry;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DocumentConverterGenerator {
    @Autowired
    JodConverterLocalProperties properties;

    @Autowired(required = false)
    OfficeManager localOfficeManager;

    @Autowired(required = false)
    DocumentFormatRegistry documentFormatRegistry;

    public DocumentConverter getLocalDocumentConverter(Map data) {
        final Map<String, Object> loadProperties = new HashMap<>();
        if (properties.isApplyDefaultLoadProperties()) {
            loadProperties.putAll(LocalConverter.DEFAULT_LOAD_PROPERTIES);
            if (properties.isUseUnsafeQuietUpdate()) {
                loadProperties.put("UpdateDocMode", UpdateDocMode.QUIET_UPDATE);
            }
        }

        return LocalConverter.builder()
                .officeManager(localOfficeManager)
                .formatRegistry(documentFormatRegistry)
                .loadDocumentMode(properties.getLoadDocumentMode())
                .loadProperties(loadProperties)
                .filterChain(new TemplateFilter(data))
                .build();
    }
}
