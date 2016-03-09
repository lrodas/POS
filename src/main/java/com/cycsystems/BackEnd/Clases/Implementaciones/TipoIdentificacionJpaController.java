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
import com.cycsystems.BackEnd.Clases.Entidades.TipoIdentificacion;
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
public class TipoIdentificacionJpaController implements Serializable {

    public TipoIdentificacionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TipoIdentificacion tipoIdentificacion) {
        if (tipoIdentificacion.getEmpleadoCollection() == null) {
            tipoIdentificacion.setEmpleadoCollection(new ArrayList<Empleado>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Empleado> attachedEmpleadoCollection = new ArrayList<Empleado>();
            for (Empleado empleadoCollectionEmpleadoToAttach : tipoIdentificacion.getEmpleadoCollection()) {
                empleadoCollectionEmpleadoToAttach = em.getReference(empleadoCollectionEmpleadoToAttach.getClass(), empleadoCollectionEmpleadoToAttach.getCodigo());
                attachedEmpleadoCollection.add(empleadoCollectionEmpleadoToAttach);
            }
            tipoIdentificacion.setEmpleadoCollection(attachedEmpleadoCollection);
            em.persist(tipoIdentificacion);
            for (Empleado empleadoCollectionEmpleado : tipoIdentificacion.getEmpleadoCollection()) {
                TipoIdentificacion oldIdentificacionOfEmpleadoCollectionEmpleado = empleadoCollectionEmpleado.getIdentificacion();
                empleadoCollectionEmpleado.setIdentificacion(tipoIdentificacion);
                empleadoCollectionEmpleado = em.merge(empleadoCollectionEmpleado);
                if (oldIdentificacionOfEmpleadoCollectionEmpleado != null) {
                    oldIdentificacionOfEmpleadoCollectionEmpleado.getEmpleadoCollection().remove(empleadoCollectionEmpleado);
                    oldIdentificacionOfEmpleadoCollectionEmpleado = em.merge(oldIdentificacionOfEmpleadoCollectionEmpleado);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TipoIdentificacion tipoIdentificacion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoIdentificacion persistentTipoIdentificacion = em.find(TipoIdentificacion.class, tipoIdentificacion.getId());
            Collection<Empleado> empleadoCollectionOld = persistentTipoIdentificacion.getEmpleadoCollection();
            Collection<Empleado> empleadoCollectionNew = tipoIdentificacion.getEmpleadoCollection();
            List<String> illegalOrphanMessages = null;
            for (Empleado empleadoCollectionOldEmpleado : empleadoCollectionOld) {
                if (!empleadoCollectionNew.contains(empleadoCollectionOldEmpleado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Empleado " + empleadoCollectionOldEmpleado + " since its identificacion field is not nullable.");
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
            tipoIdentificacion.setEmpleadoCollection(empleadoCollectionNew);
            tipoIdentificacion = em.merge(tipoIdentificacion);
            for (Empleado empleadoCollectionNewEmpleado : empleadoCollectionNew) {
                if (!empleadoCollectionOld.contains(empleadoCollectionNewEmpleado)) {
                    TipoIdentificacion oldIdentificacionOfEmpleadoCollectionNewEmpleado = empleadoCollectionNewEmpleado.getIdentificacion();
                    empleadoCollectionNewEmpleado.setIdentificacion(tipoIdentificacion);
                    empleadoCollectionNewEmpleado = em.merge(empleadoCollectionNewEmpleado);
                    if (oldIdentificacionOfEmpleadoCollectionNewEmpleado != null && !oldIdentificacionOfEmpleadoCollectionNewEmpleado.equals(tipoIdentificacion)) {
                        oldIdentificacionOfEmpleadoCollectionNewEmpleado.getEmpleadoCollection().remove(empleadoCollectionNewEmpleado);
                        oldIdentificacionOfEmpleadoCollectionNewEmpleado = em.merge(oldIdentificacionOfEmpleadoCollectionNewEmpleado);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tipoIdentificacion.getId();
                if (findTipoIdentificacion(id) == null) {
                    throw new NonexistentEntityException("The tipoIdentificacion with id " + id + " no longer exists.");
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
            TipoIdentificacion tipoIdentificacion;
            try {
                tipoIdentificacion = em.getReference(TipoIdentificacion.class, id);
                tipoIdentificacion.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipoIdentificacion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Empleado> empleadoCollectionOrphanCheck = tipoIdentificacion.getEmpleadoCollection();
            for (Empleado empleadoCollectionOrphanCheckEmpleado : empleadoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TipoIdentificacion (" + tipoIdentificacion + ") cannot be destroyed since the Empleado " + empleadoCollectionOrphanCheckEmpleado + " in its empleadoCollection field has a non-nullable identificacion field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tipoIdentificacion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TipoIdentificacion> findTipoIdentificacionEntities() {
        return findTipoIdentificacionEntities(true, -1, -1);
    }

    public List<TipoIdentificacion> findTipoIdentificacionEntities(int maxResults, int firstResult) {
        return findTipoIdentificacionEntities(false, maxResults, firstResult);
    }

    private List<TipoIdentificacion> findTipoIdentificacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TipoIdentificacion.class));
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

    public TipoIdentificacion findTipoIdentificacion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TipoIdentificacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getTipoIdentificacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TipoIdentificacion> rt = cq.from(TipoIdentificacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
