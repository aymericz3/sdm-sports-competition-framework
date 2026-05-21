package domain.participant;

public class Player {
    private String id;
    private String name;
    private String position;

    public Player(String id, String name, String position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    @Override
    public String toString() {
        return name + " (" + position + ")";
    }
}
