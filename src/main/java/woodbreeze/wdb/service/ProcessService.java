package woodbreeze.wdb.service;

import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woodbreeze.wdb.domain.*;
import woodbreeze.wdb.domain.Process;
import woodbreeze.wdb.exception.NotEnoughStockException;
import woodbreeze.wdb.repository.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProcessService {

    private final ProcessRepository processRepository;
    private final LotRepository lotRepository;
    private final ProductRepository productRepository;
    private final InspectionService inspectionService;
    private final ProductService productService;
    private final ControlStatusService controlStatusService;
    private final ControlStatusRepository controlStatusRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;


    @Transactional
    public void process1(Orders orders) {
        //planQTY 값 모두 0으로 초기화

        log.info("공정 1을 실행합니다.");
        // 주문 객체에서 주문번호를 가져옴
        String WorkOrderId = orders.getWorkOrderId();
        log.info("주문번호를 가져왔습니다.");
        //주문 번호 사용하여 정보 조회
        Product product = orderService.findByWorkOrderId(orders.getWorkOrderId()).getProduct();
        log.info("정보를 조회했습니다.");
        // 프로세스 1을 가져옵니다.
        Process process1 = processRepository.findById(1L);

        // ControlStatus 가져오기
        ControlStatus controlStatus = controlStatusService.findOne(1L);
        List<Product> products = productRepository.findAll();

        // Process 객체와 Product 객체를 인자로 전달하여 공정 실행
        executeProcess(process1, orders, controlStatus);

        // 다음 공정 실행
        process2(orders, controlStatus);
    }

    @Transactional
    public void process2 (Orders orders, ControlStatus controlStatus) {
        log.info("공정 2를 실행합니다.");
        Process process2 = processRepository.findById(2L);
        controlStatusService.findOne(2L);
        //QTY 는 이전 공정에서 받아와야함
        executeProcess(process2, orders, controlStatus);
        process3(orders,controlStatus );
    }

    @Transactional
    public void process3 (Orders orders, ControlStatus controlStatus) {
        log.info("공정 3을 실행합니다.");
        Process process3 = processRepository.findById(3L);
        controlStatusService.findOne(3L);
        executeProcess(process3, orders, controlStatus);
        process4(orders,controlStatus );
    }

    @Transactional
    public void process4 (Orders orders, ControlStatus controlStatus) {
        log.info("공정 4를 실행합니다.");
        Process process4 = processRepository.findById(4L);
        controlStatusService.findOne(4L);
        executeProcess(process4, orders, controlStatus);
        process5(orders,controlStatus );
    }

    @Transactional
    public void process5 (Orders orders, ControlStatus controlStatus) {
        log.info("공정 5을 실행합니다.");
        Process process5 = processRepository.findById(5L);
        controlStatusService.findOne(5L);
        executeProcess(process5, orders, controlStatus);
        process6(orders,controlStatus );
    }

    @Transactional
    public void process6 (Orders orders, ControlStatus controlStatus) {
        Process process6 = processRepository.findById(6L);
        log.info("공정 6을 실행합니다.");
        controlStatusService.findOne(6L);
        //여기서의 반제품은 완제품이 되어야함.
        executeProcess(process6, orders, controlStatus);

        // 주문 상태를 CREATED로 설정
        orders.setOrderStatus(OrderStatus.COMPLETED);
        orderService.saveOrder(orders);
        controlStatusService.saveControlStatus(controlStatus);
    }

    
    //전체 공정
    @Transactional
    public void executeProcess(Process process, Orders orders, ControlStatus controlStatus) {
        // 공정 실행 상태를 가져옵니다.
        log.info("공정 상태를 확인합니다.");
        boolean isRunning = controlStatusRepository.isRunning();

        log.info("주문수량을 확인합니다.");

        if (!isRunning) {
            log.info("공정을 건너뜁니다.");
            return;
        }

        log.info("검수를 진행합니다.");
        // 불량률 확인을 위한 검수 실행
        Inspection inspection = inspectionService.productInspection();
        if (InspectionStatus.FAIL.equals(inspection.getResult())) {
            log.info("불량으로 판정되었습니다.");
            if (controlStatus != null) {
                controlStatus.setDefective(controlStatus.getDefective() + 1); // 불량품 개수 업데이트
                controlStatusRepository.update(controlStatus);
            }
        } else {
            log.info("불량이 아닌 제품으로 판정되었습니다.");
            if (controlStatus != null) {
                controlStatus.setDefects(controlStatus.getDefects() + 1);


                }
            }
        // 목표 개수를 다 채웠다면 불량품과 반제품 개수를 저장하고 종료합니다.
        int targetQuantity = controlStatus.getPlanQTY();
        if (controlStatus.getDefects() == targetQuantity) {
            controlStatus.setFault(controlStatus.getFault() + controlStatus.getDefects()); // 불량품 개수를 완제품에 더함
            controlStatusRepository.update(controlStatus);
            log.info("목표 개수를 달성하여 공정을 종료합니다.");
            // 공정 종료 처리
        }
    }


    
    public ProcessName getProcessNameById(Long processId) { //추가!!! - 가람
        // 프로세스 아이디로 프로세스를 조회하여 해당 프로세스의 이름을 반환
        Process process = processRepository.findById(processId);
        if (process != null) {
            return process.getProcessName();
        } else {
            // 해당 아이디로 프로세스를 찾을 수 없는 경우 예외 처리
            throw new IllegalArgumentException("프로세스를 찾을 수 없습니다. 프로세스 아이디: " + processId);
        }
    }

    //하나찾기
    public Process findOne(Long processId) {
        return processRepository.findById(processId);
    }

    //모두 찾기
    public List<Process> findProcess() {
        return processRepository.findAll();
    }



    // 저장
    public void lotSave(Product product) {
        // Lot 업데이트
        lotRepository.updateLot(product.getLot());
    }

    // 로트 넘버 추가 메서드 (프로젝트에서 받아옴)
    public void updateLotNumber(Product product, String newLotNumber) {
        product.getLot().setLotnumber(newLotNumber);
        lotRepository.updateLot(product.getLot());
    }

}
