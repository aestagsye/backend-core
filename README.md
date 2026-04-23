[![Java CI with Spotless](https://github.com/aestagsye/backend-core/actions/workflows/main.yml/badge.svg)](https://github.com/aestagsye/backend-core/actions/workflows/main.yml)
# Сравнение стеков Servlet vs Spring Boot
## Результаты интеграционного теста
| Метрика | Servlet | Spring Boot | Комментарий |
|---------|---------|-------------|-------------|
| Время старта | 419 ms  | 2661 ms     | Spring загружает IoC контейнер |
| HTTP 200 на /leads | ✅       | ✅           | Оба работают идентично |
| Количество лидов | 1       | 1           | Данные одинаковые |
| Строк Java кода | ~150    | ~30         | Контраст 5:1 |
## Вывод
Оба стека возвращают идентичные данные, но Spring Boot требует в 5 раз меньше кода за счёт auto-configuration. Trade-off: Spring стартует медленнее из-за инициализации IoC контейнера.

*Данные получены из `StackComparisonTest.java`*