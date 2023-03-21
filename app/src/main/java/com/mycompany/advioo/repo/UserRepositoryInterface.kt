package com.mycompany.advioo.repo

import com.google.android.gms.tasks.Task
import com.mycompany.advioo.models.user.User

interface UserRepositoryInterface {

    fun uploadUserData(user: User) : Task<Void>


}