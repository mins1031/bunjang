package com.min.bunjang.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.min.bunjang.category.repository.FirstProductCategoryRepository;
import com.min.bunjang.category.repository.SecondProductCategoryRepository;
import com.min.bunjang.category.repository.ThirdProductCategoryRepository;
import com.min.bunjang.common.database.DatabaseFormat;
import com.min.bunjang.member.repository.MemberRepository;
import com.min.bunjang.product.repository.ProductRepository;
import com.min.bunjang.store.repository.StoreRepository;
import com.min.bunjang.testconfig.RestDocsConfiguration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("h2")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class DocumentBaseTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected StoreRepository storeRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected FirstProductCategoryRepository firstProductCategoryRepository;

    @Autowired
    protected SecondProductCategoryRepository secondProductCategoryRepository;

    @Autowired
    protected ThirdProductCategoryRepository thirdProductCategoryRepository;

    @Autowired
    protected BCryptPasswordEncoder bCryptPasswordEncoder;

    @LocalServerPort
    int port;

    @Autowired
    protected DatabaseFormat databaseFormat;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

}
