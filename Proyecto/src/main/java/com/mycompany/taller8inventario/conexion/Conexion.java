package com.mycompany.taller8inventario.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL =
            "jdbc:mysql://localhost:3306/tienda";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    public static Connection getConexion() {
        try {
            return DriverManager.getConnection(
                    URL, USUARIO, CLAVE);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo conectar con MySQL. "
                    + "Revisa XAMPP, el puerto y la contraseña. "
                    + e.getMessage(), e);
        }
    }
}
