/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases.Entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author angel
 */
@Embeddable
public class DetallecotizacionPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "IdDetalleCotizacion")
    private int idDetalleCotizacion;
    @Basic(optional = false)
    @Column(name = "IdCotizacion")
    private int idCotizacion;

    public DetallecotizacionPK() {
    }

    public DetallecotizacionPK(int idDetalleCotizacion, int idCotizacion) {
        this.idDetalleCotizacion = idDetalleCotizacion;
        this.idCotizacion = idCotizacion;
    }

    public int getIdDetalleCotizacion() {
        return idDetalleCotizacion;
    }

    public void setIdDetalleCotizacion(int idDetalleCotizacion) {
        this.idDetalleCotizacion = idDetalleCotizacion;
    }

    public int getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(int idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idDetalleCotizacion;
        hash += (int) idCotizacion;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetallecotizacionPK)) {
            return false;
        }
        DetallecotizacionPK other = (DetallecotizacionPK) object;
        if (this.idDetalleCotizacion != other.idDetalleCotizacion) {
            return false;
        }
        if (this.idCotizacion != other.idCotizacion) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cycsystems.BackEnd.Clases.DetallecotizacionPK[ idDetalleCotizacion=" + idDetalleCotizacion + ", idCotizacion=" + idCotizacion + " ]";
    }
    
}
