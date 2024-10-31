package guru.springframework.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import guru.springframework.domain.Product;
import guru.springframework.services.ProductService;


@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    private ResponseEntity<Product> getProductById(Integer id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("products", productService.listAllProducts());
        return "products";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> showProduct(@PathVariable Integer id) {
        return getProductById(id);
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Integer id, Model model) {
        ResponseEntity<Product> responseEntity = getProductById(id);
        if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
            return "error/404"; // Return 404 page if product is not found
        }
        model.addAttribute("product", responseEntity.getBody());
        return "productform";
    }

    @GetMapping("/new")
    public String newProduct(Model model) {
        model.addAttribute("product", new Product());
        return "productform";
    }

    @PostMapping
    public ResponseEntity<String> saveProduct(@ModelAttribute Product product) {
        try {
            productService.saveProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Product created with ID: " + product.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving the product.");
        }
    }
}
