/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases.Implementaciones;

import com.cycsystems.BackEnd.Clases.Entidades.Detalle;
import com.cycsystems.BackEnd.Clases.Entidades.DetallePK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.cycsystems.BackEnd.Clases.Entidades.Facturacion;
import com.cycsystems.BackEnd.Clases.Entidades.Ubicacion;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.NonexistentEntityException;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author angel
 */
public class DetalleJpaController implements Serializable {

    public DetalleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Detalle detalle) throws PreexistingEntityException, Exception {
        if (detalle.getDetallePK() == null) {
            detalle.setDetallePK(new DetallePK());
        }
        detalle.getDetallePK().setCorrelativoFactura(detalle.getFacturacion().getFacturacionPK().getCorrelativo());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Facturacion facturacion = detalle.getFacturacion();
            if (facturacion != null) {
                facturacion = em.getReference(facturacion.getClass(), facturacion.getFacturacionPK());
                detalle.setFacturacion(facturacion);
            }
            Ubicacion bodega = detalle.getBodega();
            if (bodega != null) {
                bodega = em.getReference(bodega.getClass(), bodega.getIdUbicacion());
                detalle.setBodega(bodega);
            }
            em.persist(detalle);
            if (facturacion != null) {
                facturacion.getDetalleCollection().add(detalle);
                facturacion = em.merge(facturacion);
            }
            if (bodega != null) {
                bodega.getDetalleCollection().add(detalle);
                bodega = em.merge(bodega);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDetalle(detalle.getDetallePK()) != null) {
                throw new PreexistingEntityException("Detalle " + detalle + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Detalle detalle) throws NonexistentEntityException, Exception {
        detalle.getDetallePK().setCorrelativoFactura(detalle.getFacturacion().getFacturacionPK().getCorrelativo());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detalle persistentDetalle = em.find(Detalle.class, detalle.getDetallePK());
            Facturacion facturacionOld = persistentDetalle.getFacturacion();
            Facturacion facturacionNew = detalle.getFacturacion();
            Ubicacion bodegaOld = persistentDetalle.getBodega();
            Ubicacion bodegaNew = detalle.getBodega();
            if (facturacionNew != null) {
                facturacionNew = em.getReference(facturacionNew.getClass(), facturacionNew.getFacturacionPK());
                detalle.setFacturacion(facturacionNew);
            }
            if (bodegaNew != null) {
                bodegaNew = em.getReference(bodegaNew.getClass(), bodegaNew.getIdUbicacion());
                detalle.setBodega(bodegaNew);
            }
            detalle = em.merge(detalle);
            if (facturacionOld != null && !facturacionOld.equals(facturacionNew)) {
                facturacionOld.getDetalleCollection().remove(detalle);
                facturacionOld = em.merge(facturacionOld);
            }
            if (facturacionNew != null && !facturacionNew.equals(facturacionOld)) {
                facturacionNew.getDetalleCollection().add(detalle);
                facturacionNew = em.merge(facturacionNew);
            }
            if (bodegaOld != null && !bodegaOld.equals(bodegaNew)) {
                bodegaOld.getDetalleCollection().remove(detalle);
                bodegaOld = em.merge(bodegaOld);
            }
            if (bodegaNew != null && !bodegaNew.equals(bodegaOld)) {
                bodegaNew.getDetalleCollection().add(detalle);
                bodegaNew = em.merge(bodegaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                DetallePK id = detalle.getDetallePK();
                if (findDetalle(id) == null) {
                    throw new NonexistentEntityException("The detalle with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(DetallePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detalle detalle;
            try {
                detalle = em.getReference(Detalle.class, id);
                detalle.getDetallePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalle with id " + id + " no longer exists.", enfe);
            }
            Facturacion facturacion = detalle.getFacturacion();
            if (facturacion != null) {
                facturacion.getDetalleCollection().remove(detalle);
                facturacion = em.merge(facturacion);
            }
            Ubicacion bodega = detalle.getBodega();
            if (bodega != null) {
                bodega.getDetalleCollection().remove(detalle);
                bodega = em.merge(bodega);
            }
            em.remove(detalle);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Detalle> findDetalleEntities() {
        return findDetalleEntities(true, -1, -1);
    }

    public List<Detalle> findDetalleEntities(int maxResults, int firstResult) {
        return findDetalleEntities(false, maxResults, firstResult);
    }

    private List<Detalle> findDetalleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Detalle.class));
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

    public Detalle findDetalle(DetallePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Detalle.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Detalle> rt = cq.from(Detalle.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
