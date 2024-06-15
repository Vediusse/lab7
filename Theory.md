# Многопоточность в Java

## Глава 1: Введение в многопоточность

Многопоточность — это способность программы выполнять несколько потоков (thread) одновременно. В Java многопоточность достигается с помощью классов `Thread` и `Runnable`.

### Пример создания потока

```java
public class MyThread extends Thread {
    public void run() {
        System.out.println("MyThread is running.");
    }

    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        myThread.start();
    }
}
```

## Глава 2: Синхронизация

### Что такое синхронизация?

Синхронизация — это механизм, который предотвращает одновременный доступ нескольких потоков к критическим участкам кода или данным. Это необходимо для предотвращения гонок данных (data races) и обеспечения целостности данных.

### Виды синхронизации

1. **Синхронизированные методы**
2. **Синхронизированные блоки**
3. **Блокировка (Lock)**
4. **ReentrantLock**
5. **Синхронизированные коллекции**
6. **Классы `java.util.concurrent`**

### Синхронизированные методы

Синхронизированные методы блокируют доступ других потоков к методу, пока один поток его выполняет.

```java
public class Counter {
    private int count = 0;

    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}
```

### Синхронизированные блоки

Синхронизированные блоки позволяют синхронизировать не весь метод, а только его часть.

```java
public class Counter {
    private int count = 0;

    public void increment() {
        synchronized(this) {
            count++;
        }
    }

    public int getCount() {
        synchronized(this) {
            return count;
        }
    }
}
```

### Блокировка (Lock)

Интерфейс `Lock` из `java.util.concurrent.locks` предоставляет более гибкий механизм для синхронизации.

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private int count = 0;
    private final Lock lock = new ReentrantLock();

    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}
```

### ReentrantLock

`ReentrantLock` — это реализация интерфейса `Lock`, предоставляющая возможность повторного входа (reentrancy).

```java
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantCounter {
    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock();

    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}
```

### Синхронизированные коллекции

Коллекции из пакета `java.util.concurrent` предоставляют безопасные в многопоточной среде реализации структур данных.

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentCollectionExample {
    private ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();

    public void add(String key, Integer value) {
        map.put(key, value);
    }

    public Integer get(String key) {
        return map.get(key);
    }
}
```

### Классы `java.util.concurrent`

Пакет `java.util.concurrent` содержит множество полезных классов для работы с многопоточностью, таких как `Executors`, `CountDownLatch`, `CyclicBarrier`, и другие.

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                System.out.println("Thread: " + Thread.currentThread().getName());
            });
        }

        executor.shutdown();
    }
}
```

## Заключение

Синхронизация играет ключевую роль в разработке многопоточных приложений. Использование различных механизмов синхронизации позволяет защитить данные от некорректного доступа и избежать ошибок, связанных с параллельным выполнением кода.




# Многопоточность в Java



## Глава 2: Проблемы многопоточности

### 1. Гонки данных (Race Conditions)

Гонки данных возникают, когда два или более потоков одновременно пытаются изменить общие данные, и результат зависит от порядка выполнения потоков.

#### Пример гонок данных

```java
public class RaceConditionExample {
    private int count = 0;

    public void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public static void main(String[] args) {
        RaceConditionExample example = new RaceConditionExample();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                example.increment();
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Count: " + example.getCount()); // Ожидаем 2000, но может быть любое значение
    }
}
```

### 2. Взаимная блокировка (Deadlock)

Взаимная блокировка происходит, когда два или более потоков ожидают освобождения ресурсов, удерживаемых друг другом, и ни один из них не может продолжить выполнение.

#### Пример взаимной блокировки

```java
public class DeadlockExample {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void method1() {
        synchronized (lock1) {
            System.out.println("Thread 1: Holding lock 1...");
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            synchronized (lock2) {
                System.out.println("Thread 1: Holding lock 1 & 2...");
            }
        }
    }

