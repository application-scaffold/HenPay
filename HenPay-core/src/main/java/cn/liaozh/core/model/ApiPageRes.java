package cn.liaozh.core.model;

import cn.liaozh.constants.ApiCodeEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/*
 * 接口返回对象
 */
@Data
@Schema(description = "分页res")
public class ApiPageRes<M> extends ApiRes {

    /**
     * 数据对象
     **/
    @Schema(description = "业务数据")
    private PageBean<M> data;


    /**
     * 业务处理成功， 封装分页数据， 仅返回必要参数
     **/
    public static <M> ApiPageRes<M> pages(IPage<M> iPage) {

        PageBean<M> innerPage = new PageBean<>();
        innerPage.setRecords(iPage.getRecords());  //记录明细
        innerPage.setTotal(iPage.getTotal()); //总条数
        innerPage.setCurrent(iPage.getCurrent()); //当前页码
        innerPage.setHasNext(iPage.getPages() > iPage.getCurrent()); //是否有下一页

        ApiPageRes result = new ApiPageRes();
        result.setData(innerPage);
        result.setCode(ApiCodeEnum.SUCCESS.getCode());
        result.setMsg(ApiCodeEnum.SUCCESS.getMsg());

        return result;
    }


    @Data
    @Schema(description = "分页data")
    public static class PageBean<M> {

        /**
         * 数据列表
         */
        @Schema(title = "records", description = "数据列表")
        private List<M> records;

        /**
         * 总数量
         */
        @Schema(title = "total", description = "总数量")
        private Long total;

        /**
         * 当前页码
         */
        @Schema(title = "current", description = "当前页码")
        private Long current;

        /**
         * 是否包含下一页， true:包含 ，false: 不包含
         */
        @Schema(title = "hasNext", description = "是否包含下一页， true:包含 ，false: 不包含")
        private boolean hasNext;

    }

}
