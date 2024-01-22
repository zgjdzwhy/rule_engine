package com.mobanker.engine.design.busi.workspace.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.mobanker.engine.design.hotdeploy.file.EngineFileContent;
import com.mobanker.engine.design.serialize.EngineFileContentSerialize;
import com.mobanker.engine.framkwork.exception.EngineException;


/**
* <p>Title: EngineZipFileUtil</p>
* <p>Description: zip工具类</p>
* <p>Company: mobanker</p> 
* @author zhujj
* @date 2017年4月12日
* @version 1.0 
*/
public class EngineZipFileUtil {
				
	private static Logger logger = LoggerFactory.getLogger(EngineZipFileUtil.class);	
	
	private static final String zipDir = System.getProperty("java.io.tmpdir") + "RuleEngineModelFile" + File.separator + "RuleZipFile";
	
	private static final String ENGINE_RULE_MODEL_ZIP_NAME="engineRuleModel.zip";
	private static final String ENGINE_RULE_MODEL_ZIP="engineRuleModel";
	private static final String RULE_FILE_FORMAT=".qr";
	
	public static byte[] zip(List<EngineFileContent> engineFlieList) {
		
		String engineFilePath=zipDir + File.separator + ENGINE_RULE_MODEL_ZIP;
		File dirFile = new File(engineFilePath);	
		if(dirFile.exists()){
			//先把原目录下数据删除,防止数据混乱
			deleteDir(dirFile);
		}
		dirFile.mkdirs();
		
		for(EngineFileContent engineFlie:engineFlieList){
			//只同步有内容的产线
			if(engineFlie.getBytes().length>0){
				byte2File(new EngineFileContentSerialize().serialize(engineFlie),engineFilePath,engineFlie.getFileName()+RULE_FILE_FORMAT);
			}
		}
		File engineFileList=new File(engineFilePath);
		logger.info("压缩中...");
		String zipFilePath=zipDir+ File.separator +ENGINE_RULE_MODEL_ZIP_NAME;
		ZipOutputStream out =null;
		try {
			out = new ZipOutputStream(new FileOutputStream(zipFilePath));
			zip(out, engineFileList, engineFileList.getName());
			
		} catch (Exception e) {
			logger.info("压缩规则文件失败,文件路径{}",engineFilePath,e);
			throw new EngineException("压缩规则文件失败",e);
			
		}finally{
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				} // 输出流关闭
			}
		}
		logger.info("压缩完成");
		
		byte[] buffer = null;
		
		File zipFile=new File(zipFilePath);
		FileInputStream fis =null;
		ByteArrayOutputStream bos =null;
        try {
			fis = new FileInputStream(zipFile);  
			bos = new ByteArrayOutputStream();  
			byte[] b = new byte[1024];  
			int n;  
			while ((n = fis.read(b)) != -1)  
			{  
			    bos.write(b, 0, n);  
			}  
			buffer = bos.toByteArray(); 
		} catch (IOException e) {
			logger.info("将Zip文件转换成Byte,文件路径{}",zipFilePath,e);
			throw new EngineException("将Zip文件转换成Byte失败",e);
		}finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}  
			}
			if(bos!=null){
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return buffer;
	}

	private static void zip(ZipOutputStream out, File f, String base){ // 方法重载
		int k = 1; // 定义递归次数变量
		FileInputStream in =null;
		try {
			if (f.isDirectory()) {
				File[] fl = f.listFiles();
				if (fl.length == 0) {
					out.putNextEntry(new ZipEntry(base + "/")); // 创建zip压缩进入点base
					logger.info(base + "/");
				}
				for (int i = 0; i < fl.length; i++) {
					//取消  base + "/"+ fl[i].getName() 文件直接压缩到根目录
					zip(out, fl[i], fl[i].getName()); // 递归遍历子文件夹
				}
				logger.info("第" + k + "次递归");
				k++;
			} else {
				out.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入点base
				logger.info(base);
				in = new FileInputStream(f);
	            int j =  0;
	            byte[] buffer = new byte[1024];
	            while((j = in.read(buffer)) > 0){
	                out.write(buffer,0,j);
	            }
			}
	    } catch (IOException e) {
			throw new EngineException("将规则文件写入zip目录失败！",e);
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static List<EngineFileContent> decompressingZipFile(MultipartFile multiFile) {
		//统一zip文件路径
		String fileName = StringUtils.replace(multiFile.getOriginalFilename(), ".zip", "");

		//zip解压文件路径
		String zipFileDir = zipDir + File.separator + fileName ;
		File dirFile = new File(zipFileDir);	
		if(dirFile.exists()){
			//先把原目录下数据删除,防止数据混乱
			deleteDir(dirFile);
		}	
		dirFile.mkdirs();
		
		File file = null;
		String zipFilePath = zipDir + File.separator + multiFile.getOriginalFilename();
		try {
			file = new File(zipFilePath);	
			
			try(FileOutputStream fos = new FileOutputStream(file)){
				
				fos.write(multiFile.getBytes());
			}				
			logger.info("保存规则Zip文件至本地成功，文件名：{}，大小：{}",file.getAbsolutePath(),multiFile.getBytes().length);
		} catch (IOException e) {
			logger.info("保存规则Zip至本地失败，路径：{}",zipFilePath,e);
			throw new EngineException("保存规则Zip文件至本地失败",e);
		}
		
		try {
			ZipInputStream Zin=new ZipInputStream(new FileInputStream(file));
			BufferedInputStream Bin=new BufferedInputStream(Zin);
			File Fout=null;
			ZipEntry entry;
			while((entry = Zin.getNextEntry())!=null && !entry.isDirectory()){
				Fout=new File(zipFileDir,entry.getName());
				if(!Fout.exists()){
					(new File(Fout.getParent())).mkdirs();
				}
				FileOutputStream out=new FileOutputStream(Fout);
				BufferedOutputStream Bout=new BufferedOutputStream(out);
				int b;
				while((b=Bin.read())!=-1){
					Bout.write(b);
				}
				Bout.close();
				out.close();
				logger.info(Fout+"解压成功！路径：{}",zipFileDir);	
			}
			Bin.close();
			Zin.close();
		} catch (IOException e) {
			logger.info("解压Zip规则至本地失败，路径：{}",zipFileDir,e);
			throw new EngineException("解压Zip规则至本地失败",e);
		}
		
		File qrFile=new File(zipFileDir);
		File[] tempList = qrFile.listFiles();
		logger.info("该目录下对象个数："+tempList.length);
		for (int i = 0; i < tempList.length; i++) {
		    if (tempList[i].isFile()) {
		    	logger.info("文     件："+tempList[i]);
		    }
		    if (tempList[i].isDirectory()) {
		    	logger.info("文件夹："+tempList[i]);
		    }
		}
		List<EngineFileContent> ruleList=new LinkedList<EngineFileContent>();
		for(File ruleFile:tempList){
			byte[] buffer = null;
            try {
				FileInputStream fis = new FileInputStream(ruleFile);  
				ByteArrayOutputStream bos = new ByteArrayOutputStream();  
				byte[] b = new byte[1024];  
				int n;  
				while ((n = fis.read(b)) != -1)  
				{  
				    bos.write(b, 0, n);  
				}  
				fis.close();  
				bos.close();  
				buffer = bos.toByteArray();
				if(buffer.length>0){
					EngineFileContent ruleModelData = new EngineFileContentSerialize().unserialize(buffer);
					ruleList.add(ruleModelData);
				}else{
					logger.info("产线规则内容为空不加载！{}",ruleFile.getName());
				}

			} catch (IOException e) {
				logger.info("解压Zip规则至本地失败，路径：{}",zipFileDir,e);
				throw new EngineException("解压Zip规则至本地失败",e);
			}
		}
			
		return ruleList;
	}
	
    public static void byte2File(byte[] buf, String filePath, String fileName)  
    {  
        BufferedOutputStream bos = null;  
        FileOutputStream fos = null;  
        File file = null;  
        try  
        {  
            File dir = new File(filePath);  
            if (!dir.exists() && dir.isDirectory())  
            {  
                dir.mkdirs();  
            }  
            file = new File(filePath + File.separator + fileName);  
            fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos);  
            bos.write(buf);  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
        finally  
        {  
            if (bos != null)  
            {  
                try  
                {  
                    bos.close();  
                }  
                catch (IOException e)  
                {  
                    e.printStackTrace();  
                }  
            }  
            if (fos != null)  
            {  
                try  
                {  
                    fos.close();  
                }  
                catch (IOException e)  
                {  
                    e.printStackTrace();  
                }  
            }  
        }  
    } 
    

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
