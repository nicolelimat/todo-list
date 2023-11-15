package br.edu.unifal.repository.impl;

import br.edu.unifal.domain.Chore;
import br.edu.unifal.repository.ChoreRepository;
import br.edu.unifal.repository.book.ChoreBook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MySQLChoreRepository implements ChoreRepository {

    // Estabelecer uma conexão com o banco de dados
    private Connection connection;

    // Realizar operações no banco de dados
    // Executam em tempo de COMPILAÇÃO (consulta estática)
    // Quando a consulta é executada uma única vez
    private Statement statement;

    // Realizar operações no banco de dados
    // Executar queries dinâmicas em tempo de EXECUÇÃO
    // Ideal para quando vamos executar a query múltiplas vezes
    // Melhor performance quando comparado ao statement
    private PreparedStatement preparedStatement;

    // Utilizado para capturar o retorno de uma consulta
    // e.g.: Utilizei o statement para fazer uma consulta de todas as chores
    //       Eu uso o result set para capturar o resultado dessa consulta
    private ResultSet resultSet;

    @Override
    public List<Chore> load() {
        if(!connectToMySQL()){
            return new ArrayList<>();
        }
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(ChoreBook.FIND_ALL_CHORES); // pegando o resultado dessa query para resultSet

            List<Chore> chores = new ArrayList<>();
            while(resultSet.next()) {
                // Poderíamos ter criado a Chore usando o construtor completo
                // OU poderíamos ter usado o construtor padrão + ter dado sets
                Chore chore = Chore.builder()
                        .id(resultSet.getLong("id"))
                        .description(resultSet.getString("description"))
                        .isCompleted(resultSet.getBoolean("isCompleted"))
                        .deadline(resultSet.getDate("deadline").toLocalDate())
                        .build();
                chores.add(chore);
            }
            return chores;
        } catch (SQLException exception) {
            System.err.println("Error when loading chores from database: " + exception.getMessage());
            exception.printStackTrace();
        }
        finally {
            closeConnections();
        }
        return null;
    }

    @Override
    public boolean saveAll(List<Chore> chores) {
        return false;
    }

    @Override
    public boolean save(Chore chore) {
        if(!connectToMySQL()){
            return Boolean.FALSE;
        }
        try {
            // Define qual a query que quer executar
            // Query de INSERT
            // Quando não sabe quais serão as informações
            preparedStatement = connection.prepareStatement(
                    ChoreBook.INSERT_CHORE);

            // Falando que os valores ? ? ? serão nomeados, setados
            preparedStatement.setString(1, chore.getDescription());
            preparedStatement.setBoolean(2, chore.getIsCompleted());
            preparedStatement.setDate(3, Date.valueOf(chore.getDeadline()));

            // Executando a query por atualização na tabela
            // Guardando quantas linhas foram afetadas pela atualização
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows > 0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } catch (SQLException exception) {
            System.out.println("Error when inserting a new chore on database");
        } finally {
            closeConnections();
        }
        return false;
    }

    @Override
    public boolean update(Chore chore){
        if(!connectToMySQL()){
            return Boolean.FALSE;
        }
        try {
            preparedStatement = connection.prepareStatement(
                    ChoreBook.UPDATE_CHORE);
            preparedStatement.setString(1, chore.getDescription());
            preparedStatement.setDate(2, Date.valueOf(chore.getDeadline()));
            preparedStatement.setLong(3, chore.getId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0){
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }catch (SQLException exception){
            System.out.println(("Error when updating the chore on database"));
        }finally {
            closeConnections();
        }
        return false;
    }

    private boolean connectToMySQL() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/db?"
                            + "user=root&password=secretpassword");
            return Boolean.TRUE;
        } catch (ClassNotFoundException | SQLException exception) {
            System.err.println("Error when connecting to the database: " + exception.getMessage());
            exception.printStackTrace();
        }
        return Boolean.FALSE;
    }

    private void closeConnections(){
        try {
            if (Objects.nonNull(connection) && !connection.isClosed()) {
                connection.close();
            }
            if (Objects.nonNull(statement) && !statement.isClosed()) {
                statement.close();
            }
            if (Objects.nonNull(preparedStatement) && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }
            if (Objects.nonNull(resultSet) && !resultSet.isClosed()) {
                resultSet.close();
            }
        } catch (SQLException exception) {
            System.out.println("Error when closing database connections");
        }

    }
}
