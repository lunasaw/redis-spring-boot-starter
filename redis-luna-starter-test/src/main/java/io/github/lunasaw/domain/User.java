package io.github.lunasaw.domain;

import lombok.Builder;
import lombok.Data;

/**
 * @author chenzhangyue
 * 2023/2/1
 */
@Data
@Builder
public class User {

    private Long userId;

    private String userName;

    private String userPassword;

}
