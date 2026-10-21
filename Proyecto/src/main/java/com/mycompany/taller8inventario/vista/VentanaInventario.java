
package com.mycompany.taller8inventario.vista;

import com.mycompany.taller8inventario.dao.CategoriaDAO;
import com.mycompany.taller8inventario.dao.ProductoDAO;
import com.mycompany.taller8inventario.model.Categoria;
import com.mycompany.taller8inventario.model.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaInventario extends JFrame {

    private final JTextField txtNombre = new JTextField(15);
    private final JTextField txtPrecio = new JTextField(15);
    private final JTextField txtCantidad = new JTextField(15);

    private final JComboBox<Categoria> cmbCategoria =
            new JComboBox<>();

    private final DefaultTableModel modelo =
            new DefaultTableModel(
                    new String[]{"ID", "Nombre", "Precio",
                        "Cantidad", "Categoría"}, 0) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };

    private final JTable tabla = new JTable(modelo);

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    private int idSeleccionado = -1;

    public VentanaInventario() {
        setTitle("Inventario - Tienda de Don Ernesto");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 480);
        setLocationRelativeTo(null);

        construirInterfaz();
        cargarCategorias();
        cargarProductos();
    }

    private void construirInterfaz() {
        JPanel formulario = new JPanel(
                new GridLayout(4, 2, 8, 8));

        formulario.setBorder(BorderFactory.createTitledBorder(
                "Datos del producto"));

        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);

        formulario.add(new JLabel("Precio:"));
        formulario.add(txtPrecio);

        formulario.add(new JLabel("Cantidad:"));
        formulario.add(txtCantidad);

        formulario.add(new JLabel("Categoría:"));
        formulario.add(cmbCategoria);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        JPanel botones = new JPanel(new FlowLayout());
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel superior = new JPanel(new BorderLayout(8, 8));
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && tabla.getSelectedRow() != -1) {
                cargarFormulario();
            }
        });
    }

    private void cargarCategorias() {
        try {
            cmbCategoria.removeAllItems();

            for (Categoria c : categoriaDAO.listar()) {
                cmbCategoria.addItem(c);
            }
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void cargarProductos() {
        try {
            modelo.setRowCount(0);

            for (Producto p : productoDAO.listar()) {
                modelo.addRow(new Object[]{
                    p.getIdProducto(),
                    p.getNombre(),
                    p.getPrecio(),
                    p.getCantidad(),
                    p.getCategoria().getNombre()
                });
            }
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private Producto leerFormulario() {
        String nombre = txtNombre.getText().trim();
        String textoPrecio = txtPrecio.getText().trim();
        String textoCantidad = txtCantidad.getText().trim();

        if (nombre.isEmpty() || textoPrecio.isEmpty()
                || textoCantidad.isEmpty()) {
            throw new IllegalArgumentException(
                    "Completa todos los campos.");
        }

        double precio;
        int cantidad;

        try {
            precio = Double.parseDouble(
                    textoPrecio.replace(',', '.'));
            cantidad = Integer.parseInt(textoCantidad);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Precio numérico y cantidad entera son obligatorios.");
        }

        if (cmbCategoria.getSelectedItem() == null) {
            throw new IllegalArgumentException(
                    "Selecciona una categoría.");
        }

        Categoria categoria =
                (Categoria) cmbCategoria.getSelectedItem();

        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setCantidad(cantidad);
        p.setCategoria(categoria);

        return p;
    }

    private void guardar() {
        try {
            Producto p = leerFormulario();

            if (productoDAO.insertar(p)) {
                JOptionPane.showMessageDialog(
                        this, "Producto guardado correctamente.");
                limpiar();
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(
                        this, "No se pudo guardar el producto.");
            }
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un producto de la tabla.");
            return;
        }

        try {
            Producto p = leerFormulario();
            p.setIdProducto(idSeleccionado);

            if (productoDAO.actualizar(p)) {
                JOptionPane.showMessageDialog(
                        this, "Producto actualizado correctamente.");
                limpiar();
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(
                        this, "No se pudo actualizar el producto.");
            }
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un producto de la tabla.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar el producto seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                if (productoDAO.eliminar(idSeleccionado)) {
                    JOptionPane.showMessageDialog(
                            this, "Producto eliminado correctamente.");
                    limpiar();
                    cargarProductos();
                } else {
                    JOptionPane.showMessageDialog(
                            this, "No se encontró el producto.");
                }
            } catch (RuntimeException e) {
                mostrarError(e);
            }
        }
    }

    private void cargarFormulario() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            return;
        }

        idSeleccionado = Integer.parseInt(
                modelo.getValueAt(fila, 0).toString());

        txtNombre.setText(
                modelo.getValueAt(fila, 1).toString());

        txtPrecio.setText(
                modelo.getValueAt(fila, 2).toString());

        txtCantidad.setText(
                modelo.getValueAt(fila, 3).toString());

        String nombreCategoria =
                modelo.getValueAt(fila, 4).toString();

        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            Categoria c = cmbCategoria.getItemAt(i);

            if (c.getNombre().equals(nombreCategoria)) {
                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("");

        if (cmbCategoria.getItemCount() > 0) {
            cmbCategoria.setSelectedIndex(0);
        }

        idSeleccionado = -1;
        tabla.clearSelection();
    }

    private void mostrarError(Exception e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
