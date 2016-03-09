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
import com.cycsystems.BackEnd.Clases.Entidades.Opcion;
import com.cycsystems.BackEnd.Clases.Entidades.Rol;
import com.cycsystems.BackEnd.Clases.Entidades.RolOpcion;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.NonexistentEntityException;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author angel
 */
public class RolOpcionJpaController implements Serializable {

    public RolOpcionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RolOpcion rolOpcion) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Opcion opcion = rolOpcion.getOpcion();
            if (opcion != null) {
                opcion = em.getReference(opcion.getClass(), opcion.getIdOpcion());
                rolOpcion.setOpcion(opcion);
            }
            Rol rol = rolOpcion.getRol();
            if (rol != null) {
                rol = em.getReference(rol.getClass(), rol.getIdRol());
                rolOpcion.setRol(rol);
            }
            em.persist(rolOpcion);
            if (opcion != null) {
                opcion.getRolOpcionCollection().add(rolOpcion);
                opcion = em.merge(opcion);
            }
            if (rol != null) {
                rol.getRolOpcionCollection().add(rolOpcion);
                rol = em.merge(rol);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRolOpcion(rolOpcion.getIdRolOpcion()) != null) {
                throw new PreexistingEntityException("RolOpcion " + rolOpcion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RolOpcion rolOpcion) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RolOpcion persistentRolOpcion = em.find(RolOpcion.class, rolOpcion.getIdRolOpcion());
            Opcion opcionOld = persistentRolOpcion.getOpcion();
            Opcion opcionNew = rolOpcion.getOpcion();
            Rol rolOld = persistentRolOpcion.getRol();
            Rol rolNew = rolOpcion.getRol();
            if (opcionNew != null) {
                opcionNew = em.getReference(opcionNew.getClass(), opcionNew.getIdOpcion());
                rolOpcion.setOpcion(opcionNew);
            }
            if (rolNew != null) {
                rolNew = em.getReference(rolNew.getClass(), rolNew.getIdRol());
                rolOpcion.setRol(rolNew);
            }
            rolOpcion = em.merge(rolOpcion);
            if (opcionOld != null && !opcionOld.equals(opcionNew)) {
                opcionOld.getRolOpcionCollection().remove(rolOpcion);
                opcionOld = em.merge(opcionOld);
            }
            if (opcionNew != null && !opcionNew.equals(opcionOld)) {
                opcionNew.getRolOpcionCollection().add(rolOpcion);
                opcionNew = em.merge(opcionNew);
            }
            if (rolOld != null && !rolOld.equals(rolNew)) {
                rolOld.getRolOpcionCollection().remove(rolOpcion);
                rolOld = em.merge(rolOld);
            }
            if (rolNew != null && !rolNew.equals(rolOld)) {
                rolNew.getRolOpcionCollection().add(rolOpcion);
                rolNew = em.merge(rolNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = rolOpcion.getIdRolOpcion();
                if (findRolOpcion(id) == null) {
                    throw new NonexistentEntityException("The rolOpcion with id " + id + " no longer exists.");
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
            RolOpcion rolOpcion;
            try {
                rolOpcion = em.getReference(RolOpcion.class, id);
                rolOpcion.getIdRolOpcion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rolOpcion with id " + id + " no longer exists.", enfe);
            }
            Opcion opcion = rolOpcion.getOpcion();
            if (opcion != null) {
                opcion.getRolOpcionCollection().remove(rolOpcion);
                opcion = em.merge(opcion);
            }
            Rol rol = rolOpcion.getRol();
            if (rol != null) {
                rol.getRolOpcionCollection().remove(rolOpcion);
                rol = em.merge(rol);
            }
            em.remove(rolOpcion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RolOpcion> findRolOpcionEntities() {
        return findRolOpcionEntities(true, -1, -1);
    }

    public List<RolOpcion> findRolOpcionEntities(int maxResults, int firstResult) {
        return findRolOpcionEntities(false, maxResults, firstResult);
    }

    private List<RolOpcion> findRolOpcionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RolOpcion.class));
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

    public RolOpcion findRolOpcion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RolOpcion.class, id);
        } finally {
            em.close();
        }
    }

    public int getRolOpcionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RolOpcion> rt = cq.from(RolOpcion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
