/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.taller8inventario;
import com.mycompany.taller8inventario.vista.VentanaInventario;
import javax.swing.SwingUtilities;


/**
 *
 * @author nicol
 */
public class Taller8Inventario {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaInventario().setVisible(true);
        });
    }
}
