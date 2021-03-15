package helloworld;

import java.util.List;

public interface ProductDao {
    
    public List<Product> listAllProducts();

    public Product getProductById(String id);

    public void saveProduct(Product product);

    public void deleteProduct(String id);
}
