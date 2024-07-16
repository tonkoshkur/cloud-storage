package ua.tonkoshkur.cloudstorage.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class StorageController {
    @GetMapping
    public String storagePage() {
        return "storage";
    }
}
