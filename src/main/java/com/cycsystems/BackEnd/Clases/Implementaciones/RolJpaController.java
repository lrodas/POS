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
import com.cycsystems.BackEnd.Clases.Entidades.RolOpcion;
import java.util.ArrayList;
import java.util.Collection;
import com.cycsystems.BackEnd.Clases.Entidades.Empleado;
import com.cycsystems.BackEnd.Clases.Entidades.Rol;
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
public class RolJpaController implements Serializable {

    public RolJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Rol rol) throws PreexistingEntityException, Exception {
        if (rol.getRolOpcionCollection() == null) {
            rol.setRolOpcionCollection(new ArrayList<RolOpcion>());
        }
        if (rol.getEmpleadoCollection() == null) {
            rol.setEmpleadoCollection(new ArrayList<Empleado>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<RolOpcion> attachedRolOpcionCollection = new ArrayList<RolOpcion>();
            for (RolOpcion rolOpcionCollectionRolOpcionToAttach : rol.getRolOpcionCollection()) {
                rolOpcionCollectionRolOpcionToAttach = em.getReference(rolOpcionCollectionRolOpcionToAttach.getClass(), rolOpcionCollectionRolOpcionToAttach.getIdRolOpcion());
                attachedRolOpcionCollection.add(rolOpcionCollectionRolOpcionToAttach);
            }
            rol.setRolOpcionCollection(attachedRolOpcionCollection);
            Collection<Empleado> attachedEmpleadoCollection = new ArrayList<Empleado>();
            for (Empleado empleadoCollectionEmpleadoToAttach : rol.getEmpleadoCollection()) {
                empleadoCollectionEmpleadoToAttach = em.getReference(empleadoCollectionEmpleadoToAttach.getClass(), empleadoCollectionEmpleadoToAttach.getCodigo());
                attachedEmpleadoCollection.add(empleadoCollectionEmpleadoToAttach);
            }
            rol.setEmpleadoCollection(attachedEmpleadoCollection);
            em.persist(rol);
            for (RolOpcion rolOpcionCollectionRolOpcion : rol.getRolOpcionCollection()) {
                Rol oldRolOfRolOpcionCollectionRolOpcion = rolOpcionCollectionRolOpcion.getRol();
                rolOpcionCollectionRolOpcion.setRol(rol);
                rolOpcionCollectionRolOpcion = em.merge(rolOpcionCollectionRolOpcion);
                if (oldRolOfRolOpcionCollectionRolOpcion != null) {
                    oldRolOfRolOpcionCollectionRolOpcion.getRolOpcionCollection().remove(rolOpcionCollectionRolOpcion);
                    oldRolOfRolOpcionCollectionRolOpcion = em.merge(oldRolOfRolOpcionCollectionRolOpcion);
                }
            }
            for (Empleado empleadoCollectionEmpleado : rol.getEmpleadoCollection()) {
                Rol oldRolOfEmpleadoCollectionEmpleado = empleadoCollectionEmpleado.getRol();
                empleadoCollectionEmpleado.setRol(rol);
                empleadoCollectionEmpleado = em.merge(empleadoCollectionEmpleado);
                if (oldRolOfEmpleadoCollectionEmpleado != null) {
                    oldRolOfEmpleadoCollectionEmpleado.getEmpleadoCollection().remove(empleadoCollectionEmpleado);
                    oldRolOfEmpleadoCollectionEmpleado = em.merge(oldRolOfEmpleadoCollectionEmpleado);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRol(rol.getIdRol()) != null) {
                throw new PreexistingEntityException("Rol " + rol + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Rol rol) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rol persistentRol = em.find(Rol.class, rol.getIdRol());
            Collection<RolOpcion> rolOpcionCollectionOld = persistentRol.getRolOpcionCollection();
            Collection<RolOpcion> rolOpcionCollectionNew = rol.getRolOpcionCollection();
            Collection<Empleado> empleadoCollectionOld = persistentRol.getEmpleadoCollection();
            Collection<Empleado> empleadoCollectionNew = rol.getEmpleadoCollection();
            List<String> illegalOrphanMessages = null;
            for (RolOpcion rolOpcionCollectionOldRolOpcion : rolOpcionCollectionOld) {
                if (!rolOpcionCollectionNew.contains(rolOpcionCollectionOldRolOpcion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RolOpcion " + rolOpcionCollectionOldRolOpcion + " since its rol field is not nullable.");
                }
            }
            for (Empleado empleadoCollectionOldEmpleado : empleadoCollectionOld) {
                if (!empleadoCollectionNew.contains(empleadoCollectionOldEmpleado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Empleado " + empleadoCollectionOldEmpleado + " since its rol field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<RolOpcion> attachedRolOpcionCollectionNew = new ArrayList<RolOpcion>();
            for (RolOpcion rolOpcionCollectionNewRolOpcionToAttach : rolOpcionCollectionNew) {
                rolOpcionCollectionNewRolOpcionToAttach = em.getReference(rolOpcionCollectionNewRolOpcionToAttach.getClass(), rolOpcionCollectionNewRolOpcionToAttach.getIdRolOpcion());
                attachedRolOpcionCollectionNew.add(rolOpcionCollectionNewRolOpcionToAttach);
            }
            rolOpcionCollectionNew = attachedRolOpcionCollectionNew;
            rol.setRolOpcionCollection(rolOpcionCollectionNew);
            Collection<Empleado> attachedEmpleadoCollectionNew = new ArrayList<Empleado>();
            for (Empleado empleadoCollectionNewEmpleadoToAttach : empleadoCollectionNew) {
                empleadoCollectionNewEmpleadoToAttach = em.getReference(empleadoCollectionNewEmpleadoToAttach.getClass(), empleadoCollectionNewEmpleadoToAttach.getCodigo());
                attachedEmpleadoCollectionNew.add(empleadoCollectionNewEmpleadoToAttach);
            }
            empleadoCollectionNew = attachedEmpleadoCollectionNew;
            rol.setEmpleadoCollection(empleadoCollectionNew);
            rol = em.merge(rol);
            for (RolOpcion rolOpcionCollectionNewRolOpcion : rolOpcionCollectionNew) {
                if (!rolOpcionCollectionOld.contains(rolOpcionCollectionNewRolOpcion)) {
                    Rol oldRolOfRolOpcionCollectionNewRolOpcion = rolOpcionCollectionNewRolOpcion.getRol();
                    rolOpcionCollectionNewRolOpcion.setRol(rol);
                    rolOpcionCollectionNewRolOpcion = em.merge(rolOpcionCollectionNewRolOpcion);
                    if (oldRolOfRolOpcionCollectionNewRolOpcion != null && !oldRolOfRolOpcionCollectionNewRolOpcion.equals(rol)) {
                        oldRolOfRolOpcionCollectionNewRolOpcion.getRolOpcionCollection().remove(rolOpcionCollectionNewRolOpcion);
                        oldRolOfRolOpcionCollectionNewRolOpcion = em.merge(oldRolOfRolOpcionCollectionNewRolOpcion);
                    }
                }
            }
            for (Empleado empleadoCollectionNewEmpleado : empleadoCollectionNew) {
                if (!empleadoCollectionOld.contains(empleadoCollectionNewEmpleado)) {
                    Rol oldRolOfEmpleadoCollectionNewEmpleado = empleadoCollectionNewEmpleado.getRol();
                    empleadoCollectionNewEmpleado.setRol(rol);
                    empleadoCollectionNewEmpleado = em.merge(empleadoCollectionNewEmpleado);
                    if (oldRolOfEmpleadoCollectionNewEmpleado != null && !oldRolOfEmpleadoCollectionNewEmpleado.equals(rol)) {
                        oldRolOfEmpleadoCollectionNewEmpleado.getEmpleadoCollection().remove(empleadoCollectionNewEmpleado);
                        oldRolOfEmpleadoCollectionNewEmpleado = em.merge(oldRolOfEmpleadoCollectionNewEmpleado);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = rol.getIdRol();
                if (findRol(id) == null) {
                    throw new NonexistentEntityException("The rol with id " + id + " no longer exists.");
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
            Rol rol;
            try {
                rol = em.getReference(Rol.class, id);
                rol.getIdRol();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rol with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<RolOpcion> rolOpcionCollectionOrphanCheck = rol.getRolOpcionCollection();
            for (RolOpcion rolOpcionCollectionOrphanCheckRolOpcion : rolOpcionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Rol (" + rol + ") cannot be destroyed since the RolOpcion " + rolOpcionCollectionOrphanCheckRolOpcion + " in its rolOpcionCollection field has a non-nullable rol field.");
            }
            Collection<Empleado> empleadoCollectionOrphanCheck = rol.getEmpleadoCollection();
            for (Empleado empleadoCollectionOrphanCheckEmpleado : empleadoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Rol (" + rol + ") cannot be destroyed since the Empleado " + empleadoCollectionOrphanCheckEmpleado + " in its empleadoCollection field has a non-nullable rol field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(rol);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Rol> findRolEntities() {
        return findRolEntities(true, -1, -1);
    }

    public List<Rol> findRolEntities(int maxResults, int firstResult) {
        return findRolEntities(false, maxResults, firstResult);
    }

    private List<Rol> findRolEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Rol.class));
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

    public Rol findRol(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Rol.class, id);
        } finally {
            em.close();
        }
    }

    public int getRolCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Rol> rt = cq.from(Rol.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
