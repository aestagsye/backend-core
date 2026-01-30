
#
# Сравнение: new внутри vs DI через конструктор
                            



# BAD: new InMemoryLeadRepository() внутри класса


```java
public class LeadService {
    // Тесная связанность!
    private final LeadRepository repository = 
    new InMemoryLeadRepository();
                                
}
```
Проблемы:

1. Невозможно подставить mock в тестах
2. Невозможно заменить на PostgreSQL без изменения кода
3. Скрытая зависимость — не видно что нужно для работы

# GOOD: DI через конструктор
```java
public class LeadService {
    private final LeadRepository repository;

    public LeadService(LeadRepository repository) {
        this.repository = repository;
    }
}
```
Преимущества:

1.  В тестах передаём mock(LeadRepository.class)
2. В production передаём InMemoryLeadRepository
3. В будущем передаём JpaLeadRepository (Sprint 7)
Зависимость явная — видно в конструкторе