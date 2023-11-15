package br.edu.unifal.repository.book;

public class ChoreBook {
    public static final String FIND_ALL_CHORES = "SELECT * FROM db.chores;";

    public static final String INSERT_CHORE = "INSERT INTO db.chores (`description`, `isCompleted`, `deadline`) VALUES (?,?,?);";

    //  fazer o update (Chore chore), o teste do service tem que funcionar
}
