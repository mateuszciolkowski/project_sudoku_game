/*
 * Copyright 2024 Mateusz Ciołkowski Szymon Kaźmierczak
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
 * You may not use the material for commercial purposes. You may copy, modify, distribute, and perform the work,
 * as long as you give appropriate credit, provide a link to the license, and indicate if changes were made.
 * Full license text: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 */

package sudoku.game;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum LevelsSudoku {
    EASY(5),
    MEDIUM(40),
    HARD(55);

    private final int numbersToRemove;

    LevelsSudoku(int numbersToRemove) {
        this.numbersToRemove = numbersToRemove;
    }

    public int getNumbersToRemove() {
        return numbersToRemove;
    }


    public void applyDifficulty(SudokuBoard board) {
        int removals = getNumbersToRemove();
        int size = SudokuBoard.BOARD_SIZE;


        List<int[]> emptyCells = new ArrayList<>();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                emptyCells.add(new int[]{x, y});
            }
        }

        Random rand = new Random();
        for (int i = 0; i < removals; i++) {
            int randomIndex = rand.nextInt(emptyCells.size());
            int[] cell = emptyCells.remove(randomIndex);
            board.setToDefault(cell[0], cell[1]);
        }
    }
}
