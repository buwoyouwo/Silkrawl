package com.buwoyouwo.silkrawl.pen;

import java.io.Serializable;



/**
 * 涂鸦Bean
 * @author buwoyouwo
 *
 */
public interface IScrawl extends Serializable{
	/**
	 * 应用涂鸦实际绘图
	 * @param drawer	绘图器，与具体图形库相关
	 */
	public void draw(IScrawlDrawer drawer);
}
