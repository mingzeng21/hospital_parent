package com.zengming.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zengming.yygh.common.result.Result;
import com.zengming.yygh.common.utils.MD5;
import com.zengming.yygh.hosp.service.HospitalSetService;
import com.zengming.yygh.model.hosp.HospitalSet;
import com.zengming.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;


@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    //
    @Autowired
    private HospitalSetService hospitalSetService;

    //test get
    @ApiOperation(value = "获取所有医院设置信息")
    @GetMapping("findAll")
    public Result findAllHospitalSet(){
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    //test delete
    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospitalSet(@PathVariable Long id){
        boolean flg = hospitalSetService.removeById(id);
        if (flg){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //3。条件查询带分页
    @ApiOperation(value = "条件查询带分页")
    @PostMapping("findPage/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        //创建Page对象，传递d当前页和limit
        Page<HospitalSet> page = new Page<>(current,limit);
        //构建条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String hoscode = hospitalSetQueryVo.getHoscode();
        String hosname = hospitalSetQueryVo.getHosname();
        if (!StringUtils.isEmpty(hosname)){
            wrapper.like("hosname",hospitalSetQueryVo.getHosname());
        }
        if (!StringUtils.isEmpty(hoscode)){
            wrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
        }

        //调用方法
        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, wrapper);

        return Result.ok(hospitalSetPage);
    }


    //4。添加
    @ApiOperation(value = "上传保存医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody(required = true) HospitalSet hospitalSet){
        //设置状态 1可用；0不可用
        hospitalSet.setStatus(1);
        //生生签名
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        //添加其它属性
        boolean save = hospitalSetService.save(hospitalSet);

        if (save){
            return Result.ok();
        }else {
            return Result.fail();
        }


    }

    //5。根据id获取
    @GetMapping("getHospitalSetById/{id}")
    public Result getHospitalSetById(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    //6。修改
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //7。批量删除
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<String> idList){
        boolean flag = hospitalSetService.removeByIds(idList);
        return Result.ok();
    }

    //锁定和解锁
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,
                                  @PathVariable Integer status){
        //根据id查出
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //修改status
        hospitalSet.setStatus(status);
        boolean flg = hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    //签名密钥
    @PutMapping("sendKey/{id}")
    public Result sendSignKey(@PathVariable Long id){
        //
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO 发送短信
        return Result.ok();
    }
}
