package iuh.fit.se.productservice.services;

import iuh.fit.se.productservice.entities.Product;

import java.util.List;

public interface ProductService {
    public Product save(Product product);
    public Product findById(Long id);
    public List<Product> findAll();
}
