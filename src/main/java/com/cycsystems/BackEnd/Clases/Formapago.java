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
@Table(name = "formapago")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Formapago.findAll", query = "SELECT f FROM Formapago f"),
    @NamedQuery(name = "Formapago.findById", query = "SELECT f FROM Formapago f WHERE f.id = :id"),
    @NamedQuery(name = "Formapago.findByDescripcion", query = "SELECT f FROM Formapago f WHERE f.descripcion = :descripcion"),
    @NamedQuery(name = "Formapago.findByAplicaCuotas", query = "SELECT f FROM Formapago f WHERE f.aplicaCuotas = :aplicaCuotas")})
public class Formapago implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Column(name = "Descripcion")
    private Integer descripcion;
    @Column(name = "Aplica_Cuotas")
    private Boolean aplicaCuotas;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "formaPago")
    private Collection<Detallepago> detallepagoCollection;

    public Formapago() {
    }

    public Formapago(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(Integer descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getAplicaCuotas() {
        return aplicaCuotas;
    }

    public void setAplicaCuotas(Boolean aplicaCuotas) {
        this.aplicaCuotas = aplicaCuotas;
    }

    @XmlTransient
    public Collection<Detallepago> getDetallepagoCollection() {
        return detallepagoCollection;
    }

    public void setDetallepagoCollection(Collection<Detallepago> detallepagoCollection) {
        this.detallepagoCollection = detallepagoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Formapago)) {
            return false;
        }
        Formapago other = (Formapago) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cycsystems.BackEnd.Clases.Formapago[ id=" + id + " ]";
    }
    
}
