/*
 * Copyright 2024 Mateusz Ciołkowski Szymon Kaźmierczak
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
 * You may not use the material for commercial purposes. You may copy, modify, distribute, and perform the work,
 * as long as you give appropriate credit, provide a link to the license, and indicate if changes were made.
 * Full license text: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 */


package view;

import java.util.ListResourceBundle;

public class AuthorsResourceBundle extends ListResourceBundle {

    private final Object[][] resources = {
            {"author1", "Mateusz Ciołkowski"},
            {"author2", "Szymon Kaźmierczak"},
    };

    @Override
    protected Object[][] getContents() {
        return resources;
    }
}

