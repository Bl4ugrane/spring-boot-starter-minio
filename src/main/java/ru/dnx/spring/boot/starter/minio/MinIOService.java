package ru.dnx.spring.boot.starter.minio;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * Сервис для взаимодействия с MinIO Client
 */
@Slf4j
@RequiredArgsConstructor
public class MinIOService {

    private final MinioClient minioClient;

    /**
     * @param bucketName - наименование бакета в хранилище
     * @param filePath   - наименование пути до файла в хранилище
     * @return поток байтов
     */
    @SneakyThrows
    public InputStream getFile(String bucketName, String filePath) {
        return minioClient.getObject(bucketName, filePath);
    }

    /**
     * Сохрание файла в хранилище
     *
     * @param bucketName - наименование бакета в хранилище
     * @param filePath   - наименование пути до файла в хранилище
     * @param file       - файл
     * @return ссылка на файл в хранилище
     */
    @SneakyThrows
    public String saveFile(String bucketName, String filePath, MultipartFile file) {
        log.info("Проверка наличия бакета");
        makeBucket(bucketName);
        log.info("Сохрание файла в хранилище");
        minioClient.putObject(bucketName, filePath, file.getInputStream(), file.getContentType());
        return filePath;
    }

    /**
     * Удаление файла в хранилище
     *
     * @param bucketName - наименование бакета в хранилище
     * @param filePath   - наименование пути до файла в хранилище
     */
    @SneakyThrows
    public void removeFile(String bucketName, String filePath) {
        minioClient.removeObject(bucketName, filePath);
    }

    /**
     * Создание бакета в хранилище
     *
     * @param bucketName - наименование бакета в хранилище
     */
    @SneakyThrows
    public void makeBucket(String bucketName) {
        if (!bucketExists(bucketName)) {
            minioClient.makeBucket(bucketName);
        }
    }

    /**
     * Удаление бакета в хранилище
     *
     * @param bucketName - наименование бакета в хранилище
     */
    @SneakyThrows
    public void removeBucket(String bucketName) {
        if (bucketExists(bucketName)) {
            minioClient.removeBucket(bucketName);
        }
    }

    /**
     * Проверка наличия бакета в хранилище
     *
     * @param bucketName - наименование бакета в хранилище
     * @return - true/false
     */
    @SneakyThrows
    private boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(bucketName);
    }
}