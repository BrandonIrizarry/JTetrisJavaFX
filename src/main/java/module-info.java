module xyz.brandonirizarry.JTetrisJavaFX {
    requires xyz.brandonirizarry.JTetrisBackend;
    requires javafx.graphics;

    opens xyz.brandonirizarry.jtetrisjavafx to javafx.fxml;
    exports xyz.brandonirizarry.jtetrisjavafx;
}
