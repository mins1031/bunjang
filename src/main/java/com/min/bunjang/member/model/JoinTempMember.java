package com.min.bunjang.member.model;

import com.min.bunjang.join.dto.TempJoinRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinTempMember {

    @Id
    private String email;
    private String password;

    private String name;
    private String phone;

    private LocalDate birthDate;
    private LocalDateTime joinDate;
    private LocalDateTime updatedDate;

    private JoinTempMember(String email, String password, String name, String phone, LocalDate birthDate) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.birthDate = birthDate;
        this.joinDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    public static JoinTempMember createJoinTempMember(TempJoinRequest tempJoinRequest, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return new JoinTempMember(
                tempJoinRequest.getEmail(),
                bCryptPasswordEncoder.encode(tempJoinRequest.getPassword()),
                tempJoinRequest.getName(),
                tempJoinRequest.getPhone(),
                tempJoinRequest.getBirthDate()
        );
    }
}
