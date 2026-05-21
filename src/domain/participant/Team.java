package domain.participant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team extends Participant {
    private String coach;
    private List<Player> players;

    public Team(String id, String name, String coach) {
        super(id, name);
        this.coach = coach;
        this.players = new ArrayList<>();
    }

    public String getCoach() { return coach; }
    public void setCoach(String coach) { this.coach = coach; }

    public List<Player> getPlayers() { return Collections.unmodifiableList(players); }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    @Override
    public String toString() {
        return name + " (coach: " + coach + ")";
    }
}
