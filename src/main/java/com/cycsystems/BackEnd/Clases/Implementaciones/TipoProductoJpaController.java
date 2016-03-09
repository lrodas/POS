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
import com.cycsystems.BackEnd.Clases.Entidades.Producto;
import com.cycsystems.BackEnd.Clases.Entidades.TipoProducto;
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
public class TipoProductoJpaController implements Serializable {

    public TipoProductoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TipoProducto tipoProducto) throws PreexistingEntityException, Exception {
        if (tipoProducto.getProductoCollection() == null) {
            tipoProducto.setProductoCollection(new ArrayList<Producto>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Producto> attachedProductoCollection = new ArrayList<Producto>();
            for (Producto productoCollectionProductoToAttach : tipoProducto.getProductoCollection()) {
                productoCollectionProductoToAttach = em.getReference(productoCollectionProductoToAttach.getClass(), productoCollectionProductoToAttach.getCodigo());
                attachedProductoCollection.add(productoCollectionProductoToAttach);
            }
            tipoProducto.setProductoCollection(attachedProductoCollection);
            em.persist(tipoProducto);
            for (Producto productoCollectionProducto : tipoProducto.getProductoCollection()) {
                TipoProducto oldTipoProductoOfProductoCollectionProducto = productoCollectionProducto.getTipoProducto();
                productoCollectionProducto.setTipoProducto(tipoProducto);
                productoCollectionProducto = em.merge(productoCollectionProducto);
                if (oldTipoProductoOfProductoCollectionProducto != null) {
                    oldTipoProductoOfProductoCollectionProducto.getProductoCollection().remove(productoCollectionProducto);
                    oldTipoProductoOfProductoCollectionProducto = em.merge(oldTipoProductoOfProductoCollectionProducto);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTipoProducto(tipoProducto.getIdTipoProducto()) != null) {
                throw new PreexistingEntityException("TipoProducto " + tipoProducto + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TipoProducto tipoProducto) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoProducto persistentTipoProducto = em.find(TipoProducto.class, tipoProducto.getIdTipoProducto());
            Collection<Producto> productoCollectionOld = persistentTipoProducto.getProductoCollection();
            Collection<Producto> productoCollectionNew = tipoProducto.getProductoCollection();
            List<String> illegalOrphanMessages = null;
            for (Producto productoCollectionOldProducto : productoCollectionOld) {
                if (!productoCollectionNew.contains(productoCollectionOldProducto)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Producto " + productoCollectionOldProducto + " since its tipoProducto field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Producto> attachedProductoCollectionNew = new ArrayList<Producto>();
            for (Producto productoCollectionNewProductoToAttach : productoCollectionNew) {
                productoCollectionNewProductoToAttach = em.getReference(productoCollectionNewProductoToAttach.getClass(), productoCollectionNewProductoToAttach.getCodigo());
                attachedProductoCollectionNew.add(productoCollectionNewProductoToAttach);
            }
            productoCollectionNew = attachedProductoCollectionNew;
            tipoProducto.setProductoCollection(productoCollectionNew);
            tipoProducto = em.merge(tipoProducto);
            for (Producto productoCollectionNewProducto : productoCollectionNew) {
                if (!productoCollectionOld.contains(productoCollectionNewProducto)) {
                    TipoProducto oldTipoProductoOfProductoCollectionNewProducto = productoCollectionNewProducto.getTipoProducto();
                    productoCollectionNewProducto.setTipoProducto(tipoProducto);
                    productoCollectionNewProducto = em.merge(productoCollectionNewProducto);
                    if (oldTipoProductoOfProductoCollectionNewProducto != null && !oldTipoProductoOfProductoCollectionNewProducto.equals(tipoProducto)) {
                        oldTipoProductoOfProductoCollectionNewProducto.getProductoCollection().remove(productoCollectionNewProducto);
                        oldTipoProductoOfProductoCollectionNewProducto = em.merge(oldTipoProductoOfProductoCollectionNewProducto);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tipoProducto.getIdTipoProducto();
                if (findTipoProducto(id) == null) {
                    throw new NonexistentEntityException("The tipoProducto with id " + id + " no longer exists.");
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
            TipoProducto tipoProducto;
            try {
                tipoProducto = em.getReference(TipoProducto.class, id);
                tipoProducto.getIdTipoProducto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipoProducto with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Producto> productoCollectionOrphanCheck = tipoProducto.getProductoCollection();
            for (Producto productoCollectionOrphanCheckProducto : productoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TipoProducto (" + tipoProducto + ") cannot be destroyed since the Producto " + productoCollectionOrphanCheckProducto + " in its productoCollection field has a non-nullable tipoProducto field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tipoProducto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TipoProducto> findTipoProductoEntities() {
        return findTipoProductoEntities(true, -1, -1);
    }

    public List<TipoProducto> findTipoProductoEntities(int maxResults, int firstResult) {
        return findTipoProductoEntities(false, maxResults, firstResult);
    }

    private List<TipoProducto> findTipoProductoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TipoProducto.class));
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

    public TipoProducto findTipoProducto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TipoProducto.class, id);
        } finally {
            em.close();
        }
    }

    public int getTipoProductoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TipoProducto> rt = cq.from(TipoProducto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
