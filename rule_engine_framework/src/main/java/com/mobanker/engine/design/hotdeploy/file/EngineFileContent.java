package com.mobanker.engine.design.hotdeploy.file;


public class EngineFileContent {
	private String fileName;
	private byte[] bytes;
	
	public EngineFileContent(){}
	
//	public EngineFileContent(File file) throws FileNotFoundException,IOException{
//		if(file == null)
//			throw new FileNotFoundException("文件找不到");
//		this.fileName = file.getName();
//		this.bytes = FileUtils.readFileToByteArray(file);
//	}
//	
	
	public EngineFileContent(String fileName,byte[] bytes){
		this.fileName = fileName;
		this.bytes = bytes;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	
	@Override
	public String toString(){
		return "fileName:"+fileName+",byteLength:"+bytes.length;
	}
}
