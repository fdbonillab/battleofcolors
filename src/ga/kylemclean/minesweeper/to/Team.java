package ga.kylemclean.minesweeper.to;

/**
 * Created by david1 on 12/23/2016.
 */

public class Team {


    int idTeam;
    String name;
    int pasos;
    int distance;
    boolean miTeam;


    public Team(int idTeam, String name, int distance) {

        this.idTeam = idTeam;
        this.name = name;
        this.distance = distance;
    }
    public Team(int idTeam, String name, int distance, boolean miTeam) {

        this.idTeam = idTeam;
        this.name = name;
        this.distance = distance;
        this.miTeam = miTeam;
    }

    public int getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(int idTeam) {
        this.idTeam = idTeam;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPasos() {
        return pasos;
    }

    public void setPasos(int pasos) {
        this.pasos = pasos;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
    public boolean isMiTeam() {
        return miTeam;
    }

    public void setMiTeam(boolean miTeam) {
        this.miTeam = miTeam;
    }

}
