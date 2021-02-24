package com.gofundme.server

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [ServerApplication::class]
)
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application-test.properties"])
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("GO-FUND-ME INTEGRATION TESTS")
class ServerApplicationTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun contextLoads() {
    }

}
