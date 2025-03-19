package sudoku.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sudoku.game.exception.SudokuNullPointerException;

import static org.junit.jupiter.api.Assertions.*;

class SudokuFieldTest {
    SudokuField field;

    @BeforeEach
    void setUp() {
        field = new SudokuField();
    }

    @Test
    void setValue() {
        field.setFieldValue(1);
        assertEquals(1, field.getFieldValue());
    }

    @Test
    void getValue() {
        field.setFieldValue(1);
        assertEquals(1, field.getFieldValue());
    }

    @Test
    void testEquals() {
        SudokuField field1 = new SudokuField();
        field1.setFieldValue(2);
        field.setFieldValue(2);
        assertEquals(field1, field);

        field.setFieldValue(1);
        assertNotEquals(field1, field);

        String other = "Not a SudokuField";
        assertNotEquals(field, other);
        assertEquals(field, field);
        assertNotEquals(field, null);
    }

    @Test
    void testHashCode() {
        field.setFieldValue(1);
        field.hashCode();
    }

    @Test
    void testToString() {
        field.setFieldValue(1);
        field.toString();
    }

    @Test
    void compareToTest(){
        field.setFieldValue(1);
        SudokuField field2 = new SudokuField();
        field2.setFieldValue(2);
        assertEquals(field.compareTo(field2), -1);
        assertEquals(field2.compareTo(field), 1);
        field2.setFieldValue(1);
        assertEquals(field.compareTo(field2), 0);
        assertEquals(field.compareTo(field2), 0);
    }

    @Test
    void compareToNullTest() throws NullPointerException{
        assertThrows(SudokuNullPointerException.class, () -> field.compareTo(null));
    }

}