    public void method2() {
        synchronized (lock2) {
            System.out.println("Thread 2: Holding lock 2...");
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            synchronized (lock1) {
                System.out.println("Thread 2: Holding lock 1 & 2...");
            }
        }
    }

    public static void main(String[] args) {
        DeadlockExample example = new DeadlockExample();

        Thread thread1 = new Thread(example::method1);
        Thread thread2 = new Thread(example::method2);

        thread1.start();
        thread2.start();
    }
}
```

### 3. Livelock

Livelock происходит, когда потоки постоянно меняют свое состояние в ответ на действия друг друга, но ни один из них не может продолжить выполнение.

#### Пример livelock

```java
public class LivelockExample {
    static class Spoon {
        private Diner owner;

        public Spoon(Diner owner) {
            this.owner = owner;
        }

        public synchronized void use() {
            System.out.println(owner.name + " is using the spoon.");
        }

        public Diner getOwner() {
            return owner;
        }

        public void setOwner(Diner owner) {
            this.owner = owner;
        }
    }

    static class Diner {
        private String name;
        private boolean isHungry;

        public Diner(String name) {
            this.name = name;
            this.isHungry = true;
        }

        public void eatWith(Spoon spoon, Diner spouse) {
            while (isHungry) {
                if (spoon.getOwner() != this) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                if (spouse.isHungry) {
                    System.out.println(name + ": You eat first my dear " + spouse.name);
                    spoon.setOwner(spouse);
                    continue;
                }

                spoon.use();
                isHungry = false;
                System.out.println(name + ": I am done eating.");
                spoon.setOwner(spouse);
            }
        }
    }

    public static void main(String[] args) {
        Diner husband = new Diner("Husband");
        Diner wife = new Diner("Wife");
        Spoon spoon = new Spoon(husband);

        new Thread(() -> husband.eatWith(spoon, wife)).start();
        new Thread(() -> wife.eatWith(spoon, husband)).start();
    }
}
```

### 4. Starvation (Голодание)

Голодание возникает, когда поток не может получить доступ к необходимым ресурсам из-за постоянного доступа других потоков к этим ресурсам.

#### Пример голодания

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StarvationExample {
    private final Lock lock = new ReentrantLock(true); // Используем справедливую блокировку

    public void performTask() {
        try {
            lock.lock();
            System.out.println(Thread.currentThread().getName() + " acquired the lock.");
            Thread.sleep(2000); // Имитация длительной операции
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println(Thread.currentThread().getName() + " released the lock.");
        }
    }

    public static void main(String[] args) {
        StarvationExample example = new StarvationExample();

        Runnable task = example::performTask;

        for (int i = 0; i < 5; i++) {
            new Thread(task, "Thread-" + i).start();
        }
    }
}
```
### 5. Нарушение целостности данных

Возникает с коллекциями или бд. =)


# Глава 1: Атомарные операции и volatile в Java

## Введение

В многопоточном программировании одной из ключевых задач является обеспечение корректного доступа к данным, разделяемым между несколькими потоками. В этой главе мы рассмотрим две важные концепции для решения этой задачи: атомарные операции и ключевое слово `volatile`.

## Атомарные операции

### Что такое атомарные операции?

Атомарная операция — это операция, которая выполняется за один шаг с точки зрения других потоков. Это означает, что атомарная операция либо полностью выполнена, либо полностью не выполнена, без промежуточных состояний. В Java такие операции необходимы для предотвращения состояний гонки (race conditions), когда несколько потоков одновременно пытаются читать и записывать одно и то же значение.

### Примеры атомарных операций

В Java класс `java.util.concurrent.atomic` предоставляет набор атомарных классов, таких как `AtomicInteger`, `AtomicLong`, `AtomicBoolean`, и `AtomicReference`. Эти классы позволяют выполнять атомарные операции над примитивами и объектами.

#### Пример использования AtomicInteger

