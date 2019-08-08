package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;

public interface BrandService {
    public List<TbBrand> findAll();

    PageResult<TbBrand> findPage(int page, int size);

    void save(TbBrand brand);

    TbBrand findOne(Long id);

    void delete(String ids);
}
