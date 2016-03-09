/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "detalle")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detalle.findAll", query = "SELECT d FROM Detalle d"),
    @NamedQuery(name = "Detalle.findByProducto", query = "SELECT d FROM Detalle d WHERE d.producto = :producto"),
    @NamedQuery(name = "Detalle.findBySerieFactura", query = "SELECT d FROM Detalle d WHERE d.detallePK.serieFactura = :serieFactura"),
    @NamedQuery(name = "Detalle.findByCorrelativoFactura", query = "SELECT d FROM Detalle d WHERE d.detallePK.correlativoFactura = :correlativoFactura")})
public class Detalle implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DetallePK detallePK;
    @Basic(optional = false)
    @Column(name = "Producto")
    private int producto;
    @JoinColumn(name = "Correlativo_Factura", referencedColumnName = "Correlativo", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Facturacion facturacion;
    @JoinColumn(name = "Bodega", referencedColumnName = "Bodega")
    @ManyToOne(optional = false)
    private Ubicacion bodega;

    public Detalle() {
    }

    public Detalle(DetallePK detallePK) {
        this.detallePK = detallePK;
    }

    public Detalle(DetallePK detallePK, int producto) {
        this.detallePK = detallePK;
        this.producto = producto;
    }

    public Detalle(String serieFactura, int correlativoFactura) {
        this.detallePK = new DetallePK(serieFactura, correlativoFactura);
    }

    public DetallePK getDetallePK() {
        return detallePK;
    }

    public void setDetallePK(DetallePK detallePK) {
        this.detallePK = detallePK;
    }

    public int getProducto() {
        return producto;
    }

    public void setProducto(int producto) {
        this.producto = producto;
    }

    public Facturacion getFacturacion() {
        return facturacion;
    }

    public void setFacturacion(Facturacion facturacion) {
        this.facturacion = facturacion;
    }

    public Ubicacion getBodega() {
        return bodega;
    }

    public void setBodega(Ubicacion bodega) {
        this.bodega = bodega;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (detallePK != null ? detallePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detalle)) {
            return false;
        }
        Detalle other = (Detalle) object;
        if ((this.detallePK == null && other.detallePK != null) || (this.detallePK != null && !this.detallePK.equals(other.detallePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cycsystems.BackEnd.Clases.Detalle[ detallePK=" + detallePK + " ]";
    }
    
}
