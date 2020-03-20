package misc;

import com.github.dreamroute.me.sdk.common.IpUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IPUtilTest {
    
    public static void main(String[] args) {
        log.info(IpUtil.getIp());
    }

}
