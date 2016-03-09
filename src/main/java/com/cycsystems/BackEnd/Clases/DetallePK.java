/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author angel
 */
@Embeddable
public class DetallePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "Serie_Factura")
    private String serieFactura;
    @Basic(optional = false)
    @Column(name = "Correlativo_Factura")
    private int correlativoFactura;

    public DetallePK() {
    }

    public DetallePK(String serieFactura, int correlativoFactura) {
        this.serieFactura = serieFactura;
        this.correlativoFactura = correlativoFactura;
    }

    public String getSerieFactura() {
        return serieFactura;
    }

    public void setSerieFactura(String serieFactura) {
        this.serieFactura = serieFactura;
    }

    public int getCorrelativoFactura() {
        return correlativoFactura;
    }

    public void setCorrelativoFactura(int correlativoFactura) {
        this.correlativoFactura = correlativoFactura;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serieFactura != null ? serieFactura.hashCode() : 0);
        hash += (int) correlativoFactura;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetallePK)) {
            return false;
        }
        DetallePK other = (DetallePK) object;
        if ((this.serieFactura == null && other.serieFactura != null) || (this.serieFactura != null && !this.serieFactura.equals(other.serieFactura))) {
            return false;
        }
        if (this.correlativoFactura != other.correlativoFactura) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cycsystems.BackEnd.Clases.DetallePK[ serieFactura=" + serieFactura + ", correlativoFactura=" + correlativoFactura + " ]";
    }
    
}
