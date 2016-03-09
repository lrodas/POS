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
import com.cycsystems.BackEnd.Clases.Entidades.Cliente;
import com.cycsystems.BackEnd.Clases.Entidades.Corte;
import com.cycsystems.BackEnd.Clases.Entidades.Detallepago;
import java.util.ArrayList;
import java.util.Collection;
import com.cycsystems.BackEnd.Clases.Entidades.Detalle;
import com.cycsystems.BackEnd.Clases.Entidades.Facturacion;
import com.cycsystems.BackEnd.Clases.Entidades.FacturacionPK;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.IllegalOrphanException;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.NonexistentEntityException;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author angel
 */
public class FacturacionJpaController implements Serializable {

    public FacturacionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Facturacion facturacion) throws PreexistingEntityException, Exception {
        if (facturacion.getFacturacionPK() == null) {
            facturacion.setFacturacionPK(new FacturacionPK());
        }
        if (facturacion.getDetallepagoCollection() == null) {
            facturacion.setDetallepagoCollection(new ArrayList<Detallepago>());
        }
        if (facturacion.getDetalleCollection() == null) {
            facturacion.setDetalleCollection(new ArrayList<Detalle>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente cliente = facturacion.getCliente();
            if (cliente != null) {
                cliente = em.getReference(cliente.getClass(), cliente.getCodigo());
                facturacion.setCliente(cliente);
            }
            Corte corte = facturacion.getCorte();
            if (corte != null) {
                corte = em.getReference(corte.getClass(), corte.getCodigo());
                facturacion.setCorte(corte);
            }
            Collection<Detallepago> attachedDetallepagoCollection = new ArrayList<Detallepago>();
            for (Detallepago detallepagoCollectionDetallepagoToAttach : facturacion.getDetallepagoCollection()) {
                detallepagoCollectionDetallepagoToAttach = em.getReference(detallepagoCollectionDetallepagoToAttach.getClass(), detallepagoCollectionDetallepagoToAttach.getIdDetallePago());
                attachedDetallepagoCollection.add(detallepagoCollectionDetallepagoToAttach);
            }
            facturacion.setDetallepagoCollection(attachedDetallepagoCollection);
            Collection<Detalle> attachedDetalleCollection = new ArrayList<Detalle>();
            for (Detalle detalleCollectionDetalleToAttach : facturacion.getDetalleCollection()) {
                detalleCollectionDetalleToAttach = em.getReference(detalleCollectionDetalleToAttach.getClass(), detalleCollectionDetalleToAttach.getDetallePK());
                attachedDetalleCollection.add(detalleCollectionDetalleToAttach);
            }
            facturacion.setDetalleCollection(attachedDetalleCollection);
            em.persist(facturacion);
            if (cliente != null) {
                cliente.getFacturacionCollection().add(facturacion);
                cliente = em.merge(cliente);
            }
            if (corte != null) {
                corte.getFacturacionCollection().add(facturacion);
                corte = em.merge(corte);
            }
            for (Detallepago detallepagoCollectionDetallepago : facturacion.getDetallepagoCollection()) {
                Facturacion oldFacturaCorrelativoOfDetallepagoCollectionDetallepago = detallepagoCollectionDetallepago.getFacturaCorrelativo();
                detallepagoCollectionDetallepago.setFacturaCorrelativo(facturacion);
                detallepagoCollectionDetallepago = em.merge(detallepagoCollectionDetallepago);
                if (oldFacturaCorrelativoOfDetallepagoCollectionDetallepago != null) {
                    oldFacturaCorrelativoOfDetallepagoCollectionDetallepago.getDetallepagoCollection().remove(detallepagoCollectionDetallepago);
                    oldFacturaCorrelativoOfDetallepagoCollectionDetallepago = em.merge(oldFacturaCorrelativoOfDetallepagoCollectionDetallepago);
                }
            }
            for (Detalle detalleCollectionDetalle : facturacion.getDetalleCollection()) {
                Facturacion oldFacturacionOfDetalleCollectionDetalle = detalleCollectionDetalle.getFacturacion();
                detalleCollectionDetalle.setFacturacion(facturacion);
                detalleCollectionDetalle = em.merge(detalleCollectionDetalle);
                if (oldFacturacionOfDetalleCollectionDetalle != null) {
                    oldFacturacionOfDetalleCollectionDetalle.getDetalleCollection().remove(detalleCollectionDetalle);
                    oldFacturacionOfDetalleCollectionDetalle = em.merge(oldFacturacionOfDetalleCollectionDetalle);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findFacturacion(facturacion.getFacturacionPK()) != null) {
                throw new PreexistingEntityException("Facturacion " + facturacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Facturacion facturacion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Facturacion persistentFacturacion = em.find(Facturacion.class, facturacion.getFacturacionPK());
            Cliente clienteOld = persistentFacturacion.getCliente();
            Cliente clienteNew = facturacion.getCliente();
            Corte corteOld = persistentFacturacion.getCorte();
            Corte corteNew = facturacion.getCorte();
            Collection<Detallepago> detallepagoCollectionOld = persistentFacturacion.getDetallepagoCollection();
            Collection<Detallepago> detallepagoCollectionNew = facturacion.getDetallepagoCollection();
            Collection<Detalle> detalleCollectionOld = persistentFacturacion.getDetalleCollection();
            Collection<Detalle> detalleCollectionNew = facturacion.getDetalleCollection();
            List<String> illegalOrphanMessages = null;
            for (Detallepago detallepagoCollectionOldDetallepago : detallepagoCollectionOld) {
                if (!detallepagoCollectionNew.contains(detallepagoCollectionOldDetallepago)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detallepago " + detallepagoCollectionOldDetallepago + " since its facturaCorrelativo field is not nullable.");
                }
            }
            for (Detalle detalleCollectionOldDetalle : detalleCollectionOld) {
                if (!detalleCollectionNew.contains(detalleCollectionOldDetalle)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detalle " + detalleCollectionOldDetalle + " since its facturacion field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (clienteNew != null) {
                clienteNew = em.getReference(clienteNew.getClass(), clienteNew.getCodigo());
                facturacion.setCliente(clienteNew);
            }
            if (corteNew != null) {
                corteNew = em.getReference(corteNew.getClass(), corteNew.getCodigo());
                facturacion.setCorte(corteNew);
            }
            Collection<Detallepago> attachedDetallepagoCollectionNew = new ArrayList<Detallepago>();
            for (Detallepago detallepagoCollectionNewDetallepagoToAttach : detallepagoCollectionNew) {
                detallepagoCollectionNewDetallepagoToAttach = em.getReference(detallepagoCollectionNewDetallepagoToAttach.getClass(), detallepagoCollectionNewDetallepagoToAttach.getIdDetallePago());
                attachedDetallepagoCollectionNew.add(detallepagoCollectionNewDetallepagoToAttach);
            }
            detallepagoCollectionNew = attachedDetallepagoCollectionNew;
            facturacion.setDetallepagoCollection(detallepagoCollectionNew);
            Collection<Detalle> attachedDetalleCollectionNew = new ArrayList<Detalle>();
            for (Detalle detalleCollectionNewDetalleToAttach : detalleCollectionNew) {
                detalleCollectionNewDetalleToAttach = em.getReference(detalleCollectionNewDetalleToAttach.getClass(), detalleCollectionNewDetalleToAttach.getDetallePK());
                attachedDetalleCollectionNew.add(detalleCollectionNewDetalleToAttach);
            }
            detalleCollectionNew = attachedDetalleCollectionNew;
            facturacion.setDetalleCollection(detalleCollectionNew);
            facturacion = em.merge(facturacion);
            if (clienteOld != null && !clienteOld.equals(clienteNew)) {
                clienteOld.getFacturacionCollection().remove(facturacion);
                clienteOld = em.merge(clienteOld);
            }
            if (clienteNew != null && !clienteNew.equals(clienteOld)) {
                clienteNew.getFacturacionCollection().add(facturacion);
                clienteNew = em.merge(clienteNew);
            }
            if (corteOld != null && !corteOld.equals(corteNew)) {
                corteOld.getFacturacionCollection().remove(facturacion);
                corteOld = em.merge(corteOld);
            }
            if (corteNew != null && !corteNew.equals(corteOld)) {
                corteNew.getFacturacionCollection().add(facturacion);
                corteNew = em.merge(corteNew);
            }
            for (Detallepago detallepagoCollectionNewDetallepago : detallepagoCollectionNew) {
                if (!detallepagoCollectionOld.contains(detallepagoCollectionNewDetallepago)) {
                    Facturacion oldFacturaCorrelativoOfDetallepagoCollectionNewDetallepago = detallepagoCollectionNewDetallepago.getFacturaCorrelativo();
                    detallepagoCollectionNewDetallepago.setFacturaCorrelativo(facturacion);
                    detallepagoCollectionNewDetallepago = em.merge(detallepagoCollectionNewDetallepago);
                    if (oldFacturaCorrelativoOfDetallepagoCollectionNewDetallepago != null && !oldFacturaCorrelativoOfDetallepagoCollectionNewDetallepago.equals(facturacion)) {
                        oldFacturaCorrelativoOfDetallepagoCollectionNewDetallepago.getDetallepagoCollection().remove(detallepagoCollectionNewDetallepago);
                        oldFacturaCorrelativoOfDetallepagoCollectionNewDetallepago = em.merge(oldFacturaCorrelativoOfDetallepagoCollectionNewDetallepago);
                    }
                }
            }
            for (Detalle detalleCollectionNewDetalle : detalleCollectionNew) {
                if (!detalleCollectionOld.contains(detalleCollectionNewDetalle)) {
                    Facturacion oldFacturacionOfDetalleCollectionNewDetalle = detalleCollectionNewDetalle.getFacturacion();
                    detalleCollectionNewDetalle.setFacturacion(facturacion);
                    detalleCollectionNewDetalle = em.merge(detalleCollectionNewDetalle);
                    if (oldFacturacionOfDetalleCollectionNewDetalle != null && !oldFacturacionOfDetalleCollectionNewDetalle.equals(facturacion)) {
                        oldFacturacionOfDetalleCollectionNewDetalle.getDetalleCollection().remove(detalleCollectionNewDetalle);
                        oldFacturacionOfDetalleCollectionNewDetalle = em.merge(oldFacturacionOfDetalleCollectionNewDetalle);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                FacturacionPK id = facturacion.getFacturacionPK();
                if (findFacturacion(id) == null) {
                    throw new NonexistentEntityException("The facturacion with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(FacturacionPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Facturacion facturacion;
            try {
                facturacion = em.getReference(Facturacion.class, id);
                facturacion.getFacturacionPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The facturacion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Detallepago> detallepagoCollectionOrphanCheck = facturacion.getDetallepagoCollection();
            for (Detallepago detallepagoCollectionOrphanCheckDetallepago : detallepagoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Facturacion (" + facturacion + ") cannot be destroyed since the Detallepago " + detallepagoCollectionOrphanCheckDetallepago + " in its detallepagoCollection field has a non-nullable facturaCorrelativo field.");
            }
            Collection<Detalle> detalleCollectionOrphanCheck = facturacion.getDetalleCollection();
            for (Detalle detalleCollectionOrphanCheckDetalle : detalleCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Facturacion (" + facturacion + ") cannot be destroyed since the Detalle " + detalleCollectionOrphanCheckDetalle + " in its detalleCollection field has a non-nullable facturacion field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cliente cliente = facturacion.getCliente();
            if (cliente != null) {
                cliente.getFacturacionCollection().remove(facturacion);
                cliente = em.merge(cliente);
            }
            Corte corte = facturacion.getCorte();
            if (corte != null) {
                corte.getFacturacionCollection().remove(facturacion);
                corte = em.merge(corte);
            }
            em.remove(facturacion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Facturacion> findFacturacionEntities() {
        return findFacturacionEntities(true, -1, -1);
    }

    public List<Facturacion> findFacturacionEntities(int maxResults, int firstResult) {
        return findFacturacionEntities(false, maxResults, firstResult);
    }

    private List<Facturacion> findFacturacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Facturacion.class));
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

    public Facturacion findFacturacion(FacturacionPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Facturacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getFacturacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Facturacion> rt = cq.from(Facturacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
