package vulgui.utils;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;

public class Console extends OutputStream {
    private TextArea console;
    byte[] tempbytes = new byte[1024];

    public Console(TextArea console) {
        this.console = console;
    }

    public void appendText(final String valueOf) {
        //在JavaFx中，如果在非Fx线程要执行Fx线程相关的任务，必须在Platform.runLater中执行
        Platform.runLater(new Runnable() {
            @Override public void run() {
                console.appendText(valueOf);
            }
        });
    }

    @Override
    public void write(int b) {
        appendText(String.valueOf((char) b));
    }
}