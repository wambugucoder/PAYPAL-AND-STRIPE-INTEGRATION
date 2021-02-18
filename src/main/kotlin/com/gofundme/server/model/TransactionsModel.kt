package com.gofundme.server.model

import com.fasterxml.jackson.annotation.JsonBackReference
import java.io.Serializable
import javax.persistence.*


@Entity
class TransactionsModel: Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id:Long=0

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    var initiator:UserModel

    @Column(nullable = false)
    var amountDonated:Double

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donation_id",referencedColumnName = "id")
    @JsonBackReference
    var donation:DonationsModel

    @Column(nullable = false)
    var receiptId:String


    constructor(initiator:UserModel, amountDonated: Double, donation:DonationsModel, receiptId:String){
        this.initiator=initiator
        this.amountDonated=amountDonated
        this.donation=donation
        this.receiptId=receiptId

    }


}