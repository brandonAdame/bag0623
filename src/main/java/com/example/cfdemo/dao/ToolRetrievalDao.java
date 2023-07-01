package com.example.cfdemo.dao;

import com.example.cfdemo.pojo.Checkout;
import com.example.cfdemo.pojo.Tool;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ToolRetrievalDao {

    @Select("SELECT * FROM TOOLS WHERE TOOL_CODE = #{toolCode}")
    Tool checkoutTool(Checkout checkoutRequest);
}
