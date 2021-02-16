package com.gofundme.server.model

import com.fasterxml.jackson.annotation.JsonBackReference
import org.hibernate.annotations.Type
import java.io.Serializable
import javax.persistence.*

@Entity
class FileModel:Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    var id:Long=0

    @Column(nullable = false)
    var name:String?

    @Column(nullable = false)
    var type:String?


    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    var data:ByteArray

    @OneToOne(mappedBy = "file",orphanRemoval = true)
    @JsonBackReference
    var donation:DonationsModel?=null

    constructor(name:String?,type:String?,data:ByteArray){
        this.id=id
        this.data=data
        this.donation=donation
        this.name=name
        this.type=type

    }

}