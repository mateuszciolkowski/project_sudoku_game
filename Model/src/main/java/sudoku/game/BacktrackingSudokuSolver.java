/*
 * Copyright 2024 Mateusz Ciołkowski Szymon Kaźmierczak
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
 * You may not use the material for commercial purposes. You may copy, modify, distribute, and perform the work,
 * as long as you give appropriate credit, provide a link to the license, and indicate if changes were made.
 * Full license text: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 */

package sudoku.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BacktrackingSudokuSolver implements SudokuSolver, Serializable {
    private static final long serialVersionUID = 1L;
    private static final int BOARD_SIZE = 9;

    @Override
    public boolean solve(SudokuBoard board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board.get(i, j) == 0) {  // Znajdź puste pole
                    List<Integer> numbers = shuffledNumbers();
                    // Sprawdzamy każdą liczbę z listy
                    for (int number : numbers) {
                        if (isSafe(i, j, number, board)) {  // Sprawdzamy, czy liczba jest bezpieczna
                            board.set(i, j, number);  // Ustawiamy liczbę
                            if (this.solve(board)) {  // Rekurencyjnie rozwiązujemy
                                return true;
                            }
                            board.getSudokuField(i,j).setUnsolvedValue();  // Jeśli nie uda się rozwiązać, cofamy zmianę
                        }
                    }
                    return false;  // Jeśli żadna liczba nie pasuje, zwróć false
                }
            }
        }
        return true;  // Jeśli cała plansza jest rozwiązana, zwróć true
    }

    // Sprawdzanie, czy liczba jest bezpieczna
    public boolean isSafe(int row, int col, int num, SudokuBoard board) {
        // Sprawdzamy, czy liczba jest już w tym wierszu
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board.get(row, i) == num) {
                return false;  // Liczba już jest w tym wierszu
            }
        }

        // Sprawdzamy, czy liczba jest już w tej kolumnie
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board.get(i, col) == num) {
                return false;  // Liczba już jest w tej kolumnie
            }
        }

        // Sprawdzamy, czy liczba jest w tej sub-planszy 3x3
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.get(startRow + i, startCol + j) == num) {
                    return false;  // Liczba już jest w tej sub-planszy
                }
            }
        }

        return true;  // Liczba jest bezpieczna
    }

    public List<Integer> shuffledNumbers() {
        List<Integer> numbers = new ArrayList<>();
        // Tworzymy listę liczb 1-9
        for (int num = 1; num <= BOARD_SIZE; num++) {
            numbers.add(num);
        }
        // Mieszamy liczby, aby wprowadzić losowość
        Collections.shuffle(numbers, new Random());
        return numbers;
    }
}
