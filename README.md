[![Java CI with Spotless](https://github.com/aestagsye/backend-core/actions/workflows/main.yml/badge.svg)](https://github.com/aestagsye/backend-core/actions/workflows/main.yml)
# InviteeController.java (problematic version) 
```java
package ru.mentee.power.crm.spring.rest.problematic;

import org.springframework.web.bind.annotation.RestController;

import ru.mentee.power.crm.spring.repository.InviteeRepository;

/**
 * ЗАДАНИЕ: Найдите все проблемы в этом контроллере используя чек-лист. Ожидается найти минимум 10
 * проблем из разных категорий.
 */
@RestController
public class InviteeController {
   @Autowired
   InviteeRepository repository;

   // TODO: Студент должен найти проблемы в этом методе
   @PostMapping("/getInvitees")
   public List<Invitee> getInvitees() {
     return repository.findAll();
   }

   // TODO: Студент должен найти проблемы в этом методе
   @GetMapping("/invitees/{id}")
   public Invitee getById(@PathVariable UUID id) {
     return repository.findById(id).orElse(null);
   }

   // TODO: Студент должен найти проблемы в этом методе
   @PostMapping("/invitees")
   public Invitee create(@RequestBody Map<String, Object> params) {
     String email = (String) params.get("email");
     String firstName = (String) params.get("firstName");

     // Проверка email через SQL
     String sql = "SELECT COUNT(*) FROM invitees WHERE email = '" + email + "'";
     // repository.executeNativeQuery(sql); // Представим что это выполняется

     Invitee invitee = new Invitee();
     invitee.setId(UUID.randomUUID());
     invitee.setEmail(email);
     invitee.setFirstName(firstName);
     invitee.setCreatedAt(Instant.now());

     return repository.save(invitee);
   }

   // TODO: Студент должен найти проблемы в этом методе
   @DeleteMapping("/invitees/{id}")
   public Invitee delete(@PathVariable UUID id) {
     Invitee invitee = repository.findById(id).orElse(null);
     if (invitee != null) {
       repository.delete(invitee);
     }
     return invitee;
   }

   // TODO: Студент должен найти проблемы в этом методе
   @PutMapping("/invitees/{id}/status")
   public Invitee updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
     try {
       Invitee invitee = repository.findById(id).orElseThrow();
       String status = body.get("status");

       // Бизнес-логика в контроллере
       if (status.equals("ACTIVE") || status.equals("INACTIVE")) {
         invitee.setStatus(status);
       } else {
         throw new RuntimeException("Invalid status");
       }

       return repository.save(invitee);
     } catch (Exception e) {
       // Пустой catch
       return null;
     }
   }
}
```

# InviteeController.java (fixed version)
```java
package ru.mentee.power.crm.spring.rest.fixed;

import org.springframework.web.bind.annotation.RestController;

import ru.mentee.power.crm.spring.repository.InviteeRepository;

/**
 * ЗАДАНИЕ: Найдите все проблемы в этом контроллере используя чек-лист. Ожидается найти минимум 10
 * проблем из разных категорий.
 */
@RestController
public class InviteeControllerFixed {
   @Autowired
   InviteeRepository repository;

   // DONE: Студент должен найти проблемы в этом методе - был неправильный naming: глагол в URL
   // отсутствие пагинации, неправильный HTTP метод, возвращался не ResponseEntity -> exposure внутренних данных
   // ФИКС
   @GetMapping("/invitees")
   public ResponseEntity<Page<InviteeResponse>> getInvitees(Pageable pageable) {
     Page<InviteeResponse> result = service.findAll(pageable);
     return ResponseEntity.ok(result);
   }

   // ОРИГИНАЛ
   @PostMapping("/getInvitees")
   public List<Invitee> getInvitees() {
     return repository.findAll();
   }

   // DONE: Студент должен найти проблемы в этом методе - отсутствие ResponseEntity, из-за чего могла быть
   // утечка данных
   // ФИКС
   @GetMapping("/invitees/{id}")
   public ResponseEntity<InviteeResponse> getById(@PathVariable UUID id) {
     return ResponseEntity.ok().body(service.findById(id));
   }

   // ОРИГИНАЛ
   @GetMapping("/invitees/{id}")
   public Invitee getById(@PathVariable UUID id) {
     return repository.findById(id).orElse(null);
   }

   // DONE: Студент должен найти проблемы в этом методе
   // Бизнес-логика в контроллере нарушает принцип SOLID - Single Responsibility Principle
   // Неправильный статус-код, должен возвращать 201, также должен возвращать ResponseEntity

   // ФИКС
   @PostMapping("/invitees")
   public ResponseEntity<InviteeResponse> create(@Valid @RequestBody CreateInviteeRequest request) {
     InviteeResponse created = service.save(request);
     URI location = URI.create("/api/invitees/" + created.id());
     return ResponseEntity.created(location).body(created);
   }

   // ОРИГИНАЛ
   @PostMapping("/invitees")
   public Invitee create(@RequestBody Map<String, Object> params) {
     String email = (String) params.get("email");
     String firstName = (String) params.get("firstName");

     // Проверка email через SQL
     String sql = "SELECT COUNT(*) FROM invitees WHERE email = '" + email + "'";
     // repository.executeNativeQuery(sql); // Представим что это выполняется

     Invitee invitee = new Invitee();
     invitee.setId(UUID.randomUUID());
     invitee.setEmail(email);
     invitee.setFirstName(firstName);
     invitee.setCreatedAt(Instant.now());

     return repository.save(invitee);
   }

   // DONE: Студент должен найти проблемы в этом методе
   // Бизнес-логика в контроллере, неправильный статус-код, отсутствие ResponseEntity

   // ФИКС
   @DeleteMapping("/invitees/{id}")
   public ResponseEntity<Void> delete(@PathVariable UUID id) {
     service.delete(id);
     return ResponseEntity.noContent().build();
   }

   // ОРИГИНАЛ
   @DeleteMapping("/invitees/{id}")
   public Invitee delete(@PathVariable UUID id) {
     Invitee invitee = repository.findById(id).orElse(null);
     if (invitee != null) {
       repository.delete(invitee);
     }
     return invitee;
   }

   // DONE: Студент должен найти проблемы в этом методе

   // ФИКС
   @PutMapping("/invitees/{id}/status")
   public ResponseEntity<InviteeResponse> updateStatus(
       @PathVariable UUID id, @Valid @RequestBody UpdateInviteeStatusRequest request) {
     InviteeResponse updated = service.updateStatus(id, request);
     return ResponseEntity.ok().body(updated);
   }

   // ОРИГИНАЛ
   @PutMapping("/invitees/{id}/status")
   public Invitee updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
     try {
       Invitee invitee = repository.findById(id).orElseThrow();
       String status = body.get("status");

       // Бизнес-логика в контроллере
       if (status.equals("ACTIVE") || status.equals("INACTIVE")) {
         invitee.setStatus(status);
       } else {
         throw new RuntimeException("Invalid status");
       }

       return repository.save(invitee);
     } catch (Exception e) {
       // Пустой catch
       return null;
     }
   }
}
```


## Issue #1: Отсутствие пагинации

**Категория:** API Design
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 26, метод getInvitees

**Что плохо:**
```java
  @PostMapping("/getInvitees")
  public List<Invitee> getInvitees() {
    return repository.findAll();
  }
```

**Почему плохо:**
Клиентский API обычно ожидает часть данных, а неожиданно для себя получает все. При большом количестве возвращаемых записей получаем также большую нагрузку на инфраструктуру.

**Как исправить:**
```java
  @PostMapping("/getInvitees")
  public Page<Invitee> getInvitees(Pageable pageable) {
    return repository.findAll(pageable);
  }
```

---
## Issue #2: Неправильные HTTP методы

**Категория:** API Design
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 24, метод getInvitees

**Что плохо:**
```java
  @PostMapping("/getInvitees")
  public List<Invitee> getInvitees() {
    return repository.findAll();
  }
```

**Почему плохо:**
Нарушение семантического соответствия HTTP-методов (RFC 7231). Может привести к непреднамеренным действиям, неожиданным результатам выполнения запроса. Также вводит в заблуждение разработчиков API.

**Как исправить:**
использовать GET-метод https://www.rfc-editor.org/rfc/rfc7231#section-4.3.1
```java
  @GetMapping("/getInvitees")
  public List<Invitee> getInvitees() {
    return repository.findAll();
  }
```
---
## Issue #3: Entity вместо DTO в response

**Категория:** API Design
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 25, метод getInvitees

**Что плохо:**
```java
  @PostMapping("/getInvitees")
  public List<Invitee> getInvitees() {
    return repository.findAll();
  }
```

**Почему плохо:**
Возможна утечка внутренних полей, что нарушает безопасность. Также возможны проблемы при наличии у сущности связей один-ко-многим (необходимость подгрузки и сериализации связанной сущности, увеличение размера json-ответа, N+1 и т.п.)

**Как исправить:**
```java
  @PostMapping("/getInvitees")
  public List<InviteeResponse> getInvitees() {
    return repository.findAll().map(InviteeMapper::toResponse);
  }
```

---

## Issue #4: Entity вместо DTO в response

**Категория:** API Design
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 31, метод getById

**Что плохо:**
```java
  @GetMapping("/invitees/{id}")
public Invitee getById(@PathVariable UUID id) {
  return repository.findById(id).orElse(null);
}
```

**Почему плохо:**
Возможна утечка внутренних полей, что нарушает безопасность. Также возможны проблемы при наличии у сущности связей один-ко-многим (необходимость подгрузки и сериализации связанной сущности, увеличение размера json-ответа, N+1 и т.п.)

**Как исправить:**
```java
  @GetMapping("/invitees/{id}")
public InviteeResponse getById(@PathVariable UUID id) {
  return repository.findById(id).map(InviteeMapper::toResponse).orElse(null);
}
```
---

## Issue #5: Entity вместо DTO в response

**Категория:** API Design
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 37, метод create

**Что плохо:**
```java
 @PostMapping("/invitees")
public Invitee create(@RequestBody Map<String, Object> params) {
  
  ...

  return repository.save(invitee);
}
```

**Почему плохо:**
Возможна утечка внутренних полей, что нарушает безопасность. Также возможны проблемы при наличии у сущности связей один-ко-многим (необходимость подгрузки и сериализации связанной сущности, увеличение размера json-ответа, N+1 и т.п.)

**Как исправить:**
```java
 @PostMapping("/invitees")
public InviteeResponse create(@RequestBody Map<String, Object> params) {
  
  ...

  return repository.save(invitee).map(InviteeMapper::toResponse);
}
```
---

## Issue #6: Плохой naming: глаголы в URL

**Категория:** API Design
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 24, метод getInvitees

**Что плохо:**
```java
  @PostMapping("/getInvitees")
```

**Почему плохо:**
Нарушение REST (единообразие интерфейсов) - взаимодействие с ресурсами должно быть стандартизировано через HTTP методы, а не через имена действий в URL.

**Как исправить:**
```java
  @PostMapping("/invitees")
```

---

## Issue #7: Неправильные статус коды

**Категория:** API Design
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 51, метод create

**Что плохо:**
```java
  public Invitee create(@RequestBody Map<String, Object> params) {

  ...

  return repository.save(invitee);
}
```

**Почему плохо:**
Статус 200 при успешном или не успешном создании сущности, нарушается семантика HTTP (RFC 7231)

**Как исправить:**
Вернуть 201 Created
```java
  @PostMapping("/invitees")
public ResponseEntity<InviteeResponse> create(@Valid @RequestBody CreateInviteeRequest request) {
  InviteeResponse created = service.save(request);
  URI location = URI.create("/api/invitees/" + created.id());
  return ResponseEntity.created(location).body(created);
}
```
---

## Issue #8: Неправильные статус коды

**Категория:** Error Handling
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 32, метод getById

**Что плохо:**
```java
  public Invitee getById(@PathVariable UUID id) {
  return repository.findById(id).orElse(null);
}
```

**Почему плохо:**
HTTP статус код должен соответствовать категории ошибки согласно RFC 7231. Нарушение этого правила усложняет клиенту обработку случая.

**Как исправить:**
вернуть 200 OK
```java
  @GetMapping("/invitees/{id}")
public ResponseEntity<Invitee> getById(@PathVariable UUID id) {
  return ResponseEntity.ok().body(service.findById(id));
}

```
---

## Issue #9: Нет валидации входных данных

**Категория:** Security
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 37, метод create

**Что плохо:**
```java
 @PostMapping("/invitees")
public Invitee create(@RequestBody Map<String, Object> params) {

```

**Почему плохо:**
Нет валидации и типизации, может быть передано что угодно. Непредсказуемое поведение.

**Как исправить:**
использовать DTO
```java
public Invitee create(@Valid @RequestBody CreateInviteeRequest request) {
```
---

## Issue #10: Нет валидации входных данных

**Категория:** Security
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 66, метод updateStatus

**Что плохо:**
```java
public Invitee updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {

```

**Почему плохо:**
Нет валидации и типизации, может быть передано что угодно. Непредсказуемое поведение.

**Как исправить:**
использовать DTO
```java
public Invitee updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateInviteeStatusRequest request) {
```
---

## Issue #11: SQL injection через конкатенацию

**Категория:** Security
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 42, метод create

**Что плохо:**
```java
    String sql = "SELECT COUNT(*) FROM invitees WHERE email = '" + email + "'";
    repository.executeNativeQuery(sql);
```

**Почему плохо:**
Пользовательский ввод напрямую конкатенируется в SQL запрос, злоумышленник может передать под видом параметра свой SQL-запрос и получить непредсказуемое поведение (пепредать под видом email строку вида admin@test.com' OR '1'='1 и получить доступ ко всем записям)

**Как исправить:**
использовать PreparedStatement с параметрами или Spring Data JPA методы:
```java
// Spring Data JPA method  
repository.findByEmail(email); // Автоматическое экранирование  
  
// Или PreparedStatement  
PreparedStatement ps = conn.prepareStatement("SELECT * FROM invitees WHERE email = ?");  
ps.setString(1, email); //параметры никогда не превратятся в SQL
```
---

## Issue #12: 500 на бизнес-ошибки вместо 4xx

**Категория:** Error Handling
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 75, метод updateStatus

**Что плохо:**
```java
      if (status.equals("ACTIVE") || status.equals("INACTIVE")) {
        invitee.setStatus(status);
      } else {
        throw new RuntimeException("Invalid status");
      }
```

**Почему плохо:**
HTTP статус код должен соответствовать категории ошибки согласно RFC 7231. Нарушение этого правила усложняет клиенту обработку случая.

**Как исправить:**
вернуть 200 ОК
```java
  @PutMapping("/invitees/{id}/status")
public ResponseEntity<InviteeResponse> updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateInviteeStatusRequest request) {

  InviteeResponse updated = service.updateStatus(id, request);

  return ResponseEntity.ok().body(updated);
}
```

---

## Issue #13: Пустые catch блоки

**Категория:** Error Handling
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 81, метод updateStatus

**Что плохо:**
```java
    try {
    ...
    } catch (Exception e) {
      // Пустой catch
      return null;
    }
```

**Почему плохо:**
Система должна падать быстро и явно, а не продолжать работу с повреждённым состоянием. Пустые try-catch скрывают проблемы, усложняют debugging и возвращает клиенту некорректные данные.

**Как исправить:**
Можно логгировать ошибку и выбросить исключение
```java
    try {
    ...
    } catch (Exception e) {
      log.error("Failed to update status for invitee {}", id, e);
       throw new ServiceException("Update failed", e);
    }
```

---

## Issue #14: Field injection через @Autowired

**Категория:** Code Quality
**Приоритет:** MINOR
**Местоположение:** InviteeController.java, строка 21

**Что плохо:**
```java
  @Autowired
  InviteeRepository repository;
```
**Почему плохо:**
Field injection усложняет тестирование (нельзя передать mock через конструктор) и делает зависимости неявными.

**Как исправить:**
использовать Constructor-based Dependency Injection (вместо конструктора можно использовать lombok-аннотацию @RequiredArgsConstructor):
```java
private final InviteeRepository repository;

  public InviteeController(InviteeRepository repository) {
    this.repository = repository;
  }
```
или Setter-based Dependency Injection
https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html

---
## Issue #15: Бизнес-логика в контроллере

**Категория:** Code Quality
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 43, метод create

**Что плохо:**
```java
    String sql = "SELECT COUNT(*) FROM invitees WHERE email = '" + email + "'";
    repository.executeNativeQuery(sql);
```

**Почему плохо:**
Нарушение Single Responsibility Principe - контроллер не должен отвечать за работу с репозиторием, проверка дублирования email и прочих бизнес-ограничений это задача сервиса.

**Как исправить:**
В контроллере вызывать сохранение сущности через сервис
```java
return service.save(invitee);
```

---

## Issue #16: Бизнес-логика в контроллере

**Категория:** Code Quality
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 58, метод delete

**Что плохо:**
```java
    if (invitee != null) {
    repository.delete(invitee);
    }
```

**Почему плохо:**
Нарушение Single Responsibility Principe - контроллер не должен отвечать за работу с репозиторием, проверка дублирования email и прочих бизнес-ограничений это задача сервиса.

**Как исправить:**
В контроллере вызывать удаление сущности через сервис
```java
return service.delete(request);
```
---

## Issue #17: Бизнес-логика в контроллере

**Категория:** Code Quality
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 72, метод updateStatus

**Что плохо:**
```java
      if (status.equals("ACTIVE") || status.equals("INACTIVE")) {
    invitee.setStatus(status);
      } else {
          throw new RuntimeException("Invalid status");
      }
```

**Почему плохо:**
Нарушение Single Responsibility Principe - контроллер не должен отвечать за работу с репозиторием, проверка дублирования email и прочих бизнес-ограничений это задача сервиса.

**Как исправить:**
В контроллере вызывать изменение статуса сущности через сервис
```java
service.updateStatus(id, status);
```
---

# Refactoring Summary: InviteeController


| Метрика | До рефакторинга | После рефакторинга |
|---------|-----------------|--------------------|
| Строк кода в контроллере | 79              | 53                 |
| Количество зависимостей | 1               | 1                  |
| Цикломатическая сложность | 5               | 1                  |
| Проблем категории CRITICAL | 9               | 0                  |
| Проблем категории MAJOR | 7               | 0                  |
| Проблем категории MINOR | 1               | 0                  |

---

## Исправленные проблемы (по категориям)

### API Design
✅ Issue #1: Отсутствие пагинации — добавление pageable в метод репозитория

✅ Issue #2: Неправильные HTTP методы — замена PostMapping на GetMapping

✅ Issue #3:  Entity вместо DTO в response — использование DTO InviteeResponse вместо Entity Invitee

✅ Issue #4:  Entity вместо DTO в response — использование DTO InviteeResponse вместо Entity Invitee

✅ Issue #5: Entity вместо DTO в response — использование DTO InviteeResponse вместо Entity Invitee

✅ Issue #6: Плохой naming: глаголы в URL — заменить "/getInvitees" в URL на "/invitees"

✅ Issue #7: Неправильные статус коды — вернуть правильный статус (Вернуть 201 Created), исключения при сохранении выбрасывать в сервисе и отлавливать глобально

✅ Issue #8: Неправильные статус коды — вернуть правильный статус (Вернуть 200 Ok), исключения при выбрасывать в сервисе и отлавливать глобально

### Security
✅ Issue #9: Нет валидации входных данных — использовать DTO CreateInviteeRequest с @Valid вместо Map<String, Object>

✅ Issue #10: Нет валидации входных данных — использовать DTO UpdateInviteeStatusRequest с @Valid вместо Map<String, String>

✅ Issue #11: SQL injection через конкатенацию — использовать derived-метод вместо native-query

### Error Handling
✅ Issue #12: 500 на бизнес-ошибки вместо 4xx — вернуть правильный статус (Вернуть 200 Ok), бизнес-проверку вынести в сервис, где выбрасывать исключение, отлавливать исключение глобально

✅ Issue #13: Пустые catch блоки — бросать исключение в методе сервиса и отлавливать глобально или тут же логгировать ошибку и выбросить исключение (в зависимости от требуемой логики)

### Code Quality
✅ Issue #14: Field injection через @Autowired — использовать использовать Constructor-based Dependency Injection

✅ Issue #15: Бизнес-логика в контроллере — в контроллере вызывать метод сервиса, в сервисе проводить необходимые проверки и передавать запрос в метод репозитория

✅ Issue #16: Бизнес-логика в контроллере — в контроллере вызывать метод сервиса, в сервисе проводить необходимые проверки и передавать запрос в метод репозитория

✅ Issue #17: Бизнес-логика в контроллере — в контроллере вызывать метод сервиса, в сервисе проводить необходимые проверки и передавать запрос в метод репозитория

---

## Ключевые архитектурные изменения
**1. Введение DTO слоя**
<pre>
До: Entity Invitee возвращался напрямую в response
После: CreateInviteeRequest (input), InviteeResponse (output)
Преимущества: Security (нет exposure internal fields), Flexibility (API контракт независим от Entity)
</pre>
**2. Вынос бизнес-логики в Service**
<pre>
До: Все проверки и логика в контроллере (80 строк метод create)
После: Контроллер только HTTP layer (15 строк), InviteeService содержит business rule
Преимущества: Testability (можно тестировать Service отдельно), Reusability (другие контроллеры могут вызывать Service)
</pre>
**3. GlobalExceptionHandler вместо дублирования try-catch**
<pre>
До: Каждый метод контроллера содержит try-catch с одинаковой логикой
После: Контроллер выбрасывает exceptions, GlobalExceptionHandler обрабатывает централизованно
Преимущества: DRY (код error handling написан один раз), Consistency (все errors имеют единый формат)
</pre>
---

## Применение на собеседованиях

### Что демонстрирует этот рефакторинг:

**Систематический подход:** Использовал чек-лист из 4 категорий вместо хаотичного поиска  
**Знание стандартов:** Ссылки на RFC 7231 (HTTP methods), RFC 7807 (Problem Details), Spring Reference  
**Приоритизация:** Сначала исправил CRITICAL (security vulnerabilities), потом MAJOR, потом MINOR  
**Конкретные решения:** Для каждой проблемы дал код пример исправления, не абстрактный совет  
**Архитектурное мышление:** Не просто "переименовал метод", а "реорганизовал в 3-слойную архитектуру"

### Типичные вопросы интервьюера после код-ревью exercise:

> **Q:** "Какую проблему вы считаете самой критичной?"

**A:** "SQL injection в методе create() через string concatenation. Это CRITICAL security vulnerability которая может привести к data breach. Исправление — использовать PreparedStatement или Spring Data JPA методы с автоматическим экранированием."

> **Q:** "Почему вы вынесли бизнес-логику в Service вместо оставить в контроллере?"

**A:** "Контроллер должен отвечать только за HTTP layer согласно Single Responsibility Principle. Бизнес-логика в контроллере усложняет тестирование (нужны @SpringBootTest вместо простых unit тестов) и переиспользование (другой контроллер или scheduled task не может вызвать эту логику)."

> **Q:** "Что бы вы сделали если автор кода не согласен с вашими замечаниями?"

**A:** "Привёл бы ссылки на официальные стандарты и документацию. Например для проблемы 'POST для чтения данных' сослался бы на RFC 7231 раздел 4.3.3 где явно сказано что POST для создания ресурсов. Если это legacy код с техническими ограничениями — попросил бы автора добавить комментарий объясняющий почему нарушается стандарт."

---

## Чек-лист для следующих код-ревью
После этой практики при ревью любого REST контроллера проверяю:

- [ ] HTTP методы соответствуют семантике (GET=read, POST=create, PUT=update, DELETE=remove)
- [ ] Статус коды корректные (201 для POST, 204 для DELETE, 404 для not found)
- [ ] DTO используются вместо Entity в request/response
- [ ] Bean Validation для входных данных (@Valid, @NotBlank, @Email)
- [ ] НЕТ SQL injection через string concatenation
- [ ] НЕТ exposure внутренних полей (password, version, audit fields)
- [ ] GlobalExceptionHandler обрабатывает ошибки, НЕТ дублирования try-catch
- [ ] Problem Details RFC 7807 для error responses
- [ ] Бизнес-логика в Service слое, НЕ в контроллере
- [ ] Constructor injection вместо field injection
- [ ] Pagination для списковых endpoints
- [ ] Тесты покрывают success cases И edge cases (404, validation errors)

---

## Выводы
Систематический код-ревью через структурированный чек-лист позволяет находить 10+ проблем за 15 минут live coding сессии на собеседовании. Ключевые навыки: категоризация проблем (API Design, Security, Error Handling, Code Quality), приоритизация (CRITICAL → MAJOR → MINOR), аргументация через стандарты (RFC, Spring Reference), конкретные предложения решений с примерами кода.