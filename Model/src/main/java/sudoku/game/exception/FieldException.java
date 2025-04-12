/*
 * Copyright 2024 Mateusz Ciołkowski Szymon Kaźmierczak
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
 * You may not use the material for commercial purposes. You may copy, modify, distribute, and perform the work,
 * as long as you give appropriate credit, provide a link to the license, and indicate if changes were made.
 * Full license text: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 */

package sudoku.game.exception;

public class FieldException extends SudokuException {
    public static final String INVALID_VALUE = "error.sudokuInvalidValue";
    public static final String DONT_HAVE_9_FIELDS = "error.groupDontHave9Fields";

    public FieldException(String message) {
        super(message);
    }
}

