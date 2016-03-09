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
public class FacturacionPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "Serie")
    private String serie;
    @Basic(optional = false)
    @Column(name = "Correlativo")
    private int correlativo;

    public FacturacionPK() {
    }

    public FacturacionPK(String serie, int correlativo) {
        this.serie = serie;
        this.correlativo = correlativo;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public int getCorrelativo() {
        return correlativo;
    }

    public void setCorrelativo(int correlativo) {
        this.correlativo = correlativo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serie != null ? serie.hashCode() : 0);
        hash += (int) correlativo;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FacturacionPK)) {
            return false;
        }
        FacturacionPK other = (FacturacionPK) object;
        if ((this.serie == null && other.serie != null) || (this.serie != null && !this.serie.equals(other.serie))) {
            return false;
        }
        if (this.correlativo != other.correlativo) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cycsystems.BackEnd.Clases.FacturacionPK[ serie=" + serie + ", correlativo=" + correlativo + " ]";
    }
    
}
