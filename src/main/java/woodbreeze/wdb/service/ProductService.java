package woodbreeze.wdb.service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;
import woodbreeze.wdb.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woodbreeze.wdb.domain.Process;
import woodbreeze.wdb.repository.OrderRepository;
import woodbreeze.wdb.repository.ProcessRepository;
import woodbreeze.wdb.repository.ProductRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final LotService lotService;




    //저장
    @Transactional
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    // 삭제
    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    // 원자재 이름으로 제품 조회
    public List<Product> getProductsByMaterialName(String materialName) {
        return productRepository.findByMaterialName(materialName);
    }

    Product getProductFromProductName(ProductName productName) {
        // 주어진 productName을 기반으로 Product 객체를 생성하여 반환합니다.
        Product product = new Product();
        product.setProductName(ProductName.valueOf(productName.toString())); // 열거형 값을 문자열로 변환하여 제품 이름으로 설정
        return product;
    }

    // 모두 찾기
    public List<Product> findProduct() {
        return productRepository.findAll();
    }

    //아이디로 찾기 
    public Product findProductById(Long id) {
        log.info("찾아온 결과 : " + productRepository.findById(id));
        return productRepository.findById(id);
    }


    public void restStockProduct(List<Product> products, MaterialName materialName) {
        // 제품 목록에서 해당 원자재를 사용한 제품을 찾아 재고를 감소시킴
        for (Product product : products) {
            // 오더 리스트의 각 제품의 원자재 수를 감소시키는 로직 추가
            ProductName productName = product.getProductName();
            if (productName != null) {
                switch (productName) {
                    case HEDGEHOG:
                        if (materialName == MaterialName.MAPLE) {
                            decreaseMaterialQuantity(product);
                        }
                        break;
                    case BOWLINGSET:
                        if (materialName == MaterialName.BEECH) {
                            decreaseMaterialQuantity(product);
                        }
                        break;
                    case JENGA:
                        if (materialName == MaterialName.WHITEPINE) {
                            decreaseMaterialQuantity(product);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Transactional
    public void restStockMaterial(List<Product> products, MaterialName materialName) {
        // 제품 목록에서 해당 원자재를 사용한 제품을 찾아 재고를 감소시킴
        for (Product product : products) {
            // 오더 리스트의 각 제품의 원자재 수를 감소시키는 로직 추가
            ProductName productName = product.getProductName();
            if (productName != null && product.getMaterialName() == materialName) {
                int currentQuantity = product.getMaterialQuantity();
                int newQuantity = currentQuantity - 1;
                if (newQuantity < 0) {
                    newQuantity = 0;
                }
                product.setMaterialQuantity(newQuantity);
                log.info(product.getMaterialName().toString() + "의 재고가 감소되었습니다.");
                productRepository.save(product); // 변경된 제품 저장
            }
        }
    }


    private void decreaseMaterialQuantity(Product product) {
        // 현재 원자재 수를 가져옴
        int currentQuantity = product.getMaterialQuantity();

        // 새로운 원자재 수는 현재 원자재 수에서 1을 감소한 값
        int newQuantity = currentQuantity - 1;

        // 최소 원자재 수는 0이어야 함
        if (newQuantity < 0) {
            newQuantity = 0;
        }

        // 새로운 원자재 수 설정
        product.setMaterialQuantity(newQuantity);
        log.info(product.getMaterialName().toString() + "의 재고가 감소되었습니다.");
    }



//
//    public void cancelOrder(Lot lot) {
//        // 재고 복구
//        if (lot != null) {
//            List<Product> products = lot.getProducts();
//            for (Product product : products) {
//                // LotService를 통해 removeProduct 메서드 호출
//                lotService.removeProduct(product);
//
//                // 다른 정보 초기화
//                product.setMaterialName(null);
//                product.setEndProduct(0);
//                product.setDefects(0);
//                product.setDateReceived(null);
//                product.setManufacturer(null);
//                product.setMaterialQuantity(0);
//                product.setExpiry(null);
//                product.setProcess(null);
//                product.setControl(null);
//                product.getProcesses().clear(); // 연관된 프로세스 제거
//            }
//
//        }
//    }



}