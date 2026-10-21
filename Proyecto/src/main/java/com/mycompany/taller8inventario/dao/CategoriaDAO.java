
package com.mycompany.taller8inventario.dao;

import com.mycompany.taller8inventario.conexion.Conexion;
import com.mycompany.taller8inventario.model.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();

        String sql = "SELECT id_categoria, nombre "
                   + "FROM categoria ORDER BY nombre";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categoria c = new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre"));
                lista.add(c);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al consultar las categorías.", e);
        }

        return lista;
    }
}

