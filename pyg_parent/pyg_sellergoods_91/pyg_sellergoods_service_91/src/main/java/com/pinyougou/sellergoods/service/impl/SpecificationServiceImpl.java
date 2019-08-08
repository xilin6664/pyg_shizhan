package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.vo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;
import org.springframework.transaction.annotation.Transactional;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	@Autowired
	private TbTypeTemplateMapper tbTypeTemplateMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

    /**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
        TbSpecification spec = specification.getSpec();
        if(null == specification.getSpec().getId()){
            //判断如果id == null，执行保存
            //1.保存规格主表，返回主键值
            specificationMapper.insert(spec);
            //2.循环保存规格从表，设置外键值
            for(TbSpecificationOption option : specification.getOptionList()){
                option.setSpecId(spec.getId());
                specificationOptionMapper.insert(option);
            }
        } else {
            //如果id!=null，主表执行更新，从表先删除旧数据，保存新数据，设置外键
            specificationMapper.updateByPrimaryKey(spec);
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(spec.getId());
            specificationOptionMapper.deleteByExample(example);
            for(TbSpecificationOption option : specification.getOptionList()){
                option.setSpecId(spec.getId());
                specificationOptionMapper.insert(option);
            }
        }



	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSpecification specification){
		specificationMapper.updateByPrimaryKey(specification);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
	    //1.查询规格主表数据
        TbSpecification spec = specificationMapper.selectByPrimaryKey(id);
        //2.查询规格选项表数据
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> optionList = specificationOptionMapper.selectByExample(example);
        //3.封装成复合类返回
		return new Specification(spec, optionList);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public List<Map> findSpecByTypeTemplateId(Long typeTemplateId) {
		//1.根据模板id查询模板对象
		TbTypeTemplate typeTemplate = tbTypeTemplateMapper.selectByPrimaryKey(typeTemplateId);
		//2.将规格字符串转换成List<Map>数据
		List<Map> specList = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
		for(Map map : specList){
			//3.根据map中的规格id查询规格选项List
			TbSpecificationOptionExample exampmle = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = exampmle.createCriteria();
			criteria.andSpecIdEqualTo(Long.valueOf(String.valueOf(map.get("id"))));
			List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(exampmle);
			//4.将查询结果放到map中：key=options
			map.put("options", options);
		}
		//5.将最终的List<Map>数据返回
        return specList;
    }

}
