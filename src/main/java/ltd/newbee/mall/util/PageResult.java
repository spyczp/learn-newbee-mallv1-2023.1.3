package ltd.newbee.mall.util;

import lombok.Data;

import java.util.List;

@Data
public class PageResult {

    //当前页
    private int currentPage;

    //每页显示条数
    private int pageSize;

    //总记录数
    private int totalCount;

    //总页数
    private int totalPage;

    //列表数据
    private List<?> list;

    public PageResult(int currentPage, int pageSize, int totalCount, List<?> list) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.list = list;
        //计算总页数
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
    }
}
