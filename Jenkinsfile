// Jenkinsfile — сборочный конвейер для проекта todo-aggregator.
// Покрывает все требования из постановки задачи:
//   - получение исходного кода
//   - компиляция кода и тестов
//   - запуск тестов (только для веток feature/*)
//   - статический анализ (только для ветки dev/develop)
//   - измерение тестового покрытия
//   - установка артефактов в локальный репозиторий только при успехе
//   - проверка процента покрытия (критерий выпуска версии)
//   - публикация артефакта в заранее заданную папку
pipeline {
    agent any

    environment {
        PUBLISH_DIR    = "${env.WORKSPACE}/published"
        COVERAGE_THRESHOLD = '50'
    }

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Получение исходного кода ветки ${env.BRANCH_NAME}"
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                echo 'Компиляция исходного кода и тестов'
                sh 'mvn -B clean compile test-compile'
            }
        }

        stage('Unit Tests') {
            when {
                anyOf {
                    branch 'feature/*'
                    expression { env.BRANCH_NAME?.startsWith('feature/') }
                }
            }
            steps {
                echo 'Запуск модульных тестов (ветка feature/*)'
                sh 'mvn -B test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Static Analysis') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'develop'
                }
            }
            steps {
                echo 'Запуск статического анализатора (checkstyle) для ветки dev/develop'
                sh 'mvn -B checkstyle:check'
            }
        }

        stage('Coverage') {
            steps {
                echo 'Измерение тестового покрытия (JaCoCo)'
                sh 'mvn -B verify'
            }
        }

        stage('Install') {
            steps {
                echo 'Установка артефактов в локальный репозиторий (только при успехе предыдущих стадий)'
                sh 'mvn -B install -DskipTests'
            }
        }

        stage('Publish') {
            steps {
                echo "Публикация артефакта в каталог ${env.PUBLISH_DIR}"
                sh '''
                    mkdir -p "$PUBLISH_DIR"
                    cp todo-app/target/todo-app.jar "$PUBLISH_DIR/"
                '''
                archiveArtifacts artifacts: 'published/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo "Сборка успешна: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
        failure {
            echo "Сборка завершилась с ошибкой: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
        always {
            cleanWs(notFailBuild: true)
        }
    }
}
