package com.mycompany.advioo.repo



import com.mycompany.advioo.models.installer.Installer


interface InstallerRepositoryInterface {

    fun getInstallersByState(stateId: String, onSuccess: (List<Installer>) -> Unit, onFailure: (Exception) -> Unit)

}