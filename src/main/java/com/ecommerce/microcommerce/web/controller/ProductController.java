package com.ecommerce.microcommerce.web.controller;
import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exception.ProductNotFoundException;
import com.ecommerce.microcommerce.web.exception.ProduitGratuitException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.*;

@RestController
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private HttpServletRequest requestContext;

    @Autowired
    private ProductDao productDao;

    @GetMapping(value="/Produits")
    public List<Product> listeProduits() {
        logger.info("Début d'appel au service Produit pour la requête : " + requestContext.getHeader("req-id"));
        return productDao.findAll();

//        SimpleBeanPropertyFilter myFilter = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
//
//        FilterProvider filtersList = new SimpleFilterProvider().addFilter("dynamicFilter",myFilter);
//
//        MappingJacksonValue productsFilters = new MappingJacksonValue(products);
//
//        productsFilters.setFilters(filtersList);
//
//        return  productsFilters;

    }

    @GetMapping(value="/Produits/{id}")
    public Product afficherUnProduit(@PathVariable int id) {
        logger.info("Début d'appel au service Produit pour la requête : " + requestContext.getHeader("req-id"));
        Product prod = productDao.findById(id);
        if(prod==null) throw new ProductNotFoundException("Le produit avec l'id "+id+" n'existe pas frère ! Fais un effort !");
        return prod;
    }

    @GetMapping(value="test/produits/{prixLimit}")
    public List<Product> testDeRequetes(@PathVariable int prixLimit) {
        logger.info("Début d'appel au service Produit pour la requête : " + requestContext.getHeader("req-id"));
        return productDao.findByPrixGreaterThan(prixLimit);
    }

    @GetMapping(value="test2/produits/{recherche}")
    public List<Product> testDeRequetes(@PathVariable String recherche) {
        logger.info("Début d'appel au service Produit pour la requête : " + requestContext.getHeader("req-id"));
        return productDao.findByNomLike("%"+recherche+"%");
    }

    @PostMapping(value="/Produits")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) {
        logger.info("Début d'appel au service Produit pour la requête : " + requestContext.getHeader("req-id"));
        if(product.getPrix() == 0) throw new ProduitGratuitException("Tu peux pas faire ça gratuit frère ! Abuse pas !");
        Product productAdded = productDao.save(product);

        if( productAdded == null)
            return ResponseEntity.noContent().build();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping (value = "/Produits/{id}")
    public void supprimerProduit(@PathVariable int id) {
        productDao.delete(productDao.findById(id));
    }

    @PutMapping(value="/Produits")
    public void updateProduit(@Valid @RequestBody Product product) {
        productDao.save(product);
    }

    @GetMapping(value="/Produits/find/{prixLimit}")
    public List<Product> findExpensiveProduct(@PathVariable int prixLimit) {
        logger.info("Début d'appel au service Produit pour la requête : " + requestContext.getHeader("req-id"));
        return productDao.chercherUnProduitCher(prixLimit);
    }

    @GetMapping(value="/Produits/AdminProduits")
    public Map<String,Integer> calculerMargeProduit() {
        logger.info("Début d'appel au service Produit pour la requête : " + requestContext.getHeader("req-id"));
        List<Product> products = productDao.findAll();
        Map<String,Integer> maMap = new HashMap<>();
        for(Product p: products) {
            maMap.put(p.toString(),p.getPrix() - p.getPrixAchat());
        }
        return maMap;
    }

    @GetMapping(value="/Produits/Trier")
    public List<Product> trierProduitsParOrdreAlphabetique() {
        logger.info("Début d'appel au service Produit pour la requête : " + requestContext.getHeader("req-id"));
        return productDao.findByOrderByNomAsc();
    }

}
