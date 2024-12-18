package cn.jarlen.photoedit.adapter;

import java.util.List;

/**
 * 水印包解析bean
 * 
 * @author jarlen
 * 
 */
public class WaterMarkFolderBean {

	/**
	 * 水印包目录
	 */
	private String folderId;

	/**
	 * 水印包名字
	 */
	private String typeName;

	/**
	 * 混合的水印
	 */
	private String fixedIcon;

	/**
	 * 水印缩略图
	 */
	private List<String> icon;

	/**
	 * 水印资源图
	 */
	private List<String> resource;

	/**
	 * 活动标签
	 */
	private int tag;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getFixedIcon() {
		return fixedIcon;
	}

	public void setFixedIcon(String fixedIcon) {
		this.fixedIcon = fixedIcon;
	}

	public List<String> getIcon() {
		return icon;
	}

	public void setIcon(List<String> icon) {
		this.icon = icon;
	}

	public List<String> getResource() {
		return resource;
	}

	public void setResource(List<String> resource) {
		this.resource = resource;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

}
