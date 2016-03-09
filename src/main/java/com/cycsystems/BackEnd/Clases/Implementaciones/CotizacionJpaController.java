/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases.Implementaciones;

import com.cycsystems.BackEnd.Clases.Entidades.Cotizacion;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.cycsystems.BackEnd.Clases.Entidades.Detallecotizacion;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.IllegalOrphanException;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.NonexistentEntityException;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author angel
 */
public class CotizacionJpaController implements Serializable {

    public CotizacionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cotizacion cotizacion) throws PreexistingEntityException, Exception {
        if (cotizacion.getDetallecotizacionCollection() == null) {
            cotizacion.setDetallecotizacionCollection(new ArrayList<Detallecotizacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Detallecotizacion> attachedDetallecotizacionCollection = new ArrayList<Detallecotizacion>();
            for (Detallecotizacion detallecotizacionCollectionDetallecotizacionToAttach : cotizacion.getDetallecotizacionCollection()) {
                detallecotizacionCollectionDetallecotizacionToAttach = em.getReference(detallecotizacionCollectionDetallecotizacionToAttach.getClass(), detallecotizacionCollectionDetallecotizacionToAttach.getDetallecotizacionPK());
                attachedDetallecotizacionCollection.add(detallecotizacionCollectionDetallecotizacionToAttach);
            }
            cotizacion.setDetallecotizacionCollection(attachedDetallecotizacionCollection);
            em.persist(cotizacion);
            for (Detallecotizacion detallecotizacionCollectionDetallecotizacion : cotizacion.getDetallecotizacionCollection()) {
                Cotizacion oldCotizacionOfDetallecotizacionCollectionDetallecotizacion = detallecotizacionCollectionDetallecotizacion.getCotizacion();
                detallecotizacionCollectionDetallecotizacion.setCotizacion(cotizacion);
                detallecotizacionCollectionDetallecotizacion = em.merge(detallecotizacionCollectionDetallecotizacion);
                if (oldCotizacionOfDetallecotizacionCollectionDetallecotizacion != null) {
                    oldCotizacionOfDetallecotizacionCollectionDetallecotizacion.getDetallecotizacionCollection().remove(detallecotizacionCollectionDetallecotizacion);
                    oldCotizacionOfDetallecotizacionCollectionDetallecotizacion = em.merge(oldCotizacionOfDetallecotizacionCollectionDetallecotizacion);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCotizacion(cotizacion.getIdCotizacion()) != null) {
                throw new PreexistingEntityException("Cotizacion " + cotizacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cotizacion cotizacion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cotizacion persistentCotizacion = em.find(Cotizacion.class, cotizacion.getIdCotizacion());
            Collection<Detallecotizacion> detallecotizacionCollectionOld = persistentCotizacion.getDetallecotizacionCollection();
            Collection<Detallecotizacion> detallecotizacionCollectionNew = cotizacion.getDetallecotizacionCollection();
            List<String> illegalOrphanMessages = null;
            for (Detallecotizacion detallecotizacionCollectionOldDetallecotizacion : detallecotizacionCollectionOld) {
                if (!detallecotizacionCollectionNew.contains(detallecotizacionCollectionOldDetallecotizacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detallecotizacion " + detallecotizacionCollectionOldDetallecotizacion + " since its cotizacion field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Detallecotizacion> attachedDetallecotizacionCollectionNew = new ArrayList<Detallecotizacion>();
            for (Detallecotizacion detallecotizacionCollectionNewDetallecotizacionToAttach : detallecotizacionCollectionNew) {
                detallecotizacionCollectionNewDetallecotizacionToAttach = em.getReference(detallecotizacionCollectionNewDetallecotizacionToAttach.getClass(), detallecotizacionCollectionNewDetallecotizacionToAttach.getDetallecotizacionPK());
                attachedDetallecotizacionCollectionNew.add(detallecotizacionCollectionNewDetallecotizacionToAttach);
            }
            detallecotizacionCollectionNew = attachedDetallecotizacionCollectionNew;
            cotizacion.setDetallecotizacionCollection(detallecotizacionCollectionNew);
            cotizacion = em.merge(cotizacion);
            for (Detallecotizacion detallecotizacionCollectionNewDetallecotizacion : detallecotizacionCollectionNew) {
                if (!detallecotizacionCollectionOld.contains(detallecotizacionCollectionNewDetallecotizacion)) {
                    Cotizacion oldCotizacionOfDetallecotizacionCollectionNewDetallecotizacion = detallecotizacionCollectionNewDetallecotizacion.getCotizacion();
                    detallecotizacionCollectionNewDetallecotizacion.setCotizacion(cotizacion);
                    detallecotizacionCollectionNewDetallecotizacion = em.merge(detallecotizacionCollectionNewDetallecotizacion);
                    if (oldCotizacionOfDetallecotizacionCollectionNewDetallecotizacion != null && !oldCotizacionOfDetallecotizacionCollectionNewDetallecotizacion.equals(cotizacion)) {
                        oldCotizacionOfDetallecotizacionCollectionNewDetallecotizacion.getDetallecotizacionCollection().remove(detallecotizacionCollectionNewDetallecotizacion);
                        oldCotizacionOfDetallecotizacionCollectionNewDetallecotizacion = em.merge(oldCotizacionOfDetallecotizacionCollectionNewDetallecotizacion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cotizacion.getIdCotizacion();
                if (findCotizacion(id) == null) {
                    throw new NonexistentEntityException("The cotizacion with id " + id + " no longer exists.");
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
            Cotizacion cotizacion;
            try {
                cotizacion = em.getReference(Cotizacion.class, id);
                cotizacion.getIdCotizacion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cotizacion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Detallecotizacion> detallecotizacionCollectionOrphanCheck = cotizacion.getDetallecotizacionCollection();
            for (Detallecotizacion detallecotizacionCollectionOrphanCheckDetallecotizacion : detallecotizacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cotizacion (" + cotizacion + ") cannot be destroyed since the Detallecotizacion " + detallecotizacionCollectionOrphanCheckDetallecotizacion + " in its detallecotizacionCollection field has a non-nullable cotizacion field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(cotizacion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cotizacion> findCotizacionEntities() {
        return findCotizacionEntities(true, -1, -1);
    }

    public List<Cotizacion> findCotizacionEntities(int maxResults, int firstResult) {
        return findCotizacionEntities(false, maxResults, firstResult);
    }

    private List<Cotizacion> findCotizacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cotizacion.class));
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

    public Cotizacion findCotizacion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cotizacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getCotizacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cotizacion> rt = cq.from(Cotizacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
