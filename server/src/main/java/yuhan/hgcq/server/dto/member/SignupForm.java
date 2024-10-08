package yuhan.hgcq.server.dto.member;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignupForm {
    private String name;
    private String email;
    private String password;
}
