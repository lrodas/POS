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
import com.cycsystems.BackEnd.Clases.Entidades.Bodega;
import com.cycsystems.BackEnd.Clases.Entidades.Licencia;
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
public class LicenciaJpaController implements Serializable {

    public LicenciaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Licencia licencia) throws PreexistingEntityException, Exception {
        if (licencia.getBodegaCollection() == null) {
            licencia.setBodegaCollection(new ArrayList<Bodega>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Bodega> attachedBodegaCollection = new ArrayList<Bodega>();
            for (Bodega bodegaCollectionBodegaToAttach : licencia.getBodegaCollection()) {
                bodegaCollectionBodegaToAttach = em.getReference(bodegaCollectionBodegaToAttach.getClass(), bodegaCollectionBodegaToAttach.getCodigo());
                attachedBodegaCollection.add(bodegaCollectionBodegaToAttach);
            }
            licencia.setBodegaCollection(attachedBodegaCollection);
            em.persist(licencia);
            for (Bodega bodegaCollectionBodega : licencia.getBodegaCollection()) {
                Licencia oldLicenciaOfBodegaCollectionBodega = bodegaCollectionBodega.getLicencia();
                bodegaCollectionBodega.setLicencia(licencia);
                bodegaCollectionBodega = em.merge(bodegaCollectionBodega);
                if (oldLicenciaOfBodegaCollectionBodega != null) {
                    oldLicenciaOfBodegaCollectionBodega.getBodegaCollection().remove(bodegaCollectionBodega);
                    oldLicenciaOfBodegaCollectionBodega = em.merge(oldLicenciaOfBodegaCollectionBodega);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findLicencia(licencia.getCodigo()) != null) {
                throw new PreexistingEntityException("Licencia " + licencia + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Licencia licencia) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Licencia persistentLicencia = em.find(Licencia.class, licencia.getCodigo());
            Collection<Bodega> bodegaCollectionOld = persistentLicencia.getBodegaCollection();
            Collection<Bodega> bodegaCollectionNew = licencia.getBodegaCollection();
            List<String> illegalOrphanMessages = null;
            for (Bodega bodegaCollectionOldBodega : bodegaCollectionOld) {
                if (!bodegaCollectionNew.contains(bodegaCollectionOldBodega)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Bodega " + bodegaCollectionOldBodega + " since its licencia field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Bodega> attachedBodegaCollectionNew = new ArrayList<Bodega>();
            for (Bodega bodegaCollectionNewBodegaToAttach : bodegaCollectionNew) {
                bodegaCollectionNewBodegaToAttach = em.getReference(bodegaCollectionNewBodegaToAttach.getClass(), bodegaCollectionNewBodegaToAttach.getCodigo());
                attachedBodegaCollectionNew.add(bodegaCollectionNewBodegaToAttach);
            }
            bodegaCollectionNew = attachedBodegaCollectionNew;
            licencia.setBodegaCollection(bodegaCollectionNew);
            licencia = em.merge(licencia);
            for (Bodega bodegaCollectionNewBodega : bodegaCollectionNew) {
                if (!bodegaCollectionOld.contains(bodegaCollectionNewBodega)) {
                    Licencia oldLicenciaOfBodegaCollectionNewBodega = bodegaCollectionNewBodega.getLicencia();
                    bodegaCollectionNewBodega.setLicencia(licencia);
                    bodegaCollectionNewBodega = em.merge(bodegaCollectionNewBodega);
                    if (oldLicenciaOfBodegaCollectionNewBodega != null && !oldLicenciaOfBodegaCollectionNewBodega.equals(licencia)) {
                        oldLicenciaOfBodegaCollectionNewBodega.getBodegaCollection().remove(bodegaCollectionNewBodega);
                        oldLicenciaOfBodegaCollectionNewBodega = em.merge(oldLicenciaOfBodegaCollectionNewBodega);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = licencia.getCodigo();
                if (findLicencia(id) == null) {
                    throw new NonexistentEntityException("The licencia with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Licencia licencia;
            try {
                licencia = em.getReference(Licencia.class, id);
                licencia.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The licencia with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Bodega> bodegaCollectionOrphanCheck = licencia.getBodegaCollection();
            for (Bodega bodegaCollectionOrphanCheckBodega : bodegaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Licencia (" + licencia + ") cannot be destroyed since the Bodega " + bodegaCollectionOrphanCheckBodega + " in its bodegaCollection field has a non-nullable licencia field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(licencia);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Licencia> findLicenciaEntities() {
        return findLicenciaEntities(true, -1, -1);
    }

    public List<Licencia> findLicenciaEntities(int maxResults, int firstResult) {
        return findLicenciaEntities(false, maxResults, firstResult);
    }

    private List<Licencia> findLicenciaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Licencia.class));
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

    public Licencia findLicencia(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Licencia.class, id);
        } finally {
            em.close();
        }
    }

    public int getLicenciaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Licencia> rt = cq.from(Licencia.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
