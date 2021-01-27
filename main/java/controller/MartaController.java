package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import main.java.MartaApp;

import java.io.IOException;
import java.nio.file.Paths;

public abstract class MartaController extends VBox {
    private FXMLLoader fxmlLoader;
    private MartaApp app;
    private MartaController previous;

    public MartaController(MartaController previous) {
        this.app = MartaApp.app;
        this.previous = previous;
        this.fxmlLoader = new FXMLLoader();
        fxmlLoader.setRoot(this);
        fxmlLoader.setControllerFactory(param -> this);

        try {
            fxmlLoader.setLocation(Paths.get(getFXMLPath()).toUri().toURL());
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public MartaApp getApp() {
        return app;
    }

    public MartaController getPrevious() {
        return previous;
    }
    public void setPrevious(MartaController previous) {
        this.previous = previous;
    }

    public abstract String getFXMLPath();
    public abstract String getTitle();
    @FXML public void back() {
        if(previous != null) {
            app.loadController(previous);
            previous.update();
        }
    }

    public void update() {}
}
