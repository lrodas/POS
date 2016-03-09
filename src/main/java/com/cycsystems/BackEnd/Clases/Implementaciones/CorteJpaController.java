/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases.Implementaciones;

import com.cycsystems.BackEnd.Clases.Entidades.Corte;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.cycsystems.BackEnd.Clases.Entidades.Empleado;
import com.cycsystems.BackEnd.Clases.Entidades.Facturacion;
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
public class CorteJpaController implements Serializable {

    public CorteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Corte corte) {
        if (corte.getFacturacionCollection() == null) {
            corte.setFacturacionCollection(new ArrayList<Facturacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empleado empleado = corte.getEmpleado();
            if (empleado != null) {
                empleado = em.getReference(empleado.getClass(), empleado.getCodigo());
                corte.setEmpleado(empleado);
            }
            Collection<Facturacion> attachedFacturacionCollection = new ArrayList<Facturacion>();
            for (Facturacion facturacionCollectionFacturacionToAttach : corte.getFacturacionCollection()) {
                facturacionCollectionFacturacionToAttach = em.getReference(facturacionCollectionFacturacionToAttach.getClass(), facturacionCollectionFacturacionToAttach.getFacturacionPK());
                attachedFacturacionCollection.add(facturacionCollectionFacturacionToAttach);
            }
            corte.setFacturacionCollection(attachedFacturacionCollection);
            em.persist(corte);
            if (empleado != null) {
                empleado.getCorteCollection().add(corte);
                empleado = em.merge(empleado);
            }
            for (Facturacion facturacionCollectionFacturacion : corte.getFacturacionCollection()) {
                Corte oldCorteOfFacturacionCollectionFacturacion = facturacionCollectionFacturacion.getCorte();
                facturacionCollectionFacturacion.setCorte(corte);
                facturacionCollectionFacturacion = em.merge(facturacionCollectionFacturacion);
                if (oldCorteOfFacturacionCollectionFacturacion != null) {
                    oldCorteOfFacturacionCollectionFacturacion.getFacturacionCollection().remove(facturacionCollectionFacturacion);
                    oldCorteOfFacturacionCollectionFacturacion = em.merge(oldCorteOfFacturacionCollectionFacturacion);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Corte corte) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Corte persistentCorte = em.find(Corte.class, corte.getCodigo());
            Empleado empleadoOld = persistentCorte.getEmpleado();
            Empleado empleadoNew = corte.getEmpleado();
            Collection<Facturacion> facturacionCollectionOld = persistentCorte.getFacturacionCollection();
            Collection<Facturacion> facturacionCollectionNew = corte.getFacturacionCollection();
            List<String> illegalOrphanMessages = null;
            for (Facturacion facturacionCollectionOldFacturacion : facturacionCollectionOld) {
                if (!facturacionCollectionNew.contains(facturacionCollectionOldFacturacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Facturacion " + facturacionCollectionOldFacturacion + " since its corte field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (empleadoNew != null) {
                empleadoNew = em.getReference(empleadoNew.getClass(), empleadoNew.getCodigo());
                corte.setEmpleado(empleadoNew);
            }
            Collection<Facturacion> attachedFacturacionCollectionNew = new ArrayList<Facturacion>();
            for (Facturacion facturacionCollectionNewFacturacionToAttach : facturacionCollectionNew) {
                facturacionCollectionNewFacturacionToAttach = em.getReference(facturacionCollectionNewFacturacionToAttach.getClass(), facturacionCollectionNewFacturacionToAttach.getFacturacionPK());
                attachedFacturacionCollectionNew.add(facturacionCollectionNewFacturacionToAttach);
            }
            facturacionCollectionNew = attachedFacturacionCollectionNew;
            corte.setFacturacionCollection(facturacionCollectionNew);
            corte = em.merge(corte);
            if (empleadoOld != null && !empleadoOld.equals(empleadoNew)) {
                empleadoOld.getCorteCollection().remove(corte);
                empleadoOld = em.merge(empleadoOld);
            }
            if (empleadoNew != null && !empleadoNew.equals(empleadoOld)) {
                empleadoNew.getCorteCollection().add(corte);
                empleadoNew = em.merge(empleadoNew);
            }
            for (Facturacion facturacionCollectionNewFacturacion : facturacionCollectionNew) {
                if (!facturacionCollectionOld.contains(facturacionCollectionNewFacturacion)) {
                    Corte oldCorteOfFacturacionCollectionNewFacturacion = facturacionCollectionNewFacturacion.getCorte();
                    facturacionCollectionNewFacturacion.setCorte(corte);
                    facturacionCollectionNewFacturacion = em.merge(facturacionCollectionNewFacturacion);
                    if (oldCorteOfFacturacionCollectionNewFacturacion != null && !oldCorteOfFacturacionCollectionNewFacturacion.equals(corte)) {
                        oldCorteOfFacturacionCollectionNewFacturacion.getFacturacionCollection().remove(facturacionCollectionNewFacturacion);
                        oldCorteOfFacturacionCollectionNewFacturacion = em.merge(oldCorteOfFacturacionCollectionNewFacturacion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = corte.getCodigo();
                if (findCorte(id) == null) {
                    throw new NonexistentEntityException("The corte with id " + id + " no longer exists.");
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
            Corte corte;
            try {
                corte = em.getReference(Corte.class, id);
                corte.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The corte with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Facturacion> facturacionCollectionOrphanCheck = corte.getFacturacionCollection();
            for (Facturacion facturacionCollectionOrphanCheckFacturacion : facturacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Corte (" + corte + ") cannot be destroyed since the Facturacion " + facturacionCollectionOrphanCheckFacturacion + " in its facturacionCollection field has a non-nullable corte field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Empleado empleado = corte.getEmpleado();
            if (empleado != null) {
                empleado.getCorteCollection().remove(corte);
                empleado = em.merge(empleado);
            }
            em.remove(corte);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Corte> findCorteEntities() {
        return findCorteEntities(true, -1, -1);
    }

    public List<Corte> findCorteEntities(int maxResults, int firstResult) {
        return findCorteEntities(false, maxResults, firstResult);
    }

    private List<Corte> findCorteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Corte.class));
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

    public Corte findCorte(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Corte.class, id);
        } finally {
            em.close();
        }
    }

    public int getCorteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Corte> rt = cq.from(Corte.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
