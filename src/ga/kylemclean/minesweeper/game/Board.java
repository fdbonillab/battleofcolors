package ga.kylemclean.minesweeper.game;

import java.util.ArrayList;

public class Board {
    String nombre;
    ArrayList matrixList;
    int [][] matrixArray;
    int [] arrayTest;
    public Board(){

    }
    public Board(int[][] matrix2, int [] arrayTest, String nombre) {
        this.matrixArray = matrix2;
        this.arrayTest = arrayTest;
        this.nombre = nombre;
    }
    public Board(int[][] matrix2) {
        this.matrixArray = matrix2;
    }

    public Board(ArrayList matrix) {
        this.matrixList = matrix;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList getMatrix() {
        return matrixList;
    }

    public void setMatrix(ArrayList matrix) {
        this.matrixList = matrix;
    }

    public int[][] getMatrix2() {
        return matrixArray;
    }

    public void setMatrix2(int[][] matrix2) {
        this.matrixArray = matrix2;
    }

    public int[] getArrayTest() {
        return arrayTest;
    }

    public void setArrayTest(int[] arrayTest) {
        this.arrayTest = arrayTest;
    }
}
