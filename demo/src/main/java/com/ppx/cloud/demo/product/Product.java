/**
 * 
 */
package com.ppx.cloud.demo.product;

/**
 * @author mark
 * @date 2018年11月9日
 */
public class Product {

	private Integer prodId;
	private String prodTitle;
	private Integer catId;
	private Integer recommend;
	private String mainImgSrc;

	public Integer getProdId() {
		return prodId;
	}

	public void setProdId(Integer prodId) {
		this.prodId = prodId;
	}

	public String getProdTitle() {
		return prodTitle;
	}

	public void setProdTitle(String prodTitle) {
		this.prodTitle = prodTitle;
	}

	public Integer getCatId() {
		return catId;
	}

	public void setCatId(Integer catId) {
		this.catId = catId;
	}

	public Integer getRecommend() {
		return recommend;
	}

	public void setRecommend(Integer recommend) {
		this.recommend = recommend;
	}

	public String getMainImgSrc() {
		return mainImgSrc;
	}

	public void setMainImgSrc(String mainImgSrc) {
		this.mainImgSrc = mainImgSrc;
	}

}
