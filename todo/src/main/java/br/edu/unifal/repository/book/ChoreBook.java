package br.edu.unifal.repository.book;

public class ChoreBook {
    public static final String FIND_ALL_CHORES = "SELECT * FROM db.chores;";

    public static final String INSERT_CHORE = "INSERT INTO db.chores (`description`, `isCompleted`, `deadline`) VALUES (?,?,?);";

    public static final String UPDATE_CHORE = "UPDATE db.chores SET `description` = ?, `deadline` = ? WHERE db.chores.id = ?;";
}
