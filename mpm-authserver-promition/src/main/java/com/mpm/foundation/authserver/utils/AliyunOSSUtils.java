package com.mpm.foundation.authserver.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.mpm.foundation.authserver.cfg.AliyunOSSConfig;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AliyunOSSUtils {
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 上传文件
	 * 
	 * @param file
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public static String putFile(File file, AliyunOSSConfig config) throws Exception {
		OSSClient ossClient = new OSSClient(config.getEndpoint(), config.getAccessKeyId(), config.getSecretAccessKey());
		String bucketName = config.getBucketName();
		String fileHost = config.getFileHost();
		String dateStr = format.format(new Date());
		try {

			// 容器不存在，就创建
			if (!ossClient.doesBucketExist(bucketName)) {
				ossClient.createBucket(bucketName);
				CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
				createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
				ossClient.createBucket(createBucketRequest);
			}
			// 创建文件路径
			String fileUrl = fileHost + "/" + (dateStr + "/" + UUID.randomUUID().toString().replace("-", ""));
			// 上传文件
			PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucketName, fileUrl, file));
			// 设置权限 这里是公开读
			ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
			if (null != result) {
				return fileUrl;
			}
		} catch (OSSException oe) {
			throw oe;
		} catch (ClientException ce) {
			throw ce;
		} finally {
			ossClient.shutdown();
		}
		return null;

	}

	/**
	 * 删除文件
	 * 
	 * @param fileKey
	 * @param config
	 * @throws Exception
	 */
	public static void deleteFile(String fileKey, AliyunOSSConfig config) throws Exception {

		try {
			OSSClient ossClient = new OSSClient(config.getEndpoint(), config.getAccessKeyId(),
					config.getSecretAccessKey());
			String bucketName = config.getBucketName();
			if (!ossClient.doesBucketExist(bucketName)) {
				throw new Exception("[bucketName:" + bucketName + "]不存在");
			} else {
				ossClient.deleteObject(bucketName, fileKey);
			}
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 获取文件
	 * 
	 * @param fileKey
	 * @param config
	 * @return
	 */
	public static InputStream getObjectList(String fileKey, AliyunOSSConfig config) {
		List<String> listRe = new ArrayList<>();

		try {
			OSSClient ossClient = new OSSClient(config.getEndpoint(), config.getAccessKeyId(),
					config.getSecretAccessKey());
			OSSObject ossObject = ossClient.getObject(new GetObjectRequest(config.getBucketName(), fileKey));

			return ossObject.getObjectContent();
		} catch (Exception ex) {
			throw ex;
		}
	}

	public static void main(String[] args) throws Exception {
		AliyunOSSConfig config = new AliyunOSSConfig();
		config.setAccessKeyId("LTAIRVHz2mGHkZ72");
		config.setBucketName("qunyitest");
		config.setEndpoint("oss-cn-qingdao.aliyuncs.com");
		config.setSecretAccessKey("zTb8csJwZRgN9JGyzIojdJb1taY4Lj");
		File f = new File("C:\\Users\\Administrator\\Desktop\\认证中心接口_2.5统一用户调整.docx");
		String key = AliyunOSSUtils.putFile(f, config);
		String fileName = "d:\\test11.docx";
		InputStream in = AliyunOSSUtils.getObjectList(key, config);
		BufferedInputStream bin = null;
		BufferedOutputStream out = null;
		bin = new BufferedInputStream(in);
		out = new BufferedOutputStream(new FileOutputStream(fileName));
		int len = -1;
		byte[] b = new byte[1024];
		while ((len = in.read(b)) != -1) {
			out.write(b, 0, len);
		}
		in.close();
		out.close();

	}

}
