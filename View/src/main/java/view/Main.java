/*
 * Copyright 2024 Mateusz Ciołkowski Szymon Kaźmierczak
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
 * You may not use the material for commercial purposes. You may copy, modify, distribute, and perform the work,
 * as long as you give appropriate credit, provide a link to the license, and indicate if changes were made.
 * Full license text: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 */

package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;


public class Main extends Application {
    private static Locale locale = Locale.getDefault();
    private static ResourceBundle bundle;
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void setLocale(Locale newLocale) {
        locale = newLocale;
        bundle = ResourceBundle.getBundle("Bundle.messages", locale);
    }

    public static Locale getLocale() {
        return locale;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            bundle = ResourceBundle.getBundle("Bundle.messages", locale);
            logger.debug("Włączanie aplikacji");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StartForm.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
        } catch (RuntimeException e) {
            logger.error("Loading error {}", e.getMessage());
        }

        primaryStage.setTitle(bundle.getString("app.title"));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
