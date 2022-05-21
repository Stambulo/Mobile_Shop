package com.stambulo.mobileshop.data.db

import androidx.room.TypeConverter
import java.math.BigDecimal

class Converters {

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal): String{
        return value.toString()
    }

    @TypeConverter
    fun stringToBigDecimal(value: String): BigDecimal{
        return BigDecimal(value)
    }
}
