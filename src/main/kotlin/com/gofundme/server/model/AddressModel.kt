package com.gofundme.server.model


import com.fasterxml.jackson.annotation.JsonBackReference
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
class AddressModel:Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long =0

    @Column(nullable = false)
    var country:String

    @Column(nullable = false)
    var city:String

    @OneToOne(mappedBy = "address",orphanRemoval = true)
    @JsonBackReference
    var user:UserModel?=null


    constructor(country:String,city:String){
        this.id=id
        this.country=country
        this.city=city
        this.user=user
    }

    constructor(addressModel: AddressModel){
        this.id=id
        this.country=addressModel.country
        this.city=addressModel.city
        this.user=addressModel.user

    }

}