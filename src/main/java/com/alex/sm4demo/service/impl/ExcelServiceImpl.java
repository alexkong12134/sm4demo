package com.alex.sm4demo.service.impl;


import com.alex.sm4demo.mapper.ExcelMapper;
import com.alex.sm4demo.pojo.ExcelPerson;
import com.alex.sm4demo.service.ExcelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


/**
 * Created by Alex on 2022/3/24 16:33
 */
@Service
public class ExcelServiceImpl extends ServiceImpl<ExcelMapper, ExcelPerson> implements ExcelService {
}