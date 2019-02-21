package scratches.data.events.book;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import scratches.data.events.author.Author;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static scratches.data.events.author.Author.Status.ACTIVE;
import static scratches.data.events.author.Author.Status.INACTIVE;

/**
 * @author Rashidi Zin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookRepositoryRestTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void createBook() throws Exception {
        String authorUri = getAuthorUri(ACTIVE);

        JSONObject request = new JSONObject();

        request.put("author", authorUri);
        request.put("title", "The Jungle Book");

        mvc.perform(
                post("/books")
                        .content(request.toString())
        )
                .andExpect(status().isCreated());
    }

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