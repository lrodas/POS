/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases.Entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "detallepago")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallepago.findAll", query = "SELECT d FROM Detallepago d"),
    @NamedQuery(name = "Detallepago.findByIdDetallePago", query = "SELECT d FROM Detallepago d WHERE d.idDetallePago = :idDetallePago"),
    @NamedQuery(name = "Detallepago.findByFacturaSerie", query = "SELECT d FROM Detallepago d WHERE d.facturaSerie = :facturaSerie"),
    @NamedQuery(name = "Detallepago.findByNumeroDocumento", query = "SELECT d FROM Detallepago d WHERE d.numeroDocumento = :numeroDocumento"),
    @NamedQuery(name = "Detallepago.findByAbono", query = "SELECT d FROM Detallepago d WHERE d.abono = :abono"),
    @NamedQuery(name = "Detallepago.findByCuotas", query = "SELECT d FROM Detallepago d WHERE d.cuotas = :cuotas")})
public class Detallepago implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdDetallePago")
    private Integer idDetallePago;
    @Basic(optional = false)
    @Column(name = "Factura_Serie")
    private String facturaSerie;
    @Column(name = "Numero_Documento")
    private String numeroDocumento;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "Abono")
    private BigDecimal abono;
    @Column(name = "Cuotas")
    private Integer cuotas;
    @JoinColumn(name = "FormaPago", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private Formapago formaPago;
    @JoinColumn(name = "Factura_Correlativo", referencedColumnName = "Correlativo")
    @ManyToOne(optional = false)
    private Facturacion facturaCorrelativo;

    public Detallepago() {
    }

    public Detallepago(Integer idDetallePago) {
        this.idDetallePago = idDetallePago;
    }

    public Detallepago(Integer idDetallePago, String facturaSerie, BigDecimal abono) {
        this.idDetallePago = idDetallePago;
        this.facturaSerie = facturaSerie;
        this.abono = abono;
    }

    public Integer getIdDetallePago() {
        return idDetallePago;
    }

    public void setIdDetallePago(Integer idDetallePago) {
        this.idDetallePago = idDetallePago;
    }

    public String getFacturaSerie() {
        return facturaSerie;
    }

    public void setFacturaSerie(String facturaSerie) {
        this.facturaSerie = facturaSerie;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public BigDecimal getAbono() {
        return abono;
    }

    public void setAbono(BigDecimal abono) {
        this.abono = abono;
    }

    public Integer getCuotas() {
        return cuotas;
    }

    public void setCuotas(Integer cuotas) {
        this.cuotas = cuotas;
    }

    public Formapago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(Formapago formaPago) {
        this.formaPago = formaPago;
    }

    public Facturacion getFacturaCorrelativo() {
        return facturaCorrelativo;
    }

    public void setFacturaCorrelativo(Facturacion facturaCorrelativo) {
        this.facturaCorrelativo = facturaCorrelativo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDetallePago != null ? idDetallePago.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detallepago)) {
            return false;
        }
        Detallepago other = (Detallepago) object;
        if ((this.idDetallePago == null && other.idDetallePago != null) || (this.idDetallePago != null && !this.idDetallePago.equals(other.idDetallePago))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cycsystems.BackEnd.Clases.Detallepago[ idDetallePago=" + idDetallePago + " ]";
    }
    
}
