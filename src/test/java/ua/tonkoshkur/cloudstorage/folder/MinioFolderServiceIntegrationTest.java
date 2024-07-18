package ua.tonkoshkur.cloudstorage.folder;

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
import ua.tonkoshkur.cloudstorage.BaseIntegrationTest;
import ua.tonkoshkur.cloudstorage.minio.MinioService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MinioFolderServiceIntegrationTest extends BaseIntegrationTest {

    private static final long USER_ID = 1;
    private static final String VALID_FOLDER_NAME = "folder";
    private static final String PARENT_FOLDER = "parentFolder";
    private static final FolderDto FOLDER = new FolderDto(VALID_FOLDER_NAME, PARENT_FOLDER);

    @Autowired
    MinioFolderService minioFolderService;
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
    void findAllByQuery_withUserIdAndExistedFolderNameAsQuery_returnsThisUserFolder() {
        minioFolderService.create(USER_ID, VALID_FOLDER_NAME, PARENT_FOLDER);

        List<FolderDto> folders = minioFolderService.findAllByQuery(USER_ID, VALID_FOLDER_NAME);

        assertThat(folders)
                .singleElement()
                .isEqualTo(FOLDER);
    }

    @Test
    void findAllByQuery_withUserIdThatDoesNotHaveFolders_returnsNoFolders() {
        minioFolderService.create(USER_ID, VALID_FOLDER_NAME, PARENT_FOLDER);

        List<FolderDto> folders = minioFolderService.findAllByQuery(2, VALID_FOLDER_NAME);

        assertThat(folders).isEmpty();
    }

    @Test
    void findAllByQuery_withNotExistedFolderNameAsQuery_returnsNoFolders() {
        minioFolderService.create(USER_ID, VALID_FOLDER_NAME, PARENT_FOLDER);

        List<FolderDto> folders = minioFolderService.findAllByQuery(USER_ID, "folder1");

        assertThat(folders).isEmpty();
    }

    @Test
    void findAllByParentPath_withExistedParentFolder_returnsThisParentFolderFolders() {
        minioFolderService.create(USER_ID, VALID_FOLDER_NAME, PARENT_FOLDER);

        List<FolderDto> folders = minioFolderService.findAllByParentPath(USER_ID, PARENT_FOLDER);

        assertThat(folders)
                .singleElement()
                .isEqualTo(FOLDER);
    }

    @Test
    void findAllByParentPath_withNotExistedParentFolder_returnsNoFolders() {
        minioFolderService.create(USER_ID, VALID_FOLDER_NAME, PARENT_FOLDER);

        List<FolderDto> folders = minioFolderService.findAllByParentPath(USER_ID, PARENT_FOLDER + "aaa");

        assertThat(folders).isEmpty();
    }

    @ParameterizedTest
    @EmptySource
    @MethodSource("getInvalidMinioCharacters")
    void create_withInvalidFolderName_throwsInvalidFolderNameException(String invalidFolderName) {
        assertThrows(InvalidFolderNameException.class,
                () -> minioFolderService.create(USER_ID, invalidFolderName, PARENT_FOLDER));
    }

    @Test
    void create_withExistedFolderName_throwsFolderAlreadyExistsException() {
        minioFolderService.create(USER_ID, VALID_FOLDER_NAME, PARENT_FOLDER);
        assertThrows(FolderAlreadyExistsException.class,
                () -> minioFolderService.create(USER_ID, VALID_FOLDER_NAME, PARENT_FOLDER));
    }

    @ParameterizedTest
    @EmptySource
    @MethodSource("getInvalidMinioCharacters")
    void rename_withInvalidFolderName_throwsInvalidFolderNameException(String invalidFolderName) {
        assertThrows(InvalidFolderNameException.class,
                () -> minioFolderService.rename(USER_ID, "", invalidFolderName));
    }

    @Test
    void rename_withExistedFolderName_throwsFolderAlreadyExistsException() {
        FolderDto folder = new FolderDto("folderToRename", PARENT_FOLDER);
        minioFolderService.create(USER_ID, VALID_FOLDER_NAME, PARENT_FOLDER);
        minioFolderService.create(USER_ID, folder.name(), folder.parentFolderPath());

        String oldPath = folder.path();
        assertThrows(FolderAlreadyExistsException.class,
                () -> minioFolderService.rename(USER_ID, oldPath, VALID_FOLDER_NAME));
    }

    @Test
    void delete_deletesFolder() {
        minioFolderService.create(USER_ID, FOLDER.name(), FOLDER.parentFolderPath());

        minioFolderService.delete(USER_ID, FOLDER.path());

        List<FolderDto> folders = minioFolderService.findAllByParentPath(USER_ID, FOLDER.parentFolderPath());
        assertThat(folders).isEmpty();
    }

}
