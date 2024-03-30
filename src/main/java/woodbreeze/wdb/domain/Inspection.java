package woodbreeze.wdb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Inspection {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //식별자

    @OneToOne(mappedBy = "inspection")
    private Process process;

    @Enumerated(EnumType.STRING)
    private InspectionStatus result;    // 검수안에 결과를 저장

}
