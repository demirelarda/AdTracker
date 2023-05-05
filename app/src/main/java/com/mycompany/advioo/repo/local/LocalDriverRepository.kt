package com.mycompany.advioo.repo.local

import com.mycompany.advioo.dao.DriverDao
import com.mycompany.advioo.models.localuser.LocalDriver
import javax.inject.Inject

class LocalDriverRepository @Inject constructor(
    private val driverDao: DriverDao
) : LocalDriverRepositoryInterface{

    override suspend fun saveDriver(driver: LocalDriver) {
        driverDao.insertLocalDriver(driver)
    }

    override suspend fun getDriver(driverId: String): LocalDriver? {
        return driverDao.getLocalDriver(driverId)
    }
}