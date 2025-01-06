package cloud.simpledoc.processor.document;

import cloud.simpledoc.domain.model.Config;
import cloud.simpledoc.domain.external.TemplateProcessorInterface;
import cloud.simpledoc.domain.model.ResultType;
import cloud.simpledoc.domain.model.exception.GenerateFileException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.util.Map;

@Service
public class HtmlDocumentProcessor implements TemplateProcessorInterface {
    @Autowired
    MustacheProcessor mustacheProcessor;

    @Override
    public byte[] processTemplate(Map<String, Object> data, Config config, ResultType resultType) throws GenerateFileException {
        byte[] processedTemplate = mustacheProcessor.applyData(config.getTemplateHtmlConfig().getTemplateFile(), data);
        ByteArrayInputStream templateInputStream = new ByteArrayInputStream(processedTemplate);
        Document document = null;
        try {
            document = Jsoup.parse(templateInputStream, "UTF-8", "http://localhost");
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ITextRenderer renderer = new ITextRenderer();
                SharedContext sharedContext = renderer.getSharedContext();
                sharedContext.setPrint(true);
                sharedContext.setInteractive(false);
                renderer.setDocumentFromString(document.html());
                renderer.layout();
                renderer.createPDF(outputStream);


                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
