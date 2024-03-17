package com.TechieTroveHub.POJO;

import java.util.List;

/**
 * ClassName: PageResult
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/17 22:47
 * @Version: 1.0
 */
public class PageResult<T> {

    private Integer total;

    private List<T> list;

    public PageResult(Integer total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
