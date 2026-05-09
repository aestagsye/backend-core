package ru.mentee.power.crm.spring.rest.fixed;

import org.springframework.web.bind.annotation.RestController;

// import ru.mentee.power.crm.spring.repository.InviteeRepository;

/**
 * ЗАДАНИЕ: Найдите все проблемы в этом контроллере используя чек-лист. Ожидается найти минимум 10
 * проблем из разных категорий.
 */
@RestController
public class InviteeControllerFixed {
  /*
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

  */
  // ЗАКОММЕНТИРОВАНО, потому что в проекте не реализованы сервис, сущность, репозиторий, маппер,
  // формы дто для Invitee
}
