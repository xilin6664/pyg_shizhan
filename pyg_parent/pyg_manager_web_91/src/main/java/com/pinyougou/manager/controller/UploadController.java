package com.pinyougou.manager.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

	@Value("${STORAGE_SERVER}")
	private String STORAGE_SERVER;

	@RequestMapping("/uploadFile")
	public Result uploadFile(MultipartFile file){
		try{
			//1.连接到FastDFS服务器
			FastDFSClient client = new FastDFSClient("classpath:config/fsdf_conf.conf");
			//2.上传文件
			//从原始文件名中获取后缀，不要.
			String originalFilename = file.getOriginalFilename();//alfjalkf.ads.2.png
			String sufixName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);//png
			//3.接受返回的文件地址
			String filePath = client.uploadFile(file.getBytes(), sufixName);//group1/M00/22/44/afjalskjflafjlajsfd.png
			//4.如果成功，返回地址:http://storageserver的ip/filePath
			return new Result(true, STORAGE_SERVER+filePath);
		} catch (Exception e){
			e.printStackTrace();
			//5.如果失败，返回错误提示
			return new Result(false, "对不起，文件上传失败！");
		}
	}
}
