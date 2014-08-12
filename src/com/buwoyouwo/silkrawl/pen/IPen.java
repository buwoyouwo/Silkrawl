package com.buwoyouwo.silkrawl.pen;

import java.io.Serializable;

/**
 * 画笔，根据参数绘制涂鸦
 * @author buwoyouwo
 *
 */
public interface IPen extends Serializable{
	/**
	 * Configure this pen
	 * @param configuration	Varies for different class of pens 
	 */
	public void config(Object configuration);
	
	/**
	 * 涂鸦一笔，获得涂鸦Bean
	 * @param operation	用户操作相关参数,对不同平台
	 * @return	涂鸦结果
	 */
	public IScrawl scrawl(Object operation);
}
