package ua.tonkoshkur.cloudstorage.file;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ua.tonkoshkur.cloudstorage.BaseIntegrationTest;
import ua.tonkoshkur.cloudstorage.minio.MinioService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MinioFileServiceIntegrationTest extends BaseIntegrationTest {

    private static final long USER_ID = 1;
    private static final String FILE_NAME = "file";
    private static final String FOLDER_NAME = "folder";
    private static final FileDto FILE = new FileDto(FILE_NAME, FOLDER_NAME);
    private static final MultipartFile MULTIPART_FILE =
            new MockMultipartFile(FILE_NAME, FILE_NAME, null, (byte[]) null);

    @Autowired
    MinioFileService minioFileService;
    @Autowired
    MinioService minioService;
    @Value("minio.bucket-name")
    String bucketName;

    @SneakyThrows
    @AfterEach
    void removeBucket() {
        Iterable<Result<Item>> results = minioService.findAll("", true);
        for (Result<Item> result : results) {
            minioService.delete(result.get().objectName());
        }
    }

    @Test
    void findAllByQuery_withUserIdAndExistedFileNameAsQuery_returnsThisUserFile() {
        minioFileService.upload(USER_ID, MULTIPART_FILE, FOLDER_NAME);

        List<FileDto> files = minioFileService.findAllByQuery(USER_ID, FILE_NAME);

        assertThat(files)
                .singleElement()
                .isEqualTo(FILE);
    }

    @Test
    void findAllByQuery_withUserIdThatDoesNotHaveFiles_returnsNoFiles() {
        minioFileService.upload(USER_ID, MULTIPART_FILE, FOLDER_NAME);

        List<FileDto> files = minioFileService.findAllByQuery(2, FILE_NAME);

        assertThat(files).isEmpty();
    }

    @Test
    void findAllByQuery_withNotExistedFileNameAsQuery_returnsNoFiles() {
        minioFileService.upload(USER_ID, MULTIPART_FILE, FOLDER_NAME);

        List<FileDto> files = minioFileService.findAllByQuery(USER_ID, "file1");

        assertThat(files).isEmpty();
    }

    @Test
    void findAllByFolderPath_withExistedFolder_returnsThisFolderFile() {
        minioFileService.upload(USER_ID, MULTIPART_FILE, FOLDER_NAME);

        List<FileDto> files = minioFileService.findAllByFolderPath(USER_ID, FOLDER_NAME);

        assertThat(files)
                .singleElement()
                .isEqualTo(FILE);
    }

    @Test
    void findAllByFolderPath_withNotExistedFolder_returnsNoFiles() {
        minioFileService.upload(USER_ID, MULTIPART_FILE, FOLDER_NAME);

        List<FileDto> files = minioFileService.findAllByFolderPath(USER_ID, FOLDER_NAME + "aaa");

        assertThat(files).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("getInvalidMinioCharacters")
    void upload_withInvalidFileName_throwsInvalidFileNameException(String invalidFileName) {
        MultipartFile multipartFile = new MockMultipartFile(invalidFileName, invalidFileName, null, (byte[]) null);
        assertThrows(InvalidFileNameException.class,
                () -> minioFileService.upload(USER_ID, multipartFile, FOLDER_NAME));
    }

    @Test
    void upload_withExistedFileName_throwsFileAlreadyExistsException() {
        minioFileService.upload(USER_ID, MULTIPART_FILE, FOLDER_NAME);
        assertThrows(FileAlreadyExistsException.class,
                () -> minioFileService.upload(USER_ID, MULTIPART_FILE, FOLDER_NAME));
    }

    @ParameterizedTest
    @EmptySource
    @MethodSource("getInvalidMinioCharacters")
    void rename_withInvalidFileName_throwsInvalidFileNameException(String invalidFileName) {
        assertThrows(InvalidFileNameException.class,
                () -> minioFileService.rename(USER_ID, "", invalidFileName));
    }

    @Test
    void rename_withExistedFileName_throwsFileAlreadyExistsException() {
        FileDto fileToRename = new FileDto("fileToRename", FOLDER_NAME);
        MockMultipartFile multipartFileToRename
                = new MockMultipartFile(fileToRename.name(), fileToRename.name(), null, new byte[]{});
        minioFileService.upload(USER_ID, multipartFileToRename, fileToRename.folderPath());
        minioFileService.upload(USER_ID, MULTIPART_FILE, FOLDER_NAME);

        String oldPath = fileToRename.path();
        assertThrows(FileAlreadyExistsException.class,
                () -> minioFileService.rename(USER_ID, oldPath, FILE_NAME));
    }

    @Test
    void delete_deletesFiles() {
        minioFileService.upload(USER_ID, MULTIPART_FILE, FOLDER_NAME);

        minioFileService.delete(USER_ID, FILE.path());

        List<FileDto> files = minioFileService.findAllByFolderPath(USER_ID, FILE.folderPath());
        assertThat(files).isEmpty();
    }

}
