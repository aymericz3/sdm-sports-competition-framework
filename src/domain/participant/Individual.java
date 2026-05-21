package domain.participant;

public class Individual extends Participant {
    private int ranking;
    private String title;

    public Individual(String id, String name, int ranking, String title) {
        super(id, name);
        this.ranking = ranking;
        this.title = title;
    }

    public int getRanking() { return ranking; }
    public void setRanking(int ranking) { this.ranking = ranking; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @Override
    public String toString() {
        return name + " (ranked #" + ranking + (title != null && !title.isEmpty() ? ", " + title : "") + ")";
    }
}
