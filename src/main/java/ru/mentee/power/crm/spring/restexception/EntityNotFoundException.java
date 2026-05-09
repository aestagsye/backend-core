package ru.mentee.power.crm.spring.restexception;

public class EntityNotFoundException extends BusinessException {

  private final String entityType;
  private final String entityId;

  public EntityNotFoundException(String entityType, String entityId) {
    super(String.format("%s with id %s not found", entityType, entityId));
    this.entityType = entityType;
    this.entityId = entityId;
  }

  public EntityNotFoundException(String message) {
    super(message);
    this.entityType = null;
    this.entityId = null;
  }

  public String getEntityId() {
    return entityId;
  }

  public String getEntityType() {
    return entityType;
  }
}
