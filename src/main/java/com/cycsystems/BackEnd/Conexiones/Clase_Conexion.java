/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Conexiones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Clase_Conexion {
    //Direccion de base de datos y Driver
    private final String Driver = "com.mysql.jdbc.Driver";
    private final String DB = "jdbc:mysql://localhost/cycsyste_seguridad";
    //Usuario y Password de la Base de Datos
    private final String User = "root", Pass = "";
    
    protected Connection Conexion;
    
    public Clase_Conexion(){
        
    }
    
    public void Conectar(){
        try{
            Conexion = DriverManager.getConnection(DB, User, Pass);
            Class.forName(Driver);
        }catch(SQLException | ClassNotFoundException e){
           JOptionPane.showMessageDialog(null, "Error Iniciando la Conexion", "POS", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void Cerrar(){
        try{
            if(Conexion != null){
                Conexion.close();
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error Cerrando la Conexion", "POS", JOptionPane.ERROR_MESSAGE);
        }
    }
}
