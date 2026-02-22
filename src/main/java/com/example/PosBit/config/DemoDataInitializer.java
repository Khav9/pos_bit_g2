package com.example.PosBit.config;

import com.example.PosBit.model.Category;
import com.example.PosBit.model.Order;
import com.example.PosBit.model.OrderItem;
import com.example.PosBit.model.Product;
import com.example.PosBit.model.UserInfo;
import com.example.PosBit.repository.CategoryRepository;
import com.example.PosBit.repository.OrderRepository;
import com.example.PosBit.repository.ProductRepository;
import com.example.PosBit.repository.UserInfoRepository;
import com.example.PosBit.service.OrderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@org.springframework.core.annotation.Order(2)
public class DemoDataInitializer implements CommandLineRunner {

    private static final String ROLE_USER = "ROLE_USER";

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserInfoRepository userInfoRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final BCryptPasswordEncoder passwordEncoder;

    public DemoDataInitializer(CategoryRepository categoryRepository,
                               ProductRepository productRepository,
                               UserInfoRepository userInfoRepository,
                               OrderRepository orderRepository,
                               OrderService orderService,
                               BCryptPasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userInfoRepository = userInfoRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedCatalog();
        seedOrders();
    }

    private void seedUsers() {
        createCashierIfMissing("cashier01", "cashier123", "Sok Dara", "cashier01@posbit.local", "+1-202-555-0101");
        createCashierIfMissing("cashier02", "cashier123", "Lina Kim", "cashier02@posbit.local", "+1-202-555-0102");
    }

    private void createCashierIfMissing(String username, String password, String fullName, String email, String phone) {
        if (userInfoRepository.findByUsername(username).isPresent()) {
            return;
        }
        UserInfo user = new UserInfo();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(ROLE_USER);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress("Retail Branch");
        userInfoRepository.save(user);
    }

    private void seedCatalog() {
        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(List.of(
                    createCategory("BEV", "Beverages", "Cold and hot drinks", "active"),
                    createCategory("SNK", "Snacks", "Packaged snacks", "active"),
                    createCategory("FRU", "Fresh Fruits", "Seasonal fruits", "active"),
                    createCategory("HYG", "Hygiene", "Daily hygiene essentials", "active")
            ));
        }

        if (productRepository.count() == 0) {
            Category beverages = categoryRepository.findById("BEV").orElseThrow();
            Category snacks = categoryRepository.findById("SNK").orElseThrow();
            Category fruits = categoryRepository.findById("FRU").orElseThrow();
            Category hygiene = categoryRepository.findById("HYG").orElseThrow();

            productRepository.saveAll(List.of(
                    createProduct("Coca-Cola 330ml", "Can drink", 1.5, 120, beverages),
                    createProduct("Orange Juice 1L", "No sugar added", 3.2, 40, beverages),
                    createProduct("Potato Chips 80g", "Classic salted", 1.2, 65, snacks),
                    createProduct("Trail Mix 200g", "Nuts and dried fruits", 4.8, 25, snacks),
                    createProduct("Banana", "Per piece", 0.4, 200, fruits),
                    createProduct("Apple", "Red apple", 0.6, 160, fruits),
                    createProduct("Hand Sanitizer 250ml", "70% alcohol", 2.9, 30, hygiene),
                    createProduct("Tissue Box", "2-ply 200 sheets", 1.9, 55, hygiene)
            ));
        }
    }

    private Category createCategory(String id, String name, String description, String status) {
        Category category = new Category();
        category.setCategoryId(id);
        category.setCategoryName(name);
        category.setCategoryDescription(description);
        category.setStatus(status);
        return category;
    }

    private Product createProduct(String name, String description, double price, int quantity, Category category) {
        Product product = new Product();
        product.setProductName(name);
        product.setProductDescription(description);
        product.setProductPrice(price);
        product.setProductQuantity(quantity);
        product.setIsActive(true);
        product.setCategory(category);
        return product;
    }

    private void seedOrders() {
        if (orderRepository.count() > 0) {
            return;
        }

        List<Product> products = productRepository.findAll();
        if (products.size() < 4) {
            return;
        }

        createDemoOrder(List.of(
                createOrderItem(products.get(0).getProductId(), 2),
                createOrderItem(products.get(2).getProductId(), 3)
        ));

        createDemoOrder(List.of(
                createOrderItem(products.get(1).getProductId(), 1),
                createOrderItem(products.get(6).getProductId(), 2)
        ));

        createDemoOrder(List.of(
                createOrderItem(products.get(4).getProductId(), 6),
                createOrderItem(products.get(7).getProductId(), 1)
        ));
    }

    private OrderItem createOrderItem(Integer productId, int quantity) {
        Product productRef = new Product();
        productRef.setProductId(productId);
        OrderItem item = new OrderItem();
        item.setProduct(productRef);
        item.setQuantity(quantity);
        return item;
    }

    private void createDemoOrder(List<OrderItem> items) {
        Order order = new Order();
        order.setItems(new ArrayList<>(items));
        orderService.createOrder(order);
    }
}
