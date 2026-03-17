package io.github.piscescup;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class LinqTest {

    private static final String[] EMAIL_SUFFIX = new String[] {
        "@qq.com",
        "@outlook.com",
        "@gmail.com",
        "@yahoo.com",
        "@hotmail.com",
        "@163.com",
        "@mails.jlu.edu.cn"
    };

    record Person(String name, int age, String address) {
        public String email() {
            String emailSuf = EMAIL_SUFFIX[new Random().nextInt(0, EMAIL_SUFFIX.length)];
            return name.toLowerCase() + age + emailSuf;
        }

        @Override
        public @NonNull String toString() {
            return name + ", " + age + ", email: " + email();
        }
    }


    private static final List<Person> PERSONS = List.of(
        new Person("Alice", 23, "New York"),
        new Person("Bob", 30, "Los Angeles"),
        new Person("Charlie", 28, "Chicago"),
        new Person("David", 35, "Houston"),
        new Person("Eve", 22, "San Francisco"),
        new Person("Frank", 40, "Seattle"),
        new Person("Grace", 27, "Boston"),
        new Person("Hank", 33, "Denver"),
        new Person("Ivy", 26, "Austin"),
        new Person("Jack", 31, "Miami"),

        new Person("LiHua", 24, "Beijing"),
        new Person("ZhangWei", 29, "Shanghai"),
        new Person("WangFang", 32, "Guangzhou"),
        new Person("LiuYang", 21, "Shenzhen"),
        new Person("ChenJie", 36, "Hangzhou"),
        new Person("YangLei", 38, "Nanjing"),
        new Person("ZhaoMin", 25, "Wuhan"),
        new Person("SunTao", 34, "Chengdu"),
        new Person("ZhouKai", 28, "Xi'an"),
        new Person("WuDi", 41, "Tianjin")
    );

    @Test
    void enumerator() {
        Enumerable<Person> people = Linq.fromIterable(PERSONS);
        try (Enumerator<Person> enumerator = people.enumerator()) {
            while (enumerator.moveNext()) {
                Person p = enumerator.current();
                System.out.println(p.email());
            }
        }
    }

    @Test
    void skip() {
        List<Person> list = Linq.fromIterable(PERSONS)
            .skip(4)
            .toList();

        assertEquals(list, List.of(
            new Person("Eve", 22, "San Francisco"),
            new Person("Frank", 40, "Seattle"),
            new Person("Grace", 27, "Boston"),
            new Person("Hank", 33, "Denver"),
            new Person("Ivy", 26, "Austin"),
            new Person("Jack", 31, "Miami"),

            new Person("LiHua", 24, "Beijing"),
            new Person("ZhangWei", 29, "Shanghai"),
            new Person("WangFang", 32, "Guangzhou"),
            new Person("LiuYang", 21, "Shenzhen"),
            new Person("ChenJie", 36, "Hangzhou"),
            new Person("YangLei", 38, "Nanjing"),
            new Person("ZhaoMin", 25, "Wuhan"),
            new Person("SunTao", 34, "Chengdu"),
            new Person("ZhouKai", 28, "Xi'an"),
            new Person("WuDi", 41, "Tianjin")
        ));
    }

    @Test
    void take() {
        List<Person> list = Linq.fromIterable(PERSONS)
            .take(4)
            .toList();

        assertEquals(list, List.of(
            new Person("Alice", 23, "New York"),
            new Person("Bob", 30, "Los Angeles"),
            new Person("Charlie", 28, "Chicago"),
            new Person("David", 35, "Houston")
        ));
    }

    @Test
    void where() {
        List<Person> list = Linq.fromIterable(PERSONS)
            .where(p -> p.age() > 30)
            .toList();

        assertEquals(list, List.of(
            new Person("David", 35, "Houston"),
            new Person("Frank", 40, "Seattle"),
            new Person("Hank", 33, "Denver"),
            new Person("Jack", 31, "Miami"),

            new Person("WangFang", 32, "Guangzhou"),
            new Person("ChenJie", 36, "Hangzhou"),
            new Person("YangLei", 38, "Nanjing"),
            new Person("SunTao", 34, "Chengdu"),
            new Person("WuDi", 41, "Tianjin")
        ));
    }

    @Test
    void takeWhile() {
        List<Person> list = Linq.fromIterable(PERSONS)
            .takeWhile(p -> p.age() < 30)
            .toList();

        assertEquals(list, List.of(
            new Person("Alice", 23, "New York"),
            new Person("Charlie", 28, "Chicago"),
            new Person("Eve", 22, "San Francisco"),
            new Person("Grace", 27, "Boston"),
            new Person("Ivy", 26, "Austin"),

            new Person("LiHua", 24, "Beijing"),
            new Person("ZhangWei", 29, "Shanghai"),
            new Person("LiuYang", 21, "Shenzhen"),
            new Person("ZhaoMin", 25, "Wuhan"),
            new Person("ZhouKai", 28, "Xi'an")
        ));
    }

    @Test
    void skipWhile() {
        List<Person> list = Linq.fromIterable(PERSONS)
            .skipWhile(p -> p.age() < 30)
            .toList();

        assertEquals(list, List.of(
            new Person("Bob", 30, "Los Angeles"),
            new Person("David", 35, "Houston"),
            new Person("Frank", 40, "Seattle"),
            new Person("Hank", 33, "Denver"),
            new Person("Jack", 31, "Miami"),

            new Person("WangFang", 32, "Guangzhou"),
            new Person("ChenJie", 36, "Hangzhou"),
            new Person("YangLei", 38, "Nanjing"),
            new Person("SunTao", 34, "Chengdu"),
            new Person("WuDi", 41, "Tianjin")
        ));
    }

    @Test
    void concat() {
        Enumerable<Person> adult = Linq.fromIterable(PERSONS)
            .where(p -> p.age >= 18);

        Enumerable<Person> notNetEmail = Linq.fromIterable(PERSONS)
            .skipWhile(p -> p.email().endsWith(EMAIL_SUFFIX[5]));

        adult.concat(notNetEmail)
            .forEach(System.out::println);

    }

    @Test
    void append() {
    }

    @Test
    void prepend() {
    }

    @Test
    void select() {
    }

    @Test
    void selectMany() {
    }

    @Test
    void groupBy() {
    }

    @Test
    void testGroupBy() {
    }

    @Test
    void testGroupBy1() {
    }

    @Test
    void testGroupBy2() {
    }

    @Test
    void groupResultBy() {
    }

    @Test
    void testGroupResultBy() {
    }

    @Test
    void testGroupResultBy1() {
    }

    @Test
    void testGroupResultBy2() {
    }

    @Test
    void orderBy() {
    }

    @Test
    void testOrderBy() {
    }

    @Test
    void orderByInt() {
    }

    @Test
    void orderByLong() {
    }

    @Test
    void orderByDouble() {
    }

    @Test
    void orderByDescending() {
    }

    @Test
    void testOrderByDescending() {
    }

    @Test
    void orderByIntDescending() {
    }

    @Test
    void orderByLongDescending() {
    }

    @Test
    void orderByDoubleDescending() {
    }

    @Test
    void mapToInt() {
        int[] array = Linq.fromIterable(PERSONS)
            .mapToInt(Person::age)
            .whereByInt(age -> age >= 30)
            .toIntArray();

        assertArrayEquals(new int[] {30, 35, 40, 33, 31, 32, 36, 38, 34, 41}, array);
    }

    @Test
    void mapToLong() {
        long[] array = Linq.fromIterable(PERSONS)
            .mapToLong(person -> person.age() * 10L)
            .take(3)
            .toLongArray();

        assertArrayEquals(new long[] {230L, 300L, 280L}, array);
    }

    @Test
    void mapToDouble() {
        double[] array = Linq.fromIterable(PERSONS)
            .mapToDouble(person -> person.age() / 10.0)
            .skip(2)
            .take(3)
            .toDoubleArray();

        assertArrayEquals(new double[] {2.8, 3.5, 2.2}, array);
    }

    @Test
    void union() {
    }

    @Test
    void testUnion() {
    }

    @Test
    void unionBy() {
    }

    @Test
    void testUnionBy() {
    }

    @Test
    void intersect() {
    }

    @Test
    void testIntersect() {
    }

    @Test
    void intersectBy() {
    }

    @Test
    void testIntersectBy() {
    }

    @Test
    void join() {
    }

    @Test
    void testJoin() {
    }

    @Test
    void leftJoin() {
    }

    @Test
    void testLeftJoin() {
    }

    @Test
    void rightJoin() {
    }

    @Test
    void testRightJoin() {
    }

    @Test
    void zip() {
    }

    @Test
    void testZip() {
    }

    @Test
    void testZip1() {
    }

    @Test
    void toList() {
    }

    @Test
    void chunk() {
    }

    @Test
    void chunkAsList() {
    }

    @Test
    void toArray() {
    }

    @Test
    void testToArray() {
    }

    @Test
    void castTo() {
    }

    @Test
    void any() {
    }

    @Test
    void testAny() {
    }

    @Test
    void all() {
    }

    @Test
    void count() {
    }

    @Test
    void countBy() {
    }

    @Test
    void distinct() {
    }

    @Test
    void contains() {
    }

    @Test
    void first() {
    }

    @Test
    void testFirst() {
    }

    @Test
    void firstOptional() {
    }

    @Test
    void testFirstOptional() {
    }

    @Test
    void single() {
    }

    @Test
    void testSingle() {
    }

    @Test
    void singleOptional() {
    }

    @Test
    void testSingleOptional() {
    }

    @Test
    void aggregate() {
    }

    @Test
    void min() {
    }

    @Test
    void minOptional() {
    }

    @Test
    void max() {
    }

    @Test
    void maxOptional() {
    }
}
