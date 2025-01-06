package cloud.simpledoc.processor.document;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class MustacheProcessor {
    MustacheFactory factory = new DefaultMustacheFactory();

    Set<String> hashList = new HashSet<>();

    public byte[] applyData(byte[] template, Map<String, Object> data)  {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            String hash = new String(digest.digest(template));
            StringWriter stringWriter = new StringWriter();
            if (hashList.contains(hash)) {
                factory.compile(hash).execute(stringWriter, data);
            } else {
                factory.compile(new StringReader(new String(template)), hash).execute(stringWriter, data);
            }
            return stringWriter.toString().getBytes();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
