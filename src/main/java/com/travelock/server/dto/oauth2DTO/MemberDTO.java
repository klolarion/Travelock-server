//13.
package com.travelock.server.dto.oauth2DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {
    private String role;
    private String email;
    private Long memberId;
    private String nickname;
    private String provider;
}
