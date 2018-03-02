package com.dianmi.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dianmi.mapper.FulitaoMapper;
import com.dianmi.model.Fulitao;
import com.dianmi.service.FuLiTaoService;
import com.dianmi.utils.excel.ExcelUtil;
import com.dianmi.utils.file.DeleteFile;
import com.dianmi.utils.file.DownloadFile;
import com.dianmi.utils.file.UploadFile;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.dianmi.utils.poi.ReadExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@SuppressWarnings("ALL")
@Service
public class FuLiTaoServiceImpl implements FuLiTaoService {

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private FulitaoMapper fulitaoMapper;

    @Value("${file.maximum.upload.size}")
    private long maxFileSize;

    @Override
    public ResultJson pageList(Integer pageSize, Integer currPage) {
        PageHelper.startPage(currPage, pageSize);
        PageInfo<Fulitao> pageInfo = new PageInfo<>(fulitaoMapper.pageList());
        return ResultUtil.success(RestEnum.SUCCESS, pageInfo);
    }

    @Override
    public ResultJson saveEntity(String fuLiTaoString) {
        if (fuLiTaoString == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        fuLiTaoString = fuLiTaoString.substring(fuLiTaoString.indexOf("{"), fuLiTaoString.lastIndexOf("}") + 1);
        int size = 0;
        try {
            // JSON 字符串转 Java对象
            Fulitao fulitao = JSONObject.parseObject(fuLiTaoString, Fulitao.class);
            size = fulitaoMapper.insertSelective(fulitao);
        } catch (Exception e) {
            logger.error("fulitaoServiceImpl @ saveEntity", e);
            throw new RuntimeException(e);
        }
        return size == 1 ? ResultUtil.success(RestEnum.SUCCESS) : ResultUtil.error(RestEnum.SAVE_FALL);
    }

    @Override
    public ResultJson getByIdEntity(Integer id) {
        if (id == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        Fulitao fulitao = fulitaoMapper.selectByPrimaryKey(id);
        if (fulitao == null) {
            return ResultUtil.error(RestEnum.ENTITY_IS_EMPTY);
        }
        return ResultUtil.success(RestEnum.SUCCESS, fulitao);
    }

    @Override
    public ResultJson updateEntity(String fuLiTaoString) {
        if (fuLiTaoString == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        fuLiTaoString = fuLiTaoString.substring(fuLiTaoString.indexOf("{"), fuLiTaoString.lastIndexOf("}") + 1);
        int size = 0;
        try {
            // JSON 字符串转 Java对象
            Fulitao fulitao = JSONObject.parseObject(fuLiTaoString, Fulitao.class);
            size = fulitaoMapper.updateByPrimaryKey(fulitao);
        } catch (Exception e) {
            logger.error("fulitaoServiceImpl @ updateEntity", e);
            throw new RuntimeException(e);
        }
        return size == 1 ? ResultUtil.success(RestEnum.SUCCESS) : ResultUtil.error(RestEnum.SAVE_FALL);
    }

    @Override
    public ResultJson deleteEntity(Integer id) {
        if (id == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        int size = fulitaoMapper.deleteByPrimaryKey(id);
        return size == 1 ? ResultUtil.success(RestEnum.SUCCESS) : ResultUtil.error(RestEnum.DELETE_FAIL);
    }

    @Override
    public ResultJson exportEntity() {
        List<Fulitao> fulitaos = fulitaoMapper.pageList();
        if (fulitaos.isEmpty()) {
            return ResultUtil.error(RestEnum.FILE_NOT_EXISTS);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String[] title = new String[]{"Id", "导航图标"};//标题

        String[][] values = new String[fulitaos.size()][];
        for (int i = 0; i < fulitaos.size(); i++) {
            values[i] = new String[title.length];
            //将对象内容转换成string
            Fulitao obj = fulitaos.get(i);
            values[i][0] = obj.getFId() + "";
            values[i][1] = obj.getProductName();
        }

        String filePath = ExcelUtil.getHSSFWorkbook("福利套信息", title, values, null);
        try {
            DownloadFile.downloadFile(filePath, "福利套信息.xlsx");
            DeleteFile.deleteFile(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultUtil.error(RestEnum.FAILD);
    }

    @Override
    public ResultJson importEntity(MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return ResultUtil.error(RestEnum.FILE_FORMATS_ERROR);
        }
        String fileName = file.getOriginalFilename();
        if (file.getSize() > maxFileSize) {
            return ResultUtil.error(RestEnum.FILE_SIZE_EXCEEDS_LIMIT);
        }
        if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
            return ResultUtil.error(RestEnum.FILE_FORMATS_ERROR);
        }
        String fileExtension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        String filePath = request.getSession().getServletContext().getRealPath("/") + "upload" + File.separator + "excel" + File.separator;
        String newFileName = UUID.randomUUID() + fileExtension;
        try {
            UploadFile.upload(file, filePath, newFileName);
        } catch (IOException e) {
            logger.info("上传异常");
        }
        filePath = filePath + newFileName;

        List<String[]> lists = ReadExcel.readExcelList(filePath).get(0); // 获取数据
        DeleteFile.DeleteFolder(filePath);// 上传成功删除Excel模板
        if (!lists.isEmpty()) {
            List<String> strings = new ArrayList<>();
            for (int i = 1; i < lists.size(); i++) {
                String[] fulitao = lists.get(i);
                String cityId = StringUtils.isEmpty(fulitao[1]) ? "" : fulitao[1];// 城市
                String supplierId = StringUtils.isEmpty(fulitao[2]) ? "" : fulitao[2]; // 供应商ID


            }

        }


        return null;
    }


}
