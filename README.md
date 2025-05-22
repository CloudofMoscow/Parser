# News Crawler — Java Maven Project

## Описание

Этот проект реализует систему для сбора новостей с RSS-ленты, обработки публикаций через очереди RabbitMQ и сохранения результатов в ElasticSearch.  
Вся обработка построена на микросервисной архитектуре с использованием Docker Compose.

## Основные компоненты

- **Crawler** — парсит RSS-ленту, извлекает публикации (заголовок, дата, автор, ссылка, текст).
- **RabbitMQ** — брокер сообщений, используется для очередей задач и результатов.
- **Worker** — обрабатывает задачи из очереди, скачивает и парсит публикации.
- **ElasticSearch** — хранит публикации, поддерживает поиск и агрегации.

## Быстрый старт

1. Остановите старые контейнеры (если были запущены):
   ```sh
   docker-compose down -v
   ```
2. Соберите и запустите все сервисы:
   ```sh
   docker-compose up --build
   ```
3. Если приложение стартовало раньше зависимых сервисов, перезапустите только приложение:
   ```sh
   docker-compose restart app
   ```

## Проверка работы

### RabbitMQ

- Откройте интерфейс управления: http://localhost:15672 (логин/пароль: guest/guest)
- Убедитесь, что существуют очереди `task_queue` и `result_queue`.
- Сообщения появляются и исчезают по мере обработки.

### ElasticSearch

- Получить все публикации:
  ```sh
  curl -X GET "http://localhost:9200/news/_search?pretty"
  ```
- Поиск по заголовку:
  ```sh
  curl -X POST "http://localhost:9200/news/_search?pretty" -H "Content-Type: application/json" -d "{\"query\":{\"match\":{\"title\":\"AI\"}}}"
  ```
- Поиск по нескольким полям (AND):
  ```sh
  curl -X POST "http://localhost:9200/news/_search?pretty" -H "Content-Type: application/json" -d "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"title\":\"AI\"}},{\"match\":{\"author\":\"Автор\"}}]}}}"
  ```
- Поиск по нескольким полям (OR):
  ```sh
  curl -X POST "http://localhost:9200/news/_search?pretty" -H "Content-Type: application/json" -d "{\"query\":{\"bool\":{\"should\":[{\"match\":{\"title\":\"AI\"}},{\"match\":{\"author\":\"Автор\"}}],\"minimum_should_match\":1}}}"
  ```
- Fuzzy-поиск по тексту:
  ```sh
  curl -X POST "http://localhost:9200/news/_search?pretty" -H "Content-Type: application/json" -d "{\"query\":{\"match\":{\"text\":{\"query\":\"искусственный интелект\",\"fuzziness\":\"AUTO\"}}}}"
  ```
- Агрегация по авторам:
  ```sh
  curl -X POST "http://localhost:9200/news/_search?pretty" -H "Content-Type: application/json" -d "{\"size\":0,\"aggs\":{\"by_author\":{\"terms\":{\"field\":\"author.keyword\"}}}}"
  ```
- Агрегация по датам публикаций:
  ```sh
  curl -X POST "http://localhost:9200/news/_search?pretty" -H "Content-Type: application/json" -d "{\"size\":0,\"aggs\":{\"by_date\":{\"date_histogram\":{\"field\":\"pubDate\",\"calendar_interval\":\"day\"}}}}"
  ```

### Проверка очереди через Basic.Get

- В интерфейсе RabbitMQ выберите нужную очередь и используйте кнопку "Get Message (Basic.Get)".

## Примечания

- Для работы требуется установленный Docker и Docker Compose.
- Все сервисы автоматически поднимаются и связываются через docker-compose.
- Для ElasticSearch отключена авторизация и кластер работает в режиме single-node.

---

