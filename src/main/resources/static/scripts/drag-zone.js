document.querySelectorAll(".drop-zone__input").forEach((inputElement) => {
    const dropZone = inputElement.closest(".drop-zone");

    dropZone.addEventListener("click", () => {
        inputElement.click();
    });

    inputElement.addEventListener("change", () => {
        if (inputElement.files.length) {
            hidePrompt(dropZone);
            addThumbnail(dropZone, inputElement.files[0]);
            document.getElementById('uploadButton').focus();
        } else {
            showPrompt(dropZone);
            removeThumbnail(dropZone);
        }
    });

    dropZone.addEventListener("dragover", (e) => {
        e.preventDefault();
        dropZone.classList.add("drop-zone--over");
    });

    ["dragleave", "dragend"].forEach((type) => {
        dropZone.addEventListener(type, () => {
            dropZone.classList.remove("drop-zone--over");
        });
    });

    dropZone.addEventListener("drop", (e) => {
        e.preventDefault();

        if (e.dataTransfer.files.length) {
            inputElement.files = e.dataTransfer.files;
            hidePrompt(dropZone);
            addThumbnail(dropZone, e.dataTransfer.files[0]);
        }

        dropZone.classList.remove("drop-zone--over");
    });
});

function hidePrompt(dropZoneElement) {
    getPrompt(dropZoneElement).style.display = 'none';
}

function showPrompt(dropZoneElement) {
    getPrompt(dropZoneElement).style.display = 'block';
}

function getPrompt(dropZoneElement) {
    return dropZoneElement.querySelector(".drop-zone__prompt");
}

function addThumbnail(dropZoneElement, file) {
    let thumbnail = getThumbnail(dropZoneElement);
    if (!thumbnail) {
        thumbnail = document.createElement("div");
        thumbnail.classList.add("drop-zone__thumb");
        thumbnail.classList.add("bg-primary-subtle");
        dropZoneElement.appendChild(thumbnail);
    }
    thumbnail.dataset.label = file.name;

    // Show thumbnail for image files
    if (file.type.startsWith("image/")) {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
            thumbnail.style.backgroundImage = `url('${reader.result}')`;
        };
    } else {
        thumbnail.style.backgroundImage = null;
    }
}

function removeThumbnail(dropZoneElement) {
    // getThumbnail(dropZoneElement).style.display = 'none';
    let thumbnail = getThumbnail(dropZoneElement);
    if (thumbnail) {
        thumbnail.remove();
    }
}

function getThumbnail(dropZoneElement) {
    return dropZoneElement.querySelector(".drop-zone__thumb");
}