```java
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicExample {
    private AtomicInteger count = new AtomicInteger(0);

    public void increment() {
        count.incrementAndGet(); // Атомарное увеличение на 1
    }

    public int getCount() {
        return count.get(); // Атомарное чтение значения
    }

    public static void main(String[] args) {
        AtomicExample example = new AtomicExample();
        example.increment();
        System.out.println("Count: " + example.getCount()); // Output: Count: 1
    }
}
```

### Преимущества и недостатки атомарных операций

**Преимущества:**
- Простота использования и понимания.
- Высокая производительность в некоторых сценариях по сравнению с блокировками (synchronized).

**Недостатки:**
- Ограниченность в функциональности, так как они работают только с отдельными значениями.
- Не могут заменить все случаи использования блокировок.

## Ключевое слово `volatile`

### Что такое `volatile`?

Ключевое слово `volatile` в Java используется для переменных, которые могут быть изменены разными потоками. Оно гарантирует, что изменения в переменной сразу же становятся видимыми всем потокам. Это предотвращает кэширование переменной потоком и заставляет потоки всегда читать ее из основной памяти.

### Пример использования `volatile`

```java
public class VolatileExample {
    private volatile boolean running = true;

    public void stop() {
        running = false;
    }

    public void run() {
        while (running) {
            // Выполнение кода
        }
    }

    public static void main(String[] args) throws InterruptedException {
        VolatileExample example = new VolatileExample();

        Thread runner = new Thread(example::run);
        runner.start();

        Thread.sleep(1000); // Позволим потоку немного поработать

        example.stop(); // Остановка потока
        runner.join();  // Ожидание завершения потока
    }
}
```

### Преимущества и недостатки `volatile`

**Преимущества:**
- Обеспечивает видимость изменений между потоками.
- Простота использования по сравнению с блокировками.

**Недостатки:**
- Не гарантирует атомарность операций.
- Работает только для переменных, которые могут быть корректно изменены атомарными операциями.

## Различия и назначение

### Различия между атомарными операциями и `volatile`

- **Атомарные операции** гарантируют, что операция полностью завершена до начала следующей, обеспечивая целостность данных при изменении.
- **`volatile` переменные** гарантируют, что изменения переменной сразу видны другим потокам, но не гарантируют атомарность этих изменений.

### Назначение

- Используйте **атомарные операции**, когда нужно выполнять несколько операций, которые должны быть атомарными (например, инкремент, декремент, сравнение и замена).
- Используйте **`volatile` переменные**, когда нужно просто обеспечить видимость изменений переменной между потоками и операции над этой переменной являются атомарными сами по себе (например, присвоение нового значения).
- Есть long double они 64-битные типы данных, но вот есть операционные системы с 32-битной работой, в результате в таких ос работы с данными не будет атомарной поэтмоу надо задучаться

## Введение

В мире многопоточности в Java часто требуется создавать и управлять потоками для выполнения параллельных задач. В этом конспекте мы рассмотрим основные виды пулов потоков, предоставляемых Java, и объясним их работу на бытовых примерах. Это поможет вам понять, как и когда использовать каждый из этих пулов для повышения эффективности и производительности ваших приложений.

---

### 1. `newFixedThreadPool`

`newFixedThreadPool` создает пул потоков с фиксированным числом потоков. Если все потоки заняты, новые задачи будут ожидать, пока один из потоков не освободится.

#### Бытовой пример:
Представьте, что вы управляете рестораном с четырьмя официантами. У каждого официанта только один стол, за которым он может обслуживать посетителей. Если все официанты заняты, новые посетители должны подождать, пока один из столов не освободится.

#### Пример кода:
```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FixedThreadPoolExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 10; i++) {
            final int taskNumber = i;
            executor.submit(() -> {
                System.out.println("Task " + taskNumber + " is being processed by " + Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
    }
}
```

---

### 2. `newCachedThreadPool`

`newCachedThreadPool` создает пул потоков, который создает новые потоки по мере необходимости, но переиспользует ранее созданные потоки, когда они становятся доступными.

