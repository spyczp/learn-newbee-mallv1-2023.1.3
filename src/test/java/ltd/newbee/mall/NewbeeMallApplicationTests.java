package ltd.newbee.mall;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class NewbeeMallApplicationTests {

    // 注入数据源对象
    @Autowired
    private DataSource defaultDataSource;

    @Test
    void datasourceTest() throws SQLException {
        // 获取数据库连接对象
        Connection conn = defaultDataSource.getConnection();
        System.out.println("获取的连接：");

        // 判断连接对象是否为空
        System.out.println(conn != null);
        conn.close();
    }

}
