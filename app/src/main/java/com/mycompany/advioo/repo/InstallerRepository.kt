package com.mycompany.advioo.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.mycompany.advioo.models.installer.Installer
import javax.inject.Inject


class InstallerRepository @Inject constructor(
    private val db: FirebaseFirestore
) : InstallerRepositoryInterface {



    private val installersCollection = db.collection("installers")


    override fun getInstallersByState(stateId: String, onSuccess: (List<Installer>) -> Unit, onFailure: (Exception) -> Unit) {

        installersCollection.whereEqualTo("stateId", stateId)
            .get()
            .addOnSuccessListener { snapshot ->
                val installerList = mutableListOf<Installer>()
                snapshot.forEach { document ->
                    val installer = document.toObject<Installer>()
                    installerList.add(installer)
                }
                onSuccess(installerList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }



}