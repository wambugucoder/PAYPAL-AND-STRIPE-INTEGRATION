package com.gofundme.server.model

import com.vladmihalcea.hibernate.type.array.LongArrayType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*


@TypeDefs(
    TypeDef(name = "long-array", typeClass = LongArrayType::class)
)
@Entity
@EntityListeners(AuditingEntityListener::class)
class DonationsModel:Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    var id:Long=0

    @Column(nullable = false)
    var details:String

    @Column(nullable = false)
    var category:String

    @Column(nullable = false)
    var target:String

    @Column(nullable = false)
    var moneyDonated:Int=0

    @Type(type="long-array")
    @Column(nullable = false)
    var donors: Array<Long> = emptyArray()

    @Column(nullable = false)
    var createdBy:String

    @CreatedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column( nullable = false, columnDefinition = "TIMESTAMP")
    var lastModifiedDate: LocalDateTime = LocalDateTime.now()

  constructor(details:String,createdBy:String,category:String,target:String){
      this.target=target
      this.details=details
      this.createdBy=createdBy
      this.category=category
      this.id=id
      this.lastModifiedDate=lastModifiedDate
      this.createdDate=createdDate
      this.donors=donors
      this.moneyDonated=moneyDonated
  }


}