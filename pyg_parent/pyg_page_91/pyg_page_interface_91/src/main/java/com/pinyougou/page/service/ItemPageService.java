package com.pinyougou.page.service;

import java.io.IOException;

public interface ItemPageService {
    void generateHtml(Long goodsId) throws Exception;

    void generateHtmlAll() throws Exception;
}
