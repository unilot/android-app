package com.vovasoft.unilot.repository.room.dao

import android.arch.persistence.room.*
import com.vovasoft.unilot.repository.models.entities.Wallet


/***************************************************************************
 * Created by arseniy on 14/10/2017.
 ****************************************************************************/
@Dao
interface WalletsDao {
    @Query("SELECT * FROM results")
    fun getWallets(): List<Wallet>

    @Query("SELECT number FROM results")
    fun getWalletsNumbers(): List<String>

    @Query("SELECT * FROM results WHERE number = :arg0 LIMIT 1")
    fun getWalletByNumber(number: String): Wallet

    @Insert(onConflict= OnConflictStrategy.REPLACE)
    fun insert(wallet: Wallet)

    @Insert(onConflict= OnConflictStrategy.REPLACE)
    fun insertAll(wallets: List<Wallet>)

    @Delete
    fun delete(wallet: Wallet)

    @Query("DELETE FROM results")
    fun deleteAll()
}