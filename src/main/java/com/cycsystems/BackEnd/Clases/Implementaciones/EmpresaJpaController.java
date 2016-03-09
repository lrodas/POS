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
import com.cycsystems.BackEnd.Clases.Entidades.Empresa;
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
public class EmpresaJpaController implements Serializable {

    public EmpresaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Empresa empresa) {
        if (empresa.getBodegaCollection() == null) {
            empresa.setBodegaCollection(new ArrayList<Bodega>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Bodega> attachedBodegaCollection = new ArrayList<Bodega>();
            for (Bodega bodegaCollectionBodegaToAttach : empresa.getBodegaCollection()) {
                bodegaCollectionBodegaToAttach = em.getReference(bodegaCollectionBodegaToAttach.getClass(), bodegaCollectionBodegaToAttach.getCodigo());
                attachedBodegaCollection.add(bodegaCollectionBodegaToAttach);
            }
            empresa.setBodegaCollection(attachedBodegaCollection);
            em.persist(empresa);
            for (Bodega bodegaCollectionBodega : empresa.getBodegaCollection()) {
                Empresa oldEmpresaOfBodegaCollectionBodega = bodegaCollectionBodega.getEmpresa();
                bodegaCollectionBodega.setEmpresa(empresa);
                bodegaCollectionBodega = em.merge(bodegaCollectionBodega);
                if (oldEmpresaOfBodegaCollectionBodega != null) {
                    oldEmpresaOfBodegaCollectionBodega.getBodegaCollection().remove(bodegaCollectionBodega);
                    oldEmpresaOfBodegaCollectionBodega = em.merge(oldEmpresaOfBodegaCollectionBodega);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Empresa empresa) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empresa persistentEmpresa = em.find(Empresa.class, empresa.getCodigo());
            Collection<Bodega> bodegaCollectionOld = persistentEmpresa.getBodegaCollection();
            Collection<Bodega> bodegaCollectionNew = empresa.getBodegaCollection();
            List<String> illegalOrphanMessages = null;
            for (Bodega bodegaCollectionOldBodega : bodegaCollectionOld) {
                if (!bodegaCollectionNew.contains(bodegaCollectionOldBodega)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Bodega " + bodegaCollectionOldBodega + " since its empresa field is not nullable.");
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
            empresa.setBodegaCollection(bodegaCollectionNew);
            empresa = em.merge(empresa);
            for (Bodega bodegaCollectionNewBodega : bodegaCollectionNew) {
                if (!bodegaCollectionOld.contains(bodegaCollectionNewBodega)) {
                    Empresa oldEmpresaOfBodegaCollectionNewBodega = bodegaCollectionNewBodega.getEmpresa();
                    bodegaCollectionNewBodega.setEmpresa(empresa);
                    bodegaCollectionNewBodega = em.merge(bodegaCollectionNewBodega);
                    if (oldEmpresaOfBodegaCollectionNewBodega != null && !oldEmpresaOfBodegaCollectionNewBodega.equals(empresa)) {
                        oldEmpresaOfBodegaCollectionNewBodega.getBodegaCollection().remove(bodegaCollectionNewBodega);
                        oldEmpresaOfBodegaCollectionNewBodega = em.merge(oldEmpresaOfBodegaCollectionNewBodega);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = empresa.getCodigo();
                if (findEmpresa(id) == null) {
                    throw new NonexistentEntityException("The empresa with id " + id + " no longer exists.");
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
            Empresa empresa;
            try {
                empresa = em.getReference(Empresa.class, id);
                empresa.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The empresa with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Bodega> bodegaCollectionOrphanCheck = empresa.getBodegaCollection();
            for (Bodega bodegaCollectionOrphanCheckBodega : bodegaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Empresa (" + empresa + ") cannot be destroyed since the Bodega " + bodegaCollectionOrphanCheckBodega + " in its bodegaCollection field has a non-nullable empresa field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(empresa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Empresa> findEmpresaEntities() {
        return findEmpresaEntities(true, -1, -1);
    }

    public List<Empresa> findEmpresaEntities(int maxResults, int firstResult) {
        return findEmpresaEntities(false, maxResults, firstResult);
    }

    private List<Empresa> findEmpresaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Empresa.class));
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

    public Empresa findEmpresa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Empresa.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmpresaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Empresa> rt = cq.from(Empresa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
