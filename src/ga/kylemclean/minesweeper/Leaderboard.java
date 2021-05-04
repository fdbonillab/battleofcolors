package ga.kylemclean.minesweeper;

import java.util.List;

public interface Leaderboard {
   public void submitScore(String user, int score);
   public void consultarTopPlayers();
   public List getListPlayers();
   public boolean checkConectionInternet();
}