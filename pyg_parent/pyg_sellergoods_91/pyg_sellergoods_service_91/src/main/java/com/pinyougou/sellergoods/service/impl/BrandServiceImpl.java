package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    public List<TbBrand> findAll(){
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult<TbBrand> findPage(int page, int size) {
        PageHelper.startPage(page, size);//设置分页参数
        Page<TbBrand> pageResult = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult<>(pageResult.getTotal(), pageResult.getResult());
    }

    @Override
    public void save(TbBrand brand) {
        //如果id为null，执行保存
        if(null == brand.getId()){
            brandMapper.insert(brand);
        } else {
            //如果id不为null，执行更新
            brandMapper.updateByPrimaryKey(brand);
        }
    }

    @Override
    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void delete(String ids) {
        //1.切割ids
        String[] idsArr = ids.split(",");
        //2.循环删除
        for(String id : idsArr){
            brandMapper.deleteByPrimaryKey(Long.valueOf(id));
        }
    }
}
