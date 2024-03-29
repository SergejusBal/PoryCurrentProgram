module ser.bal.pyro {
    requires javafx.controls;
    requires javafx.fxml;
    requires purejavacomm;
	requires javafx.graphics;
    
    opens ser.bal.pyro to javafx.fxml;
    exports ser.bal.pyro;
}
