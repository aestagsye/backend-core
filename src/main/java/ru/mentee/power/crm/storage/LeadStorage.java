package ru.mentee.power.crm.storage;

import java.util.UUID;

import ru.mentee.power.crm.domain.Lead;

public class LeadStorage {
  private Lead[] leads = new Lead[100];

  public boolean add(Lead lead) {
    for (int index = 0; index < leads.length; index++) {
      if (leads[index] != null && leads[index].email().equals(lead.email())) {
        return false;
      }
    }

    for (int index = 0; index < leads.length; index++) {
      if (leads[index] == null){
        leads[index] = lead;
        return true;
      }
    }
    // 3. Если цикл завершился и свободной ячейки нет:
    throw new IllegalStateException("Storage is full, cannot add more leads");
  }
  public Lead[] findAll() {
    // 1. Посчитай количество ненулевых элементов:
    //    int count = 0;
    //    Пройди цикл по массиву leads
    //    Если leads[index] != null, увеличь count++
    int count = 0;
    for (int index = 0; index < leads.length; index++) {
      if (leads[index] != null) {
        count++;
      }
    }
    // 2. Создай новый массив размером count:
    Lead[] result = new Lead[count];
    // 3. Заполни result ненулевыми элементами:
    //    int resultIndex = 0;
    //    Пройди цикл по массиву leads
    //    Если leads[index] != null:
    //        result[resultIndex++] = leads[index];
    int resultIndex = 0;
    for (int index = 0; index < leads.length; index++) {
      if (leads[index] != null){
        result[resultIndex++] = leads[index];
      }
    }
    return result;// 4. Верни result
  }

  public int size() {
    int count = 0;
    for (int index = 0; index < leads.length; index++) {
      if (leads[index] != null){
        count++;
      }
    }
    return count;
  }

  public Lead findById(UUID id) {
    for (Lead lead : leads) {
      if (lead != null && lead.id().equals(id)) {
        return lead;
      }
    }
    return null; // или можно бросить исключение
  }
}
