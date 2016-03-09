/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases.Implementaciones;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.cycsystems.BackEnd.Clases.Entidades.Estadoempleado;
import com.cycsystems.BackEnd.Clases.Entidades.Rol;
import com.cycsystems.BackEnd.Clases.Entidades.TipoIdentificacion;
import com.cycsystems.BackEnd.Clases.Entidades.Corte;
import com.cycsystems.BackEnd.Clases.Entidades.Empleado;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.IllegalOrphanException;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author angel
 */
public class EmpleadoJpaController implements Serializable {

    public EmpleadoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Empleado empleado) {
        if (empleado.getCorteCollection() == null) {
            empleado.setCorteCollection(new ArrayList<Corte>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estadoempleado estadoEmpleado = empleado.getEstadoEmpleado();
            if (estadoEmpleado != null) {
                estadoEmpleado = em.getReference(estadoEmpleado.getClass(), estadoEmpleado.getIdEstadoEmpleado());
                empleado.setEstadoEmpleado(estadoEmpleado);
            }
            Rol rol = empleado.getRol();
            if (rol != null) {
                rol = em.getReference(rol.getClass(), rol.getIdRol());
                empleado.setRol(rol);
            }
            TipoIdentificacion identificacion = empleado.getIdentificacion();
            if (identificacion != null) {
                identificacion = em.getReference(identificacion.getClass(), identificacion.getId());
                empleado.setIdentificacion(identificacion);
            }
            Collection<Corte> attachedCorteCollection = new ArrayList<Corte>();
            for (Corte corteCollectionCorteToAttach : empleado.getCorteCollection()) {
                corteCollectionCorteToAttach = em.getReference(corteCollectionCorteToAttach.getClass(), corteCollectionCorteToAttach.getCodigo());
                attachedCorteCollection.add(corteCollectionCorteToAttach);
            }
            empleado.setCorteCollection(attachedCorteCollection);
            em.persist(empleado);
            if (estadoEmpleado != null) {
                estadoEmpleado.getEmpleadoCollection().add(empleado);
                estadoEmpleado = em.merge(estadoEmpleado);
            }
            if (rol != null) {
                rol.getEmpleadoCollection().add(empleado);
                rol = em.merge(rol);
            }
            if (identificacion != null) {
                identificacion.getEmpleadoCollection().add(empleado);
                identificacion = em.merge(identificacion);
            }
            for (Corte corteCollectionCorte : empleado.getCorteCollection()) {
                Empleado oldEmpleadoOfCorteCollectionCorte = corteCollectionCorte.getEmpleado();
                corteCollectionCorte.setEmpleado(empleado);
                corteCollectionCorte = em.merge(corteCollectionCorte);
                if (oldEmpleadoOfCorteCollectionCorte != null) {
                    oldEmpleadoOfCorteCollectionCorte.getCorteCollection().remove(corteCollectionCorte);
                    oldEmpleadoOfCorteCollectionCorte = em.merge(oldEmpleadoOfCorteCollectionCorte);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Empleado empleado) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empleado persistentEmpleado = em.find(Empleado.class, empleado.getCodigo());
            Estadoempleado estadoEmpleadoOld = persistentEmpleado.getEstadoEmpleado();
            Estadoempleado estadoEmpleadoNew = empleado.getEstadoEmpleado();
            Rol rolOld = persistentEmpleado.getRol();
            Rol rolNew = empleado.getRol();
            TipoIdentificacion identificacionOld = persistentEmpleado.getIdentificacion();
            TipoIdentificacion identificacionNew = empleado.getIdentificacion();
            Collection<Corte> corteCollectionOld = persistentEmpleado.getCorteCollection();
            Collection<Corte> corteCollectionNew = empleado.getCorteCollection();
            List<String> illegalOrphanMessages = null;
            for (Corte corteCollectionOldCorte : corteCollectionOld) {
                if (!corteCollectionNew.contains(corteCollectionOldCorte)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Corte " + corteCollectionOldCorte + " since its empleado field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (estadoEmpleadoNew != null) {
                estadoEmpleadoNew = em.getReference(estadoEmpleadoNew.getClass(), estadoEmpleadoNew.getIdEstadoEmpleado());
                empleado.setEstadoEmpleado(estadoEmpleadoNew);
            }
            if (rolNew != null) {
                rolNew = em.getReference(rolNew.getClass(), rolNew.getIdRol());
                empleado.setRol(rolNew);
            }
            if (identificacionNew != null) {
                identificacionNew = em.getReference(identificacionNew.getClass(), identificacionNew.getId());
                empleado.setIdentificacion(identificacionNew);
            }
            Collection<Corte> attachedCorteCollectionNew = new ArrayList<Corte>();
            for (Corte corteCollectionNewCorteToAttach : corteCollectionNew) {
                corteCollectionNewCorteToAttach = em.getReference(corteCollectionNewCorteToAttach.getClass(), corteCollectionNewCorteToAttach.getCodigo());
                attachedCorteCollectionNew.add(corteCollectionNewCorteToAttach);
            }
            corteCollectionNew = attachedCorteCollectionNew;
            empleado.setCorteCollection(corteCollectionNew);
            empleado = em.merge(empleado);
            if (estadoEmpleadoOld != null && !estadoEmpleadoOld.equals(estadoEmpleadoNew)) {
                estadoEmpleadoOld.getEmpleadoCollection().remove(empleado);
                estadoEmpleadoOld = em.merge(estadoEmpleadoOld);
            }
            if (estadoEmpleadoNew != null && !estadoEmpleadoNew.equals(estadoEmpleadoOld)) {
                estadoEmpleadoNew.getEmpleadoCollection().add(empleado);
                estadoEmpleadoNew = em.merge(estadoEmpleadoNew);
            }
            if (rolOld != null && !rolOld.equals(rolNew)) {
                rolOld.getEmpleadoCollection().remove(empleado);
                rolOld = em.merge(rolOld);
            }
            if (rolNew != null && !rolNew.equals(rolOld)) {
                rolNew.getEmpleadoCollection().add(empleado);
                rolNew = em.merge(rolNew);
            }
            if (identificacionOld != null && !identificacionOld.equals(identificacionNew)) {
                identificacionOld.getEmpleadoCollection().remove(empleado);
                identificacionOld = em.merge(identificacionOld);
            }
            if (identificacionNew != null && !identificacionNew.equals(identificacionOld)) {
                identificacionNew.getEmpleadoCollection().add(empleado);
                identificacionNew = em.merge(identificacionNew);
            }
            for (Corte corteCollectionNewCorte : corteCollectionNew) {
                if (!corteCollectionOld.contains(corteCollectionNewCorte)) {
                    Empleado oldEmpleadoOfCorteCollectionNewCorte = corteCollectionNewCorte.getEmpleado();
                    corteCollectionNewCorte.setEmpleado(empleado);
                    corteCollectionNewCorte = em.merge(corteCollectionNewCorte);
                    if (oldEmpleadoOfCorteCollectionNewCorte != null && !oldEmpleadoOfCorteCollectionNewCorte.equals(empleado)) {
                        oldEmpleadoOfCorteCollectionNewCorte.getCorteCollection().remove(corteCollectionNewCorte);
                        oldEmpleadoOfCorteCollectionNewCorte = em.merge(oldEmpleadoOfCorteCollectionNewCorte);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = empleado.getCodigo();
                if (findEmpleado(id) == null) {
                    throw new NonexistentEntityException("The empleado with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empleado empleado;
            try {
                empleado = em.getReference(Empleado.class, id);
                empleado.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The empleado with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Corte> corteCollectionOrphanCheck = empleado.getCorteCollection();
            for (Corte corteCollectionOrphanCheckCorte : corteCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Empleado (" + empleado + ") cannot be destroyed since the Corte " + corteCollectionOrphanCheckCorte + " in its corteCollection field has a non-nullable empleado field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Estadoempleado estadoEmpleado = empleado.getEstadoEmpleado();
            if (estadoEmpleado != null) {
                estadoEmpleado.getEmpleadoCollection().remove(empleado);
                estadoEmpleado = em.merge(estadoEmpleado);
            }
            Rol rol = empleado.getRol();
            if (rol != null) {
                rol.getEmpleadoCollection().remove(empleado);
                rol = em.merge(rol);
            }
            TipoIdentificacion identificacion = empleado.getIdentificacion();
            if (identificacion != null) {
                identificacion.getEmpleadoCollection().remove(empleado);
                identificacion = em.merge(identificacion);
            }
            em.remove(empleado);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Empleado> findEmpleadoEntities() {
        return findEmpleadoEntities(true, -1, -1);
    }

    public List<Empleado> findEmpleadoEntities(int maxResults, int firstResult) {
        return findEmpleadoEntities(false, maxResults, firstResult);
    }

    private List<Empleado> findEmpleadoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Empleado.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Empleado findEmpleado(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Empleado.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmpleadoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Empleado> rt = cq.from(Empleado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
