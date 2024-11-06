module xyz.brandonirizarry.JTetrisJavaFX {
    requires xyz.brandonirizarry.JTetrisBackend;
    requires javafx.controls;

    opens xyz.brandonirizarry.jtetrisjavafx.app to javafx.fxml;
    opens xyz.brandonirizarry.jtetrisjavafx.app.animation to javafx.fxml;
    exports xyz.brandonirizarry.jtetrisjavafx.app;
    exports xyz.brandonirizarry.jtetrisjavafx.app.animation;
}
