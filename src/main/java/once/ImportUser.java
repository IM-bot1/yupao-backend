package once;

import com.alibaba.excel.EasyExcel;
import com.yupi.yupao.model.domain.User;

import java.util.List;

/**
 * ClassName:ImportUser
 * Package:once
 * User:HP
 * Date:2025/3/2
 * Time:13:46
 *
 * @Author 周东汉
 * @Version 1.0
 * Description:
 */
public class ImportUser {
    public static void main(String[] args) {
        String fileName = "E:\\鱼皮项目\\伙伴匹配系统\\yupao-backend\\src\\main\\resources\\prodExcel.xlsx";
        List<UserInfo> userList = EasyExcel.read(fileName).head(UserInfo.class).sheet().doReadSync();
        System.out.println("总数" + userList.size());

    }
}
