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
import com.cycsystems.BackEnd.Clases.Entidades.Empleado;
import com.cycsystems.BackEnd.Clases.Entidades.Estadoempleado;
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
public class EstadoempleadoJpaController implements Serializable {

    public EstadoempleadoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Estadoempleado estadoempleado) throws PreexistingEntityException, Exception {
        if (estadoempleado.getEmpleadoCollection() == null) {
            estadoempleado.setEmpleadoCollection(new ArrayList<Empleado>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Empleado> attachedEmpleadoCollection = new ArrayList<Empleado>();
            for (Empleado empleadoCollectionEmpleadoToAttach : estadoempleado.getEmpleadoCollection()) {
                empleadoCollectionEmpleadoToAttach = em.getReference(empleadoCollectionEmpleadoToAttach.getClass(), empleadoCollectionEmpleadoToAttach.getCodigo());
                attachedEmpleadoCollection.add(empleadoCollectionEmpleadoToAttach);
            }
            estadoempleado.setEmpleadoCollection(attachedEmpleadoCollection);
            em.persist(estadoempleado);
            for (Empleado empleadoCollectionEmpleado : estadoempleado.getEmpleadoCollection()) {
                Estadoempleado oldEstadoEmpleadoOfEmpleadoCollectionEmpleado = empleadoCollectionEmpleado.getEstadoEmpleado();
                empleadoCollectionEmpleado.setEstadoEmpleado(estadoempleado);
                empleadoCollectionEmpleado = em.merge(empleadoCollectionEmpleado);
                if (oldEstadoEmpleadoOfEmpleadoCollectionEmpleado != null) {
                    oldEstadoEmpleadoOfEmpleadoCollectionEmpleado.getEmpleadoCollection().remove(empleadoCollectionEmpleado);
                    oldEstadoEmpleadoOfEmpleadoCollectionEmpleado = em.merge(oldEstadoEmpleadoOfEmpleadoCollectionEmpleado);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEstadoempleado(estadoempleado.getIdEstadoEmpleado()) != null) {
                throw new PreexistingEntityException("Estadoempleado " + estadoempleado + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estadoempleado estadoempleado) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estadoempleado persistentEstadoempleado = em.find(Estadoempleado.class, estadoempleado.getIdEstadoEmpleado());
            Collection<Empleado> empleadoCollectionOld = persistentEstadoempleado.getEmpleadoCollection();
            Collection<Empleado> empleadoCollectionNew = estadoempleado.getEmpleadoCollection();
            List<String> illegalOrphanMessages = null;
            for (Empleado empleadoCollectionOldEmpleado : empleadoCollectionOld) {
                if (!empleadoCollectionNew.contains(empleadoCollectionOldEmpleado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Empleado " + empleadoCollectionOldEmpleado + " since its estadoEmpleado field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Empleado> attachedEmpleadoCollectionNew = new ArrayList<Empleado>();
            for (Empleado empleadoCollectionNewEmpleadoToAttach : empleadoCollectionNew) {
                empleadoCollectionNewEmpleadoToAttach = em.getReference(empleadoCollectionNewEmpleadoToAttach.getClass(), empleadoCollectionNewEmpleadoToAttach.getCodigo());
                attachedEmpleadoCollectionNew.add(empleadoCollectionNewEmpleadoToAttach);
            }
            empleadoCollectionNew = attachedEmpleadoCollectionNew;
            estadoempleado.setEmpleadoCollection(empleadoCollectionNew);
            estadoempleado = em.merge(estadoempleado);
            for (Empleado empleadoCollectionNewEmpleado : empleadoCollectionNew) {
                if (!empleadoCollectionOld.contains(empleadoCollectionNewEmpleado)) {
                    Estadoempleado oldEstadoEmpleadoOfEmpleadoCollectionNewEmpleado = empleadoCollectionNewEmpleado.getEstadoEmpleado();
                    empleadoCollectionNewEmpleado.setEstadoEmpleado(estadoempleado);
                    empleadoCollectionNewEmpleado = em.merge(empleadoCollectionNewEmpleado);
                    if (oldEstadoEmpleadoOfEmpleadoCollectionNewEmpleado != null && !oldEstadoEmpleadoOfEmpleadoCollectionNewEmpleado.equals(estadoempleado)) {
                        oldEstadoEmpleadoOfEmpleadoCollectionNewEmpleado.getEmpleadoCollection().remove(empleadoCollectionNewEmpleado);
                        oldEstadoEmpleadoOfEmpleadoCollectionNewEmpleado = em.merge(oldEstadoEmpleadoOfEmpleadoCollectionNewEmpleado);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = estadoempleado.getIdEstadoEmpleado();
                if (findEstadoempleado(id) == null) {
                    throw new NonexistentEntityException("The estadoempleado with id " + id + " no longer exists.");
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
            Estadoempleado estadoempleado;
            try {
                estadoempleado = em.getReference(Estadoempleado.class, id);
                estadoempleado.getIdEstadoEmpleado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estadoempleado with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Empleado> empleadoCollectionOrphanCheck = estadoempleado.getEmpleadoCollection();
            for (Empleado empleadoCollectionOrphanCheckEmpleado : empleadoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Estadoempleado (" + estadoempleado + ") cannot be destroyed since the Empleado " + empleadoCollectionOrphanCheckEmpleado + " in its empleadoCollection field has a non-nullable estadoEmpleado field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(estadoempleado);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Estadoempleado> findEstadoempleadoEntities() {
        return findEstadoempleadoEntities(true, -1, -1);
    }

    public List<Estadoempleado> findEstadoempleadoEntities(int maxResults, int firstResult) {
        return findEstadoempleadoEntities(false, maxResults, firstResult);
    }

    private List<Estadoempleado> findEstadoempleadoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estadoempleado.class));
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

    public Estadoempleado findEstadoempleado(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estadoempleado.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstadoempleadoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estadoempleado> rt = cq.from(Estadoempleado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
