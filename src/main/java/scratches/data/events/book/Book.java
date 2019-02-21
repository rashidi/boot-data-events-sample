package scratches.data.events.book;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import scratches.data.events.author.Author;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Rashidi Zin
 */
@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Book {

    @Id
    @GeneratedValue
    private Long id;

    @JoinColumn
    @ManyToOne(optional = false)
    @NotNull
    private Author author;

    @NotBlank
    private String title;

}
