# Spring Data REST with Event Example
Utilize events in [Spring Data REST][1] to perform pre and post operations with `@RepositoryEventHandler`.

## Background
[Spring Data REST][1] helps developer to create REST application with little implementation as it turns `Repository` classes into REST endpoint. In this example we will explore 
how we can perform actions before and after persisting an entity using `@RepositoryEventHandler` and its related annotations.

## Entity and Repository Classes
There are two `@Entity` classes; [Author][2] and [Book][3], and `JpaRepository` classes; [AuthorRepository][4] and [BookRepository][5]. 

### Author
Author `@Entity` consists of `id`, `name`, and `status`:

```java
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
```

While its `JpaRepository` class contains no additional methods:

```java
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
```

### Book
Book `@Entity` consists of `id`, `author`, and `title` fields:

```java
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
```

Same as `Author`, its repository contains no additional methods:

```java
public interface BookRepository extends JpaRepository<Book, Long> {
}
```

As we can see, both classes are normal JPA entity classes without any additional configurations. Once we boot the 
application there will be two REST endpoints available; `/authors` and `/books`.

## Add Author Validation
Next is to add a validation against `Book.author` where we will ensure that an `Author` of a `Book` must be `ACTIVE` before 
adding it into the database. For this we will use `@RepositoryEventHandler` with `@HandleBeforeCreate`.

By annotating our class with [RepositoryEventHandler][6], we are informing Spring that this class will manage repository related events. 
Along with it we will use [HandleBeforeCreate][7], which indicate that the method need to be executed before persisting given `@Entity`.

We will implement our validation in [AuthorRepositoryEventHandler][8]:

```java
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
```

As we can see, the method `validateAuthorStatus` takes `Book` as a parameter and validate `status` of an `Author` to make 
sure that the author is still `ACTIVE`.

## Configuration Class
Next is to inform the application that we would like to register `AuthorRepositoryEventHandler` as a `Bean` and thus will be triggered 
when related entity is being used. This can be found in [RepositoryHandlerConfiguration][9]:

```java
@Configuration
public class RepositoryHandlerConfiguration {

    @Bean
    public AuthorRepositoryEventHandler authorRepositoryEventHandler() {
        return new AuthorRepositoryEventHandler();
    }

}
```

## Verify Implementation
As always, we will verify our implementation through an [integration tests][10]. In the following test we will create an `Author` 
with status `INACTIVE` and followed by creating a `Book` that ties to the `Author`.

We will expect that it will return `Internal Server Error` with a message `book author must be active`:

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookRepositoryRestTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void createBookWithInactiveAuthor() throws Exception {
        String authorUri = getAuthorUri(INACTIVE);

        JSONObject request = new JSONObject();

        request.put("author", authorUri);
        request.put("title", "If");

        mvc.perform(
                post("/books")
                        .content(request.toString())
        )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("book author must be active")));
    }

    private String getAuthorUri(Author.Status status) throws Exception {
        JSONObject request = new JSONObject();

        request.put("name", "Rudyard Kipling");
        request.put("status", status.name());

        return mvc.perform(
                post("/authors")
                        .content(request.toString())
        )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader(LOCATION);
    }

}
```

With a correct implementation, the test above should pass.

[1]: https://spring.io/projects/spring-data-rest
[2]: src/main/java/scratches/data/events/author/Author.java
[3]: src/main/java/scratches/data/events/book/Book.java
[4]: src/main/java/scratches/data/events/author/AuthorRepository.java
[5]: src/main/java/scratches/data/events/book/BookRepository.java
[6]: https://docs.spring.io/spring-data/rest/docs/current/api/org/springframework/data/rest/core/annotation/RepositoryEventHandler.html
[7]: https://docs.spring.io/spring-data/rest/docs/current/api/org/springframework/data/rest/core/annotation/HandleBeforeCreate.html
[8]: src/main/java/scratches/data/events/author/AuthorRepositoryEventHandler.java
[9]: src/main/java/scratches/data/events/configuration/RepositoryHandlerConfiguration.java
[10]: src/test/java/scratches/data/events/book/BookRepositoryRestTests.java
