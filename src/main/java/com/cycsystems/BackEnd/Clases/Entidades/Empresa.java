/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases.Entidades;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author angel
 */
@Entity
@Table(name = "empresa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Empresa.findAll", query = "SELECT e FROM Empresa e"),
    @NamedQuery(name = "Empresa.findByCodigo", query = "SELECT e FROM Empresa e WHERE e.codigo = :codigo"),
    @NamedQuery(name = "Empresa.findByNombreComercial", query = "SELECT e FROM Empresa e WHERE e.nombreComercial = :nombreComercial"),
    @NamedQuery(name = "Empresa.findByRazonSocial", query = "SELECT e FROM Empresa e WHERE e.razonSocial = :razonSocial"),
    @NamedQuery(name = "Empresa.findByDireccion", query = "SELECT e FROM Empresa e WHERE e.direccion = :direccion"),
    @NamedQuery(name = "Empresa.findByNit", query = "SELECT e FROM Empresa e WHERE e.nit = :nit"),
    @NamedQuery(name = "Empresa.findByTelefonoMovil", query = "SELECT e FROM Empresa e WHERE e.telefonoMovil = :telefonoMovil"),
    @NamedQuery(name = "Empresa.findByTelefonoFijo", query = "SELECT e FROM Empresa e WHERE e.telefonoFijo = :telefonoFijo")})
public class Empresa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "Nombre_Comercial")
    private String nombreComercial;
    @Basic(optional = false)
    @Column(name = "Razon_Social")
    private String razonSocial;
    @Basic(optional = false)
    @Column(name = "Direccion")
    private String direccion;
    @Basic(optional = false)
    @Column(name = "NIT")
    private String nit;
    @Column(name = "Telefono_Movil")
    private String telefonoMovil;
    @Column(name = "Telefono_Fijo")
    private String telefonoFijo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "empresa")
    private Collection<Bodega> bodegaCollection;

    public Empresa() {
    }

    public Empresa(Integer codigo) {
        this.codigo = codigo;
    }

    public Empresa(Integer codigo, String nombreComercial, String razonSocial, String direccion, String nit) {
        this.codigo = codigo;
        this.nombreComercial = nombreComercial;
        this.razonSocial = razonSocial;
        this.direccion = direccion;
        this.nit = nit;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getTelefonoMovil() {
        return telefonoMovil;
    }

    public void setTelefonoMovil(String telefonoMovil) {
        this.telefonoMovil = telefonoMovil;
    }

    public String getTelefonoFijo() {
        return telefonoFijo;
    }

    public void setTelefonoFijo(String telefonoFijo) {
        this.telefonoFijo = telefonoFijo;
    }

    @XmlTransient
    public Collection<Bodega> getBodegaCollection() {
        return bodegaCollection;
    }

    public void setBodegaCollection(Collection<Bodega> bodegaCollection) {
        this.bodegaCollection = bodegaCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigo != null ? codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Empresa)) {
            return false;
        }
        Empresa other = (Empresa) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cycsystems.BackEnd.Clases.Empresa[ codigo=" + codigo + " ]";
    }
    
}
