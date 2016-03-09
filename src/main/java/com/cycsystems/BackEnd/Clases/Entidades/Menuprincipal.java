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
@Table(name = "menuprincipal")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Menuprincipal.findAll", query = "SELECT m FROM Menuprincipal m"),
    @NamedQuery(name = "Menuprincipal.findByIdMenuPrincipal", query = "SELECT m FROM Menuprincipal m WHERE m.idMenuPrincipal = :idMenuPrincipal"),
    @NamedQuery(name = "Menuprincipal.findByDescripcion", query = "SELECT m FROM Menuprincipal m WHERE m.descripcion = :descripcion")})
public class Menuprincipal implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "IdMenuPrincipal")
    private Integer idMenuPrincipal;
    @Column(name = "Descripcion")
    private String descripcion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "menuPrincipal")
    private Collection<Opcion> opcionCollection;

    public Menuprincipal() {
    }

    public Menuprincipal(Integer idMenuPrincipal) {
        this.idMenuPrincipal = idMenuPrincipal;
    }

    public Integer getIdMenuPrincipal() {
        return idMenuPrincipal;
    }

    public void setIdMenuPrincipal(Integer idMenuPrincipal) {
        this.idMenuPrincipal = idMenuPrincipal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    public Collection<Opcion> getOpcionCollection() {
        return opcionCollection;
    }

    public void setOpcionCollection(Collection<Opcion> opcionCollection) {
        this.opcionCollection = opcionCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMenuPrincipal != null ? idMenuPrincipal.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Menuprincipal)) {
            return false;
        }
        Menuprincipal other = (Menuprincipal) object;
        if ((this.idMenuPrincipal == null && other.idMenuPrincipal != null) || (this.idMenuPrincipal != null && !this.idMenuPrincipal.equals(other.idMenuPrincipal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cycsystems.BackEnd.Clases.Menuprincipal[ idMenuPrincipal=" + idMenuPrincipal + " ]";
    }
    
}
