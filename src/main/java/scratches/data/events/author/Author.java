package scratches.data.events.author;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

/**
 * @author Rashidi Zin
 */
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Author {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String name;

}
