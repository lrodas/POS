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
import com.cycsystems.BackEnd.Clases.Entidades.Menuprincipal;
import com.cycsystems.BackEnd.Clases.Entidades.Opcion;
import com.cycsystems.BackEnd.Clases.Entidades.RolOpcion;
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
public class OpcionJpaController implements Serializable {

    public OpcionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Opcion opcion) throws PreexistingEntityException, Exception {
        if (opcion.getRolOpcionCollection() == null) {
            opcion.setRolOpcionCollection(new ArrayList<RolOpcion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Menuprincipal menuPrincipal = opcion.getMenuPrincipal();
            if (menuPrincipal != null) {
                menuPrincipal = em.getReference(menuPrincipal.getClass(), menuPrincipal.getIdMenuPrincipal());
                opcion.setMenuPrincipal(menuPrincipal);
            }
            Collection<RolOpcion> attachedRolOpcionCollection = new ArrayList<RolOpcion>();
            for (RolOpcion rolOpcionCollectionRolOpcionToAttach : opcion.getRolOpcionCollection()) {
                rolOpcionCollectionRolOpcionToAttach = em.getReference(rolOpcionCollectionRolOpcionToAttach.getClass(), rolOpcionCollectionRolOpcionToAttach.getIdRolOpcion());
                attachedRolOpcionCollection.add(rolOpcionCollectionRolOpcionToAttach);
            }
            opcion.setRolOpcionCollection(attachedRolOpcionCollection);
            em.persist(opcion);
            if (menuPrincipal != null) {
                menuPrincipal.getOpcionCollection().add(opcion);
                menuPrincipal = em.merge(menuPrincipal);
            }
            for (RolOpcion rolOpcionCollectionRolOpcion : opcion.getRolOpcionCollection()) {
                Opcion oldOpcionOfRolOpcionCollectionRolOpcion = rolOpcionCollectionRolOpcion.getOpcion();
                rolOpcionCollectionRolOpcion.setOpcion(opcion);
                rolOpcionCollectionRolOpcion = em.merge(rolOpcionCollectionRolOpcion);
                if (oldOpcionOfRolOpcionCollectionRolOpcion != null) {
                    oldOpcionOfRolOpcionCollectionRolOpcion.getRolOpcionCollection().remove(rolOpcionCollectionRolOpcion);
                    oldOpcionOfRolOpcionCollectionRolOpcion = em.merge(oldOpcionOfRolOpcionCollectionRolOpcion);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findOpcion(opcion.getIdOpcion()) != null) {
                throw new PreexistingEntityException("Opcion " + opcion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Opcion opcion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Opcion persistentOpcion = em.find(Opcion.class, opcion.getIdOpcion());
            Menuprincipal menuPrincipalOld = persistentOpcion.getMenuPrincipal();
            Menuprincipal menuPrincipalNew = opcion.getMenuPrincipal();
            Collection<RolOpcion> rolOpcionCollectionOld = persistentOpcion.getRolOpcionCollection();
            Collection<RolOpcion> rolOpcionCollectionNew = opcion.getRolOpcionCollection();
            List<String> illegalOrphanMessages = null;
            for (RolOpcion rolOpcionCollectionOldRolOpcion : rolOpcionCollectionOld) {
                if (!rolOpcionCollectionNew.contains(rolOpcionCollectionOldRolOpcion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RolOpcion " + rolOpcionCollectionOldRolOpcion + " since its opcion field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (menuPrincipalNew != null) {
                menuPrincipalNew = em.getReference(menuPrincipalNew.getClass(), menuPrincipalNew.getIdMenuPrincipal());
                opcion.setMenuPrincipal(menuPrincipalNew);
            }
            Collection<RolOpcion> attachedRolOpcionCollectionNew = new ArrayList<RolOpcion>();
            for (RolOpcion rolOpcionCollectionNewRolOpcionToAttach : rolOpcionCollectionNew) {
                rolOpcionCollectionNewRolOpcionToAttach = em.getReference(rolOpcionCollectionNewRolOpcionToAttach.getClass(), rolOpcionCollectionNewRolOpcionToAttach.getIdRolOpcion());
                attachedRolOpcionCollectionNew.add(rolOpcionCollectionNewRolOpcionToAttach);
            }
            rolOpcionCollectionNew = attachedRolOpcionCollectionNew;
            opcion.setRolOpcionCollection(rolOpcionCollectionNew);
            opcion = em.merge(opcion);
            if (menuPrincipalOld != null && !menuPrincipalOld.equals(menuPrincipalNew)) {
                menuPrincipalOld.getOpcionCollection().remove(opcion);
                menuPrincipalOld = em.merge(menuPrincipalOld);
            }
            if (menuPrincipalNew != null && !menuPrincipalNew.equals(menuPrincipalOld)) {
                menuPrincipalNew.getOpcionCollection().add(opcion);
                menuPrincipalNew = em.merge(menuPrincipalNew);
            }
            for (RolOpcion rolOpcionCollectionNewRolOpcion : rolOpcionCollectionNew) {
                if (!rolOpcionCollectionOld.contains(rolOpcionCollectionNewRolOpcion)) {
                    Opcion oldOpcionOfRolOpcionCollectionNewRolOpcion = rolOpcionCollectionNewRolOpcion.getOpcion();
                    rolOpcionCollectionNewRolOpcion.setOpcion(opcion);
                    rolOpcionCollectionNewRolOpcion = em.merge(rolOpcionCollectionNewRolOpcion);
                    if (oldOpcionOfRolOpcionCollectionNewRolOpcion != null && !oldOpcionOfRolOpcionCollectionNewRolOpcion.equals(opcion)) {
                        oldOpcionOfRolOpcionCollectionNewRolOpcion.getRolOpcionCollection().remove(rolOpcionCollectionNewRolOpcion);
                        oldOpcionOfRolOpcionCollectionNewRolOpcion = em.merge(oldOpcionOfRolOpcionCollectionNewRolOpcion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = opcion.getIdOpcion();
                if (findOpcion(id) == null) {
                    throw new NonexistentEntityException("The opcion with id " + id + " no longer exists.");
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
            Opcion opcion;
            try {
                opcion = em.getReference(Opcion.class, id);
                opcion.getIdOpcion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The opcion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<RolOpcion> rolOpcionCollectionOrphanCheck = opcion.getRolOpcionCollection();
            for (RolOpcion rolOpcionCollectionOrphanCheckRolOpcion : rolOpcionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Opcion (" + opcion + ") cannot be destroyed since the RolOpcion " + rolOpcionCollectionOrphanCheckRolOpcion + " in its rolOpcionCollection field has a non-nullable opcion field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Menuprincipal menuPrincipal = opcion.getMenuPrincipal();
            if (menuPrincipal != null) {
                menuPrincipal.getOpcionCollection().remove(opcion);
                menuPrincipal = em.merge(menuPrincipal);
            }
            em.remove(opcion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Opcion> findOpcionEntities() {
        return findOpcionEntities(true, -1, -1);
    }

    public List<Opcion> findOpcionEntities(int maxResults, int firstResult) {
        return findOpcionEntities(false, maxResults, firstResult);
    }

    private List<Opcion> findOpcionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Opcion.class));
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

    public Opcion findOpcion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Opcion.class, id);
        } finally {
            em.close();
        }
    }

    public int getOpcionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Opcion> rt = cq.from(Opcion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
