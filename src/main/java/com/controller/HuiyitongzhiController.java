package com.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.HuiyitongzhiEntity;
import com.entity.view.HuiyitongzhiView;

import com.service.HuiyitongzhiService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;
import java.io.IOException;
import com.service.StoreupService;
import com.entity.StoreupEntity;

/**
 * 会议通知
 * 后端接口
 * @author 
 * @email 
 * @date 2022-04-11 20:34:35
 */
@RestController
@RequestMapping("/huiyitongzhi")
public class HuiyitongzhiController {
    @Autowired
    private HuiyitongzhiService huiyitongzhiService;


    @Autowired
    private StoreupService storeupService;

    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,HuiyitongzhiEntity huiyitongzhi, 
		HttpServletRequest request){

		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("daoshi")) {
			huiyitongzhi.setJiaoshigonghao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<HuiyitongzhiEntity> ew = new EntityWrapper<HuiyitongzhiEntity>();
    	PageUtils page = huiyitongzhiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, huiyitongzhi), params), params));
		request.setAttribute("data", page);
        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,HuiyitongzhiEntity huiyitongzhi, 
		HttpServletRequest request){
        EntityWrapper<HuiyitongzhiEntity> ew = new EntityWrapper<HuiyitongzhiEntity>();
    	PageUtils page = huiyitongzhiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, huiyitongzhi), params), params));
		request.setAttribute("data", page);
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( HuiyitongzhiEntity huiyitongzhi){
       	EntityWrapper<HuiyitongzhiEntity> ew = new EntityWrapper<HuiyitongzhiEntity>();
      	ew.allEq(MPUtil.allEQMapPre( huiyitongzhi, "huiyitongzhi")); 
        return R.ok().put("data", huiyitongzhiService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(HuiyitongzhiEntity huiyitongzhi){
        EntityWrapper< HuiyitongzhiEntity> ew = new EntityWrapper< HuiyitongzhiEntity>();
 		ew.allEq(MPUtil.allEQMapPre( huiyitongzhi, "huiyitongzhi")); 
		HuiyitongzhiView huiyitongzhiView =  huiyitongzhiService.selectView(ew);
		return R.ok("查询会议通知成功").put("data", huiyitongzhiView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        HuiyitongzhiEntity huiyitongzhi = huiyitongzhiService.selectById(id);
        return R.ok().put("data", huiyitongzhi);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        HuiyitongzhiEntity huiyitongzhi = huiyitongzhiService.selectById(id);
        return R.ok().put("data", huiyitongzhi);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody HuiyitongzhiEntity huiyitongzhi, HttpServletRequest request){
    	huiyitongzhi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(huiyitongzhi);

        huiyitongzhiService.insert(huiyitongzhi);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody HuiyitongzhiEntity huiyitongzhi, HttpServletRequest request){
    	huiyitongzhi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(huiyitongzhi);

        huiyitongzhiService.insert(huiyitongzhi);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody HuiyitongzhiEntity huiyitongzhi, HttpServletRequest request){
        //ValidatorUtils.validateEntity(huiyitongzhi);
        huiyitongzhiService.updateById(huiyitongzhi);//全部更新
        return R.ok();
    }
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        huiyitongzhiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 提醒接口
     */
	@RequestMapping("/remind/{columnName}/{type}")
	public R remindCount(@PathVariable("columnName") String columnName, HttpServletRequest request, 
						 @PathVariable("type") String type,@RequestParam Map<String, Object> map) {
		map.put("column", columnName);
		map.put("type", type);
		
		if(type.equals("2")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			Date remindStartDate = null;
			Date remindEndDate = null;
			if(map.get("remindstart")!=null) {
				Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
				c.setTime(new Date()); 
				c.add(Calendar.DAY_OF_MONTH,remindStart);
				remindStartDate = c.getTime();
				map.put("remindstart", sdf.format(remindStartDate));
			}
			if(map.get("remindend")!=null) {
				Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH,remindEnd);
				remindEndDate = c.getTime();
				map.put("remindend", sdf.format(remindEndDate));
			}
		}
		
		Wrapper<HuiyitongzhiEntity> wrapper = new EntityWrapper<HuiyitongzhiEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}

		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("daoshi")) {
			wrapper.eq("jiaoshigonghao", (String)request.getSession().getAttribute("username"));
		}

		int count = huiyitongzhiService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	
	





}
