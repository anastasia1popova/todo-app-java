# TODO Aggregator

Учебный проект по учебной практике: управление списком задач с применением Git,
Maven, JUnit + Mockito + Jacoco и Jenkins.

## Структура

```
todo-aggregator/        ← проект-агрегатор (parent pom)
├── pom.xml
├── Jenkinsfile         ← сборочный конвейер
├── todo-core/          ← вспомогательный модуль: модель, репозиторий, валидатор
│   ├── pom.xml
│   └── src/
└── todo-app/           ← основной модуль: CLI, сервисы, сборка fat JAR
    ├── pom.xml
    └── src/
```

## Требования

- JDK 17+
- Maven 3.9+

## Сборка

```bash
mvn clean package
```

После сборки появится единственный исполняемый jar:
`todo-app/target/todo-app.jar`

## Запуск

```bash
java -jar todo-app/target/todo-app.jar
```

## Тесты и покрытие

```bash
mvn verify           # тесты + Jacoco report + проверка порога 50%
```

Отчёт о покрытии: `todo-app/target/site/jacoco/index.html`,
`todo-core/target/site/jacoco/index.html`.

## Git-flow

```
main      ← релизные версии
└── develop          ← интеграционная ветка
    ├── feature/1   ← фичевая ветка
    ├── feature/2   ← фичевая ветка
    └── release/*   ← подготовка к релизу
```

## Jenkins

Pipeline описан в `Jenkinsfile` и охватывает:
получение кода → компиляцию → тесты (для feature/*) → checkstyle (для dev) →
покрытие → проверку порога → install в локальный репозиторий → копирование
артефакта в `published/`.

## Release 1.0
Снимок проекта, передаваемый группе тестирования.
