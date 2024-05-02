package com.TechieTroveHub.handler;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ClassName: JsonObjectTypeHandler
 * Description: 将Java中的JSONObject对象转换为JSON字符串进行存储
 *
 * @Author agility6
 * @Create 2024/5/1 14:45
 * @Version: 1.0
 */
@MappedTypes(JSONObject.class)
@MappedJdbcTypes(JdbcType.LONGVARCHAR)
public class JsonObjectTypeHandler extends BaseTypeHandler<JSONObject> {

    /**
     * 设置非空参数
     * 将Java中的JSONObject对象转换为String类型
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONObject parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, String.valueOf(parameter.toJSONString()));
    }

    /**
     * 根据列名，获取可以为空的结果
     */
    @Override
    public JSONObject getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String sqlJson = rs.getString(columnName);
        if (null != sqlJson){
            return JSONObject.parseObject(sqlJson);
        }
        return null;
    }

    /**
     * 根据列索引，获取可以为空的结果
     */
    @Override
    public JSONObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String sqlJson = rs.getString(columnIndex);
        if (null != sqlJson){
            return JSONObject.parseObject(sqlJson);
        }
        return null;
    }

    @Override
    public JSONObject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String sqlJson = cs.getString(columnIndex);
        if (null != sqlJson){
            return JSONObject.parseObject(sqlJson);
        }
        return null;
    }
}
