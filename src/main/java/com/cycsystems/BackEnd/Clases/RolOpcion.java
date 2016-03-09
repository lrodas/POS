/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author angel
 */
@Entity
@Table(name = "rol_opcion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RolOpcion.findAll", query = "SELECT r FROM RolOpcion r"),
    @NamedQuery(name = "RolOpcion.findByIdRolOpcion", query = "SELECT r FROM RolOpcion r WHERE r.idRolOpcion = :idRolOpcion"),
    @NamedQuery(name = "RolOpcion.findByAltas", query = "SELECT r FROM RolOpcion r WHERE r.altas = :altas"),
    @NamedQuery(name = "RolOpcion.findByBajas", query = "SELECT r FROM RolOpcion r WHERE r.bajas = :bajas"),
    @NamedQuery(name = "RolOpcion.findByCambios", query = "SELECT r FROM RolOpcion r WHERE r.cambios = :cambios")})
public class RolOpcion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "IdRol_Opcion")
    private Integer idRolOpcion;
    @Column(name = "Altas")
    private Boolean altas;
    @Column(name = "Bajas")
    private Boolean bajas;
    @Column(name = "Cambios")
    private Boolean cambios;
    @JoinColumn(name = "Opcion", referencedColumnName = "IdOpcion")
    @ManyToOne(optional = false)
    private Opcion opcion;
    @JoinColumn(name = "Rol", referencedColumnName = "IdRol")
    @ManyToOne(optional = false)
    private Rol rol;

    public RolOpcion() {
    }

    public RolOpcion(Integer idRolOpcion) {
        this.idRolOpcion = idRolOpcion;
    }

    public Integer getIdRolOpcion() {
        return idRolOpcion;
    }

    public void setIdRolOpcion(Integer idRolOpcion) {
        this.idRolOpcion = idRolOpcion;
    }

    public Boolean getAltas() {
        return altas;
    }

    public void setAltas(Boolean altas) {
        this.altas = altas;
    }

    public Boolean getBajas() {
        return bajas;
    }

    public void setBajas(Boolean bajas) {
        this.bajas = bajas;
    }

    public Boolean getCambios() {
        return cambios;
    }

    public void setCambios(Boolean cambios) {
        this.cambios = cambios;
    }

    public Opcion getOpcion() {
        return opcion;
    }

    public void setOpcion(Opcion opcion) {
        this.opcion = opcion;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRolOpcion != null ? idRolOpcion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RolOpcion)) {
            return false;
        }
        RolOpcion other = (RolOpcion) object;
        if ((this.idRolOpcion == null && other.idRolOpcion != null) || (this.idRolOpcion != null && !this.idRolOpcion.equals(other.idRolOpcion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cycsystems.BackEnd.Clases.RolOpcion[ idRolOpcion=" + idRolOpcion + " ]";
    }
    
}
