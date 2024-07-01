package com.riteny.mybatis.controller

import com.mybatisflex.core.query.QueryWrapper
import com.riteny.mybatis.entity.Account
import com.riteny.mybatis.mapper.AccountMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/test/mybatis")
class TestController @Autowired constructor(val accountMapper: AccountMapper) {

    @GetMapping("/")
    fun test() {
        val queryWrapper = QueryWrapper()
            .select().eq("id", 1)

        val create = Account()
        create.accountName = UUID.randomUUID().toString()
        create.password = UUID.randomUUID().toString()
        accountMapper.insertSelective(create)

        val account = accountMapper.selectOneByQuery(queryWrapper)
        val accounts = accountMapper.selectAll()

        println(account)
        println(accounts)
    }
}
