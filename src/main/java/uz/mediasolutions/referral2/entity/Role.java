package uz.mediasolutions.referral2.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import uz.mediasolutions.referral2.entity.template.AbsLong;
import uz.mediasolutions.referral2.enums.RoleName;

import javax.persistence.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "roles")
public class Role extends AbsLong implements GrantedAuthority {

    @Column(nullable = false, name = "name")
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Override
    public String getAuthority() {
        return name.name();
    }
}
