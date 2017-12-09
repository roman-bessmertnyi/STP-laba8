package testing;

import org.junit.Test;
import replists.*;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class GithubTest {

    @Test
    public void getMostStarredRepositoriesDataTest() throws IOException {
        Github github = new Github();
        assertTrue(github.getMostStarredRepositoriesData());
    }

    @Test
    public void getMostCommittedRepositoriesData() throws IOException {
        Github github = new Github();
        assertTrue(github.getMostCommittedRepositoriesData("2017-05-05","2017-05-13"));
    }
}
