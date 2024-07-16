package ua.tonkoshkur.cloudstorage.storage;

import ua.tonkoshkur.cloudstorage.file.FileDto;
import ua.tonkoshkur.cloudstorage.folder.FolderDto;

import java.util.List;

public record StorageContentDto(List<FolderDto> folders,
                                List<FileDto> files) {
}
