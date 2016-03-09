/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "estadoempleado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Estadoempleado.findAll", query = "SELECT e FROM Estadoempleado e"),
    @NamedQuery(name = "Estadoempleado.findByIdEstadoEmpleado", query = "SELECT e FROM Estadoempleado e WHERE e.idEstadoEmpleado = :idEstadoEmpleado"),
    @NamedQuery(name = "Estadoempleado.findByDescripcion", query = "SELECT e FROM Estadoempleado e WHERE e.descripcion = :descripcion")})
public class Estadoempleado implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "IdEstadoEmpleado")
    private Integer idEstadoEmpleado;
    @Column(name = "Descripcion")
    private String descripcion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "estadoEmpleado")
    private Collection<Empleado> empleadoCollection;

    public Estadoempleado() {
    }

    public Estadoempleado(Integer idEstadoEmpleado) {
        this.idEstadoEmpleado = idEstadoEmpleado;
    }

    public Integer getIdEstadoEmpleado() {
        return idEstadoEmpleado;
    }

    public void setIdEstadoEmpleado(Integer idEstadoEmpleado) {
        this.idEstadoEmpleado = idEstadoEmpleado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    public Collection<Empleado> getEmpleadoCollection() {
        return empleadoCollection;
    }

    public void setEmpleadoCollection(Collection<Empleado> empleadoCollection) {
        this.empleadoCollection = empleadoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEstadoEmpleado != null ? idEstadoEmpleado.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Estadoempleado)) {
            return false;
        }
        Estadoempleado other = (Estadoempleado) object;
        if ((this.idEstadoEmpleado == null && other.idEstadoEmpleado != null) || (this.idEstadoEmpleado != null && !this.idEstadoEmpleado.equals(other.idEstadoEmpleado))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cycsystems.BackEnd.Clases.Estadoempleado[ idEstadoEmpleado=" + idEstadoEmpleado + " ]";
    }
    
}
