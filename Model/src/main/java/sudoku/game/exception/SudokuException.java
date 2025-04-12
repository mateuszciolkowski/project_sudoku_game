/*
 * Copyright 2024 Mateusz Ciołkowski Szymon Kaźmierczak
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
 * You may not use the material for commercial purposes. You may copy, modify, distribute, and perform the work,
 * as long as you give appropriate credit, provide a link to the license, and indicate if changes were made.
 * Full license text: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 */

package sudoku.game.exception;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SudokuException extends RuntimeException {
    private static final ResourceBundle bundle;

    public static final String CLONE_PROBLEM = "error.cloningProblem";


    static {
        Locale locale = Locale.getDefault(Locale.Category.DISPLAY);
        bundle = ResourceBundle.getBundle("bundle.exception", locale);
    }

    public SudokuException(String message, Throwable e) {
        super(message, e);
    }

    public SudokuException(String message) {
        super(message);
    }

    @Override
    public String getLocalizedMessage() {
        String message;
        try {
            message = bundle.getString(getMessage());
        } catch (MissingResourceException e) {
            throw new RuntimeException(e);
        }
        return message;
    }
}
