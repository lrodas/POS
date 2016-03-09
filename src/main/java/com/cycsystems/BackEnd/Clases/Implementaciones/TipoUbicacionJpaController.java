/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases.Implementaciones;

import com.cycsystems.BackEnd.Clases.Entidades.TipoUbicacion;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.cycsystems.BackEnd.Clases.Entidades.Ubicacion;
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
public class TipoUbicacionJpaController implements Serializable {

    public TipoUbicacionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TipoUbicacion tipoUbicacion) {
        if (tipoUbicacion.getUbicacionCollection() == null) {
            tipoUbicacion.setUbicacionCollection(new ArrayList<Ubicacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Ubicacion> attachedUbicacionCollection = new ArrayList<Ubicacion>();
            for (Ubicacion ubicacionCollectionUbicacionToAttach : tipoUbicacion.getUbicacionCollection()) {
                ubicacionCollectionUbicacionToAttach = em.getReference(ubicacionCollectionUbicacionToAttach.getClass(), ubicacionCollectionUbicacionToAttach.getIdUbicacion());
                attachedUbicacionCollection.add(ubicacionCollectionUbicacionToAttach);
            }
            tipoUbicacion.setUbicacionCollection(attachedUbicacionCollection);
            em.persist(tipoUbicacion);
            for (Ubicacion ubicacionCollectionUbicacion : tipoUbicacion.getUbicacionCollection()) {
                TipoUbicacion oldTipoUbicacionOfUbicacionCollectionUbicacion = ubicacionCollectionUbicacion.getTipoUbicacion();
                ubicacionCollectionUbicacion.setTipoUbicacion(tipoUbicacion);
                ubicacionCollectionUbicacion = em.merge(ubicacionCollectionUbicacion);
                if (oldTipoUbicacionOfUbicacionCollectionUbicacion != null) {
                    oldTipoUbicacionOfUbicacionCollectionUbicacion.getUbicacionCollection().remove(ubicacionCollectionUbicacion);
                    oldTipoUbicacionOfUbicacionCollectionUbicacion = em.merge(oldTipoUbicacionOfUbicacionCollectionUbicacion);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TipoUbicacion tipoUbicacion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoUbicacion persistentTipoUbicacion = em.find(TipoUbicacion.class, tipoUbicacion.getId());
            Collection<Ubicacion> ubicacionCollectionOld = persistentTipoUbicacion.getUbicacionCollection();
            Collection<Ubicacion> ubicacionCollectionNew = tipoUbicacion.getUbicacionCollection();
            List<String> illegalOrphanMessages = null;
            for (Ubicacion ubicacionCollectionOldUbicacion : ubicacionCollectionOld) {
                if (!ubicacionCollectionNew.contains(ubicacionCollectionOldUbicacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ubicacion " + ubicacionCollectionOldUbicacion + " since its tipoUbicacion field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Ubicacion> attachedUbicacionCollectionNew = new ArrayList<Ubicacion>();
            for (Ubicacion ubicacionCollectionNewUbicacionToAttach : ubicacionCollectionNew) {
                ubicacionCollectionNewUbicacionToAttach = em.getReference(ubicacionCollectionNewUbicacionToAttach.getClass(), ubicacionCollectionNewUbicacionToAttach.getIdUbicacion());
                attachedUbicacionCollectionNew.add(ubicacionCollectionNewUbicacionToAttach);
            }
            ubicacionCollectionNew = attachedUbicacionCollectionNew;
            tipoUbicacion.setUbicacionCollection(ubicacionCollectionNew);
            tipoUbicacion = em.merge(tipoUbicacion);
            for (Ubicacion ubicacionCollectionNewUbicacion : ubicacionCollectionNew) {
                if (!ubicacionCollectionOld.contains(ubicacionCollectionNewUbicacion)) {
                    TipoUbicacion oldTipoUbicacionOfUbicacionCollectionNewUbicacion = ubicacionCollectionNewUbicacion.getTipoUbicacion();
                    ubicacionCollectionNewUbicacion.setTipoUbicacion(tipoUbicacion);
                    ubicacionCollectionNewUbicacion = em.merge(ubicacionCollectionNewUbicacion);
                    if (oldTipoUbicacionOfUbicacionCollectionNewUbicacion != null && !oldTipoUbicacionOfUbicacionCollectionNewUbicacion.equals(tipoUbicacion)) {
                        oldTipoUbicacionOfUbicacionCollectionNewUbicacion.getUbicacionCollection().remove(ubicacionCollectionNewUbicacion);
                        oldTipoUbicacionOfUbicacionCollectionNewUbicacion = em.merge(oldTipoUbicacionOfUbicacionCollectionNewUbicacion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tipoUbicacion.getId();
                if (findTipoUbicacion(id) == null) {
                    throw new NonexistentEntityException("The tipoUbicacion with id " + id + " no longer exists.");
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
            TipoUbicacion tipoUbicacion;
            try {
                tipoUbicacion = em.getReference(TipoUbicacion.class, id);
                tipoUbicacion.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipoUbicacion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Ubicacion> ubicacionCollectionOrphanCheck = tipoUbicacion.getUbicacionCollection();
            for (Ubicacion ubicacionCollectionOrphanCheckUbicacion : ubicacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TipoUbicacion (" + tipoUbicacion + ") cannot be destroyed since the Ubicacion " + ubicacionCollectionOrphanCheckUbicacion + " in its ubicacionCollection field has a non-nullable tipoUbicacion field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tipoUbicacion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TipoUbicacion> findTipoUbicacionEntities() {
        return findTipoUbicacionEntities(true, -1, -1);
    }

    public List<TipoUbicacion> findTipoUbicacionEntities(int maxResults, int firstResult) {
        return findTipoUbicacionEntities(false, maxResults, firstResult);
    }

    private List<TipoUbicacion> findTipoUbicacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TipoUbicacion.class));
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

    public TipoUbicacion findTipoUbicacion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TipoUbicacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getTipoUbicacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TipoUbicacion> rt = cq.from(TipoUbicacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
