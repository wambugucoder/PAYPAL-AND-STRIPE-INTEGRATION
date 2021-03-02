package com.gofundme.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.gofundme.server.repository.DonationsRepository
import com.gofundme.server.repository.UserRepository
import com.gofundme.server.requestHandler.*
import com.gofundme.server.service.JwtService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledOnJre
import org.junit.jupiter.api.condition.JRE
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
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

    @Autowired
    lateinit var donationsRepository:DonationsRepository




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
        val registrationDetails= RegisterHandler(username = "jos123",email = "jos@gmail.com",password = "123456",country = "Kenya",city = "Nairobi")
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
            MockMvcRequestBuilders.put("/api/v1/auth/activate/$token").secure(false).contentType(MediaType.APPLICATION_JSON).accept( MediaType.APPLICATION_JSON)
        )
            //EXPECTATIONS
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
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
    @DisplayName("/api/v1/moderator/makeAdmin/{id} -Expect status 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun makeAdmin(){
        //GIVEN A TOKEN AND USER ID
        val userDetails=userRepository.findByEmail("jos@gmail.com")

        val jwtToken=jwtService.generateLoginToken(userDetails)

        //WHEN
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/moderator/makeAdmin/${userDetails.id}")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer $jwtToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Order(8)
    @DisplayName("/api/v1/admin/all-users -Expect status 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun getAllUsers(){
        //GIVEN A TOKEN
        val userDetails=userRepository.findByEmail("jos@gmail.com")

        val jwtToken=jwtService.generateLoginToken(userDetails)

        //WHEN
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/admin/all-users").secure(true).contentType(MediaType.APPLICATION_JSON).accept(
                MediaType.APPLICATION_JSON).header("Authorization", "Bearer $jwtToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

    }
    @Test
    @Order(9)
    @DisplayName("/api/v1/admin/all-users -Expect status 300")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun failToFetchAllUsers(){
        //GIVEN NO TOKEN
        //WHEN
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/admin/all-users").secure(false).contentType(MediaType.APPLICATION_JSON).accept(
                MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
            .andReturn()

    }
    @Test
    @Order(10)
    @DisplayName("/api/v1/admin/specific-user/{id} -Expect status 200")
    fun getSpecificUserAsAdmin(){
        //GIVEN TOKEN AND ADMIN CREDENTIALS
        val userDetails=userRepository.findByEmail("jos@gmail.com")
        val jwtToken=jwtService.generateLoginToken(userDetails)

        //PERFORM THE GET REQUEST
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/admin/specific-user/${userDetails.id}")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer $jwtToken")
        )
                //EXPECTATIONS
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
    @Test
    @Order(11)
    @DisplayName("/api/v1/admin/specific-user/{id} -Expect status 300 or 400")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun doNotGetSpecificUser(){
        //GIVEN NO TOKEN AND ASSUMING USER HAS ROLE USER
        val userDetails=userRepository.findByEmail("jos@gmail.com")
        // PERFORM THE GET REQUEST
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/admin/specific-user/${userDetails.id}")
                .secure(false)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)

        )
            //EXPECTATIONS
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
    }
    @Test
    @Order(12)
    @DisplayName("/api/v1/admin/update-user/{id} -Expect status 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun updateUserDetails(){
        //GIVEN TOKEN AND ADMIN CREDENTIALS
        val userDetails=userRepository.findByEmail("jos@gmail.com")
        val jwtToken=jwtService.generateLoginToken(userDetails)
        val detailsToUpdate = objectMapper.writeValueAsString(UpdateUserRequest("Wambugu","abc@gmail.com"))

        //PERFORM THE PUT REQUEST
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/admin/update-user/${userDetails.id}")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(detailsToUpdate)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer $jwtToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

    }
    @Test
    @Order(13)
    @DisplayName("/api/v1/admin/update-user/{id} -Expect status 300 or 400")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun failToUpdateWithPartialDetails(){
        //GIVEN TOKEN AND ADMIN CREDENTIALS
        val userDetails=userRepository.findByEmail("abc@gmail.com")
        val jwtToken=jwtService.generateLoginToken(userDetails)
        val detailsToUpdate = objectMapper.writeValueAsString(UpdateUserRequest("Wambugu","abc@gmail.com"))


        //PERFORM PUT REQUEST WITH NO DATA
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/admin/update-user/${userDetails.id}")
                .secure(false)
                .contentType(MediaType.APPLICATION_JSON)
                .content(detailsToUpdate)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer $jwtToken")
        )
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)

    }
    @Test
    @Order(14)
    @DisplayName("/api/v1/users/{id}/create-donation - Expect status 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun createDonation(){
        //GIVEN TOKEN AND USER CREDENTIALS
        val userDetails=userRepository.findByEmail("abc@gmail.com")
        val jwtToken=jwtService.generateLoginToken(userDetails)

        //CREATE TEST FILE
        val file = MockMultipartFile(
            "file",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".toByteArray()
        )
        //PERFORM POST REQUEST
        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/v1/users/${userDetails.id}/create-donation?details=sales&category=sale&target=23")
                .file(file)
                .secure(true)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer $jwtToken")

        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
    @Test
    @Order(15)
    @DisplayName("/api/v1/users/{id}/all-donations - Expect 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun getAllDonations(){
        //GIVEN A TOKEN
        val userDetails=userRepository.findByEmail("abc@gmail.com")
        val jwtToken=jwtService.generateLoginToken(userDetails)

       //PERFORM THE GET REQUEST
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/users/${userDetails.id}/all-donations")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer $jwtToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

    }
    @Test
    @Order(16)
    @DisplayName("/api/v1/users/{uid}/donations/{did} -Expect 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun getSpecificDonationById(){
        //GIVEN A TOKEN,USER ID,DONATION ID
        val userDetails=userRepository.findByEmail("abc@gmail.com")
        val jwtToken=jwtService.generateLoginToken(userDetails)
        val donationDetails=donationsRepository.findDonationByDetails("sales")

        //PERFORM GET REQUEST FOR SPECIFIC DONATION
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/users/${userDetails.id}/donations/${donationDetails.id} ")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer $jwtToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
    @Test
    @Order(17)
    @DisplayName("/api/v1/{uid}/{did}/paypal/make-pay -Expect 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun payWithPaypal(){
        // GIVEN A TOKEN,USER ID,DONATION ID
        val userDetails=userRepository.findByEmail("abc@gmail.com")
        val jwtToken=jwtService.generateLoginToken(userDetails)
        val donationDetails=donationsRepository.findDonationByDetails("sales")
        val paypalData= objectMapper.writeValueAsString( PaypalRequest("10"))

        // PERFORM THE PAYMENT PROCEDURE
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/${userDetails.id}/${donationDetails.id}/paypal/make-pay")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(paypalData)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer $jwtToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
            .andReturn()
    }
    @Test
    @Order(18)
    @DisplayName("/api/v1/paypal-payment/{uid}/{did}/cancel -Expect 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun cancelPaypalTransaction(){
        // GIVEN A TOKEN,USER ID,DONATION ID
        val userDetails=userRepository.findByEmail("abc@gmail.com")
        val jwtToken=jwtService.generateLoginToken(userDetails)
        val donationDetails=donationsRepository.findDonationByDetails("sales")

        // PERFORM THE CANCEL REQUEST
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/paypal-payment/${userDetails.id}/${donationDetails.id}/cancel")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer $jwtToken")
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()

    }
    @Test
    @Order(19)
    @DisplayName("/api/v1/paypal-payment/{uid}/{did}/success -Expect 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun failToCompletePayment(){
        // GIVEN A TOKEN,USER ID,DONATION ID
        val userDetails=userRepository.findByEmail("abc@gmail.com")
        val jwtToken=jwtService.generateLoginToken(userDetails)
        val donationDetails=donationsRepository.findDonationByDetails("sales")

        // PERFORM ...
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/paypal-payment/${userDetails.id}/${donationDetails.id}/success?paymentId=qwqewrwwqq&PayerID=232")
                .secure(true)
                .header("Authorization","Bearer $jwtToken")
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
            .andReturn()

    }
    @Test
    @Order(20)
    @DisplayName("/api/v1/{uid}/{did}/stripe/make-pay -Expect 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun makeStripePayment(){
        // GIVEN A TOKEN,USER ID,DONATION ID AND CARD DETAILS
        val userDetails=userRepository.findByEmail("abc@gmail.com")
        val jwtToken=jwtService.generateLoginToken(userDetails)
        val donationDetails=donationsRepository.findDonationByDetails("sales")
        val cardDetails=objectMapper.writeValueAsString(StripeChargeRequest(1000,"4242424242424242",7,2022,"2345")
        )
        // PERFORM ...
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/${userDetails.id}/${donationDetails.id}/stripe/make-pay")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(cardDetails)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer $jwtToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

    }

    @Test
    @Order(21)
    @DisplayName("/api/v1/users/{uid}/close-donation/{did} -Expect 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun closeDonation(){
        // GIVEN A TOKEN,USER ID,DONATION ID
        val userDetails=userRepository.findByEmail("abc@gmail.com")
        val jwtToken=jwtService.generateLoginToken(userDetails)
        val donationDetails=donationsRepository.findDonationByDetails("sales")

       // PERFORM...
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/users/${userDetails.id}/close-donation/${donationDetails.id}")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer $jwtToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

    }
    @Test
    @Order(22)
    @DisplayName("/api/v1/users/{uid}/delete-donation/{did} -Expect 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun deleteDonation(){
        // GIVEN A TOKEN,USER ID,DONATION ID
        val userDetails=userRepository.findByEmail("abc@gmail.com")
        val jwtToken=jwtService.generateLoginToken(userDetails)
        val donationDetails=donationsRepository.findDonationByDetails("sales")

        // PERFORM..
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/users/${userDetails.id}/delete-donation/${donationDetails.id}")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer $jwtToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

    }
    @Test
    @Order(23)
    @DisplayName("/api/v1/admin/block-user/{email} -Expect 200")
    @EnabledOnJre(JRE.JAVA_8,disabledReason = "Server was programmed to run on Java 8")
    fun blockUser(){
        // GIVEN A TOKEN,USER ID,DONATION ID AND CARD DETAILS
        val userDetails=userRepository.findByEmail("abc@gmail.com")
        val jwtToken=jwtService.generateLoginToken(userDetails)

        //PERFORM
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/admin/block-user/abc@gmail.com")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer $jwtToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

    }


}
