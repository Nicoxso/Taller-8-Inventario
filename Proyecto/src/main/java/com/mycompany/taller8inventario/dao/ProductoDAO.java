
package com.mycompany.taller8inventario.dao;

import com.mycompany.taller8inventario.conexion.Conexion;
import com.mycompany.taller8inventario.model.Categoria;
import com.mycompany.taller8inventario.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public boolean insertar(Producto p) {
        String sql = "INSERT INTO producto "
                + "(nombre, precio, cantidad, id_categoria) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getCantidad());
            ps.setInt(4, p.getCategoria().getIdCategoria());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar.", e);
        }
    }

    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT p.id_producto, p.nombre, "
                + "p.precio, p.cantidad, "
                + "c.id_categoria, c.nombre AS nombre_categoria "
                + "FROM producto p "
                + "INNER JOIN categoria c "
                + "ON p.id_categoria = c.id_categoria "
                + "ORDER BY p.id_producto";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categoria c = new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre_categoria"));

                Producto p = new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad"),
                        c);

                lista.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar.", e);
        }

        return lista;
    }

    public Producto buscarPorId(int id) {
        String sql = "SELECT p.id_producto, p.nombre, "
                + "p.precio, p.cantidad, "
                + "c.id_categoria, c.nombre AS nombre_categoria "
                + "FROM producto p "
                + "INNER JOIN categoria c "
                + "ON p.id_categoria = c.id_categoria "
                + "WHERE p.id_producto = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Categoria c = new Categoria(
                            rs.getInt("id_categoria"),
                            rs.getString("nombre_categoria"));

                    return new Producto(
                            rs.getInt("id_producto"),
                            rs.getString("nombre"),
                            rs.getDouble("precio"),
                            rs.getInt("cantidad"),
                            c);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar.", e);
        }

        return null;
    }

    public boolean actualizar(Producto p) {
        String sql = "UPDATE producto SET nombre=?, precio=?, "
                + "cantidad=?, id_categoria=? WHERE id_producto=?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getCantidad());
            ps.setInt(4, p.getCategoria().getIdCategoria());
            ps.setInt(5, p.getIdProducto());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar.", e);
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM producto WHERE id_producto=?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar.", e);
        }
    }
}
