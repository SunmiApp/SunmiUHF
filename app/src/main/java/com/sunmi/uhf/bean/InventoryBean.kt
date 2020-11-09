package com.sunmi.uhf.bean

/**
 * @ClassName: InventoryBean
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-15 下午4:03
 * @UpdateDate: 20-9-15 下午4:03
 */
data class InventoryBean(
    var type: Int,                            //类型
    var name: String?,                        //名称
    var des: String?,                         //描述
    var rfLinkDb: String?,                    //射频强度
    var mode: String?,                        //模式
    var power: Boolean?,                      //动态功率
    var focus: Boolean?,                      //标签焦点
    var label: String?,                       //盘存标志
    var rfLink: String?                       //射频link
)