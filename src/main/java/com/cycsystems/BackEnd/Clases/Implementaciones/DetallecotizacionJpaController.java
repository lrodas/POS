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
import com.cycsystems.BackEnd.Clases.Entidades.Cotizacion;
import com.cycsystems.BackEnd.Clases.Entidades.Detallecotizacion;
import com.cycsystems.BackEnd.Clases.Entidades.DetallecotizacionPK;
import com.cycsystems.BackEnd.Clases.Entidades.Producto;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.NonexistentEntityException;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author angel
 */
public class DetallecotizacionJpaController implements Serializable {

    public DetallecotizacionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Detallecotizacion detallecotizacion) throws PreexistingEntityException, Exception {
        if (detallecotizacion.getDetallecotizacionPK() == null) {
            detallecotizacion.setDetallecotizacionPK(new DetallecotizacionPK());
        }
        detallecotizacion.getDetallecotizacionPK().setIdCotizacion(detallecotizacion.getCotizacion().getIdCotizacion());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Bodega bodega = detallecotizacion.getBodega();
            if (bodega != null) {
                bodega = em.getReference(bodega.getClass(), bodega.getCodigo());
                detallecotizacion.setBodega(bodega);
            }
            Cotizacion cotizacion = detallecotizacion.getCotizacion();
            if (cotizacion != null) {
                cotizacion = em.getReference(cotizacion.getClass(), cotizacion.getIdCotizacion());
                detallecotizacion.setCotizacion(cotizacion);
            }
            Producto producto = detallecotizacion.getProducto();
            if (producto != null) {
                producto = em.getReference(producto.getClass(), producto.getCodigo());
                detallecotizacion.setProducto(producto);
            }
            em.persist(detallecotizacion);
            if (bodega != null) {
                bodega.getDetallecotizacionCollection().add(detallecotizacion);
                bodega = em.merge(bodega);
            }
            if (cotizacion != null) {
                cotizacion.getDetallecotizacionCollection().add(detallecotizacion);
                cotizacion = em.merge(cotizacion);
            }
            if (producto != null) {
                producto.getDetallecotizacionCollection().add(detallecotizacion);
                producto = em.merge(producto);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDetallecotizacion(detallecotizacion.getDetallecotizacionPK()) != null) {
                throw new PreexistingEntityException("Detallecotizacion " + detallecotizacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Detallecotizacion detallecotizacion) throws NonexistentEntityException, Exception {
        detallecotizacion.getDetallecotizacionPK().setIdCotizacion(detallecotizacion.getCotizacion().getIdCotizacion());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detallecotizacion persistentDetallecotizacion = em.find(Detallecotizacion.class, detallecotizacion.getDetallecotizacionPK());
            Bodega bodegaOld = persistentDetallecotizacion.getBodega();
            Bodega bodegaNew = detallecotizacion.getBodega();
            Cotizacion cotizacionOld = persistentDetallecotizacion.getCotizacion();
            Cotizacion cotizacionNew = detallecotizacion.getCotizacion();
            Producto productoOld = persistentDetallecotizacion.getProducto();
            Producto productoNew = detallecotizacion.getProducto();
            if (bodegaNew != null) {
                bodegaNew = em.getReference(bodegaNew.getClass(), bodegaNew.getCodigo());
                detallecotizacion.setBodega(bodegaNew);
            }
            if (cotizacionNew != null) {
                cotizacionNew = em.getReference(cotizacionNew.getClass(), cotizacionNew.getIdCotizacion());
                detallecotizacion.setCotizacion(cotizacionNew);
            }
            if (productoNew != null) {
                productoNew = em.getReference(productoNew.getClass(), productoNew.getCodigo());
                detallecotizacion.setProducto(productoNew);
            }
            detallecotizacion = em.merge(detallecotizacion);
            if (bodegaOld != null && !bodegaOld.equals(bodegaNew)) {
                bodegaOld.getDetallecotizacionCollection().remove(detallecotizacion);
                bodegaOld = em.merge(bodegaOld);
            }
            if (bodegaNew != null && !bodegaNew.equals(bodegaOld)) {
                bodegaNew.getDetallecotizacionCollection().add(detallecotizacion);
                bodegaNew = em.merge(bodegaNew);
            }
            if (cotizacionOld != null && !cotizacionOld.equals(cotizacionNew)) {
                cotizacionOld.getDetallecotizacionCollection().remove(detallecotizacion);
                cotizacionOld = em.merge(cotizacionOld);
            }
            if (cotizacionNew != null && !cotizacionNew.equals(cotizacionOld)) {
                cotizacionNew.getDetallecotizacionCollection().add(detallecotizacion);
                cotizacionNew = em.merge(cotizacionNew);
            }
            if (productoOld != null && !productoOld.equals(productoNew)) {
                productoOld.getDetallecotizacionCollection().remove(detallecotizacion);
                productoOld = em.merge(productoOld);
            }
            if (productoNew != null && !productoNew.equals(productoOld)) {
                productoNew.getDetallecotizacionCollection().add(detallecotizacion);
                productoNew = em.merge(productoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                DetallecotizacionPK id = detallecotizacion.getDetallecotizacionPK();
                if (findDetallecotizacion(id) == null) {
                    throw new NonexistentEntityException("The detallecotizacion with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(DetallecotizacionPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detallecotizacion detallecotizacion;
            try {
                detallecotizacion = em.getReference(Detallecotizacion.class, id);
                detallecotizacion.getDetallecotizacionPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detallecotizacion with id " + id + " no longer exists.", enfe);
            }
            Bodega bodega = detallecotizacion.getBodega();
            if (bodega != null) {
                bodega.getDetallecotizacionCollection().remove(detallecotizacion);
                bodega = em.merge(bodega);
            }
            Cotizacion cotizacion = detallecotizacion.getCotizacion();
            if (cotizacion != null) {
                cotizacion.getDetallecotizacionCollection().remove(detallecotizacion);
                cotizacion = em.merge(cotizacion);
            }
            Producto producto = detallecotizacion.getProducto();
            if (producto != null) {
                producto.getDetallecotizacionCollection().remove(detallecotizacion);
                producto = em.merge(producto);
            }
            em.remove(detallecotizacion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Detallecotizacion> findDetallecotizacionEntities() {
        return findDetallecotizacionEntities(true, -1, -1);
    }

    public List<Detallecotizacion> findDetallecotizacionEntities(int maxResults, int firstResult) {
        return findDetallecotizacionEntities(false, maxResults, firstResult);
    }

    private List<Detallecotizacion> findDetallecotizacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Detallecotizacion.class));
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

    public Detallecotizacion findDetallecotizacion(DetallecotizacionPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Detallecotizacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetallecotizacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Detallecotizacion> rt = cq.from(Detallecotizacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
