package RepListis;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Contributor {
    private String user;
    private int contributionCount = 0;

    public int compareTo(Contributor commit)
    {
        return Integer.compare(commit.contributionCount,this.contributionCount);
    }
}
