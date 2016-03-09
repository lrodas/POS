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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "bodega")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bodega.findAll", query = "SELECT b FROM Bodega b"),
    @NamedQuery(name = "Bodega.findByCodigo", query = "SELECT b FROM Bodega b WHERE b.codigo = :codigo"),
    @NamedQuery(name = "Bodega.findByNombre", query = "SELECT b FROM Bodega b WHERE b.nombre = :nombre"),
    @NamedQuery(name = "Bodega.findByDireccion", query = "SELECT b FROM Bodega b WHERE b.direccion = :direccion")})
public class Bodega implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Codigo")
    private Integer codigo;
    @Column(name = "Nombre")
    private String nombre;
    @Column(name = "Direccion")
    private String direccion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bodega")
    private Collection<Ubicacion> ubicacionCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bodega")
    private Collection<Detallecotizacion> detallecotizacionCollection;
    @JoinColumn(name = "Empresa", referencedColumnName = "Codigo")
    @ManyToOne(optional = false)
    private Empresa empresa;
    @JoinColumn(name = "Licencia", referencedColumnName = "Codigo")
    @ManyToOne(optional = false)
    private Licencia licencia;

    public Bodega() {
    }

    public Bodega(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @XmlTransient
    public Collection<Ubicacion> getUbicacionCollection() {
        return ubicacionCollection;
    }

    public void setUbicacionCollection(Collection<Ubicacion> ubicacionCollection) {
        this.ubicacionCollection = ubicacionCollection;
    }

    @XmlTransient
    public Collection<Detallecotizacion> getDetallecotizacionCollection() {
        return detallecotizacionCollection;
    }

    public void setDetallecotizacionCollection(Collection<Detallecotizacion> detallecotizacionCollection) {
        this.detallecotizacionCollection = detallecotizacionCollection;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Licencia getLicencia() {
        return licencia;
    }

    public void setLicencia(Licencia licencia) {
        this.licencia = licencia;
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
        if (!(object instanceof Bodega)) {
            return false;
        }
        Bodega other = (Bodega) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cycsystems.BackEnd.Clases.Bodega[ codigo=" + codigo + " ]";
    }
    
}
