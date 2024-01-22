package com.mobanker.engine.common.page;

public class PageQco {
	protected int pageNum;
	protected int pageSize;
	public int getPageNum() {
		return pageNum;
	}
	public void setPage(int pageNum,int pageSize) {
		this.pageSize = pageSize;
		this.pageNum = pageNum;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}		
	
	
}
