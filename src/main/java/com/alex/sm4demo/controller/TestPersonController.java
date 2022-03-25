package com.alex.sm4demo.controller;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alex.sm4demo.pojo.ExcelPerson;
import com.alex.sm4demo.pojo.TestPerson;
import com.alex.sm4demo.pojo.TestPerson2;
import com.alex.sm4demo.service.ExcelService;
import com.alex.sm4demo.service.TestPersonService;
import com.alex.sm4demo.service.TestPersonService2;
import com.alex.sm4demo.utils.ConfigUtils;
import com.alex.sm4demo.utils.ExcelUtils;
import com.alex.sm4demo.utils.SM4Utils;
import com.alibaba.fastjson.JSON;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Alex
 * @since 2022-03-24
 */
@RestController
@RequestMapping("/sm4demo/test-person")
public class TestPersonController {

    @Autowired
    TestPersonService testPersonService;

    @Autowired
    TestPersonService2 testPersonService2;

    @Autowired
    ExcelService excelService;

    SM4Utils util = new SM4Utils();
    
    //读取数据库写入缓存
    @GetMapping(value = "/addRedis")
    public  void addRedis(String[] args) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(ConfigUtils.getMyConf("redis.ip"), Integer.parseInt(ConfigUtils.getMyConf("redis.port")));
        System.out.println("连接成功");
        // 查看服务是否运行
        System.out.println("服务正在运行: " + jedis.ping());

