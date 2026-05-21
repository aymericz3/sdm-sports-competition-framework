package domain.participant;

public abstract class Participant {
    protected String id;
    protected String name;

    public Participant(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}
