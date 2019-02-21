package scratches.data.events.author;

import lombok.AllArgsConstructor;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.util.Assert;
import scratches.data.events.book.Book;

/**
 * @author Rashidi Zin
 */
@RepositoryEventHandler
@AllArgsConstructor
@SuppressWarnings("unused")
public class AuthorRepositoryEventHandler {

    @HandleBeforeCreate
    public void validateAuthorStatus(Book book) {
        Author author = book.getAuthor();

        Assert.isTrue(Author.Status.ACTIVE == author.getStatus(), "book author must be active");
    }

}
