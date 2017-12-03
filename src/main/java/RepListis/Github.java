package RepListis;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.*;

public class Github {
    private CloseableHttpClient httpclient;
    private ArrayList<Repository> mostStarredRepositories;
    private ArrayList<Repository> mostCommittedRepositories;
    private JsonParser parser;
    private final String TOKEN = "9129bb1665168c2dbb12a04c6a2074efb181c630";

    public Github() {
        httpclient = HttpClients.createDefault();
        mostCommittedRepositories = new ArrayList<>();
        mostStarredRepositories = new ArrayList<>();
        parser = new JsonParser();
    }

    private CloseableHttpResponse requestMostStarred() throws IOException {
        URIBuilder URI = new URIBuilder()
                .setScheme("https")
                .setHost("api.github.com")
                .setPath("/search/repositories")
                .addParameter("q", "stars:>1")
                .addParameter("sort", "stars")
                .addParameter("order", "desc")
                .addParameter("per_page", "10");
        HttpGet httpGet = new HttpGet(URI.toString());
        httpGet.addHeader("Authorization", "token " + TOKEN);
        return httpclient.execute(httpGet);
    }

    private ArrayList<Repository> getMostStarred(CloseableHttpResponse responseRepository)
            throws IOException {
        try {
            BufferedReader brRepository = new BufferedReader(new InputStreamReader(responseRepository
                    .getEntity().getContent()));
            String repositoryData = brRepository.readLine();
            mostStarredRepositories = parser.parseRepositoryJson(repositoryData);
            for (int i = 0; i < mostStarredRepositories.size(); i++) {
                getRepositoryCommits(requestRepositoryCommits(mostStarredRepositories.get(i)));
                mostStarredRepositories.get(i).
                        setContributors(getRepositoryCommits(requestRepositoryCommits(mostStarredRepositories.get(i))));
            }
        } finally {
            responseRepository.close();
        }
        return mostStarredRepositories;
    }

    private void printMostStarred() {
        System.out.println("Most Starred Repositories");
        for (Repository repo : mostStarredRepositories) {
            System.out.println("\n#" + (mostStarredRepositories.indexOf(repo) + 1));
            System.out.println("Name: " + repo.getName());
            System.out.println("Owner: " + repo.getOwner());
            System.out.println("Description: " + repo.getDescription());
            System.out.println("Language: " + repo.getLanguage());
            System.out.println("Stars: " + repo.getStars());
            System.out.println("Users with most commits:");
            for (int i = 0; i < 5; i++) {
                System.out.println("\t---> " + repo.getContributors().get(i).getContributionCount() +
                        " by " + repo.getContributors().get(i).getUser());
            }
        }
    }

    private CloseableHttpResponse requestRepositoryCommits(Repository repository)
            throws IOException {
        URIBuilder URI = new URIBuilder()
                .setScheme("https")
                .setHost("api.github.com")
                .setPath("/repos/" + repository.getOwner() + "/" + repository.getName() + "/contributors")
                .addParameter("per_page", "100");
        HttpGet httpGet = new HttpGet(URI.toString());
        httpGet.addHeader("Authorization", "token " + TOKEN);
        return httpclient.execute(httpGet);
    }

    private ArrayList<Contributor> getRepositoryCommits(CloseableHttpResponse response)
            throws IOException {
        ArrayList<Contributor> contributors;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent()));
            String data = br.readLine();
            contributors = parser.parseRepositoryContributionJson(data);
        } finally {
            response.close();
        }
        return contributors;
    }

    private ArrayList<Repository> getMostCommitted(CloseableHttpResponse response) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent()));
            String repositoryData = br.readLine();
            ArrayList<Repository> allRepos = parser.parseRepositoryJson(repositoryData);
            for (int i = 0; i < allRepos.size(); i++) {
                getRepositoryCommits(requestRepositoryCommits(allRepos.get(i)));
                allRepos.get(i).
                        setContributors(getRepositoryCommits(requestRepositoryCommits(allRepos.get(i))));
            }
            for (int i = 0; i < allRepos.size(); i++) {
                for (Contributor contributor : allRepos.get(i).getContributors()) {
                    allRepos.get(i).addContribution(contributor.getContributionCount());
                }
            }
            allRepos.sort(Comparator.comparing(Repository::getContributionCount).reversed());
            for (int i = 0; i < 10; i++)
                mostCommittedRepositories.add(allRepos.get(i));
        } finally {
            response.close();
        }
        return mostStarredRepositories;
    }

    private CloseableHttpResponse requestMostCommitted(String dateFrom, String dateTo) throws IOException {
        URIBuilder URI = new URIBuilder()
                .setScheme("https")
                .setHost("api.github.com")
                .setPath("/search/repositories")
                .setParameter("q", "created:" + dateFrom + ".." + dateTo);
        HttpGet httpGet = new HttpGet(URI.toString());
        httpGet.addHeader("Authorization", "token " + TOKEN);
        httpGet.addHeader("Accept", "application/vnd.github.cloak-preview+json");
        return httpclient.execute(httpGet);
    }

    private void printMostCommitted() {
        System.out.println("\nMost Committed Repositories");
        for (Repository repo : mostCommittedRepositories) {
            System.out.println("\n#" + (mostCommittedRepositories.indexOf(repo) + 1));
            System.out.println("Name: " + repo.getName());
            System.out.println("Owner: " + repo.getOwner());
            System.out.println("Description: " + repo.getDescription());
            System.out.println("Language: " + repo.getLanguage());
            System.out.println("Number of contributions: " + repo.getContributionCount());
            System.out.println("Users with most commits:");
            if (repo.getContributors().size() < 5) {
                for (int i = 0; i < repo.getContributors().size(); i++) {
                    System.out.println("\t---> " + repo.getContributors().get(i).getContributionCount() +
                            " by " + repo.getContributors().get(i).getUser());
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    System.out.println("\t---> " + repo.getContributors().get(i).getContributionCount() +
                            " by " + repo.getContributors().get(i).getUser());
                }
            }
        }
    }

    public boolean getMostStarredRepositoriesData() throws IOException {
        getMostStarred(requestMostStarred());
        printMostStarred();
        return true;
    }

    public boolean getMostCommittedRepositoriesData(String dateFrom, String dateTo) throws IOException {
        getMostCommitted(requestMostCommitted(dateFrom, dateTo));
        printMostCommitted();
        return true;
    }
}
