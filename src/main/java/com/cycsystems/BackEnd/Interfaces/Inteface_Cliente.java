package com.cycsystems.BackEnd.Interfaces;

import com.cycsystems.BackEnd.Clases.Cliente;
import java.util.List;

/**
 *
 * @author angel
 * Interface de la clase cliente en donde se declaran las funciones 
 */
public interface Inteface_Cliente {
    public void registrarCliente(Cliente cli) throws Exception;
    public void modificarCliente(Cliente clie) throws Exception;
    public void eliminarCliente(Cliente cli) throws Exception;
    public List<Cliente> ListarClientes() throws Exception; 
}
