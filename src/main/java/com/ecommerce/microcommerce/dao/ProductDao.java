package com.ecommerce.microcommerce.dao;

import com.ecommerce.microcommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDao extends JpaRepository<Product,Integer> {
    public List<Product> findAll();
    public Product findById(int id);
    public Product save(Product product);
    public List<Product> findByPrixGreaterThan(int prixLimit);
    public List<Product> findByNomLike(String recherche);

    @Query("SELECT p FROM Product p WHERE p.prix > :prixLimit")
    public List<Product> chercherUnProduitCher(@Param("prixLimit")int prixLimit);

    public List<Product> findByOrderByNomAsc();
}
