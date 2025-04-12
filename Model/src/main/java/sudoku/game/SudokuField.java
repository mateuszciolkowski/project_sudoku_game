/*
 * Copyright 2024 Mateusz Ciołkowski Szymon Kaźmierczak
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
 * You may not use the material for commercial purposes. You may copy, modify, distribute, and perform the work,
 * as long as you give appropriate credit, provide a link to the license, and indicate if changes were made.
 * Full license text: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 */

package sudoku.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sudoku.game.exception.FieldException;
import sudoku.game.exception.SudokuException;
import sudoku.game.exception.SudokuNullPointerException;

import java.io.Serializable;
import java.util.Objects;
import java.util.ResourceBundle;

public class SudokuField implements Serializable,Cloneable, Comparable<SudokuField> {
    private static final long serialVersionUID = 1L;
    private int value;
    private static final Logger logger = LoggerFactory.getLogger(SudokuField.class);
    private static final ResourceBundle messages = ResourceBundle.getBundle(
            "bundle.exception", java.util.Locale.getDefault());

    public SudokuField() {

    }

    public int getFieldValue() {
        return value;
    }

    public void setUnsolvedValue() {
        this.value = 0;
    }

    public void setFieldValue(int value) {
        if (value < 1 || value > 9) {
            String errorMessage = messages.getString("error.sudokuInvalidValue");
            logger.error(errorMessage);
            throw new FieldException(FieldException.INVALID_VALUE);
        } else {
            this.value = value;
        }
    }

    public void setToDefault() {
        this.value = 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SudokuField that)) {
            return false;
        }
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "SudokuField{" + "value=" + value + '}';
    }

    @Override
    public int compareTo(SudokuField o) {
        if (o == null) {
            String errorMessage = messages.getString("error.nullPointer");
            logger.error(errorMessage);
            throw new SudokuNullPointerException(SudokuNullPointerException.NULL_POINTER);
        }
        if (this.value > o.value) {
            return 1;
        }
        if (this.value < o.value) {
            return -1;
        }
        return 0;
    }

    @Override
    public SudokuField clone() {
        try {
            SudokuField clone = (SudokuField) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            String errorMessage = messages.getString("error.cloningProblem");
            logger.error(errorMessage,e.getMessage());
            throw new SudokuException(SudokuException.CLONE_PROBLEM, e);
        }
    }
}
