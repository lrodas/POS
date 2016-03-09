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
import com.cycsystems.BackEnd.Clases.Entidades.Producto;
import com.cycsystems.BackEnd.Clases.Entidades.TipoUbicacion;
import com.cycsystems.BackEnd.Clases.Entidades.Detalle;
import com.cycsystems.BackEnd.Clases.Entidades.Ubicacion;
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
public class UbicacionJpaController implements Serializable {

    public UbicacionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ubicacion ubicacion) throws PreexistingEntityException, Exception {
        if (ubicacion.getDetalleCollection() == null) {
            ubicacion.setDetalleCollection(new ArrayList<Detalle>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Bodega bodega = ubicacion.getBodega();
            if (bodega != null) {
                bodega = em.getReference(bodega.getClass(), bodega.getCodigo());
                ubicacion.setBodega(bodega);
            }
            Producto producto = ubicacion.getProducto();
            if (producto != null) {
                producto = em.getReference(producto.getClass(), producto.getCodigo());
                ubicacion.setProducto(producto);
            }
            TipoUbicacion tipoUbicacion = ubicacion.getTipoUbicacion();
            if (tipoUbicacion != null) {
                tipoUbicacion = em.getReference(tipoUbicacion.getClass(), tipoUbicacion.getId());
                ubicacion.setTipoUbicacion(tipoUbicacion);
            }
            Collection<Detalle> attachedDetalleCollection = new ArrayList<Detalle>();
            for (Detalle detalleCollectionDetalleToAttach : ubicacion.getDetalleCollection()) {
                detalleCollectionDetalleToAttach = em.getReference(detalleCollectionDetalleToAttach.getClass(), detalleCollectionDetalleToAttach.getDetallePK());
                attachedDetalleCollection.add(detalleCollectionDetalleToAttach);
            }
            ubicacion.setDetalleCollection(attachedDetalleCollection);
            em.persist(ubicacion);
            if (bodega != null) {
                bodega.getUbicacionCollection().add(ubicacion);
                bodega = em.merge(bodega);
            }
            if (producto != null) {
                producto.getUbicacionCollection().add(ubicacion);
                producto = em.merge(producto);
            }
            if (tipoUbicacion != null) {
                tipoUbicacion.getUbicacionCollection().add(ubicacion);
                tipoUbicacion = em.merge(tipoUbicacion);
            }
            for (Detalle detalleCollectionDetalle : ubicacion.getDetalleCollection()) {
                Ubicacion oldBodegaOfDetalleCollectionDetalle = detalleCollectionDetalle.getBodega();
                detalleCollectionDetalle.setBodega(ubicacion);
                detalleCollectionDetalle = em.merge(detalleCollectionDetalle);
                if (oldBodegaOfDetalleCollectionDetalle != null) {
                    oldBodegaOfDetalleCollectionDetalle.getDetalleCollection().remove(detalleCollectionDetalle);
                    oldBodegaOfDetalleCollectionDetalle = em.merge(oldBodegaOfDetalleCollectionDetalle);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUbicacion(ubicacion.getIdUbicacion()) != null) {
                throw new PreexistingEntityException("Ubicacion " + ubicacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ubicacion ubicacion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ubicacion persistentUbicacion = em.find(Ubicacion.class, ubicacion.getIdUbicacion());
            Bodega bodegaOld = persistentUbicacion.getBodega();
            Bodega bodegaNew = ubicacion.getBodega();
            Producto productoOld = persistentUbicacion.getProducto();
            Producto productoNew = ubicacion.getProducto();
            TipoUbicacion tipoUbicacionOld = persistentUbicacion.getTipoUbicacion();
            TipoUbicacion tipoUbicacionNew = ubicacion.getTipoUbicacion();
            Collection<Detalle> detalleCollectionOld = persistentUbicacion.getDetalleCollection();
            Collection<Detalle> detalleCollectionNew = ubicacion.getDetalleCollection();
            List<String> illegalOrphanMessages = null;
            for (Detalle detalleCollectionOldDetalle : detalleCollectionOld) {
                if (!detalleCollectionNew.contains(detalleCollectionOldDetalle)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detalle " + detalleCollectionOldDetalle + " since its bodega field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (bodegaNew != null) {
                bodegaNew = em.getReference(bodegaNew.getClass(), bodegaNew.getCodigo());
                ubicacion.setBodega(bodegaNew);
            }
            if (productoNew != null) {
                productoNew = em.getReference(productoNew.getClass(), productoNew.getCodigo());
                ubicacion.setProducto(productoNew);
            }
            if (tipoUbicacionNew != null) {
                tipoUbicacionNew = em.getReference(tipoUbicacionNew.getClass(), tipoUbicacionNew.getId());
                ubicacion.setTipoUbicacion(tipoUbicacionNew);
            }
            Collection<Detalle> attachedDetalleCollectionNew = new ArrayList<Detalle>();
            for (Detalle detalleCollectionNewDetalleToAttach : detalleCollectionNew) {
                detalleCollectionNewDetalleToAttach = em.getReference(detalleCollectionNewDetalleToAttach.getClass(), detalleCollectionNewDetalleToAttach.getDetallePK());
                attachedDetalleCollectionNew.add(detalleCollectionNewDetalleToAttach);
            }
            detalleCollectionNew = attachedDetalleCollectionNew;
            ubicacion.setDetalleCollection(detalleCollectionNew);
            ubicacion = em.merge(ubicacion);
            if (bodegaOld != null && !bodegaOld.equals(bodegaNew)) {
                bodegaOld.getUbicacionCollection().remove(ubicacion);
                bodegaOld = em.merge(bodegaOld);
            }
            if (bodegaNew != null && !bodegaNew.equals(bodegaOld)) {
                bodegaNew.getUbicacionCollection().add(ubicacion);
                bodegaNew = em.merge(bodegaNew);
            }
            if (productoOld != null && !productoOld.equals(productoNew)) {
                productoOld.getUbicacionCollection().remove(ubicacion);
                productoOld = em.merge(productoOld);
            }
            if (productoNew != null && !productoNew.equals(productoOld)) {
                productoNew.getUbicacionCollection().add(ubicacion);
                productoNew = em.merge(productoNew);
            }
            if (tipoUbicacionOld != null && !tipoUbicacionOld.equals(tipoUbicacionNew)) {
                tipoUbicacionOld.getUbicacionCollection().remove(ubicacion);
                tipoUbicacionOld = em.merge(tipoUbicacionOld);
            }
            if (tipoUbicacionNew != null && !tipoUbicacionNew.equals(tipoUbicacionOld)) {
                tipoUbicacionNew.getUbicacionCollection().add(ubicacion);
                tipoUbicacionNew = em.merge(tipoUbicacionNew);
            }
            for (Detalle detalleCollectionNewDetalle : detalleCollectionNew) {
                if (!detalleCollectionOld.contains(detalleCollectionNewDetalle)) {
                    Ubicacion oldBodegaOfDetalleCollectionNewDetalle = detalleCollectionNewDetalle.getBodega();
                    detalleCollectionNewDetalle.setBodega(ubicacion);
                    detalleCollectionNewDetalle = em.merge(detalleCollectionNewDetalle);
                    if (oldBodegaOfDetalleCollectionNewDetalle != null && !oldBodegaOfDetalleCollectionNewDetalle.equals(ubicacion)) {
                        oldBodegaOfDetalleCollectionNewDetalle.getDetalleCollection().remove(detalleCollectionNewDetalle);
                        oldBodegaOfDetalleCollectionNewDetalle = em.merge(oldBodegaOfDetalleCollectionNewDetalle);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ubicacion.getIdUbicacion();
                if (findUbicacion(id) == null) {
                    throw new NonexistentEntityException("The ubicacion with id " + id + " no longer exists.");
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
            Ubicacion ubicacion;
            try {
                ubicacion = em.getReference(Ubicacion.class, id);
                ubicacion.getIdUbicacion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ubicacion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Detalle> detalleCollectionOrphanCheck = ubicacion.getDetalleCollection();
            for (Detalle detalleCollectionOrphanCheckDetalle : detalleCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ubicacion (" + ubicacion + ") cannot be destroyed since the Detalle " + detalleCollectionOrphanCheckDetalle + " in its detalleCollection field has a non-nullable bodega field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Bodega bodega = ubicacion.getBodega();
            if (bodega != null) {
                bodega.getUbicacionCollection().remove(ubicacion);
                bodega = em.merge(bodega);
            }
            Producto producto = ubicacion.getProducto();
            if (producto != null) {
                producto.getUbicacionCollection().remove(ubicacion);
                producto = em.merge(producto);
            }
            TipoUbicacion tipoUbicacion = ubicacion.getTipoUbicacion();
            if (tipoUbicacion != null) {
                tipoUbicacion.getUbicacionCollection().remove(ubicacion);
                tipoUbicacion = em.merge(tipoUbicacion);
            }
            em.remove(ubicacion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ubicacion> findUbicacionEntities() {
        return findUbicacionEntities(true, -1, -1);
    }

    public List<Ubicacion> findUbicacionEntities(int maxResults, int firstResult) {
        return findUbicacionEntities(false, maxResults, firstResult);
    }

    private List<Ubicacion> findUbicacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ubicacion.class));
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

    public Ubicacion findUbicacion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ubicacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getUbicacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ubicacion> rt = cq.from(Ubicacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
