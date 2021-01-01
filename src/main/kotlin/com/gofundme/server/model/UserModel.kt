package com.gofundme.server.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)

class UserModel:Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id:Long=0

    @Column(nullable = false)
    var username:String

    @Column(unique = true,nullable = false)
    var email:String

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    var roles:UserRoles=UserRoles.ROLE_USER

    @Column(nullable = false)
    var password:String

    @OneToOne(orphanRemoval = true,fetch = FetchType.EAGER,cascade = [CascadeType.ALL])
    @JoinColumn(name="address_id",referencedColumnName = "id")
    @JsonManagedReference
    var address:AddressModel


    @Column(nullable = false)
    var isEnabled:Boolean=false

    @Column(nullable = false)
    var isAccountLocked:Boolean=true

    @Column(nullable = false)
    var isAccountNonExpired:Boolean=true


    @CreatedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column( nullable = false, columnDefinition = "TIMESTAMP")
    var lastModifiedDate: LocalDateTime = LocalDateTime.now()


    constructor(username:String,email:String,password:String,address:AddressModel){
        this.id=id
        this.username=username
        this.email=email
        this.password=password
        this.createdDate=createdDate
        this.roles=roles
        this.address=address
        this.isAccountLocked=isAccountLocked
        this.isEnabled=isEnabled
        this.isAccountNonExpired=isAccountNonExpired
        this.lastModifiedDate=lastModifiedDate


   }
     constructor(userModel: UserModel){
         this.id=userModel.id
         this.username=userModel.username
         this.email=userModel.email
         this.password=userModel.password
         this.createdDate=userModel.createdDate
         this.address=userModel.address
         this.roles=userModel.roles
         this.isAccountLocked=userModel.isAccountLocked
         this.isEnabled=userModel.isEnabled
         this.isAccountNonExpired=userModel.isAccountNonExpired
         this.lastModifiedDate=userModel.lastModifiedDate


    }



}