        List<TestPerson> list = testPersonService.list();
        Map<String, String> map =new HashMap<>();
        for (TestPerson person:list) {
            jedis.set(person.getIdnum().toString(),JSON.toJSONString(person));
        }
        jedis.close();
    }
    
    //读取缓存，加密后写入数据库
    @GetMapping(value = "/en_addDB")
    public void enAddDB(){
        Jedis jedis = new Jedis(ConfigUtils.getMyConf("redis.ip"), Integer.parseInt(ConfigUtils.getMyConf("redis.port")));

        TestPerson2 person = null;
        String cursor = ScanParams.SCAN_POINTER_START;
        String key = "*";
        ScanParams scanParams = new ScanParams();
        scanParams.match(key);// 匹配以 PLFX-ZZSFP-* 为前缀的 key
        scanParams.count(1000);
        List<String> listAll=new ArrayList<String>();
        while (true){
            //使用scan命令获取数据，使用cursor游标记录位置，下次循环使用
            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
            cursor=scanResult.getCursor();
            List<String> list = scanResult.getResult();
            listAll.addAll(list);
            System.out.println("获取" + listAll.size()
                    + "条数据,cursor:" + cursor);
            if ("0".equals(cursor)){
                break;
            }
        }
        List<TestPerson2> personList = new ArrayList<TestPerson2>();
        for (String s:listAll) {
            String value = jedis.get(s);
            person = JSON.parseObject(value, TestPerson2.class);
            person.setAddr(util.encryptHex(person.getAddr()));
            person.setAge(util.encryptHex(person.getAge()));
            person.setIdcard(util.encryptHex(person.getIdcard()));
            person.setUname(util.encryptHex(person.getUname()));
            person.setPhone(util.encryptHex(person.getPhone()));
            person.setSex(util.encryptHex(person.getSex()));
            personList.add(person);

            TestPerson2 byId = testPersonService2.getById(person.getIdnum());
            if (null == byId) {
                testPersonService2.save(person);
            } else {
                System.out.println("error：该条信息已存在 message：" + byId);
            }
        }
        jedis.close();

    }

    //读取数据库，加密写入Excel
    @GetMapping(value = "/en_addExcel")
    public void enAddExcel() throws IOException {
        List<TestPerson> list = testPersonService.list();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd hhmmss");
        Workbook wb = new XSSFWorkbook();
        //标题行抽出字段
        String[] title = {"序号","姓名", "地址", "年龄", "性别", "idcard", "phone"};
        //设置sheet名称，并创建新的sheet对象
        String sheetName = "加密";
        Sheet safeSheet = wb.createSheet(sheetName);
        //获取表头行
        Row titleRow = safeSheet.createRow(0);
        //创建单元格，设置style居中，字体，单元格大小等
        CellStyle style = wb.createCellStyle();
        Cell cell = null;
        //把已经写好的标题行写入excel文件中
        for (int i = 0; i < title.length; i++) {
            cell = titleRow.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        //把从数据库中取得的数据一一写入excel文件中
        Row row = null;
        for (int i = 0; i < list.size(); i++) {
            //创建list.size()行数据
            row = safeSheet.createRow(i + 1);
            //把值一一写进单元格里
            row.createCell(0).setCellValue(list.get(i).getIdnum());
            row.createCell(1).setCellValue(util.encryptHex(list.get(i).getUname()));
            row.createCell(2).setCellValue(util.encryptHex(list.get(i).getAddr()));
            row.createCell(3).setCellValue(util.encryptHex(list.get(i).getAge().toString()));
            row.createCell(4).setCellValue(util.encryptHex(list.get(i).getSex()));
            row.createCell(5).setCellValue(util.encryptHex(list.get(i).getIdcard()));
            row.createCell(6).setCellValue(util.encryptHex(list.get(i).getPhone()));

        }
        //设置单元格宽度自适应，在此基础上把宽度调至1.5倍
        for (int i = 0; i < title.length; i++) {
            safeSheet.autoSizeColumn(i, true);
            safeSheet.setColumnWidth(i, safeSheet.getColumnWidth(i) * 15 / 10);
        }
        //获取配置文件中保存对应excel文件的路径，本地也可以直接写成F：excel/stuInfoExcel路径
        String folderPath = "E:\\ideaXM";

        //创建上传文件目录
        File folder = new File(folderPath);
        //如果文件夹不存在创建对应的文件夹
        if (!folder.exists()) {
            folder.mkdirs();
        }
        //设置文件名
        String fileName = sdf1.format(new Date()) + sheetName + ".xlsx";
        String savePath = folderPath + File.separator + fileName;

        OutputStream fileOut = new FileOutputStream(savePath);
        wb.write(fileOut);
        fileOut.close();
        System.out.println(savePath);
    };

    //读取加密数据库，写入Excel
    @GetMapping(value = "/en_pullExcel")
    public void enPullExcel() throws IOException {
        List<ExcelPerson> list = excelService.list();

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd hhmmss");
        Workbook wb = new XSSFWorkbook();
        //标题行抽出字段
        String[] title = {"序号","姓名", "地址", "年龄", "性别", "idcard", "phone"};
        //设置sheet名称，并创建新的sheet对象
        String sheetName = "解密";
        Sheet safeSheet = wb.createSheet(sheetName);
        //获取表头行
        Row titleRow = safeSheet.createRow(0);
        //创建单元格，设置style居中，字体，单元格大小等
        CellStyle style = wb.createCellStyle();
        Cell cell = null;
        //把已经写好的标题行写入excel文件中
        for (int i = 0; i < title.length; i++) {
            cell = titleRow.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        //把从数据库中取得的数据一一写入excel文件中
        Row row = null;
        for (int i = 0; i < list.size(); i++) {
            //创建list.size()行数据
            row = safeSheet.createRow(i + 1);
            //把值一一写进单元格里
            row.createCell(0).setCellValue(list.get(i).getIdnum());
            row.createCell(1).setCellValue(util.decryptStr(list.get(i).getUname()));
            row.createCell(2).setCellValue(util.decryptStr(list.get(i).getAddr()));
            row.createCell(3).setCellValue(util.decryptStr(list.get(i).getAge()));
            row.createCell(4).setCellValue(util.decryptStr(list.get(i).getSex()));
            row.createCell(5).setCellValue(util.decryptStr(list.get(i).getIdcard()));
            row.createCell(6).setCellValue(util.decryptStr(list.get(i).getPhone()));

        }
        //设置单元格宽度自适应，在此基础上把宽度调至1.5倍
        for (int i = 0; i < title.length; i++) {
            safeSheet.autoSizeColumn(i, true);
            safeSheet.setColumnWidth(i, safeSheet.getColumnWidth(i) * 15 / 10);
        }
        //获取配置文件中保存对应excel文件的路径，本地也可以直接写成F：excel/stuInfoExcel路径
        String folderPath = "E:\\ideaXM";

        //创建上传文件目录
        File folder = new File(folderPath);
        //如果文件夹不存在创建对应的文件夹
        if (!folder.exists()) {
            folder.mkdirs();
        }
        //设置文件名
        String fileName = sdf1.format(new Date()) + sheetName + ".xlsx";
        String savePath = folderPath + File.separator + fileName;

        OutputStream fileOut = new FileOutputStream(savePath);
        wb.write(fileOut);
        fileOut.close();
        System.out.println(savePath);
    };

    @RequestMapping("/de_excelToDB")
    public void deExcelToDB(@RequestParam("file") MultipartFile file) throws Exception {
        String name = file.getOriginalFilename();
        if(name.length() < 5 || !name.substring(name.length() - 5).equals(".xlsx")) {
            throw new Exception("文件格式错误");
        }
        // 获取Excel中的数据
        List<ExcelPerson> excelList = ExcelUtils.excelToShopIdList(file.getInputStream());
        // 向数据库遍历添加数据库
        for (int i = 0; i < excelList.size(); i++) {
            // 获取行信息
            ExcelPerson forExcel = excelList.get(i);
            // 先根据id查询数据库里有没有一样的，没有就进行添加
            ExcelPerson byId = excelService.getById(forExcel.getIdnum());

            if (null == byId) {
                excelService.save(forExcel);
            } else {
                System.out.println("error：该条信息已存在 message：" + byId);
            }
        }
    }
}
