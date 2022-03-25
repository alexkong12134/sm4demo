package com.alex.sm4demo.service.impl;

import com.alex.sm4demo.mapper.TestPersonMapper2;
import com.alex.sm4demo.pojo.TestPerson2;
import com.alex.sm4demo.service.TestPersonService2;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Alex
 * @since 2022-03-25
 */
@Service
@DS("slave_1")
public class TestPersonServiceImpl2 extends ServiceImpl<TestPersonMapper2, TestPerson2> implements TestPersonService2 {

}
