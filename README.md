# Spring Boot Starter MinIO

Библиотека для взаимодействия с файловым хранилищем [MinIO](https://docs.min.io/docs/minio-quickstart-guide.html)

### При использовании стартера, необходимо в файле настроек указать:

```yaml
  minio:
    url: MINIO_URL
    access-key: MINIO_ACCESS_KEY
    secret-key: MINIO_SECRET_KEY
```

### Пример использования:

```kotlin
/**
 * Сервис для извлечение файлов из файлового хранилища
 */
@Service
class FileStorageService(
    private val minIOService: MinIOService
) {

    private companion object : KLogging()

    /**
     * Запрос файлов из файлового хранилища
     * @param - request
     */
    fun retrieveFiles(folderName: String, fileName: String): File {
        logger.info { "Запрос файлов из файлового хранилища" }
        val file = File(fileName)
        FileUtils.copyInputStreamToFile(
            minIOService.getFile(
                "bucketName", String.format("%s/%s", folderName, fileName)
            ), file
        )
        return file
    }

    /**
     * Сохранение файла в файловом хранилище и получение ссылки на файл
     * @param - request
     */
    fun saveFile(file: MultipartFile): String {
        logger.info { "Загрузка файла в файловое хранилище" }
        val folderName: String = UUID.randomUUID().toString()
        val fileName: String = file.name
        return minIOService.saveFile("bucketName", String.format("%s/%s", folderName, fileName), file)
    }
}
```

### Запуск MinIO в Docker

```yaml
version: '3.7'

services:
  minio:
    image: minio/minio:latest
    command: server --console-address ":9001" /data/
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: MINIO_ACCESS_KEY
      MINIO_ROOT_PASSWORD: MINIO_SECRET_KEY
    volumes:
      - minio-storage:/data
    healthcheck:
      test: [ "CMD", "curl", "-f", "http://localhost:9000/minio/health/live" ]
      interval: 30s
      timeout: 20s
      retries: 3
volumes:
  minio-storage:
```
