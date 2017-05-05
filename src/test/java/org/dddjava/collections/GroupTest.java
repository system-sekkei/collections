package org.dddjava.collections;

import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupTest {

	static Group<MonthDay> 節句;
	static Group<MonthDay> 祭日;

	static MonthDay 七草 = MonthDay.of(1, 7);
	static MonthDay 桃の節句 = MonthDay.of(3,3);
	static MonthDay 端午 = MonthDay.of(5, 5);
	static MonthDay 七夕 = MonthDay.of(7, 7);
	static MonthDay 菊の節句 = MonthDay.of(9, 9);

	static MonthDay 正月 = MonthDay.of(1,1);
	static MonthDay 子供の日 = MonthDay.of(5, 5);
	static MonthDay 体育の日 = MonthDay.of(10, 10);

	@BeforeClass
	public static void beforeClass() {
		節句 = Group.of(七草,桃の節句,端午,七夕,菊の節句);
		祭日 = Group.of(正月,子供の日,体育の日);

		System.out.println(節句);
		System.out.println(祭日);
	}

	@Test
	public void size() throws Exception {
		assertThat(節句.size()).isEqualTo(5);
	}

	@Test
	public void isEmpty() throws Exception {
		assertThat(節句.isEmpty()).isFalse();
	}

	@Test
	public void includes() throws Exception {
		MonthDay 雛祭 = MonthDay.of(3, 3);
		assertThat(節句.includes(雛祭)).isTrue();
	}

	@Test
	public void includesGroup() throws Exception {
		MonthDay 雛祭 = MonthDay.of(3, 3);
		MonthDay 菖蒲 = MonthDay.of(5, 5);

		Group<MonthDay> こどもの節句 = Group.of(雛祭,菖蒲);
		assertThat(節句.includes(こどもの節句)).isTrue();
	}

	@Test
	public void contains() throws Exception {
		Predicate<MonthDay> 七夕より後 = each -> each.compareTo(七夕) > 0 ;
		assertThat(節句.contains(七夕より後)).isTrue();
	}

	@Test
	public void occurrencesOf() throws Exception {
		Predicate<MonthDay> 七夕以降 = each -> each.compareTo(七夕) >= 0 ;
		assertThat(節句.occurrencesOf(七夕以降)).isEqualTo(2);
	}

	@Test
	public void union() throws Exception {
		Group<MonthDay> expected =
			Group.of(正月,七草,桃の節句,端午,七夕,菊の節句,体育の日 );

		assertThat(節句.union(祭日).equals(expected)).isTrue();
	}

	@Test
	public void minus() throws Exception {
		Group<MonthDay> expected = Group.of(七草,桃の節句,七夕,菊の節句 );

		assertThat(節句.minus(祭日).equals(expected)).isTrue();
	}

	@Test
	public void intersect() throws Exception {
		Group<MonthDay> expected = Group.of(端午);

		assertThat(節句.intersect(祭日).equals(expected)).isTrue();
	}

	@Test
	public void select() throws Exception {

		Predicate<MonthDay> 七夕以降 = each -> each.compareTo(七夕) >= 0 ;

		Group<MonthDay> 七夕以降の節句 = Group.of(七夕,菊の節句);

		assertThat(節句.select(七夕以降).equals(七夕以降の節句)).isTrue();
	}

	@Test
	public void reject() throws Exception {
		Predicate<MonthDay> 七夕以降 = each -> each.compareTo(七夕) >= 0 ;

		Group<MonthDay> 七夕より前の節句 = Group.of(七草,桃の節句,端午);

		assertThat(節句.reject(七夕以降).equals(七夕より前の節句)).isTrue();
	}

	@Test
	public void detect() throws Exception {
		Predicate<MonthDay> 七夕より後の節句 = each -> each.compareTo(七夕) > 0 ;
		assertThat(節句.detect(七夕より後の節句)).isEqualTo(菊の節句);
	}

	@Test(expected = NoSuchElementException.class)
	public void detectThrowException() throws Exception {
		Predicate<MonthDay> 菊の節句より後の節句 = each -> each.compareTo(菊の節句) > 0 ;
		節句.detect(菊の節句より後の節句);
	}

	@Test
	public void detectOrDefault() throws Exception {
		Predicate<MonthDay> 菊の節句より後の節句 = each -> each.compareTo(菊の節句) > 0 ;

		MonthDay 既定値 = 七草;
		MonthDay target = 節句.detectOrDefault(菊の節句より後の節句,七草);
		assertThat(target).isEqualTo(既定値);
	}

	@Test
	public void reduce() throws Exception {
		BinaryOperator<MonthDay> 遅い節句 =
				(one, another) -> one.isAfter(another)? one : another;
		MonthDay target = 節句.reduce(正月, 遅い節句);
		assertThat(target).isEqualTo(菊の節句);
	}

	@Test
	public void mapReduce() throws Exception {
		//TODO 順序性がないので、reduce方法を再検討する
		String expected = "節句: 7月7日 9月9日 5月5日 1月7日 3月3日 ";

		DateTimeFormatter 月日 = DateTimeFormatter.ofPattern("M月d日");
		Function<MonthDay,String> 文字列に = each -> each.format(月日);
		BinaryOperator<String> 追記 = (one,another)->one + another + " ";
		String target = 節句.map(文字列に)
				.reduce("節句: ",(one,another)->one + another + " ");
		assertThat(target).isEqualTo(expected);

	}
//
//	@Test
//	public void mapTest() throws Exception {
//		Function<BigDecimal,Integer> function = each -> each.intValue();
//
//		Group<Integer> result = Group.of(
//			new Integer("0"),
//			new Integer("1"),
//			new Integer("2")
//		);
//
//		assertThat(節句.map(function).equals(result)).isTrue();
//	}

}