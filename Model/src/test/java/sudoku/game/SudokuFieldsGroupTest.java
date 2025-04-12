package sudoku.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SudokuFieldsGroupTest {
    private SudokuBoard sudokuBoard;
    private SudokuSolver solver;
    private static final int SIZE = 9;

    @BeforeEach
    void setUp() {
        solver = new BacktrackingSudokuSolver();
        sudokuBoard = new SudokuBoard(solver);
    }

    @Test
    void columnTest() {
        List<SudokuField> fields = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1);
            fields.add(field);
        }
        SudokuFieldsGroup column = new SudokuColumn(fields);
        assertTrue(column.verify());
    }

    @Test
    void invalidColumnTest() {
        List<SudokuField> fields = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE - 1; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1);
            fields.add(field);
        }
        fields.add(new SudokuField());
        fields.get(SIZE - 1).setFieldValue(2);
        SudokuFieldsGroup column = new SudokuColumn(fields);
        assertFalse(column.verify());
    }

    @Test
    void rowTest() {
        List<SudokuField> fields = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1);
            fields.add(field);
        }
        SudokuFieldsGroup row = new SudokuRow(fields);
        assertTrue(row.verify());
    }

    @Test
    void invalidRowTest() {
        List<SudokuField> fields = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE - 1; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1);
            fields.add(field);
        }
        fields.add(new SudokuField());
        fields.get(SIZE - 1).setFieldValue(2); 
        SudokuFieldsGroup row = new SudokuRow(fields);
        assertFalse(row.verify());
    }

    @Test
    void boxTest() {
        List<SudokuField> fields = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1);
            fields.add(field);
        }
        SudokuFieldsGroup box = new SudokuBox(fields);
        assertTrue(box.verify());
    }

    @Test
    void invalidBoxTest() {
        List<SudokuField> fields = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE - 1; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1);
            fields.add(field);
        }
        fields.add(new SudokuField());
        fields.get(SIZE - 1).setFieldValue(2); 
        SudokuFieldsGroup box = new SudokuBox(fields);
        assertFalse(box.verify());
    }


    @Test
    void testEquals() {
        sudokuBoard.solveGame();
        SudokuFieldsGroup fields = sudokuBoard.getColumn(0);
        SudokuFieldsGroup fields2 = sudokuBoard.getColumn(1);
        assertFalse(fields.equals(fields2));
        assertTrue(fields.equals(fields));
        assertFalse(fields.equals(null));
        assertFalse(fields.equals(solver));
    }

    @Test
    void testHashCode() {
        sudokuBoard.solveGame();
        sudokuBoard.getColumn(0).hashCode();
    }
    @Test
    void testToString() {
        sudokuBoard.solveGame();
        sudokuBoard.getColumn(0).toString();
    }

    @Test
    void cloneTest(){
        List<SudokuField> fields = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1);
            fields.add(field);
        }
        SudokuFieldsGroup fieldGroup = new SudokuColumn(fields);
        SudokuFieldsGroup clonedFieldGroup = fieldGroup.clone();
        assertTrue(fieldGroup.equals(clonedFieldGroup));
        assertNotSame(fieldGroup, clonedFieldGroup);
    }

    @Test
    public void testValidBoard() {
        List<SudokuField> fields = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE - 1; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1);
            fields.add(field);
        }
        fields.add(new SudokuField());
        fields.get(SIZE - 1).setFieldValue(2);
        SudokuFieldsGroup box = new SudokuBox(fields);
        assertFalse(box.isValidBoard());
    }

    @Test
    public void testInvalidBoard() {
        List<SudokuField> fields = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            SudokuField field = new SudokuField();
            field.setToDefault();
            fields.add(field);
        }
        SudokuFieldsGroup box = new SudokuBox(fields);
        assertTrue(box.isValidBoard());
    }
}
