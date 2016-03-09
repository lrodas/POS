/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases.Implementaciones;

import com.cycsystems.BackEnd.Clases.Entidades.Detallepago;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.cycsystems.BackEnd.Clases.Entidades.Formapago;
import com.cycsystems.BackEnd.Clases.Entidades.Facturacion;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author angel
 */
public class DetallepagoJpaController implements Serializable {

    public DetallepagoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Detallepago detallepago) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Formapago formaPago = detallepago.getFormaPago();
            if (formaPago != null) {
                formaPago = em.getReference(formaPago.getClass(), formaPago.getId());
                detallepago.setFormaPago(formaPago);
            }
            Facturacion facturaCorrelativo = detallepago.getFacturaCorrelativo();
            if (facturaCorrelativo != null) {
                facturaCorrelativo = em.getReference(facturaCorrelativo.getClass(), facturaCorrelativo.getFacturacionPK());
                detallepago.setFacturaCorrelativo(facturaCorrelativo);
            }
            em.persist(detallepago);
            if (formaPago != null) {
                formaPago.getDetallepagoCollection().add(detallepago);
                formaPago = em.merge(formaPago);
            }
            if (facturaCorrelativo != null) {
                facturaCorrelativo.getDetallepagoCollection().add(detallepago);
                facturaCorrelativo = em.merge(facturaCorrelativo);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Detallepago detallepago) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detallepago persistentDetallepago = em.find(Detallepago.class, detallepago.getIdDetallePago());
            Formapago formaPagoOld = persistentDetallepago.getFormaPago();
            Formapago formaPagoNew = detallepago.getFormaPago();
            Facturacion facturaCorrelativoOld = persistentDetallepago.getFacturaCorrelativo();
            Facturacion facturaCorrelativoNew = detallepago.getFacturaCorrelativo();
            if (formaPagoNew != null) {
                formaPagoNew = em.getReference(formaPagoNew.getClass(), formaPagoNew.getId());
                detallepago.setFormaPago(formaPagoNew);
            }
            if (facturaCorrelativoNew != null) {
                facturaCorrelativoNew = em.getReference(facturaCorrelativoNew.getClass(), facturaCorrelativoNew.getFacturacionPK());
                detallepago.setFacturaCorrelativo(facturaCorrelativoNew);
            }
            detallepago = em.merge(detallepago);
            if (formaPagoOld != null && !formaPagoOld.equals(formaPagoNew)) {
                formaPagoOld.getDetallepagoCollection().remove(detallepago);
                formaPagoOld = em.merge(formaPagoOld);
            }
            if (formaPagoNew != null && !formaPagoNew.equals(formaPagoOld)) {
                formaPagoNew.getDetallepagoCollection().add(detallepago);
                formaPagoNew = em.merge(formaPagoNew);
            }
            if (facturaCorrelativoOld != null && !facturaCorrelativoOld.equals(facturaCorrelativoNew)) {
                facturaCorrelativoOld.getDetallepagoCollection().remove(detallepago);
                facturaCorrelativoOld = em.merge(facturaCorrelativoOld);
            }
            if (facturaCorrelativoNew != null && !facturaCorrelativoNew.equals(facturaCorrelativoOld)) {
                facturaCorrelativoNew.getDetallepagoCollection().add(detallepago);
                facturaCorrelativoNew = em.merge(facturaCorrelativoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = detallepago.getIdDetallePago();
                if (findDetallepago(id) == null) {
                    throw new NonexistentEntityException("The detallepago with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detallepago detallepago;
            try {
                detallepago = em.getReference(Detallepago.class, id);
                detallepago.getIdDetallePago();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detallepago with id " + id + " no longer exists.", enfe);
            }
            Formapago formaPago = detallepago.getFormaPago();
            if (formaPago != null) {
                formaPago.getDetallepagoCollection().remove(detallepago);
                formaPago = em.merge(formaPago);
            }
            Facturacion facturaCorrelativo = detallepago.getFacturaCorrelativo();
            if (facturaCorrelativo != null) {
                facturaCorrelativo.getDetallepagoCollection().remove(detallepago);
                facturaCorrelativo = em.merge(facturaCorrelativo);
            }
            em.remove(detallepago);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Detallepago> findDetallepagoEntities() {
        return findDetallepagoEntities(true, -1, -1);
    }

    public List<Detallepago> findDetallepagoEntities(int maxResults, int firstResult) {
        return findDetallepagoEntities(false, maxResults, firstResult);
    }

    private List<Detallepago> findDetallepagoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Detallepago.class));
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

    public Detallepago findDetallepago(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Detallepago.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetallepagoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Detallepago> rt = cq.from(Detallepago.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
