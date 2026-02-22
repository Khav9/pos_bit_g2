package com.example.PosBit;

import com.example.PosBit.model.Category;
import com.example.PosBit.model.Order;
import com.example.PosBit.model.Product;
import com.example.PosBit.model.UserInfo;
import com.example.PosBit.repository.CategoryRepository;
import com.example.PosBit.repository.OrderItemRepository;
import com.example.PosBit.repository.OrderRepository;
import com.example.PosBit.repository.ProductRepository;
import com.example.PosBit.repository.UserInfoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PosIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @BeforeEach
    void cleanData() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userInfoRepository.deleteAll();
    }

    @Test
    void registrationAlwaysStoresRoleUser() throws Exception {
        String registerBody = """
                {
                  "username": "new_user",
                  "password": "secret123",
                  "roles": "ROLE_USER,ROLE_ADMIN"
                }
                """;

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User created successfully")));

        UserInfo savedUser = userInfoRepository.findByUsername("new_user").orElseThrow();
        assertThat(savedUser.getRoles()).isEqualTo("ROLE_USER");
        assertThat(passwordEncoder.matches("secret123", savedUser.getPassword())).isTrue();

        String token = loginAndGetToken("new_user", "secret123");
        JsonNode payload = decodeJwtPayload(token);

        assertThat(payload.get("roles").toString()).contains("ROLE_USER");
        assertThat(payload.get("roles").toString()).doesNotContain("ROLE_ADMIN");
    }

    @Test
    void createProductRequiresAdminRole() throws Exception {
        Category category = new Category();
        category.setCategoryId("CAT001");
        category.setCategoryName("Drinks");
        category.setCategoryDescription("Beverages");
        category.setStatus("active");
        categoryRepository.save(category);

        String userToken = registerAndLogin("cashier_user", "secret123");
        String productBody = """
                {
                  "productName": "Orange Juice",
                  "productDescription": "Fresh juice",
                  "productPrice": 2.5,
                  "productQuantity": 20,
                  "isActive": true,
                  "category": { "categoryId": "CAT001" }
                }
                """;

        mockMvc.perform(post("/products")
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productBody))
                .andExpect(status().isForbidden());

        createAdminUser("admin_user", "secret123");
        String adminToken = loginAndGetToken("admin_user", "secret123");

        mockMvc.perform(post("/products")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productBody))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Product created successfully")));

        assertThat(productRepository.count()).isEqualTo(1);
    }

    @Test
    void createOrderDecreasesStockAndPersistsSale() throws Exception {
        Category category = new Category();
        category.setCategoryId("CAT002");
        category.setCategoryName("Snacks");
        category.setCategoryDescription("Food");
        category.setStatus("active");
        categoryRepository.save(category);

        Product product = new Product();
        product.setProductName("Potato Chips");
        product.setProductDescription("Salted");
        product.setProductPrice(3.0);
        product.setProductQuantity(10);
        product.setIsActive(true);
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);

        String userToken = registerAndLogin("sales_user", "secret123");
        String orderBody = """
                {
                  "items": [
                    {
                      "product": { "productId": %d },
                      "quantity": 3
                    }
                  ]
                }
                """.formatted(savedProduct.getProductId());

        mockMvc.perform(post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").isNumber())
                .andExpect(jsonPath("$.paymentStatus").value("PAID"))
                .andExpect(jsonPath("$.totalAmount").value(9.0));

        Product updatedProduct = productRepository.findById(savedProduct.getProductId()).orElseThrow();
        assertThat(updatedProduct.getProductQuantity()).isEqualTo(7);

        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(orderItemRepository.count()).isEqualTo(1);
        Order savedOrder = orderRepository.findAll().getFirst();
        assertThat(savedOrder.getTotalAmount()).isEqualTo(9.0);
    }

    @Test
    void protectedEndpointRejectsMissingToken() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isForbidden());
    }

    @Test
    void ordersAndSummaryAreScopedPerUser() throws Exception {
        Category category = new Category();
        category.setCategoryId("CAT100");
        category.setCategoryName("General");
        category.setCategoryDescription("General goods");
        category.setStatus("active");
        categoryRepository.save(category);

        Product product = new Product();
        product.setProductName("Scoped Item");
        product.setProductDescription("Test item");
        product.setProductPrice(5.0);
        product.setProductQuantity(30);
        product.setIsActive(true);
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);

        String user1Token = registerAndLogin("scope_user_1", "secret123");
        String user2Token = registerAndLogin("scope_user_2", "secret123");

        createOrderWithToken(user1Token, savedProduct.getProductId(), 1);
        createOrderWithToken(user1Token, savedProduct.getProductId(), 2);
        createOrderWithToken(user2Token, savedProduct.getProductId(), 1);

        mockMvc.perform(post("/sales-orders/all")
                        .header(HttpHeaders.AUTHORIZATION, bearer(user1Token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(post("/sales-orders/summary")
                        .header(HttpHeaders.AUTHORIZATION, bearer(user1Token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(2));

        mockMvc.perform(post("/sales-orders/all")
                        .header(HttpHeaders.AUTHORIZATION, bearer(user2Token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(post("/sales-orders/summary")
                        .header(HttpHeaders.AUTHORIZATION, bearer(user2Token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(1));
    }

    @Test
    void usersEndpointIsAdminOnly() throws Exception {
        String userToken = registerAndLogin("staff_user", "secret123");
        mockMvc.perform(get("/users")
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isForbidden());

        createAdminUser("boss_user", "secret123");
        String adminToken = loginAndGetToken("boss_user", "secret123");
        mockMvc.perform(get("/users")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk());
    }

    @Test
    void userCanViewAndUpdateOwnProfile() throws Exception {
        String token = registerAndLogin("profile_user", "secret123");

        mockMvc.perform(get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("profile_user"))
                .andExpect(jsonPath("$.roles").value("ROLE_USER"));

        String updateBody = """
                {
                  "fullName": "Profile User",
                  "email": "profile_user@posbit.local",
                  "phone": "+1-202-555-0110",
                  "address": "Test Address"
                }
                """;

        mockMvc.perform(put("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Profile User"))
                .andExpect(jsonPath("$.email").value("profile_user@posbit.local"));
    }

    @Test
    void userCanChangePasswordAndLoginWithNewPassword() throws Exception {
        String token = registerAndLogin("password_user", "secret123");

        String passwordBody = """
                {
                  "currentPassword": "secret123",
                  "newPassword": "secret456",
                  "confirmPassword": "secret456"
                }
                """;

        mockMvc.perform(put("/users/me/password")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordBody))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Password updated successfully")));

        String newToken = loginAndGetToken("password_user", "secret456");
        assertThat(newToken).isNotBlank();
    }

    private String registerAndLogin(String username, String password) throws Exception {
        String registerBody = """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk());

        return loginAndGetToken(username, password);
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String loginBody = """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);

        return mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private void createAdminUser(String username, String rawPassword) {
        UserInfo admin = new UserInfo();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setRoles("ROLE_USER,ROLE_ADMIN");
        userInfoRepository.save(admin);
    }

    private JsonNode decodeJwtPayload(String token) throws Exception {
        String payloadPart = token.split("\\.")[1];
        byte[] decoded = Base64.getUrlDecoder().decode(payloadPart);
        String payloadJson = new String(decoded, StandardCharsets.UTF_8);
        return objectMapper.readTree(payloadJson);
    }

    private void createOrderWithToken(String token, Integer productId, int quantity) throws Exception {
        String orderBody = """
                {
                  "items": [
                    {
                      "product": { "productId": %d },
                      "quantity": %d
                    }
                  ]
                }
                """.formatted(productId, quantity);

        mockMvc.perform(post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderBody))
                .andExpect(status().isOk());
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
