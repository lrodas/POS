/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author angel
 */
@Entity
@Table(name = "facturacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Facturacion.findAll", query = "SELECT f FROM Facturacion f"),
    @NamedQuery(name = "Facturacion.findBySerie", query = "SELECT f FROM Facturacion f WHERE f.facturacionPK.serie = :serie"),
    @NamedQuery(name = "Facturacion.findByCorrelativo", query = "SELECT f FROM Facturacion f WHERE f.facturacionPK.correlativo = :correlativo"),
    @NamedQuery(name = "Facturacion.findByFechaVenta", query = "SELECT f FROM Facturacion f WHERE f.fechaVenta = :fechaVenta"),
    @NamedQuery(name = "Facturacion.findByTotal", query = "SELECT f FROM Facturacion f WHERE f.total = :total"),
    @NamedQuery(name = "Facturacion.findByDescuentoTotal", query = "SELECT f FROM Facturacion f WHERE f.descuentoTotal = :descuentoTotal")})
public class Facturacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected FacturacionPK facturacionPK;
    @Basic(optional = false)
    @Column(name = "FechaVenta")
    @Temporal(TemporalType.DATE)
    private Date fechaVenta;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "Total")
    private BigDecimal total;
    @Column(name = "DescuentoTotal")
    private BigDecimal descuentoTotal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "facturaCorrelativo")
    private Collection<Detallepago> detallepagoCollection;
    @JoinColumn(name = "Cliente", referencedColumnName = "Codigo")
    @ManyToOne(optional = false)
    private Cliente cliente;
    @JoinColumn(name = "Corte", referencedColumnName = "Codigo")
    @ManyToOne(optional = false)
    private Corte corte;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "facturacion")
    private Collection<Detalle> detalleCollection;

    public Facturacion() {
    }

    public Facturacion(FacturacionPK facturacionPK) {
        this.facturacionPK = facturacionPK;
    }

    public Facturacion(FacturacionPK facturacionPK, Date fechaVenta, BigDecimal total) {
        this.facturacionPK = facturacionPK;
        this.fechaVenta = fechaVenta;
        this.total = total;
    }

    public Facturacion(String serie, int correlativo) {
        this.facturacionPK = new FacturacionPK(serie, correlativo);
    }

    public FacturacionPK getFacturacionPK() {
        return facturacionPK;
    }

    public void setFacturacionPK(FacturacionPK facturacionPK) {
        this.facturacionPK = facturacionPK;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getDescuentoTotal() {
        return descuentoTotal;
    }

    public void setDescuentoTotal(BigDecimal descuentoTotal) {
        this.descuentoTotal = descuentoTotal;
    }

    @XmlTransient
    public Collection<Detallepago> getDetallepagoCollection() {
        return detallepagoCollection;
    }

    public void setDetallepagoCollection(Collection<Detallepago> detallepagoCollection) {
        this.detallepagoCollection = detallepagoCollection;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Corte getCorte() {
        return corte;
    }

    public void setCorte(Corte corte) {
        this.corte = corte;
    }

    @XmlTransient
    public Collection<Detalle> getDetalleCollection() {
        return detalleCollection;
    }

    public void setDetalleCollection(Collection<Detalle> detalleCollection) {
        this.detalleCollection = detalleCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (facturacionPK != null ? facturacionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Facturacion)) {
            return false;
        }
        Facturacion other = (Facturacion) object;
        if ((this.facturacionPK == null && other.facturacionPK != null) || (this.facturacionPK != null && !this.facturacionPK.equals(other.facturacionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cycsystems.BackEnd.Clases.Facturacion[ facturacionPK=" + facturacionPK + " ]";
    }
    
}
