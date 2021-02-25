package com.gofundme.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.gofundme.server.model.AddressModel
import com.gofundme.server.model.UserModel
import com.gofundme.server.repository.UserRepository
import com.gofundme.server.requestHandler.LoginHandler
import com.gofundme.server.requestHandler.RegisterHandler
import com.gofundme.server.service.JwtService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledOnJre
import org.junit.jupiter.api.condition.JRE
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

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

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var jwtService:JwtService

    @Autowired
    lateinit var userRepository: UserRepository




    @Test
    @Order(1)
    @DisplayName("/api/v1/auth/register  -Expect status 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun registerUser(){

        // GIVEN
        val registrationDetails= RegisterHandler(username = "jos123",email = "jos@gmail.com",password = "123456",country = "Kenya",city = "Nairobi")
        val jsonBody=objectMapper.writeValueAsString(registrationDetails)

        // WHEN
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/register").secure(true).content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(
                MediaType.APPLICATION_JSON)
        )
        // EXPECTATIONS
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
            .andReturn()


    }
    @Test
    @Order(2)
    @DisplayName("/api/v1/auth/register -Expect status 400")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun doNotRegisterUser(){

        //GIVEN WRONG DETAILS
        val registrationDetails= RegisterHandler(username = "23",email = "jos.com",password = "1",country = "Kenya",city = "Nairobi")
        val jsonBody=objectMapper.writeValueAsString(registrationDetails)

        // WHEN
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/register").secure(true).content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(
                MediaType.APPLICATION_JSON)
        )

            // EXPECTATIONS
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()
    }
    @Test
    @Order(3)
    @DisplayName("/api/v1/auth/activate/token -Expect status 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun activateEmail(){
        //GIVEN A TOKEN
        val token=jwtService.generateActivationToken(email = "jos@gmail.com")

        //WHEN
        mockMvc.perform (
        MockMvcRequestBuilders.put("/api/v1/auth/activate/$token").secure(true).contentType(MediaType.APPLICATION_JSON).accept( MediaType.APPLICATION_JSON)
        )
        //EXPECTATIONS
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
            .andReturn()
    }
    @Test
    @Order(4)
    @DisplayName("/api/v1/auth/activate/token -Expect status 400")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun doNotActivateEmail(){
        //GIVEN WRONG TOKEN
        val token=jwtService.generateActivationToken(email = "jos@gmail.com")

        //WHEN
        mockMvc.perform (
            MockMvcRequestBuilders.put("/api/v1/auth/activate/$token").secure(true).contentType(MediaType.APPLICATION_JSON).accept( MediaType.APPLICATION_JSON)
        )
            //EXPECTATIONS
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()
    }
    @Test
    @Order(5)
    @DisplayName("/api/v1/auth/login -Expect status 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun loginUser(){

        //GIVEN CORRECT CREDENTIALS
        val loginDetails=objectMapper.writeValueAsString(LoginHandler(email = "jos@gmail.com",password = "123456"))
        //WHEN
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/login").secure(true).content(loginDetails).contentType(MediaType.APPLICATION_JSON).accept(
                MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists())
            .andReturn()
    }
    @Test
    @Order(6)
    @DisplayName("/api/v1/auth/login -Expect status 400")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun doNotLoginUser(){

        //GIVEN CORRECT CREDENTIALS
        val loginDetails=objectMapper.writeValueAsString(LoginHandler(email = "abc@gmail.com",password = "123456"))
        //WHEN
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/login").secure(true).content(loginDetails).contentType(MediaType.APPLICATION_JSON).accept(
                MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
            .andReturn()
    }
    @Test
    @Order(7)
    @DisplayName("/api/v1/admin/all-users -Expect status 400")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun getAllUsers(){
        //GIVEN A TOKEN
        val userDetails=userRepository.findByEmail("jos@gmail.com")

        var jwtToken=jwtService.generateLoginToken(userDetails)

        //WHEN
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/admin/all-users").secure(true).contentType(MediaType.APPLICATION_JSON).accept(
                MediaType.APPLICATION_JSON).header("Authorization", "Bearer $jwtToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

    }

}
