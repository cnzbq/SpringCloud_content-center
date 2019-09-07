package cn.zbq.springcloud.contentcenter;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 *
 * @author Dingwq
 * @since 2019/9/5 22:18
 */
@Slf4j
public class LombokTest {
    public static void main(String[] args) {
        // 建造者模式
        UserRegisterDTO build = UserRegisterDTO.builder()
                .email("xxx")
                .password("xx")
                .agreement(true)
                .build();
        log.info("构造出来的UserRegisterDTO{}", build);
    }

}

//@Setter
//@Getter
//@ToString
//@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
// 为标记为final的字段生成构造方法
//@RequiredArgsConstructor
@Builder
class UserRegisterDTO {
    private String email;
    private String password;
    private String confirmPassword;
    private String mobile;
    private Boolean agreement;

}
