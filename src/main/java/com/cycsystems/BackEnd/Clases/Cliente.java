package com.cycsystems.BackEnd.Clases;

import java.util.Date;

/**
 *
 * @author angel
 * Delaracion de la clase Clientes y sus atributos getters y setters
 */
public class Cliente {
    public int Codigo = 0;
    private String PNombre = "";
    private String SNombre = "";
    private String PApellido = "";
    private String SApellido = "";
    private String Direccion = "";
    private String NIT = "";
    private String Celular = "";
    private String Telefono = "";
    private Date Fecha_Nacimiento;
    
    public int getCodigo() {
        return Codigo;
    }

    public void setCodigo(int Codigo) {
        this.Codigo = Codigo;
    }

    public void setPNombre(String PNombre) {
        this.PNombre = PNombre;
    }

    public void setSNombre(String SNombre) {
        this.SNombre = SNombre;
    }

    public void setPApellido(String PApellido) {
        this.PApellido = PApellido;
    }

    public void setSApellido(String SApellido) {
        this.SApellido = SApellido;
    }

    public void setDireccion(String Direccion) {
        this.Direccion = Direccion;
    }

    public void setNIT(String NIT) {
        this.NIT = NIT;
    }

    public void setCelular(String Celular) {
        this.Celular = Celular;
    }

    public void setTelefono(String Telefono) {
        this.Telefono = Telefono;
    }

    public void setFecha_Nacimiento(Date Fecha_Nacimiento) {
        this.Fecha_Nacimiento = Fecha_Nacimiento;
    }

    public String getPNombre() {
        return PNombre;
    }

    public String getSNombre() {
        return SNombre;
    }

    public String getPApellido() {
        return PApellido;
    }

    public String getSApellido() {
        return SApellido;
    }

    public String getDireccion() {
        return Direccion;
    }

    public String getNIT() {
        return NIT;
    }

    public String getCelular() {
        return Celular;
    }

    public String getTelefono() {
        return Telefono;
    }

    public Date getFecha_Nacimiento() {
        return Fecha_Nacimiento;
    }
    
}