#### Бытовой пример:
Представьте, что у вас есть колл-центр с неограниченным числом операторов, которые могут выйти на линию, когда поступает звонок. Если звонков мало, операторы отдыхают, но как только звонки учащаются, в дело вступает больше операторов.

#### Пример кода:
```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPoolExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {
            final int taskNumber = i;
            executor.submit(() -> {
                System.out.println("Task " + taskNumber + " is being processed by " + Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
    }
}
```

---

### 3. `newSingleThreadExecutor`

`newSingleThreadExecutor` создает пул потоков с единственным потоком, который выполняет задачи последовательно.

#### Бытовой пример:
Представьте, что у вас есть единственный почтальон, который разносит письма. Он выполняет свою работу последовательно, переходя от одного дома к другому.

#### Пример кода:
```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadExecutorExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (int i = 0; i < 10; i++) {
            final int taskNumber = i;
            executor.submit(() -> {
                System.out.println("Task " + taskNumber + " is being processed by " + Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
    }
}
```

---

### 4. `newScheduledThreadPool`

`newScheduledThreadPool` создает пул потоков, который может выполнять задачи по расписанию или с фиксированной задержкой.

#### Бытовой пример:
Представьте, что у вас есть команда садовников, которые должны поливать растения каждые 6 часов. У каждого садовника своя зона, и они начинают работать одновременно через равные интервалы времени.

#### Пример кода:
```java
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExample {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

        for (int i = 0; i < 4; i++) {
            final int taskNumber = i;
            scheduler.scheduleAtFixedRate(() -> {
                System.out.println("Task " + taskNumber + " is being processed by " + Thread.currentThread().getName());
            }, 0, 6, TimeUnit.HOURS);
        }
    }
}
```

---

### 5. `newWorkStealingPool`

`newWorkStealingPool` создает пул потоков, который использует алгоритм "воровства работы". Потоки могут "воровать" задачи у других потоков для более равномерного распределения нагрузки.

#### Бытовой пример:
Представьте, что у вас есть несколько независимых подрядчиков, каждый из которых занимается своим проектом. Если у кого-то из них заканчивается работа, он может помочь другому подрядчику с его задачей.

#### Пример кода:
```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkStealingPoolExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newWorkStealingPool();

        for (int i = 0; i < 10; i++) {
            final int taskNumber = i;
            executor.submit(() -> {
                System.out.println("Task " + taskNumber + " is being processed by " + Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
    }
}
```

---

### 6. `ForkJoinPool`

`ForkJoinPool` используется для выполнения задач, которые могут быть рекурсивно разделены на подзадачи. Это основной пул потоков для параллельных операций в рамках `ForkJoin` парадигмы.

#### Бытовой пример:
Представьте, что у вас есть большая работа, например, уборка парка. Вы делите парк на несколько зон и каждому работнику даете свою зону. Если зона слишком большая для одного работника, он делит её на еще более мелкие зоны и распределяет их между другими работниками.

#### Пример кода:
```java
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinPoolExample {
    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        RecursiveTask<Long> task = new SumTask(0, 1000);
        long result = forkJoinPool.invoke(task);

        System.out.println("Sum: " + result);
    }
}

class SumTask extends RecursiveTask<Long> {
    private final int start;
    private final int end;
    private static final int THRESHOLD = 100;

    public SumTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += i;
            }
            return sum;
        } else {
            int mid = (start + end) / 2;
            SumTask leftTask = new SumTask(start, mid);
            SumTask rightTask = new SumTask(mid, end);
            leftTask.fork();
            return rightTask.compute() + leftTask.join();
        }
    }
}
```

---

Этот конспект охватывает основные виды пулов потоков в Java и предоставляет примеры их использования с понятными аналогиями из жизни. Используя эти знания, вы сможете выбрать подходящий пул потоков для ваших задач и эффективно управлять многопоточными операциями в ваших приложениях.

