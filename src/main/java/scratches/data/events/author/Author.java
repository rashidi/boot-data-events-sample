package scratches.data.events.author;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import scratches.data.events.book.Book;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * @author Rashidi Zin
 */
@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Author {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String name;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private Set<Book> books;

    private Status status;

    public enum Status {

        ACTIVE,

        INACTIVE

    }
}
