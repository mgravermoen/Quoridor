module finalProject {
	requires java.desktop;
	requires javafx.base;
	requires javafx.graphics;
	requires javafx.controls;
	
	opens finalProject to javafx.graphics;
}