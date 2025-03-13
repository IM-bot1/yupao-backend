package once;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode
public class UserInfo {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @ExcelProperty("成员编号")
    private Long id;

    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;
}