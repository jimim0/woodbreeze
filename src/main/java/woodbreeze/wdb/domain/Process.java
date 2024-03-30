package woodbreeze.wdb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Slf4j // 로그

@Getter @Setter
public class Process {

    @Id
    @GeneratedValue
    @Column(name = "process_id")
    private Long id;

    @OneToMany(mappedBy = "process")
    private List<Product> products;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id")
    private Inspection inspection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "control_id")
    private Control control;

    @OneToMany(mappedBy = "process")
    private List<Orders> orders;

    @ManyToOne
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @Enumerated(EnumType.STRING)
    private ProcessName processName;

    private String result;


    private int halfProduct; // 공정전 제품

    private int endProduct; // 공정후 제품

    private int createProduct; // 완제품

    private String process;

    private boolean started; // 공정 시작 여부를 나타냄
    private boolean stopped; // 공정 중지 여부를 나타냄



    //랜덤
    public static boolean random() {
        Random random = new Random();
        int randomNum = random.nextInt(10) + 1;
        return randomNum <= 9;
    }
}



// 프로세스와 프로덕트는 양방향이다
//    private void addProcessProduct(Product product) {
//        this.product = product;
//        product.getProcesses().add(this);
//    }


//@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//@JoinColumn(name = "orders_id")
//private Orders orders;

//@ElementCollection
//@CollectionTable(name = "process_names", joinColumns = @JoinColumn(name = "control_id"))
//@Column(name = "process_name")
//private final List<String> processNames = new ArrayList<>();


// 사용한 클래스
//    class Orders {}
//    class Process {
//        Control getControl() {
//            return new Control();
//        }
//        int getId() {
//            return 1;
//        }
//        void setResult(String result) {}
//    }
//    class Product {
//        Lot getLot() {
//            return new Lot();
//        }
//        int getDefects() {
//            return 0;
//        }
//        void setDefects(int defects) {}
//        int getFault() {
//            return 0;
//        }
//        void setFault(int fault) {}
//    }
//    class Control {
//        boolean getProcessRunningStatus(ProcessName processName) {
//            return true;
//        }
//    }
//    class Lot {
//        String getLotnumber() {
//            return "123";
//        }
//        void setLotnumber(String lotnumber) {}
//    }
//
//    enum ProcessName {
//        Materialprocessing, // 재료 처리 및 가공 단계
//        Assembly, //조립 단계
//        Surfacetreatment, // 표면 처리 및 마감 단계
//        Painting, // 품질 검사 단계
//        Qualityinspection, // 품질 검사 단계
//        Packagingandshipping // 포장 및 포장 및 출하 단계
//    }
//}