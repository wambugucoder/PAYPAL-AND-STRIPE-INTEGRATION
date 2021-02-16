package com.gofundme.server.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.vladmihalcea.hibernate.type.array.ListArrayType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@TypeDef(
    name = "list-array",
    typeClass = ListArrayType::class
    )
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
    var isOpen:Boolean=true

    @Column(nullable = false)
    var approved:Boolean=false

    @Column(nullable = false)
    var target:String

    @Column(nullable = false)
    var moneyDonated:Int=0

    @OneToOne(orphanRemoval = true,fetch = FetchType.EAGER,cascade = [CascadeType.ALL])
    @JoinColumn(name="file_id",referencedColumnName = "id")
    @JsonManagedReference
    var file:FileModel

    @Type(type = "list-array")
    @Column(
        nullable = false,
        name = "donors",
        columnDefinition = "text[]"
    )
   var donors: MutableList<String> =  ArrayList()


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    @JsonManagedReference
    var createdBy:UserModel

    @OneToMany(orphanRemoval = true,cascade = [CascadeType.ALL])
   // @JsonManagedReference
    var transactions:List<TransactionsModel>? = emptyList()

    @CreatedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column( nullable = false, columnDefinition = "TIMESTAMP")
    var lastModifiedDate: LocalDateTime = LocalDateTime.now()

  constructor(details:String, category:String, target: String, createdBy:UserModel,file:FileModel){
      this.target=target
      this.details=details
      this.createdBy=createdBy
      this.category=category
      this.isOpen=isOpen
      this.id=id
      this.lastModifiedDate=lastModifiedDate
      this.createdDate=createdDate
      this.moneyDonated=moneyDonated
      this.donors=donors
      this.approved=approved
      this.file=file
      this.transactions=transactions
  }


}