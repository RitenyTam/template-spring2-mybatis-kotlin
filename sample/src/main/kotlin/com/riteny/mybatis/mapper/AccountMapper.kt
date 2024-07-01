package com.riteny.mybatis.mapper

import com.mybatisflex.core.BaseMapper
import com.riteny.mybatis.entity.Account
import org.apache.ibatis.annotations.Mapper


/**
 * 映射层。
 *
 * @author riten
 * @since 2024-06-13
 */
@Mapper
interface AccountMapper : BaseMapper<Account> {
    override fun selectAll(): List<Account>
}
