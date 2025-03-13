package once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ListUtils;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

/**
 * ClassName:ImportExcel
 * Package:once
 * User:HP
 * Date:2025/3/2
 * Time:10:48
 *
 * @Author 周东汉
 * @Version 1.0
 * Description:
 */
public class ImportExcel {
    public static void main(String[] args) {

        String fileName = "E:\\鱼皮项目\\伙伴匹配系统\\yupao-backend\\src\\main\\resources\\test.xlsx";
        // 监听器
//        readListener(fileName);
        // 同步读取
        synchronousRead(fileName);
    }

    /**
     * 读取监听器
     * @param fileName
     */
    public static void readListener(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        EasyExcel.read(fileName, UserInfo.class, new DemoDataListener()).sheet().doRead();
    }

    /**
     * 同步读取
     * @param fileName
     */
    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<UserInfo> list = EasyExcel.read(fileName).head(UserInfo.class).sheet().doReadSync();
         // 这里简单打印一下，你可以自己业务逻辑处理
        for (UserInfo user : list) {
            System.out.println(user);
        }
    }
}
