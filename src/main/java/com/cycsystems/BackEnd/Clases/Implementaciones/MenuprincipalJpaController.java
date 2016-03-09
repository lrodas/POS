/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases.Implementaciones;

import com.cycsystems.BackEnd.Clases.Entidades.Menuprincipal;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.cycsystems.BackEnd.Clases.Entidades.Opcion;
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
public class MenuprincipalJpaController implements Serializable {

    public MenuprincipalJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Menuprincipal menuprincipal) throws PreexistingEntityException, Exception {
        if (menuprincipal.getOpcionCollection() == null) {
            menuprincipal.setOpcionCollection(new ArrayList<Opcion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Opcion> attachedOpcionCollection = new ArrayList<Opcion>();
            for (Opcion opcionCollectionOpcionToAttach : menuprincipal.getOpcionCollection()) {
                opcionCollectionOpcionToAttach = em.getReference(opcionCollectionOpcionToAttach.getClass(), opcionCollectionOpcionToAttach.getIdOpcion());
                attachedOpcionCollection.add(opcionCollectionOpcionToAttach);
            }
            menuprincipal.setOpcionCollection(attachedOpcionCollection);
            em.persist(menuprincipal);
            for (Opcion opcionCollectionOpcion : menuprincipal.getOpcionCollection()) {
                Menuprincipal oldMenuPrincipalOfOpcionCollectionOpcion = opcionCollectionOpcion.getMenuPrincipal();
                opcionCollectionOpcion.setMenuPrincipal(menuprincipal);
                opcionCollectionOpcion = em.merge(opcionCollectionOpcion);
                if (oldMenuPrincipalOfOpcionCollectionOpcion != null) {
                    oldMenuPrincipalOfOpcionCollectionOpcion.getOpcionCollection().remove(opcionCollectionOpcion);
                    oldMenuPrincipalOfOpcionCollectionOpcion = em.merge(oldMenuPrincipalOfOpcionCollectionOpcion);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMenuprincipal(menuprincipal.getIdMenuPrincipal()) != null) {
                throw new PreexistingEntityException("Menuprincipal " + menuprincipal + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Menuprincipal menuprincipal) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Menuprincipal persistentMenuprincipal = em.find(Menuprincipal.class, menuprincipal.getIdMenuPrincipal());
            Collection<Opcion> opcionCollectionOld = persistentMenuprincipal.getOpcionCollection();
            Collection<Opcion> opcionCollectionNew = menuprincipal.getOpcionCollection();
            List<String> illegalOrphanMessages = null;
            for (Opcion opcionCollectionOldOpcion : opcionCollectionOld) {
                if (!opcionCollectionNew.contains(opcionCollectionOldOpcion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Opcion " + opcionCollectionOldOpcion + " since its menuPrincipal field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Opcion> attachedOpcionCollectionNew = new ArrayList<Opcion>();
            for (Opcion opcionCollectionNewOpcionToAttach : opcionCollectionNew) {
                opcionCollectionNewOpcionToAttach = em.getReference(opcionCollectionNewOpcionToAttach.getClass(), opcionCollectionNewOpcionToAttach.getIdOpcion());
                attachedOpcionCollectionNew.add(opcionCollectionNewOpcionToAttach);
            }
            opcionCollectionNew = attachedOpcionCollectionNew;
            menuprincipal.setOpcionCollection(opcionCollectionNew);
            menuprincipal = em.merge(menuprincipal);
            for (Opcion opcionCollectionNewOpcion : opcionCollectionNew) {
                if (!opcionCollectionOld.contains(opcionCollectionNewOpcion)) {
                    Menuprincipal oldMenuPrincipalOfOpcionCollectionNewOpcion = opcionCollectionNewOpcion.getMenuPrincipal();
                    opcionCollectionNewOpcion.setMenuPrincipal(menuprincipal);
                    opcionCollectionNewOpcion = em.merge(opcionCollectionNewOpcion);
                    if (oldMenuPrincipalOfOpcionCollectionNewOpcion != null && !oldMenuPrincipalOfOpcionCollectionNewOpcion.equals(menuprincipal)) {
                        oldMenuPrincipalOfOpcionCollectionNewOpcion.getOpcionCollection().remove(opcionCollectionNewOpcion);
                        oldMenuPrincipalOfOpcionCollectionNewOpcion = em.merge(oldMenuPrincipalOfOpcionCollectionNewOpcion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = menuprincipal.getIdMenuPrincipal();
                if (findMenuprincipal(id) == null) {
                    throw new NonexistentEntityException("The menuprincipal with id " + id + " no longer exists.");
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
            Menuprincipal menuprincipal;
            try {
                menuprincipal = em.getReference(Menuprincipal.class, id);
                menuprincipal.getIdMenuPrincipal();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The menuprincipal with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Opcion> opcionCollectionOrphanCheck = menuprincipal.getOpcionCollection();
            for (Opcion opcionCollectionOrphanCheckOpcion : opcionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Menuprincipal (" + menuprincipal + ") cannot be destroyed since the Opcion " + opcionCollectionOrphanCheckOpcion + " in its opcionCollection field has a non-nullable menuPrincipal field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(menuprincipal);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Menuprincipal> findMenuprincipalEntities() {
        return findMenuprincipalEntities(true, -1, -1);
    }

    public List<Menuprincipal> findMenuprincipalEntities(int maxResults, int firstResult) {
        return findMenuprincipalEntities(false, maxResults, firstResult);
    }

    private List<Menuprincipal> findMenuprincipalEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Menuprincipal.class));
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

    public Menuprincipal findMenuprincipal(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Menuprincipal.class, id);
        } finally {
            em.close();
        }
    }

    public int getMenuprincipalCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Menuprincipal> rt = cq.from(Menuprincipal.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
