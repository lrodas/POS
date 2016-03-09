/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases.Entidades;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "empleado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Empleado.findAll", query = "SELECT e FROM Empleado e"),
    @NamedQuery(name = "Empleado.findByCodigo", query = "SELECT e FROM Empleado e WHERE e.codigo = :codigo"),
    @NamedQuery(name = "Empleado.findByPNombre", query = "SELECT e FROM Empleado e WHERE e.pNombre = :pNombre"),
    @NamedQuery(name = "Empleado.findBySNombre", query = "SELECT e FROM Empleado e WHERE e.sNombre = :sNombre"),
    @NamedQuery(name = "Empleado.findByPApellido", query = "SELECT e FROM Empleado e WHERE e.pApellido = :pApellido"),
    @NamedQuery(name = "Empleado.findBySApellido", query = "SELECT e FROM Empleado e WHERE e.sApellido = :sApellido"),
    @NamedQuery(name = "Empleado.findByDireccion", query = "SELECT e FROM Empleado e WHERE e.direccion = :direccion"),
    @NamedQuery(name = "Empleado.findByTelefonoMovil", query = "SELECT e FROM Empleado e WHERE e.telefonoMovil = :telefonoMovil"),
    @NamedQuery(name = "Empleado.findByTelefonoFijo", query = "SELECT e FROM Empleado e WHERE e.telefonoFijo = :telefonoFijo"),
    @NamedQuery(name = "Empleado.findByFechaNacimiento", query = "SELECT e FROM Empleado e WHERE e.fechaNacimiento = :fechaNacimiento"),
    @NamedQuery(name = "Empleado.findByNumeroIdentificacion", query = "SELECT e FROM Empleado e WHERE e.numeroIdentificacion = :numeroIdentificacion"),
    @NamedQuery(name = "Empleado.findByNacionalidad", query = "SELECT e FROM Empleado e WHERE e.nacionalidad = :nacionalidad"),
    @NamedQuery(name = "Empleado.findByUsuario", query = "SELECT e FROM Empleado e WHERE e.usuario = :usuario"),
    @NamedQuery(name = "Empleado.findByContrasenia", query = "SELECT e FROM Empleado e WHERE e.contrasenia = :contrasenia")})
public class Empleado implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "PNombre")
    private String pNombre;
    @Basic(optional = false)
    @Column(name = "SNombre")
    private String sNombre;
    @Basic(optional = false)
    @Column(name = "PApellido")
    private String pApellido;
    @Basic(optional = false)
    @Column(name = "SApellido")
    private String sApellido;
    @Basic(optional = false)
    @Column(name = "Direccion")
    private String direccion;
    @Column(name = "Telefono_Movil")
    private String telefonoMovil;
    @Column(name = "Telefono_Fijo")
    private String telefonoFijo;
    @Basic(optional = false)
    @Column(name = "FechaNacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;
    @Basic(optional = false)
    @Column(name = "NumeroIdentificacion")
    private String numeroIdentificacion;
    @Column(name = "Nacionalidad")
    private String nacionalidad;
    @Column(name = "Usuario")
    private String usuario;
    @Column(name = "Contrasenia")
    private String contrasenia;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "empleado")
    private Collection<Corte> corteCollection;
    @JoinColumn(name = "EstadoEmpleado", referencedColumnName = "IdEstadoEmpleado")
    @ManyToOne(optional = false)
    private Estadoempleado estadoEmpleado;
    @JoinColumn(name = "Rol", referencedColumnName = "IdRol")
    @ManyToOne(optional = false)
    private Rol rol;
    @JoinColumn(name = "Identificacion", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private TipoIdentificacion identificacion;

    public Empleado() {
    }

    public Empleado(Integer codigo) {
        this.codigo = codigo;
    }

    public Empleado(Integer codigo, String pNombre, String sNombre, String pApellido, String sApellido, String direccion, Date fechaNacimiento, String numeroIdentificacion) {
        this.codigo = codigo;
        this.pNombre = pNombre;
        this.sNombre = sNombre;
        this.pApellido = pApellido;
        this.sApellido = sApellido;
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getPNombre() {
        return pNombre;
    }

    public void setPNombre(String pNombre) {
        this.pNombre = pNombre;
    }

    public String getSNombre() {
        return sNombre;
    }

    public void setSNombre(String sNombre) {
        this.sNombre = sNombre;
    }

    public String getPApellido() {
        return pApellido;
    }

    public void setPApellido(String pApellido) {
        this.pApellido = pApellido;
    }

    public String getSApellido() {
        return sApellido;
    }

    public void setSApellido(String sApellido) {
        this.sApellido = sApellido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefonoMovil() {
        return telefonoMovil;
    }

    public void setTelefonoMovil(String telefonoMovil) {
        this.telefonoMovil = telefonoMovil;
    }

    public String getTelefonoFijo() {
        return telefonoFijo;
    }

    public void setTelefonoFijo(String telefonoFijo) {
        this.telefonoFijo = telefonoFijo;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    @XmlTransient
    public Collection<Corte> getCorteCollection() {
        return corteCollection;
    }

    public void setCorteCollection(Collection<Corte> corteCollection) {
        this.corteCollection = corteCollection;
    }

    public Estadoempleado getEstadoEmpleado() {
        return estadoEmpleado;
    }

    public void setEstadoEmpleado(Estadoempleado estadoEmpleado) {
        this.estadoEmpleado = estadoEmpleado;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public TipoIdentificacion getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(TipoIdentificacion identificacion) {
        this.identificacion = identificacion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigo != null ? codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Empleado)) {
            return false;
        }
        Empleado other = (Empleado) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cycsystems.BackEnd.Clases.Empleado[ codigo=" + codigo + " ]";
    }
    
}
