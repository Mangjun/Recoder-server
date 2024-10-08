package yuhan.hgcq.server.dto.member;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginForm {
    private String email;
    private String password;
}
