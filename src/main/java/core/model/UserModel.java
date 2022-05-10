package core.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)


public class UserModel {

        public long id;
        public String username;
        public String firstName;
        public String lastName;
        public String email;
        public String password;
        public String phone;
        public int userStatus;
    }


