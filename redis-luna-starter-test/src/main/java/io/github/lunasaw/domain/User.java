package io.github.lunasaw.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenzhangyue
 * 2023/2/1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long userId;

    private String userName;

    private String userPassword;

}
