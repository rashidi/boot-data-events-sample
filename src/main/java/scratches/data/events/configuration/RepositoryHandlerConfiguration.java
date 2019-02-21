package scratches.data.events.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import scratches.data.events.author.AuthorRepositoryEventHandler;

/**
 * @author Rashidi Zin
 */
@Configuration
public class RepositoryHandlerConfiguration {

    @Bean
    public AuthorRepositoryEventHandler authorRepositoryEventHandler() {
        return new AuthorRepositoryEventHandler();
    }

}
