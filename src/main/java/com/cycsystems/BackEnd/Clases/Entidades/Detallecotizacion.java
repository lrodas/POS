/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases.Entidades;

import java.io.Serializable;
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
@Table(name = "detallecotizacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallecotizacion.findAll", query = "SELECT d FROM Detallecotizacion d"),
    @NamedQuery(name = "Detallecotizacion.findByIdDetalleCotizacion", query = "SELECT d FROM Detallecotizacion d WHERE d.detallecotizacionPK.idDetalleCotizacion = :idDetalleCotizacion"),
    @NamedQuery(name = "Detallecotizacion.findByIdCotizacion", query = "SELECT d FROM Detallecotizacion d WHERE d.detallecotizacionPK.idCotizacion = :idCotizacion")})
public class Detallecotizacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DetallecotizacionPK detallecotizacionPK;
    @JoinColumn(name = "Bodega", referencedColumnName = "Codigo")
    @ManyToOne(optional = false)
    private Bodega bodega;
    @JoinColumn(name = "IdCotizacion", referencedColumnName = "IdCotizacion", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Cotizacion cotizacion;
    @JoinColumn(name = "Producto", referencedColumnName = "Codigo")
    @ManyToOne(optional = false)
    private Producto producto;

    public Detallecotizacion() {
    }

    public Detallecotizacion(DetallecotizacionPK detallecotizacionPK) {
        this.detallecotizacionPK = detallecotizacionPK;
    }

    public Detallecotizacion(int idDetalleCotizacion, int idCotizacion) {
        this.detallecotizacionPK = new DetallecotizacionPK(idDetalleCotizacion, idCotizacion);
    }

    public DetallecotizacionPK getDetallecotizacionPK() {
        return detallecotizacionPK;
    }

    public void setDetallecotizacionPK(DetallecotizacionPK detallecotizacionPK) {
        this.detallecotizacionPK = detallecotizacionPK;
    }

    public Bodega getBodega() {
        return bodega;
    }

    public void setBodega(Bodega bodega) {
        this.bodega = bodega;
    }

    public Cotizacion getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(Cotizacion cotizacion) {
        this.cotizacion = cotizacion;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (detallecotizacionPK != null ? detallecotizacionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detallecotizacion)) {
            return false;
        }
        Detallecotizacion other = (Detallecotizacion) object;
        if ((this.detallecotizacionPK == null && other.detallecotizacionPK != null) || (this.detallecotizacionPK != null && !this.detallecotizacionPK.equals(other.detallecotizacionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cycsystems.BackEnd.Clases.Detallecotizacion[ detallecotizacionPK=" + detallecotizacionPK + " ]";
    }
    
}
