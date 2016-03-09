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
import com.cycsystems.BackEnd.Clases.Entidades.Proveedor;
import com.cycsystems.BackEnd.Clases.Entidades.TipoProducto;
import com.cycsystems.BackEnd.Clases.Entidades.Ubicacion;
import java.util.ArrayList;
import java.util.Collection;
import com.cycsystems.BackEnd.Clases.Entidades.Detallecotizacion;
import com.cycsystems.BackEnd.Clases.Entidades.Producto;
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
public class ProductoJpaController implements Serializable {

    public ProductoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Producto producto) throws PreexistingEntityException, Exception {
        if (producto.getUbicacionCollection() == null) {
            producto.setUbicacionCollection(new ArrayList<Ubicacion>());
        }
        if (producto.getDetallecotizacionCollection() == null) {
            producto.setDetallecotizacionCollection(new ArrayList<Detallecotizacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proveedor proveedor = producto.getProveedor();
            if (proveedor != null) {
                proveedor = em.getReference(proveedor.getClass(), proveedor.getIdProveedor());
                producto.setProveedor(proveedor);
            }
            TipoProducto tipoProducto = producto.getTipoProducto();
            if (tipoProducto != null) {
                tipoProducto = em.getReference(tipoProducto.getClass(), tipoProducto.getIdTipoProducto());
                producto.setTipoProducto(tipoProducto);
            }
            Collection<Ubicacion> attachedUbicacionCollection = new ArrayList<Ubicacion>();
            for (Ubicacion ubicacionCollectionUbicacionToAttach : producto.getUbicacionCollection()) {
                ubicacionCollectionUbicacionToAttach = em.getReference(ubicacionCollectionUbicacionToAttach.getClass(), ubicacionCollectionUbicacionToAttach.getIdUbicacion());
                attachedUbicacionCollection.add(ubicacionCollectionUbicacionToAttach);
            }
            producto.setUbicacionCollection(attachedUbicacionCollection);
            Collection<Detallecotizacion> attachedDetallecotizacionCollection = new ArrayList<Detallecotizacion>();
            for (Detallecotizacion detallecotizacionCollectionDetallecotizacionToAttach : producto.getDetallecotizacionCollection()) {
                detallecotizacionCollectionDetallecotizacionToAttach = em.getReference(detallecotizacionCollectionDetallecotizacionToAttach.getClass(), detallecotizacionCollectionDetallecotizacionToAttach.getDetallecotizacionPK());
                attachedDetallecotizacionCollection.add(detallecotizacionCollectionDetallecotizacionToAttach);
            }
            producto.setDetallecotizacionCollection(attachedDetallecotizacionCollection);
            em.persist(producto);
            if (proveedor != null) {
                proveedor.getProductoCollection().add(producto);
                proveedor = em.merge(proveedor);
            }
            if (tipoProducto != null) {
                tipoProducto.getProductoCollection().add(producto);
                tipoProducto = em.merge(tipoProducto);
            }
            for (Ubicacion ubicacionCollectionUbicacion : producto.getUbicacionCollection()) {
                Producto oldProductoOfUbicacionCollectionUbicacion = ubicacionCollectionUbicacion.getProducto();
                ubicacionCollectionUbicacion.setProducto(producto);
                ubicacionCollectionUbicacion = em.merge(ubicacionCollectionUbicacion);
                if (oldProductoOfUbicacionCollectionUbicacion != null) {
                    oldProductoOfUbicacionCollectionUbicacion.getUbicacionCollection().remove(ubicacionCollectionUbicacion);
                    oldProductoOfUbicacionCollectionUbicacion = em.merge(oldProductoOfUbicacionCollectionUbicacion);
                }
            }
            for (Detallecotizacion detallecotizacionCollectionDetallecotizacion : producto.getDetallecotizacionCollection()) {
                Producto oldProductoOfDetallecotizacionCollectionDetallecotizacion = detallecotizacionCollectionDetallecotizacion.getProducto();
                detallecotizacionCollectionDetallecotizacion.setProducto(producto);
                detallecotizacionCollectionDetallecotizacion = em.merge(detallecotizacionCollectionDetallecotizacion);
                if (oldProductoOfDetallecotizacionCollectionDetallecotizacion != null) {
                    oldProductoOfDetallecotizacionCollectionDetallecotizacion.getDetallecotizacionCollection().remove(detallecotizacionCollectionDetallecotizacion);
                    oldProductoOfDetallecotizacionCollectionDetallecotizacion = em.merge(oldProductoOfDetallecotizacionCollectionDetallecotizacion);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProducto(producto.getCodigo()) != null) {
                throw new PreexistingEntityException("Producto " + producto + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Producto producto) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Producto persistentProducto = em.find(Producto.class, producto.getCodigo());
            Proveedor proveedorOld = persistentProducto.getProveedor();
            Proveedor proveedorNew = producto.getProveedor();
            TipoProducto tipoProductoOld = persistentProducto.getTipoProducto();
            TipoProducto tipoProductoNew = producto.getTipoProducto();
            Collection<Ubicacion> ubicacionCollectionOld = persistentProducto.getUbicacionCollection();
            Collection<Ubicacion> ubicacionCollectionNew = producto.getUbicacionCollection();
            Collection<Detallecotizacion> detallecotizacionCollectionOld = persistentProducto.getDetallecotizacionCollection();
            Collection<Detallecotizacion> detallecotizacionCollectionNew = producto.getDetallecotizacionCollection();
            List<String> illegalOrphanMessages = null;
            for (Ubicacion ubicacionCollectionOldUbicacion : ubicacionCollectionOld) {
                if (!ubicacionCollectionNew.contains(ubicacionCollectionOldUbicacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ubicacion " + ubicacionCollectionOldUbicacion + " since its producto field is not nullable.");
                }
            }
            for (Detallecotizacion detallecotizacionCollectionOldDetallecotizacion : detallecotizacionCollectionOld) {
                if (!detallecotizacionCollectionNew.contains(detallecotizacionCollectionOldDetallecotizacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detallecotizacion " + detallecotizacionCollectionOldDetallecotizacion + " since its producto field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (proveedorNew != null) {
                proveedorNew = em.getReference(proveedorNew.getClass(), proveedorNew.getIdProveedor());
                producto.setProveedor(proveedorNew);
            }
            if (tipoProductoNew != null) {
                tipoProductoNew = em.getReference(tipoProductoNew.getClass(), tipoProductoNew.getIdTipoProducto());
                producto.setTipoProducto(tipoProductoNew);
            }
            Collection<Ubicacion> attachedUbicacionCollectionNew = new ArrayList<Ubicacion>();
            for (Ubicacion ubicacionCollectionNewUbicacionToAttach : ubicacionCollectionNew) {
                ubicacionCollectionNewUbicacionToAttach = em.getReference(ubicacionCollectionNewUbicacionToAttach.getClass(), ubicacionCollectionNewUbicacionToAttach.getIdUbicacion());
                attachedUbicacionCollectionNew.add(ubicacionCollectionNewUbicacionToAttach);
            }
            ubicacionCollectionNew = attachedUbicacionCollectionNew;
            producto.setUbicacionCollection(ubicacionCollectionNew);
            Collection<Detallecotizacion> attachedDetallecotizacionCollectionNew = new ArrayList<Detallecotizacion>();
            for (Detallecotizacion detallecotizacionCollectionNewDetallecotizacionToAttach : detallecotizacionCollectionNew) {
                detallecotizacionCollectionNewDetallecotizacionToAttach = em.getReference(detallecotizacionCollectionNewDetallecotizacionToAttach.getClass(), detallecotizacionCollectionNewDetallecotizacionToAttach.getDetallecotizacionPK());
                attachedDetallecotizacionCollectionNew.add(detallecotizacionCollectionNewDetallecotizacionToAttach);
            }
            detallecotizacionCollectionNew = attachedDetallecotizacionCollectionNew;
            producto.setDetallecotizacionCollection(detallecotizacionCollectionNew);
            producto = em.merge(producto);
            if (proveedorOld != null && !proveedorOld.equals(proveedorNew)) {
                proveedorOld.getProductoCollection().remove(producto);
                proveedorOld = em.merge(proveedorOld);
            }
            if (proveedorNew != null && !proveedorNew.equals(proveedorOld)) {
                proveedorNew.getProductoCollection().add(producto);
                proveedorNew = em.merge(proveedorNew);
            }
            if (tipoProductoOld != null && !tipoProductoOld.equals(tipoProductoNew)) {
                tipoProductoOld.getProductoCollection().remove(producto);
                tipoProductoOld = em.merge(tipoProductoOld);
            }
            if (tipoProductoNew != null && !tipoProductoNew.equals(tipoProductoOld)) {
                tipoProductoNew.getProductoCollection().add(producto);
                tipoProductoNew = em.merge(tipoProductoNew);
            }
            for (Ubicacion ubicacionCollectionNewUbicacion : ubicacionCollectionNew) {
                if (!ubicacionCollectionOld.contains(ubicacionCollectionNewUbicacion)) {
                    Producto oldProductoOfUbicacionCollectionNewUbicacion = ubicacionCollectionNewUbicacion.getProducto();
                    ubicacionCollectionNewUbicacion.setProducto(producto);
                    ubicacionCollectionNewUbicacion = em.merge(ubicacionCollectionNewUbicacion);
                    if (oldProductoOfUbicacionCollectionNewUbicacion != null && !oldProductoOfUbicacionCollectionNewUbicacion.equals(producto)) {
                        oldProductoOfUbicacionCollectionNewUbicacion.getUbicacionCollection().remove(ubicacionCollectionNewUbicacion);
                        oldProductoOfUbicacionCollectionNewUbicacion = em.merge(oldProductoOfUbicacionCollectionNewUbicacion);
                    }
                }
            }
            for (Detallecotizacion detallecotizacionCollectionNewDetallecotizacion : detallecotizacionCollectionNew) {
                if (!detallecotizacionCollectionOld.contains(detallecotizacionCollectionNewDetallecotizacion)) {
                    Producto oldProductoOfDetallecotizacionCollectionNewDetallecotizacion = detallecotizacionCollectionNewDetallecotizacion.getProducto();
                    detallecotizacionCollectionNewDetallecotizacion.setProducto(producto);
                    detallecotizacionCollectionNewDetallecotizacion = em.merge(detallecotizacionCollectionNewDetallecotizacion);
                    if (oldProductoOfDetallecotizacionCollectionNewDetallecotizacion != null && !oldProductoOfDetallecotizacionCollectionNewDetallecotizacion.equals(producto)) {
                        oldProductoOfDetallecotizacionCollectionNewDetallecotizacion.getDetallecotizacionCollection().remove(detallecotizacionCollectionNewDetallecotizacion);
                        oldProductoOfDetallecotizacionCollectionNewDetallecotizacion = em.merge(oldProductoOfDetallecotizacionCollectionNewDetallecotizacion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = producto.getCodigo();
                if (findProducto(id) == null) {
                    throw new NonexistentEntityException("The producto with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Producto producto;
            try {
                producto = em.getReference(Producto.class, id);
                producto.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The producto with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Ubicacion> ubicacionCollectionOrphanCheck = producto.getUbicacionCollection();
            for (Ubicacion ubicacionCollectionOrphanCheckUbicacion : ubicacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Producto (" + producto + ") cannot be destroyed since the Ubicacion " + ubicacionCollectionOrphanCheckUbicacion + " in its ubicacionCollection field has a non-nullable producto field.");
            }
            Collection<Detallecotizacion> detallecotizacionCollectionOrphanCheck = producto.getDetallecotizacionCollection();
            for (Detallecotizacion detallecotizacionCollectionOrphanCheckDetallecotizacion : detallecotizacionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Producto (" + producto + ") cannot be destroyed since the Detallecotizacion " + detallecotizacionCollectionOrphanCheckDetallecotizacion + " in its detallecotizacionCollection field has a non-nullable producto field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Proveedor proveedor = producto.getProveedor();
            if (proveedor != null) {
                proveedor.getProductoCollection().remove(producto);
                proveedor = em.merge(proveedor);
            }
            TipoProducto tipoProducto = producto.getTipoProducto();
            if (tipoProducto != null) {
                tipoProducto.getProductoCollection().remove(producto);
                tipoProducto = em.merge(tipoProducto);
            }
            em.remove(producto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Producto> findProductoEntities() {
        return findProductoEntities(true, -1, -1);
    }

    public List<Producto> findProductoEntities(int maxResults, int firstResult) {
        return findProductoEntities(false, maxResults, firstResult);
    }

    private List<Producto> findProductoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Producto.class));
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

    public Producto findProducto(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Producto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Producto> rt = cq.from(Producto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
