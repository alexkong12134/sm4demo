package com.alex.sm4demo.service.impl;

import com.alex.sm4demo.mapper.TestPersonMapper;
import com.alex.sm4demo.pojo.TestPerson;
import com.alex.sm4demo.service.TestPersonService;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Alex
 * @since 2022-03-24
 */
@Service
@DS("master")
public class TestPersonServiceImpl extends ServiceImpl<TestPersonMapper, TestPerson> implements TestPersonService {
}
