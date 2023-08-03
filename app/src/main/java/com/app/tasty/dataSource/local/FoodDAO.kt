package com.app.tasty.dataSource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.tasty.model.CartItem
import com.app.tasty.model.FoodItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDAO {

    @Query("UPDATE foodTable SET quantity =:quantity WHERE id =:id")
    fun updateQuantityInFoodTable(quantity: Int, id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFood(foodItem: FoodItem)

    @Query("SELECT * FROM foodTable WHERE quantity > 0")
    fun getAllFoodItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM foodTable")
    fun getAllFood(): Flow<List<FoodItem>>

    @Query("SELECT * FROM foodTable WHERE id =:id")
    fun getFoodItem(id: Int): Flow<FoodItem>

    @Query("SELECT SUM(quantity) FROM foodTable")
    fun getSumOfQuantity(): Flow<Int?>

    @Query("SELECT SUM(quantity*price) FROM foodTable WHERE quantity > 0")
    fun getSumOfPrices(): Flow<Int?>

    @Query("SELECT quantity FROM cartTable where id = :id")
    fun getItemCount(id: Int): Flow<Int?>

    @Delete
    fun deleteFoodItem(foodItem: FoodItem)

    //CartItems
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCartItem(cartItem: CartItem)

    @Delete
    fun deleteCartItem(cartItem: CartItem)

    @Query("SELECT * FROM cartTable WHERE quantity > 0")
    fun getAllCartItems(): Flow<List<CartItem>>

    @Query("SELECT SUM(quantity) FROM cartTable")
    fun getSumOfCartQuantity(): Flow<Int?>

    @Query("SELECT SUM(quantity*price) FROM cartTable WHERE quantity > 0")
    fun getSumOfCartPrices(): Flow<Int?>
}
