/*
 * Copyright 2024 Mateusz Ciołkowski Szymon Kaźmierczak
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
 * You may not use the material for commercial purposes. You may copy, modify, distribute, and perform the work,
 * as long as you give appropriate credit, provide a link to the license, and indicate if changes were made.
 * Full license text: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 */

package sudoku.game;

import sudoku.game.exception.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcSudokuBoardDao implements Dao<SudokuBoard> {
    private final String url = "jdbc:postgresql://localhost:5432/sudoku";
    private final String user = "postgres";
    private final String password = "admin";
    Connection connection;

    public JdbcSudokuBoardDao() throws SQLException {
        createDatabaseIfNotExists();
        connect();
    }

    private void createDatabaseIfNotExists() throws SQLException {
        String baseUrl = "jdbc:postgresql://localhost:5432/";
        try (Connection connection = DriverManager.getConnection(baseUrl, user, password);
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE sudoku");
        } catch (SQLException e) {
            if (!e.getMessage().contains("already exists")) {
                throw e;
            }
        }
    }

    private void connect() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        createTablesIfNotExist();
    }

    private void createTablesIfNotExist() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS SudokuBoards (\n"
                    + "id SERIAL PRIMARY KEY,\n"
                    + "name VARCHAR(255) UNIQUE NOT NULL\n"
                    + ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS SudokuFields (\n"
                    + "board_id INT NOT NULL,\n"
                    + "row INT NOT NULL,\n"
                    + "col INT NOT NULL,\n"
                    + "value INT NOT NULL,\n"
                    + "FOREIGN KEY (board_id) REFERENCES SudokuBoards(id)\n"
                    + ")");
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new SQLException("Error rolling back table creation transaction", rollbackEx);
            }
            throw e;
        }
    }

    @Override
    public void write(SudokuBoard board, String name) throws DaoException {
        try (PreparedStatement insertBoard = connection.prepareStatement(
                "INSERT INTO SudokuBoards (name) VALUES (?) RETURNING id");
             PreparedStatement insertField = connection.prepareStatement(
                     "INSERT INTO SudokuFields (board_id, row, col, value) VALUES (?, ?, ?, ?)")) {
            insertBoard.setString(1, name);
            ResultSet rs = insertBoard.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Failed to retrieve board ID after insert.");
            }
            int boardId = rs.getInt(1);

            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    insertField.setInt(1, boardId);
                    insertField.setInt(2, row);
                    insertField.setInt(3, col);
                    insertField.setInt(4, board.get(row, col));
                    insertField.addBatch();
                }
            }
            insertField.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new DaoException("Error rolling back transaction");
            }
            throw new DaoException("Error saving Sudoku board to the database");
        }
    }

    @Override
    public SudokuBoard read(String name) throws DaoException {
        try (PreparedStatement selectBoard = connection.prepareStatement(
                "SELECT id FROM SudokuBoards WHERE name = ?");
             PreparedStatement selectFields = connection.prepareStatement(
                     "SELECT row, col, value FROM SudokuFields WHERE board_id = ?")) {
            selectBoard.setString(1, name);
            ResultSet boardResult = selectBoard.executeQuery();

            if (!boardResult.next()) {
                throw new DaoException("Sudoku board not found: " + name);
            }
            int boardId = boardResult.getInt(1);

            SudokuBoard board = new SudokuBoard(new BacktrackingSudokuSolver());
            selectFields.setInt(1, boardId);
            ResultSet fieldResult = selectFields.executeQuery();
            while (fieldResult.next()) {
                int row = fieldResult.getInt("row");
                int col = fieldResult.getInt("col");
                int value = fieldResult.getInt("value");
                if (value == 0) {
                    board.setToDefault(row, col);
                } else {
                    board.set(row, col, value);
                }
            }
            connection.commit();
            return board;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new DaoException("Error rolling back transaction");
            }
            throw new DaoException("Error loading Sudoku board from the database");
        }
    }

    public List<String> names() throws DaoException {
        try {
            if (connection == null || connection.isClosed()) {
                throw new DaoException("Connection is closed");
            }
        } catch (SQLException e) {
            throw new DaoException("Error retrieving sudoku boards from the database");
        }

        List<String> boardNames = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT name FROM SudokuBoards");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                boardNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new DaoException("Error reading Sudoku boards from the database");
        }

        return boardNames;
    }


    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }


}
