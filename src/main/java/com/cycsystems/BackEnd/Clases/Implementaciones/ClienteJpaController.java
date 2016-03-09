/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases.Implementaciones;

import com.cycsystems.BackEnd.Clases.Entidades.Cliente;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.cycsystems.BackEnd.Clases.Entidades.Facturacion;
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
public class ClienteJpaController implements Serializable {

    public ClienteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cliente cliente) {
        if (cliente.getFacturacionCollection() == null) {
            cliente.setFacturacionCollection(new ArrayList<Facturacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Facturacion> attachedFacturacionCollection = new ArrayList<Facturacion>();
            for (Facturacion facturacionCollectionFacturacionToAttach : cliente.getFacturacionCollection()) {
                facturacionCollectionFacturacionToAttach = em.getReference(facturacionCollectionFacturacionToAttach.getClass(), facturacionCollectionFacturacionToAttach.getFacturacionPK());
                attachedFacturacionCollection.add(facturacionCollectionFacturacionToAttach);
            }
            cliente.setFacturacionCollection(attachedFacturacionCollection);
            em.persist(cliente);
            for (Facturacion facturacionCollectionFacturacion : cliente.getFacturacionCollection()) {
                Cliente oldClienteOfFacturacionCollectionFacturacion = facturacionCollectionFacturacion.getCliente();
                facturacionCollectionFacturacion.setCliente(cliente);
                facturacionCollectionFacturacion = em.merge(facturacionCollectionFacturacion);
                if (oldClienteOfFacturacionCollectionFacturacion != null) {
                    oldClienteOfFacturacionCollectionFacturacion.getFacturacionCollection().remove(facturacionCollectionFacturacion);
                    oldClienteOfFacturacionCollectionFacturacion = em.merge(oldClienteOfFacturacionCollectionFacturacion);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cliente cliente) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente persistentCliente = em.find(Cliente.class, cliente.getCodigo());
            Collection<Facturacion> facturacionCollectionOld = persistentCliente.getFacturacionCollection();
            Collection<Facturacion> facturacionCollectionNew = cliente.getFacturacionCollection();
            List<String> illegalOrphanMessages = null;
            for (Facturacion facturacionCollectionOldFacturacion : facturacionCollectionOld) {
                if (!facturacionCollectionNew.contains(facturacionCollectionOldFacturacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Facturacion " + facturacionCollectionOldFacturacion + " since its cliente field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Facturacion> attachedFacturacionCollectionNew = new ArrayList<Facturacion>();
            for (Facturacion facturacionCollectionNewFacturacionToAttach : facturacionCollectionNew) {
                facturacionCollectionNewFacturacionToAttach = em.getReference(facturacionCollectionNewFacturacionToAttach.getClass(), facturacionCollectionNewFacturacionToAttach.getFacturacionPK());
                attachedFacturacionCollectionNew.add(facturacionCollectionNewFacturacionToAttach);
            }
            facturacionCollectionNew = attachedFacturacionCollectionNew;
            cliente.setFacturacionCollection(facturacionCollectionNew);
            cliente = em.merge(cliente);
            for (Facturacion facturacionCollectionNewFacturacion : facturacionCollectionNew) {
                if (!facturacionCollectionOld.contains(facturacionCollectionNewFacturacion)) {
                    Cliente oldClienteOfFacturacionCollectionNewFacturacion = facturacionCollectionNewFacturacion.getCliente();
                    facturacionCollectionNewFacturacion.setCliente(cliente);
                    facturacionCollectionNewFacturacion = em.merge(facturacionCollectionNewFacturacion);
                    if (oldClienteOfFacturacionCollectionNewFacturacion != null && !oldClienteOfFacturacionCollectionNewFacturacion.equals(cliente)) {
                        oldClienteOfFacturacionCollectionNewFacturacion.getFacturacionCollection().remove(facturacionCollectionNewFacturacion);
                        oldClienteOfFacturacionCollectionNewFacturacion = em.merge(oldClienteOfFacturacionCollectionNewFacturacion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cliente.getCodigo();
                if (findCliente(id) == null) {
                    throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.");
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
            Cliente cliente;
            try {
                cliente = em.getReference(Cliente.class, id);
                cliente.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Facturacion> facturacionCollectionOrphanCheck = cliente.getFacturacionCollection();
            for (Facturacion facturacionCollectionOrphanCheckFacturacion : facturacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cliente (" + cliente + ") cannot be destroyed since the Facturacion " + facturacionCollectionOrphanCheckFacturacion + " in its facturacionCollection field has a non-nullable cliente field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(cliente);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cliente> findClienteEntities() {
        return findClienteEntities(true, -1, -1);
    }

    public List<Cliente> findClienteEntities(int maxResults, int firstResult) {
        return findClienteEntities(false, maxResults, firstResult);
    }

    private List<Cliente> findClienteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cliente.class));
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

    public Cliente findCliente(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cliente.class, id);
        } finally {
            em.close();
        }
    }

    public int getClienteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cliente> rt = cq.from(Cliente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
