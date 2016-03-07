package com.cycsystems.BackEnd.Implementaciones;

import com.cycsystems.BackEnd.Clases.Cliente;
import com.cycsystems.BackEnd.Conexiones.Clase_Conexion;
import com.cycsystems.BackEnd.Interfaces.Inteface_Cliente;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author angel
 * Implementacion de la clase cliente y la interface cliente donde definimos el funcionamiento de las clases 
 */
public class Implementacion_Cliente extends Clase_Conexion implements Inteface_Cliente {

    private String Query = "";    

    @Override
    public void registrarCliente(Cliente cli) throws Exception {
        Query = "INSERT INTO Cliente(PNombre, SNombre, PApellido, SApellido, Direccion, NIT, Telefono_Movil, Telefono_Fijo, FechaNacimiento)"
                + "VALUES(?,?,?,?,?,?,?,?,?)";
        try{
            this.Conectar();
            PreparedStatement ps = this.Conexion.prepareStatement(Query);
            ps.setString(1, cli.getPNombre());
            ps.setString(2, cli.getSNombre());
            ps.setString(3, cli.getPApellido());
            ps.setString(4, cli.getSApellido());
            ps.setString(5, cli.getDireccion());
            ps.setString(6, cli.getNIT());
            ps.setString(8, cli.getCelular());
            ps.setString(7, cli.getTelefono());
            ps.setDate(9, (Date) cli.getFecha_Nacimiento());
            ps.executeUpdate();
        }catch(Exception e){
            throw e;
        }finally{
            this.Cerrar();
        }
    }

    @Override
    public void modificarCliente(Cliente clie) throws Exception {
        Query = "UPDATE Cliente SET PNombre = ?, SNombre = ?, PApellido = ?, SApellido = ?, Direccion = ?, NIT = ?, Telefono_Movil = ?, Telefono_Fijo = ?, FechaNacimiento = ? WHERE Codigo = ?";
        try{
            this.Conectar();
            PreparedStatement ps = this.Conexion.prepareStatement(Query);
            ps.setString(1, clie.getPNombre());
            ps.setString(2, clie.getSNombre());
            ps.setString(3, clie.getPApellido());
            ps.setString(4, clie.getSApellido());
            ps.setString(5, clie.getDireccion());
            ps.setString(6, clie.getNIT());
            ps.setString(8, clie.getCelular());
            ps.setString(7, clie.getTelefono());
            ps.setDate(9, (Date) clie.getFecha_Nacimiento());
            ps.setInt(10, clie.getCodigo());
            ps.executeUpdate();
        }catch(Exception e){
            throw e;
        }finally{
            this.Cerrar();
        }
    }

    @Override
    public void eliminarCliente(Cliente cli) throws Exception {
        Query = "DELETE FROM Cliente WHERE Codigo = ?";
        try{
            this.Conectar();
            PreparedStatement ps = this.Conexion.prepareStatement(Query);
            ps.setInt(1, cli.getCodigo());
            ps.executeUpdate();
        }catch(Exception e){
            throw e;
        }finally{
            this.Cerrar();
        }
    }

    @Override
    public List<Cliente> ListarClientes() throws Exception {
        Query = "SELECT * FROM Cliente";
        List<Cliente> lista = null;
        try{
            this.Conectar();
            PreparedStatement ps = this.Conexion.prepareStatement(Query);
            lista = new ArrayList();
            ResultSet rs =  ps.executeQuery();
            while(rs.next()){
                Cliente cli = new Cliente();
                cli.setCodigo(rs.getInt("Codigo"));
                cli.setPNombre(rs.getString("PNombre"));
                cli.setSNombre(rs.getString("SNombre"));
                cli.setPApellido(rs.getString("PApellido"));
                cli.setSApellido(rs.getString("SApellido"));
                cli.setDireccion(rs.getString("Direccion"));
                cli.setNIT(rs.getString("NIT"));
                cli.setTelefono(rs.getString("Telefono_Fijo"));
                cli.setCelular(rs.getString("Telefono_Movil"));
                cli.setFecha_Nacimiento(rs.getDate("FechaNacimiento"));
                lista.add(cli);
            }
        }catch(Exception e){
            throw e;
        }finally{
            this.Cerrar();
        }
        
        return lista;
    }
}
