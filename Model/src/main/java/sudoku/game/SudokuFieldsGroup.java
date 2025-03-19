/*
 * Copyright 2024 Mateusz Ciołkowski Szymon Kaźmierczak
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
 * You may not use the material for commercial purposes. You may copy, modify, distribute, and perform the work,
 * as long as you give appropriate credit, provide a link to the license, and indicate if changes were made.
 * Full license text: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 */

package sudoku.game;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sudoku.game.exception.FieldException;
import sudoku.game.exception.SudokuException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SudokuFieldsGroup implements Serializable, Cloneable {
    public static final int SIZE = 9;
    private List<SudokuField> fieldsToCheck;
    private static final Logger logger = LoggerFactory.getLogger(SudokuFieldsGroup.class);
    private static final ResourceBundle messages = ResourceBundle.getBundle(
            "bundle.exception", java.util.Locale.getDefault());


    public SudokuFieldsGroup(List<SudokuField> fields) {
        if (fields.size() != SIZE) {
            String errorMassage = messages.getString("error.groupDontHave9Fields");
            logger.error(errorMassage);
            throw new FieldException(FieldException.DONT_HAVE_9_FIELDS);
        }
        fieldsToCheck = fields;
    }

    public boolean verify() {
        for (int i = 0; i < SIZE - 1; i++) {
            for (int j = i + 1; j < SIZE; j++) {
                if (fieldsToCheck.get(i).getFieldValue() == fieldsToCheck.get(j).getFieldValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder fieldsString = new StringBuilder();

        for (SudokuField field : fieldsToCheck) {
            fieldsString.append(field.getFieldValue()).append(" ");
        }
        fieldsString.append(System.lineSeparator());
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("fields", fieldsString.toString().trim())
                .toString();
    }

    public boolean isValidBoard() {
        for (int i = 0; i < SIZE - 1; i++) {
            for (int j = i + 1; j < SIZE; j++) {
                int value1 = fieldsToCheck.get(i).getFieldValue();
                int value2 = fieldsToCheck.get(j).getFieldValue();

                if (value1 != 0 && value2 != 0 && value1 == value2) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof SudokuFieldsGroup) {
            SudokuFieldsGroup that = (SudokuFieldsGroup) obj;
            return new EqualsBuilder()
                    .append(fieldsToCheck, that.fieldsToCheck)
                    .isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fieldsToCheck)
                .toHashCode();
    }

    @Override
    public SudokuFieldsGroup clone() {
        try {
            SudokuFieldsGroup clone = (SudokuFieldsGroup) super.clone();
            clone.fieldsToCheck = new ArrayList<>(this.fieldsToCheck);
            return clone;
        } catch (CloneNotSupportedException e) {
            String errorMessage = messages.getString("error.cloningProblem ");
            logger.error(errorMessage,e.getMessage());
            throw new SudokuException(SudokuException.CLONE_PROBLEM, e);
        }
    }
}
