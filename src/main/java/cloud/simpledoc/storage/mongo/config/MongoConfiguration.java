package cloud.simpledoc.storage.mongo.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoClientFactoryBean;

@Configuration
public class MongoConfiguration {
    public @Bean ReactiveMongoClientFactoryBean mongo() {
        ReactiveMongoClientFactoryBean mongo = new ReactiveMongoClientFactoryBean();
        mongo.setConnectionString("mongodb://root:example@localhost/docugen");
        mongo.setMongoClientSettings(MongoClientSettings.builder().credential(MongoCredential.createCredential("root", "admin", "example".toCharArray())).build());
        return mongo;
    }
}
