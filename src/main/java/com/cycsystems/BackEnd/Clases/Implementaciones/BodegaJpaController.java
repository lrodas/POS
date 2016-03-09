/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cycsystems.BackEnd.Clases.Implementaciones;

import com.cycsystems.BackEnd.Clases.Entidades.Bodega;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.cycsystems.BackEnd.Clases.Entidades.Empresa;
import com.cycsystems.BackEnd.Clases.Entidades.Licencia;
import com.cycsystems.BackEnd.Clases.Entidades.Ubicacion;
import java.util.ArrayList;
import java.util.Collection;
import com.cycsystems.BackEnd.Clases.Entidades.Detallecotizacion;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.IllegalOrphanException;
import com.cycsystems.BackEnd.Clases.Implementaciones.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author angel
 */
public class BodegaJpaController implements Serializable {

    public BodegaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Bodega bodega) {
        if (bodega.getUbicacionCollection() == null) {
            bodega.setUbicacionCollection(new ArrayList<Ubicacion>());
        }
        if (bodega.getDetallecotizacionCollection() == null) {
            bodega.setDetallecotizacionCollection(new ArrayList<Detallecotizacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empresa empresa = bodega.getEmpresa();
            if (empresa != null) {
                empresa = em.getReference(empresa.getClass(), empresa.getCodigo());
                bodega.setEmpresa(empresa);
            }
            Licencia licencia = bodega.getLicencia();
            if (licencia != null) {
                licencia = em.getReference(licencia.getClass(), licencia.getCodigo());
                bodega.setLicencia(licencia);
            }
            Collection<Ubicacion> attachedUbicacionCollection = new ArrayList<Ubicacion>();
            for (Ubicacion ubicacionCollectionUbicacionToAttach : bodega.getUbicacionCollection()) {
                ubicacionCollectionUbicacionToAttach = em.getReference(ubicacionCollectionUbicacionToAttach.getClass(), ubicacionCollectionUbicacionToAttach.getIdUbicacion());
                attachedUbicacionCollection.add(ubicacionCollectionUbicacionToAttach);
            }
            bodega.setUbicacionCollection(attachedUbicacionCollection);
            Collection<Detallecotizacion> attachedDetallecotizacionCollection = new ArrayList<Detallecotizacion>();
            for (Detallecotizacion detallecotizacionCollectionDetallecotizacionToAttach : bodega.getDetallecotizacionCollection()) {
                detallecotizacionCollectionDetallecotizacionToAttach = em.getReference(detallecotizacionCollectionDetallecotizacionToAttach.getClass(), detallecotizacionCollectionDetallecotizacionToAttach.getDetallecotizacionPK());
                attachedDetallecotizacionCollection.add(detallecotizacionCollectionDetallecotizacionToAttach);
            }
            bodega.setDetallecotizacionCollection(attachedDetallecotizacionCollection);
            em.persist(bodega);
            if (empresa != null) {
                empresa.getBodegaCollection().add(bodega);
                empresa = em.merge(empresa);
            }
            if (licencia != null) {
                licencia.getBodegaCollection().add(bodega);
                licencia = em.merge(licencia);
            }
            for (Ubicacion ubicacionCollectionUbicacion : bodega.getUbicacionCollection()) {
                Bodega oldBodegaOfUbicacionCollectionUbicacion = ubicacionCollectionUbicacion.getBodega();
                ubicacionCollectionUbicacion.setBodega(bodega);
                ubicacionCollectionUbicacion = em.merge(ubicacionCollectionUbicacion);
                if (oldBodegaOfUbicacionCollectionUbicacion != null) {
                    oldBodegaOfUbicacionCollectionUbicacion.getUbicacionCollection().remove(ubicacionCollectionUbicacion);
                    oldBodegaOfUbicacionCollectionUbicacion = em.merge(oldBodegaOfUbicacionCollectionUbicacion);
                }
            }
            for (Detallecotizacion detallecotizacionCollectionDetallecotizacion : bodega.getDetallecotizacionCollection()) {
                Bodega oldBodegaOfDetallecotizacionCollectionDetallecotizacion = detallecotizacionCollectionDetallecotizacion.getBodega();
                detallecotizacionCollectionDetallecotizacion.setBodega(bodega);
                detallecotizacionCollectionDetallecotizacion = em.merge(detallecotizacionCollectionDetallecotizacion);
                if (oldBodegaOfDetallecotizacionCollectionDetallecotizacion != null) {
                    oldBodegaOfDetallecotizacionCollectionDetallecotizacion.getDetallecotizacionCollection().remove(detallecotizacionCollectionDetallecotizacion);
                    oldBodegaOfDetallecotizacionCollectionDetallecotizacion = em.merge(oldBodegaOfDetallecotizacionCollectionDetallecotizacion);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Bodega bodega) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Bodega persistentBodega = em.find(Bodega.class, bodega.getCodigo());
            Empresa empresaOld = persistentBodega.getEmpresa();
            Empresa empresaNew = bodega.getEmpresa();
            Licencia licenciaOld = persistentBodega.getLicencia();
            Licencia licenciaNew = bodega.getLicencia();
            Collection<Ubicacion> ubicacionCollectionOld = persistentBodega.getUbicacionCollection();
            Collection<Ubicacion> ubicacionCollectionNew = bodega.getUbicacionCollection();
            Collection<Detallecotizacion> detallecotizacionCollectionOld = persistentBodega.getDetallecotizacionCollection();
            Collection<Detallecotizacion> detallecotizacionCollectionNew = bodega.getDetallecotizacionCollection();
            List<String> illegalOrphanMessages = null;
            for (Ubicacion ubicacionCollectionOldUbicacion : ubicacionCollectionOld) {
                if (!ubicacionCollectionNew.contains(ubicacionCollectionOldUbicacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ubicacion " + ubicacionCollectionOldUbicacion + " since its bodega field is not nullable.");
                }
            }
            for (Detallecotizacion detallecotizacionCollectionOldDetallecotizacion : detallecotizacionCollectionOld) {
                if (!detallecotizacionCollectionNew.contains(detallecotizacionCollectionOldDetallecotizacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detallecotizacion " + detallecotizacionCollectionOldDetallecotizacion + " since its bodega field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (empresaNew != null) {
                empresaNew = em.getReference(empresaNew.getClass(), empresaNew.getCodigo());
                bodega.setEmpresa(empresaNew);
            }
            if (licenciaNew != null) {
                licenciaNew = em.getReference(licenciaNew.getClass(), licenciaNew.getCodigo());
                bodega.setLicencia(licenciaNew);
            }
            Collection<Ubicacion> attachedUbicacionCollectionNew = new ArrayList<Ubicacion>();
            for (Ubicacion ubicacionCollectionNewUbicacionToAttach : ubicacionCollectionNew) {
                ubicacionCollectionNewUbicacionToAttach = em.getReference(ubicacionCollectionNewUbicacionToAttach.getClass(), ubicacionCollectionNewUbicacionToAttach.getIdUbicacion());
                attachedUbicacionCollectionNew.add(ubicacionCollectionNewUbicacionToAttach);
            }
            ubicacionCollectionNew = attachedUbicacionCollectionNew;
            bodega.setUbicacionCollection(ubicacionCollectionNew);
            Collection<Detallecotizacion> attachedDetallecotizacionCollectionNew = new ArrayList<Detallecotizacion>();
            for (Detallecotizacion detallecotizacionCollectionNewDetallecotizacionToAttach : detallecotizacionCollectionNew) {
                detallecotizacionCollectionNewDetallecotizacionToAttach = em.getReference(detallecotizacionCollectionNewDetallecotizacionToAttach.getClass(), detallecotizacionCollectionNewDetallecotizacionToAttach.getDetallecotizacionPK());
                attachedDetallecotizacionCollectionNew.add(detallecotizacionCollectionNewDetallecotizacionToAttach);
            }
            detallecotizacionCollectionNew = attachedDetallecotizacionCollectionNew;
            bodega.setDetallecotizacionCollection(detallecotizacionCollectionNew);
            bodega = em.merge(bodega);
            if (empresaOld != null && !empresaOld.equals(empresaNew)) {
                empresaOld.getBodegaCollection().remove(bodega);
                empresaOld = em.merge(empresaOld);
            }
            if (empresaNew != null && !empresaNew.equals(empresaOld)) {
                empresaNew.getBodegaCollection().add(bodega);
                empresaNew = em.merge(empresaNew);
            }
            if (licenciaOld != null && !licenciaOld.equals(licenciaNew)) {
                licenciaOld.getBodegaCollection().remove(bodega);
                licenciaOld = em.merge(licenciaOld);
            }
            if (licenciaNew != null && !licenciaNew.equals(licenciaOld)) {
                licenciaNew.getBodegaCollection().add(bodega);
                licenciaNew = em.merge(licenciaNew);
            }
            for (Ubicacion ubicacionCollectionNewUbicacion : ubicacionCollectionNew) {
                if (!ubicacionCollectionOld.contains(ubicacionCollectionNewUbicacion)) {
                    Bodega oldBodegaOfUbicacionCollectionNewUbicacion = ubicacionCollectionNewUbicacion.getBodega();
                    ubicacionCollectionNewUbicacion.setBodega(bodega);
                    ubicacionCollectionNewUbicacion = em.merge(ubicacionCollectionNewUbicacion);
                    if (oldBodegaOfUbicacionCollectionNewUbicacion != null && !oldBodegaOfUbicacionCollectionNewUbicacion.equals(bodega)) {
                        oldBodegaOfUbicacionCollectionNewUbicacion.getUbicacionCollection().remove(ubicacionCollectionNewUbicacion);
                        oldBodegaOfUbicacionCollectionNewUbicacion = em.merge(oldBodegaOfUbicacionCollectionNewUbicacion);
                    }
                }
            }
            for (Detallecotizacion detallecotizacionCollectionNewDetallecotizacion : detallecotizacionCollectionNew) {
                if (!detallecotizacionCollectionOld.contains(detallecotizacionCollectionNewDetallecotizacion)) {
                    Bodega oldBodegaOfDetallecotizacionCollectionNewDetallecotizacion = detallecotizacionCollectionNewDetallecotizacion.getBodega();
                    detallecotizacionCollectionNewDetallecotizacion.setBodega(bodega);
                    detallecotizacionCollectionNewDetallecotizacion = em.merge(detallecotizacionCollectionNewDetallecotizacion);
                    if (oldBodegaOfDetallecotizacionCollectionNewDetallecotizacion != null && !oldBodegaOfDetallecotizacionCollectionNewDetallecotizacion.equals(bodega)) {
                        oldBodegaOfDetallecotizacionCollectionNewDetallecotizacion.getDetallecotizacionCollection().remove(detallecotizacionCollectionNewDetallecotizacion);
                        oldBodegaOfDetallecotizacionCollectionNewDetallecotizacion = em.merge(oldBodegaOfDetallecotizacionCollectionNewDetallecotizacion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = bodega.getCodigo();
                if (findBodega(id) == null) {
                    throw new NonexistentEntityException("The bodega with id " + id + " no longer exists.");
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
            Bodega bodega;
            try {
                bodega = em.getReference(Bodega.class, id);
                bodega.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The bodega with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Ubicacion> ubicacionCollectionOrphanCheck = bodega.getUbicacionCollection();
            for (Ubicacion ubicacionCollectionOrphanCheckUbicacion : ubicacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Bodega (" + bodega + ") cannot be destroyed since the Ubicacion " + ubicacionCollectionOrphanCheckUbicacion + " in its ubicacionCollection field has a non-nullable bodega field.");
            }
            Collection<Detallecotizacion> detallecotizacionCollectionOrphanCheck = bodega.getDetallecotizacionCollection();
            for (Detallecotizacion detallecotizacionCollectionOrphanCheckDetallecotizacion : detallecotizacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Bodega (" + bodega + ") cannot be destroyed since the Detallecotizacion " + detallecotizacionCollectionOrphanCheckDetallecotizacion + " in its detallecotizacionCollection field has a non-nullable bodega field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Empresa empresa = bodega.getEmpresa();
            if (empresa != null) {
                empresa.getBodegaCollection().remove(bodega);
                empresa = em.merge(empresa);
            }
            Licencia licencia = bodega.getLicencia();
            if (licencia != null) {
                licencia.getBodegaCollection().remove(bodega);
                licencia = em.merge(licencia);
            }
            em.remove(bodega);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Bodega> findBodegaEntities() {
        return findBodegaEntities(true, -1, -1);
    }

    public List<Bodega> findBodegaEntities(int maxResults, int firstResult) {
        return findBodegaEntities(false, maxResults, firstResult);
    }

    private List<Bodega> findBodegaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Bodega.class));
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

    public Bodega findBodega(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Bodega.class, id);
        } finally {
            em.close();
        }
    }

    public int getBodegaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Bodega> rt = cq.from(Bodega.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
