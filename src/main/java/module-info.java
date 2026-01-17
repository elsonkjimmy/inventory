module com.inventory {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires MaterialFX;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.materialdesign2;

    requires java.sql;
    requires jbcrypt;

    requires org.slf4j;

    opens com.inventory to javafx.fxml;
    opens com.inventory.controllers to javafx.fxml;
    opens com.inventory.models to javafx.base;

    exports com.inventory;
    exports com.inventory.controllers;
    exports com.inventory.models;
    exports com.inventory.dao;
    exports com.inventory.utils;
}
