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
import com.cycsystems.BackEnd.Clases.Entidades.Detallepago;
import com.cycsystems.BackEnd.Clases.Entidades.Formapago;
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
public class FormapagoJpaController implements Serializable {

    public FormapagoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Formapago formapago) {
        if (formapago.getDetallepagoCollection() == null) {
            formapago.setDetallepagoCollection(new ArrayList<Detallepago>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Detallepago> attachedDetallepagoCollection = new ArrayList<Detallepago>();
            for (Detallepago detallepagoCollectionDetallepagoToAttach : formapago.getDetallepagoCollection()) {
                detallepagoCollectionDetallepagoToAttach = em.getReference(detallepagoCollectionDetallepagoToAttach.getClass(), detallepagoCollectionDetallepagoToAttach.getIdDetallePago());
                attachedDetallepagoCollection.add(detallepagoCollectionDetallepagoToAttach);
            }
            formapago.setDetallepagoCollection(attachedDetallepagoCollection);
            em.persist(formapago);
            for (Detallepago detallepagoCollectionDetallepago : formapago.getDetallepagoCollection()) {
                Formapago oldFormaPagoOfDetallepagoCollectionDetallepago = detallepagoCollectionDetallepago.getFormaPago();
                detallepagoCollectionDetallepago.setFormaPago(formapago);
                detallepagoCollectionDetallepago = em.merge(detallepagoCollectionDetallepago);
                if (oldFormaPagoOfDetallepagoCollectionDetallepago != null) {
                    oldFormaPagoOfDetallepagoCollectionDetallepago.getDetallepagoCollection().remove(detallepagoCollectionDetallepago);
                    oldFormaPagoOfDetallepagoCollectionDetallepago = em.merge(oldFormaPagoOfDetallepagoCollectionDetallepago);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Formapago formapago) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Formapago persistentFormapago = em.find(Formapago.class, formapago.getId());
            Collection<Detallepago> detallepagoCollectionOld = persistentFormapago.getDetallepagoCollection();
            Collection<Detallepago> detallepagoCollectionNew = formapago.getDetallepagoCollection();
            List<String> illegalOrphanMessages = null;
            for (Detallepago detallepagoCollectionOldDetallepago : detallepagoCollectionOld) {
                if (!detallepagoCollectionNew.contains(detallepagoCollectionOldDetallepago)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detallepago " + detallepagoCollectionOldDetallepago + " since its formaPago field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Detallepago> attachedDetallepagoCollectionNew = new ArrayList<Detallepago>();
            for (Detallepago detallepagoCollectionNewDetallepagoToAttach : detallepagoCollectionNew) {
                detallepagoCollectionNewDetallepagoToAttach = em.getReference(detallepagoCollectionNewDetallepagoToAttach.getClass(), detallepagoCollectionNewDetallepagoToAttach.getIdDetallePago());
                attachedDetallepagoCollectionNew.add(detallepagoCollectionNewDetallepagoToAttach);
            }
            detallepagoCollectionNew = attachedDetallepagoCollectionNew;
            formapago.setDetallepagoCollection(detallepagoCollectionNew);
            formapago = em.merge(formapago);
            for (Detallepago detallepagoCollectionNewDetallepago : detallepagoCollectionNew) {
                if (!detallepagoCollectionOld.contains(detallepagoCollectionNewDetallepago)) {
                    Formapago oldFormaPagoOfDetallepagoCollectionNewDetallepago = detallepagoCollectionNewDetallepago.getFormaPago();
                    detallepagoCollectionNewDetallepago.setFormaPago(formapago);
                    detallepagoCollectionNewDetallepago = em.merge(detallepagoCollectionNewDetallepago);
                    if (oldFormaPagoOfDetallepagoCollectionNewDetallepago != null && !oldFormaPagoOfDetallepagoCollectionNewDetallepago.equals(formapago)) {
                        oldFormaPagoOfDetallepagoCollectionNewDetallepago.getDetallepagoCollection().remove(detallepagoCollectionNewDetallepago);
                        oldFormaPagoOfDetallepagoCollectionNewDetallepago = em.merge(oldFormaPagoOfDetallepagoCollectionNewDetallepago);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = formapago.getId();
                if (findFormapago(id) == null) {
                    throw new NonexistentEntityException("The formapago with id " + id + " no longer exists.");
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
            Formapago formapago;
            try {
                formapago = em.getReference(Formapago.class, id);
                formapago.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The formapago with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Detallepago> detallepagoCollectionOrphanCheck = formapago.getDetallepagoCollection();
            for (Detallepago detallepagoCollectionOrphanCheckDetallepago : detallepagoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Formapago (" + formapago + ") cannot be destroyed since the Detallepago " + detallepagoCollectionOrphanCheckDetallepago + " in its detallepagoCollection field has a non-nullable formaPago field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(formapago);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Formapago> findFormapagoEntities() {
        return findFormapagoEntities(true, -1, -1);
    }

    public List<Formapago> findFormapagoEntities(int maxResults, int firstResult) {
        return findFormapagoEntities(false, maxResults, firstResult);
    }

    private List<Formapago> findFormapagoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Formapago.class));
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

    public Formapago findFormapago(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Formapago.class, id);
        } finally {
            em.close();
        }
    }

    public int getFormapagoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Formapago> rt = cq.from(Formapago.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
