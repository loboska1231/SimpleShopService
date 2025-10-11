package org.project.shopservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.project.shopservice.config.TestContainersConfig;
import org.project.shopservice.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = TestContainersConfig.class)
class ProductRepositoryTest {
	@Autowired
	ProductRepository productRepository;

	@BeforeEach
	void setup() {
		productRepository.deleteAll();
		productRepository.saveAll(List.of(
				Product.builder().id("0x1").type("test 1").price(BigDecimal.valueOf(100)).category("testing").build(),
				Product.builder().id("0x2").type("test 2").price(BigDecimal.valueOf(150)).category("testing").build(),
				Product.builder().id("0x3").type("test 3").price(BigDecimal.valueOf(200)).category("testing 1").build(),
				Product.builder().id("0x4").type("test 4").price(BigDecimal.valueOf(250)).category("testing 1").build(),
				Product.builder().id("0x5").type("test 5").price(BigDecimal.valueOf(300)).category("casting").build(),
				Product.builder().id("0x6").type("test 6").price(BigDecimal.valueOf(400)).category("casting").build(),
				Product.builder().id("0x7").type("test 7").price(BigDecimal.valueOf(600)).category("testing 2").build(),
				Product.builder().id("0x8").type("test 8").price(BigDecimal.valueOf(900)).category("testing 3").build(),
				Product.builder().id("0x9").type("test 9").price(BigDecimal.valueOf(850)).category("testing 3").build(),
				Product.builder().id("0xa").type("test 10").price(BigDecimal.valueOf(1500)).category("testing").build()
		));
	}

	static Stream<Arguments> testFindAllByIdIn(){
		return Stream.of(
				Arguments.of(List.of("0x1","0x2","0x3","0x4")),
				Arguments.of(List.of("0x5","0x6","0x7","0x8")),
				Arguments.of(List.of("0x9","0xa","0x1","0xb")),
				Arguments.of(List.of("0xa","0xd","0xe","0xf")),
				Arguments.of(List.of(""," ","0x1","0xfffff"))
		);
	}

	@ParameterizedTest
	@MethodSource("testFindAllByIdIn")
	void findAllByIdIn(List<String> ids){
		assertFalse(productRepository.findAllByIdIn(ids).isEmpty());
	}
	@Test
	void findAllByIdIn_expectingEmpty(){
		assertTrue(productRepository.findAllByIdIn(List.of(""," ","0xaa","0xfffff")).isEmpty());
	}

	static Stream<Arguments> testFindAllByPriceLTEandGTEAndCategoryIsIgnoreCase(){
		return Stream.of(
				Arguments.of(100, 2000, "testing"),
				Arguments.of(150, 500, "tesTING 1"),
				Arguments.of(300, 900, "TESTing 2"),
				Arguments.of(400, 700, "testIng"),
				Arguments.of(500, 600, "testinG"),
				Arguments.of(750, 950, "TESTING 3"),
				Arguments.of(1000,2000,"testing"),
				Arguments.of(200, 500, "CASting"),
				Arguments.of(100, 150, "testing")
		);
	}
	@ParameterizedTest
	@MethodSource("testFindAllByPriceLTEandGTEAndCategoryIsIgnoreCase")
	void findAllByPriceGreaterThanEqualAndPriceLessThanEqualAndCategoryIsIgnoreCase(int min, int max, String category){
		assertFalse(productRepository.findAllByPriceGreaterThanEqualAndPriceLessThanEqualAndCategoryIsIgnoreCase(BigDecimal.valueOf(min),BigDecimal.valueOf(max),category).isEmpty());

	}
	static Stream<Arguments> testFindAllByPriceLTEandGTE(){
		return Stream.of(
				Arguments.of(100, 1000),
				Arguments.of(150, 500),
				Arguments.of(300, 900),
				Arguments.of(400, 700),
				Arguments.of(500, 600),
				Arguments.of(750, 950),
				Arguments.of(150, 250),
				Arguments.of(200, 500),
				Arguments.of(100, 150)
		);
	}
	@ParameterizedTest
	@MethodSource("testFindAllByPriceLTEandGTE")
	void findAllByPriceGreaterThanEqualAndPriceLessThanEqual(int min, int max){
		assertFalse(productRepository.findAllByPriceGreaterThanEqualAndPriceLessThanEqual(BigDecimal.valueOf(min),BigDecimal.valueOf(max)).isEmpty());
	}
	@Test
	void findAllByPricegteandlte(){
		assertFalse(productRepository.findAllByPriceGreaterThanEqualAndPriceLessThanEqual(BigDecimal.valueOf(100),BigDecimal.valueOf(1000)).isEmpty());
	}

	static Stream<Arguments> testFORFindAllByPriceLTEorGTE(){
		return Stream.of(
				Arguments.of(100),
				Arguments.of(200),
				Arguments.of(500),
				Arguments.of(600),
				Arguments.of(1500),
				Arguments.of(1000),
				Arguments.of(1450)
		);
	}
	@ParameterizedTest
	@MethodSource("testFORFindAllByPriceLTEorGTE")
	void findAllByPriceLessThanEqual(int min){
		assertFalse(productRepository.findAllByPriceLessThanEqual(BigDecimal.valueOf(min)).isEmpty());
	}

	@ParameterizedTest
	@MethodSource("testFORFindAllByPriceLTEorGTE")
	void findAllByPriceGreaterThanEqual(int min){
		assertFalse(productRepository.findAllByPriceLessThanEqual(BigDecimal.valueOf(min)).isEmpty());
	}
	static Stream<Arguments> testForfindAllByPriceLTEorGTEAndCategoryIsIgnoreCase(){
		return Stream.of(
				Arguments.of(100 ,"testing"),
				Arguments.of(200 ,"tesTING 1" ),
				Arguments.of(600 ,"TESTing 2" ),
				Arguments.of(600 ,"testIng"),
				Arguments.of(1500,"testinG"),
				Arguments.of(880,"TESTING 3" ),
				Arguments.of(1450,"testing")
		);
	}
	@ParameterizedTest
	@MethodSource("testForfindAllByPriceLTEorGTEAndCategoryIsIgnoreCase")
	void findAllByPriceLessThanEqualAndCategoryIsIgnoreCase(int  min, String category){
		assertFalse(productRepository.findAllByPriceLessThanEqualAndCategoryIsIgnoreCase(BigDecimal.valueOf(min),category).isEmpty());
	}
	@ParameterizedTest
	@MethodSource("testForfindAllByPriceLTEorGTEAndCategoryIsIgnoreCase")
	void findAllByPriceGreaterThanEqualAndCategoryIsIgnoreCase(int  min, String category){
		assertFalse(productRepository.findAllByPriceGreaterThanEqualAndCategoryIsIgnoreCase(BigDecimal.valueOf(min),category).isEmpty());
	}
}