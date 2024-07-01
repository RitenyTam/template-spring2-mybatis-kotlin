package com.riteny.mybatis.entity

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.KeyType
import com.mybatisflex.annotation.Table
import java.io.Serializable

/**
 * 实体类。
 *
 * @author riten
 * @since 2024-06-13
 */
@Table(value = "account")
class Account : Serializable {
    @Id(keyType = KeyType.Auto)
    var id: Long? = null

    var accountName: String? = null

    var password: String? = null

    override fun toString(): String {
        return "Account{" +
                "id=" + id +
                ", accountName='" + accountName + '\'' +
                ", password='" + password + '\'' +
                '}'
    }
}
