package ga.kylemclean.minesweeper.to;

/**
 * Created by david1 on 12/26/2016.
 */

public class User {
    String idUserGoogle;
    String name;
    int pasos;
    int distance;
    int idTeam;



    String idCountry;

    public User(String idUserGoogle, String name) {
        this.idUserGoogle = idUserGoogle;
        this.name = name;
    }

    public User(String idUserGoogle, int distance ) {
        this.distance = distance;
        this.idUserGoogle = idUserGoogle;
    }
    public User(String idUserGoogle, int distance, int idTeam, boolean noaplica ) {
        this.distance = distance;
        this.idUserGoogle = idUserGoogle;
        this.idTeam = idTeam;
        //this.pasos = pasos;
    }
    public User(String idUserGoogle, int distance, int idTeam, String idCountry ) {
        this.distance = distance;
        this.idUserGoogle = idUserGoogle;
        this.idTeam = idTeam;
        this.idCountry = idCountry;
        //this.pasos = pasos;
    }
    public User(int pasos, String name, String idUserGoogle) {
        this.pasos = pasos;
        this.name = name;
        this.idUserGoogle = idUserGoogle;
    }
    public User(String idUserGoogle, int distance, int pasos) {
        this.pasos = pasos;
        this.distance = distance;
        this.idUserGoogle = idUserGoogle;
    }
    public User(String idUserGoogle, int distance, int pasos, String idCountry, boolean noAplica) {
        this.pasos = pasos;
        this.distance = distance;
        this.idUserGoogle = idUserGoogle;
        this.idCountry = idCountry;
    }

    public String getIdUserGoogle() {
        return idUserGoogle;
    }

    public void setIdUserGoogle(String idUserGoogle) {
        this.idUserGoogle = idUserGoogle;
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


    public int getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(int idTeam) {
        this.idTeam = idTeam;
    }

    public String getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(String idCountry) {
        this.idCountry = idCountry;
    }



}

