package io.github.piscescup;

import io.github.piscescup.linq4j2.Enumerable;
import io.github.piscescup.linq4j2.Enumerator;
import io.github.piscescup.linq4j2.Groupable;
import io.github.piscescup.linq4j2.Linq;
import io.github.piscescup.linq4j2.ReadOnlyGroup;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.function.Function;

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
    void enumeratorReset() {
        try (Enumerator<Integer> enumerator = Linq.of(1, 2, 3).enumerator()) {
            assertTrue(enumerator.hasNext());
            assertEquals(1, enumerator.next());
            enumerator.reset();

            assertEquals(1, enumerator.next());
            assertEquals(2, enumerator.next());
            assertEquals(3, enumerator.next());
            assertFalse(enumerator.hasNext());
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

        int[] intArray = Linq.ofInts(1, 2, 3, 4, 5, 6, 7, 8)
            .skip(5)
            .toIntArray();
        assertArrayEquals(new int[]{6, 7, 8}, intArray);

        long[] longArray = Linq.ofLongs(10L, 20L, 30L, 40L, 50L)
            .skip(3)
            .toLongArray();
        assertArrayEquals(new long[]{40L, 50L}, longArray);

        double[] doubleArray = Linq.ofDoubles(1.1, 2.2, 3.3, 4.4, 5.5)
            .skip(2)
            .toDoubleArray();
        assertArrayEquals(new double[]{3.3, 4.4, 5.5}, doubleArray);
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

        int[] intArray = Linq.ofInts(1, 2, 3, 4, 5, 6, 7, 8)
            .take(5)
            .toIntArray();
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, intArray);

        long[] longArray = Linq.ofLongs(10L, 20L, 30L, 40L, 50L)
            .take(3)
            .toLongArray();
        assertArrayEquals(new long[]{10L, 20L, 30L}, longArray);

        double[] doubleArray = Linq.ofDoubles(1.1, 2.2, 3.3, 4.4, 5.5)
            .take(4)
            .toDoubleArray();
        assertArrayEquals(new double[]{1.1, 2.2, 3.3, 4.4}, doubleArray);
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

        List<Integer> integerList = Linq.ofInts(1, 3, 5, 7, 8, 10)
            .takeWhileByInt(n -> n % 2 == 1)
            .toList();
        assertEquals(integerList, List.of(1, 3, 5, 7));

        List<Long> longList = Linq.ofLongs(10L, 20L, 30L, 40L, 50L)
            .takeWhileByLong(n -> n < 35L)
            .toList();
        assertEquals(longList, List.of(10L, 20L, 30L));

        List<Double> doubleList = Linq.ofDoubles(1.1, 2.2, 3.3, 4.4, 5.5)
            .takeWhileByDouble(n -> n < 4.0)
            .toList();
        assertEquals(doubleList, List.of(1.1, 2.2, 3.3));
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
        Enumerable<Person> people1 = Linq.fromIterable(PERSONS)
            .takeWhile(p -> p.age() < 30);
        Enumerable<Person> people2 = Linq.fromIterable(PERSONS)
            .skipWhile(p -> p.name.length() > 3);

        List<Person> people = people1.concat(people2)
            .toList();

        assertEquals(people, List.of(
            // takeWhile(p -> p.age() < 30)
            new Person("Alice", 23, "New York"),

            // skipWhile(p -> p.name.length() > 3)
            // new Person("Alice", 23, "New York"),
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
        ));
    }

    @Test
    void append() {
        List<Person> list = Linq.fromIterable(PERSONS)
            .append(new Person("ZhangSan", 27, "Shenyang"))
            .toList();

        assertEquals(list, List.of(
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
            new Person("WuDi", 41, "Tianjin"),
            new Person("ZhangSan", 27, "Shenyang")
        ));

        List<Integer> integerList = Linq.ofInts(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .append(11)
            .toList();
        assertEquals(integerList, List.of(
            1, 2 ,3, 4, 5, 6, 7, 8, 9, 10, 11
        ));

        List<Long> longList = Linq.ofLongs(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L)
            .append(11)
            .toList();
        assertEquals(longList, List.of(
            1L, 2L ,3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L
        ));

        List<Double> doubleList = Linq.ofDoubles(1.1, 2.2, 3.3, 4.4, 5.5)
            .append(6.6)
            .toList();
        assertEquals(doubleList, List.of(
            1.1, 2.2, 3.3, 4.4, 5.5,  6.6
        ));

    }

    @Test
    void prepend() {
        List<Person> list = Linq.fromIterable(PERSONS)
            .prepend(new Person("ZhangSan", 27, "Shenyang"))
            .toList();

        assertEquals(list, List.of(
            new Person("ZhangSan", 27, "Shenyang"),
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
        ));

        List<Integer> integerList = Linq.ofInts(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .prepend(11)
            .toList();
        assertEquals(integerList, List.of(
            11, 1, 2 ,3, 4, 5, 6, 7, 8, 9, 10
        ));

        List<Long> longList = Linq.ofLongs(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L)
            .prepend(11)
            .toList();
        assertEquals(longList, List.of(
            11L, 1L, 2L ,3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L
        ));

        List<Double> doubleList = Linq.ofDoubles(1.1, 2.2, 3.3, 4.4, 5.5)
            .prepend(6.6)
            .toList();
        assertEquals(doubleList, List.of(
            6.6, 1.1, 2.2, 3.3, 4.4, 5.5
        ));
    }

    @Test
    void select() {
        Linq.fromIterable(PERSONS)
            .select(Person::email)
            .forEach(System.out::println);

        List<Integer> integerList = Linq.ofInts(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .select(n -> n * n)
            .toList();
        assertEquals(integerList, List.of(
            1, 4, 9, 16, 25, 36, 49, 64, 81, 100
        ));

        List<Long> longList = Linq.ofLongs(1L, 2L, 3L)
            .select(n -> n * n)
            .toList();
        assertEquals(longList, List.of(
            1L, 4L, 9L
        ));

    }

    @Test
    void selectMany() {
    }

    @Test
    void groupBy() {
        Linq.fromIterable(PERSONS)
            .groupBy(p -> p.name.charAt(0))
            .forEach(System.out::println);
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
        Linq.fromIterable(PERSONS)
            .append(new Person("Tom", 29, "New York"))
            .append(new Person("Jerry211", 31, "Los Angeles"))
            .append(new Person("Jerry211", 32, "Chicago"))
            .groupResultBy(
                Person::address, // group by city
                Person::name, // element selector: name
                (city, names) -> city + ": " + names.toList() // result selector
            )
            .forEach(System.out::println);
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
        long t3 = System.nanoTime();
        List<Person> list1 = io.github.piscescup.linq.Linq.fromIterable(PERSONS)
            .orderBy(Person::age)
            .thenBy(Person::name)
            .thenDescendingBy(Person::address)
            .toList();
        long t4 = System.nanoTime();


        long t1 = System.nanoTime();
        List<Person> list = Linq.fromIterable(PERSONS)
            .orderBy(Person::age)
            .thenOrderBy(Person::name)
            .thenOrderByDescending(Person::address)
            .toList();
        long t2 = System.nanoTime();

        long st1  = System.nanoTime();
        List<Person> list2 = PERSONS.stream()
            .sorted(Comparator.comparing(Person::age)
                .thenComparing(Person::name)
                .thenComparing(Comparator.comparing(Person::email).reversed())
            )
            .toList();
        long st2 = System.nanoTime();

        System.out.println("Linq2 :" + (t2 - t1) / 1000  + " us" );
        System.out.println("Linq  :" + (t4 - t3) / 1000 + " us" );
        System.out.println("Stream:" + (st2 - st1) / 1000 + " us");

        long t5 = System.nanoTime();
        io.github.piscescup.linq.Linq.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
            .select(x -> x * x)
            .toList();
        long t6 = System.nanoTime();
        long t7 = System.nanoTime();
        Linq.ofInts(1, 2, 3, 4, 5, 6, 7, 8, 9)
            .select(x -> x * x)
            .toList();
        long t8 = System.nanoTime();

        System.out.println("Int Linq : " + (t6 - t5) / 1000 + " us");
        System.out.println("Int Linq2: " + (t8 - t7) / 1000 + " us");
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
        class Animal {
            private String type;
            private int age;


            public Animal(String type, int age) {
                this.type = type;
                this.age = age;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public int getAge() {
                return age;
            }

            public void setAge(int age) {
                this.age = age;
            }

            public String toString() {
                return "Animal{species = " + type + ", age = " + age + "}";
            }
        }

        class Dog extends Animal {
            private String name;
            public Dog() {
                super("哺乳动物", 0);
            }

            public Dog(String name) {
                this(0, name);
            }

            public Dog(int age, String name) {
                super("哺乳动物", age);
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String toString() {
                return "Dog{name = " + name + "}";
            }
        }

        class Cat extends Animal {
            private String name;
            public Cat() {
                super("哺乳动物", 0);
            }

            public Cat(int age, String name) {
                super("哺乳动物", age);
                this.name = name;
            }

            public Cat(String name) {
                this(0, name);
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String toString() {
                return "Cat{name = " + name + "}";
            }
        }

        Animal dog1 = new Dog("Da Huang");
        Animal dog2 = new Dog("Wang Cai");
        Animal dog3 = new Dog("Wang Huang");

        Animal cat1 = new Cat("Bu Ding");
        Animal cat2 = new Cat("Nai Cha");
        Animal cat3 = new Cat("Nai Ding");

        Linq.of(dog1, dog2, dog3)
            .castTo(Dog.class)
            .select(Dog::getClass)
            .forEach(System.out::println);
        assertThrows(ClassCastException.class, () -> Linq.of(dog1, dog2, dog3)
            .castTo(Cat.class)
            .forEach(System.out::println));

        Linq.of(cat1, cat2, cat3)
            .castTo(Cat.class)
            .select(Cat::getClass)
            .forEach(System.out::println);
        assertThrows(ClassCastException.class, () -> Linq.of(cat1, cat2, cat3)
            .castTo(Dog.class)
            .forEach(System.out::println));
    }

    @Test
    void any() {
        boolean any = Linq.fromIterable(PERSONS)
            .any();
        assertTrue(any);

        assertFalse(Linq.fromIterable(PERSONS)
            .any(p -> p.age() > 100));
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
        int sum = Linq.of(1, 2, 3, 4)
            .aggregate(0, Integer::sum);
        assertEquals(10, sum);

        String joined = Linq.of("a", "bb", "ccc")
            .aggregate(
                new StringBuilder(),
                StringBuilder::append,
                StringBuilder::toString
            );
        assertEquals("abbccc", joined);

        int product = Linq.of(2, 3, 4)
            .aggregate((left, right) -> left * right);
        assertEquals(24, product);

        assertThrows(NoSuchElementException.class, () ->
            Linq.<Integer>of().aggregate(Integer::sum)
        );

        List<Groupable<Character, Integer>> totalsByInitial = Linq.fromIterable(PERSONS)
            .aggregateBy(
                person -> person.name().charAt(0),
                0,
                (sumByInitial, person) -> sumByInitial + person.age()
            )
            .toList();
        assertEquals(List.of(
            new ReadOnlyGroup<>('A', List.of(23)),
            new ReadOnlyGroup<>('B', List.of(30)),
            new ReadOnlyGroup<>('C', List.of(64)),
            new ReadOnlyGroup<>('D', List.of(35)),
            new ReadOnlyGroup<>('E', List.of(22)),
            new ReadOnlyGroup<>('F', List.of(40)),
            new ReadOnlyGroup<>('G', List.of(27)),
            new ReadOnlyGroup<>('H', List.of(33)),
            new ReadOnlyGroup<>('I', List.of(26)),
            new ReadOnlyGroup<>('J', List.of(31)),
            new ReadOnlyGroup<>('L', List.of(45)),
            new ReadOnlyGroup<>('Z', List.of(82)),
            new ReadOnlyGroup<>('W', List.of(73)),
            new ReadOnlyGroup<>('Y', List.of(38)),
            new ReadOnlyGroup<>('S', List.of(34))
        ), totalsByInitial);

        Function<Integer, String> seedByAgeGroup = ageGroup -> "group-" + ageGroup + ":";
        List<Groupable<Integer, String>> namesByAgeGroup = Linq.fromIterable(PERSONS)
            .<Integer, String>aggregateBy(
                person -> person.age() / 10,
                seedByAgeGroup,
                (accumulator, person) -> accumulator + person.name() + "|"
            )
            .toList();
        assertEquals(List.of(
            new ReadOnlyGroup<>(2, List.of("group-2:Alice|Charlie|Eve|Grace|Ivy|LiHua|ZhangWei|LiuYang|ZhaoMin|ZhouKai|")),
            new ReadOnlyGroup<>(3, List.of("group-3:Bob|David|Hank|Jack|WangFang|ChenJie|YangLei|SunTao|")),
            new ReadOnlyGroup<>(4, List.of("group-4:Frank|WuDi|"))
        ), namesByAgeGroup);
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
