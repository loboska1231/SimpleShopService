package org.project.shopservice.repository;

import org.bson.types.Decimal128;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
				Arguments.of(900, 7000, "testIng"),
				Arguments.of(1400, 1600, "testinG")
		);
	}
	@ParameterizedTest
	@MethodSource("testFindAllByPriceLTEandGTEAndCategoryIsIgnoreCase")
	void findAllByPriceGreaterThanEqualAndPriceLessThanEqualAndCategoryIsIgnoreCase(int min, int max, String category){
		assertFalse(productRepository.findAllByPriceGreaterThanEqualAndPriceLessThanEqualAndCategoryIsIgnoreCase(new Decimal128(min),new Decimal128(max),category).isEmpty());

	}

	static Stream<Arguments> testFindAllByPriceLTEandGTEAndCategoryIsIgnoreCase_expectingEmpty(){
		return Stream.of(
				Arguments.of(0, 50, "testing"),
				Arguments.of(150, 50, "tesTING 1"),
				Arguments.of(1000,2000,"test1ng"),
				Arguments.of(200, 500, "CAS ting"),
				Arguments.of(100, 150, "testing ")
		);
	}
	@ParameterizedTest
	@MethodSource("testFindAllByPriceLTEandGTEAndCategoryIsIgnoreCase_expectingEmpty")
	void findAllByPriceGreaterThanEqualAndPriceLessThanEqualAndCategoryIsIgnoreCase_expectingEmpty(int min, int max, String category){
		assertTrue(productRepository.findAllByPriceGreaterThanEqualAndPriceLessThanEqualAndCategoryIsIgnoreCase(new Decimal128(min),new Decimal128(max),category).isEmpty());

	}

	static Stream<Arguments> testFindAllByPriceLTEandGTE(){
		return Stream.of(
				Arguments.of(100, 1000),
				Arguments.of(150, 500),
				Arguments.of(100, 150),
				Arguments.of(99, 151)
		);
	}
	@ParameterizedTest
	@MethodSource("testFindAllByPriceLTEandGTE")
	void findAllByPriceGreaterThanEqualAndPriceLessThanEqual(int min, int max){
		assertFalse(productRepository.findAllByPriceGreaterThanEqualAndPriceLessThanEqual(new Decimal128(BigDecimal.valueOf(min)),new Decimal128(BigDecimal.valueOf(max))).isEmpty());
	}
	static Stream<Arguments> testFindAllByPriceLTEandGTE_expectingEmpty(){
		return Stream.of(
				Arguments.of( 555 , 560),
				Arguments.of( 0 , 0),
				Arguments.of( -1 , 1),
				Arguments.of( 1 , -1)
		);
	}
	@ParameterizedTest
	@MethodSource("testFindAllByPriceLTEandGTE_expectingEmpty")
	void findAllByPriceGreaterThanEqualAndPriceLessThanEqual_expectingEmpty(int min, int max){
		assertTrue(productRepository.findAllByPriceGreaterThanEqualAndPriceLessThanEqual(new Decimal128(BigDecimal.valueOf(min)),new Decimal128(BigDecimal.valueOf(max))).isEmpty());
	}

	static Stream<Arguments> testFORFindAllByPriceLTEorGTE(){
		return Stream.of(
				Arguments.of(100),
				Arguments.of(200),
				Arguments.of(1500),
				Arguments.of(1000),
				Arguments.of(1450)
		);
	}
	@ParameterizedTest
	@MethodSource("testFORFindAllByPriceLTEorGTE")
	void findAllByPriceLessThanEqual(int min){
		assertFalse(productRepository.findAllByPriceLessThanEqual(new Decimal128(BigDecimal.valueOf(min))).isEmpty());
	}

	@ParameterizedTest
	@MethodSource("testFORFindAllByPriceLTEorGTE")
	void findAllByPriceGreaterThanEqual(int min){
		assertFalse(productRepository.findAllByPriceLessThanEqual(new Decimal128(BigDecimal.valueOf(min))).isEmpty());
	}
	static Stream<Arguments> testFORFindAllByPriceLTE_expectingEmpty(){
		return Stream.of(
				Arguments.of(99),
				Arguments.of(0),
				Arguments.of(-1)
		);
	}
	@ParameterizedTest
	@MethodSource("testFORFindAllByPriceLTE_expectingEmpty")
	void findAllByPriceLessThanEqual_expectingEmpty(int max){
		assertTrue(productRepository.findAllByPriceLessThanEqual(new Decimal128(BigDecimal.valueOf(max))).isEmpty());
	}

	@Test
	void findAllByPriceGreaterThanEqual_expectingEmpty(){
		assertFalse(productRepository.findAllByPriceLessThanEqual(new Decimal128(BigDecimal.valueOf(1501))).isEmpty());
	}
	static Stream<Arguments> testForfindAllByPriceLTEorGTEAndCategoryIsIgnoreCase(){
		return Stream.of(
				Arguments.of(100 ,"testing"),
				Arguments.of(200 ,"tesTING 1" ),
				Arguments.of(1500,"testinG"),
				Arguments.of(880,"TESTING 3" ),
				Arguments.of(1450,"testing")
		);
	}
	@ParameterizedTest
	@MethodSource("testForfindAllByPriceLTEorGTEAndCategoryIsIgnoreCase")
	void findAllByPriceLessThanEqualAndCategoryIsIgnoreCase(int  min, String category){
		assertFalse(productRepository.findAllByPriceLessThanEqualAndCategoryIsIgnoreCase(new Decimal128(BigDecimal.valueOf(min)),category).isEmpty());
	}
	@ParameterizedTest
	@MethodSource("testForfindAllByPriceLTEorGTEAndCategoryIsIgnoreCase")
	void findAllByPriceGreaterThanEqualAndCategoryIsIgnoreCase(int  max, String category){
		assertFalse(productRepository.findAllByPriceGreaterThanEqualAndCategoryIsIgnoreCase(new Decimal128(BigDecimal.valueOf(max)),category).isEmpty());
	}
	static Stream<Arguments> testForfindAllByPriceLTEAndCategoryIsIgnoreCase_expectingEmpty(){
		return Stream.of(
				Arguments.of(99 ,"testing"),
				Arguments.of(199 ,"tesTING 1" ),
				Arguments.of(299 ,"CaStInG")
		);
	}
	@ParameterizedTest
	@MethodSource("testForfindAllByPriceLTEAndCategoryIsIgnoreCase_expectingEmpty")
	void findAllByPriceLessThanEqualAndCategoryIsIgnoreCase_expectingEmpty(int max, String category){
		assertTrue(productRepository.findAllByPriceLessThanEqualAndCategoryIsIgnoreCase(new Decimal128(BigDecimal.valueOf(max)),category).isEmpty());
	}
	static Stream<Arguments> testForfindAllByPriceGTEAndCategoryIsIgnoreCase_expectingEmpty(){
		return Stream.of(
				Arguments.of(251 ,"tesTING 1" ),
				Arguments.of(901 ,"TESTing 2" ),
				Arguments.of(401 ,"CaStInG")
		);
	}
	@ParameterizedTest
	@MethodSource("testForfindAllByPriceGTEAndCategoryIsIgnoreCase_expectingEmpty")
	void findAllByPriceGreaterThanEqualAndCategoryIsIgnoreCase_expectingEmpty(int  min, String category){
		assertTrue(productRepository.findAllByPriceGreaterThanEqualAndCategoryIsIgnoreCase(new Decimal128(BigDecimal.valueOf(min)),category).isEmpty());
	}

	static Stream<Arguments> testForfindByCategoryIsIgnoreCase(){
		return Stream.of(
				Arguments.of("testing"),
				Arguments.of("tesTING 1" ),
				Arguments.of("TESTing 2" ),
				Arguments.of("CaStInG")
		);
	}

	@ParameterizedTest
	@MethodSource("testForfindByCategoryIsIgnoreCase")
	void findAllByCategory_ExpectingNotEmpty(String category){
		assertFalse(productRepository.findAllByCategoryIgnoreCase(category).isEmpty());
	}

}