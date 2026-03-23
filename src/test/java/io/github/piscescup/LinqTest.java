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
