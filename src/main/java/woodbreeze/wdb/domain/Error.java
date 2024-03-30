package woodbreeze.wdb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Error {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String errorCode;   // 에러코드

    private String errorMessage;   // 에러 메세지

    private String errorName;   // 에러 이름

    @Enumerated(EnumType.STRING)
    private ErrorType errorType;

}
