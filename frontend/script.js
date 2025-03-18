document.addEventListener("DOMContentLoaded", function () {
    let majorAutocomplete, minorAutocomplete;
    fetchAutocompleteOptions().then(data => {
        // The API returns data.dropdown1 for majors and data.dropdown2 for minors.
        majorAutocomplete = setupAutocomplete("majorInput", "majorSuggestions", "selectedMajors", data.dropdown1, 2);
        minorAutocomplete = setupAutocomplete("minorInput", "minorSuggestions", "selectedMinors", data.dropdown2, 2);
    });

    document.getElementById("uploadButton").addEventListener("click", uploadFile);
    document.getElementById("submitButton").addEventListener("click", function() {
        // Note: We now send keys "dropdown1" and "dropdown2" per the backend requirements.
        submitSelections(majorAutocomplete.getSelected(), minorAutocomplete.getSelected());
    });
    document.getElementById("progressButton").addEventListener("click", fetchStudentProgress);
});

function fetchAutocompleteOptions() {
    return fetch("https://degree-planner-backend.onrender.com/api/dropdown-options")
        .then(response => response.json())
        .catch(error => console.error("Error fetching options:", error));
}

function setupAutocomplete(inputId, suggestionId, selectedContainerId, optionsArray, maxSelections) {
    const input = document.getElementById(inputId);
    const suggestionContainer = document.getElementById(suggestionId);
    const selectedContainer = document.getElementById(selectedContainerId);
    let selected = [];

    input.addEventListener("input", function() {
        const value = input.value.toLowerCase();
        suggestionContainer.innerHTML = "";
        if (!value) return;
        const filtered = optionsArray.filter(option => option.toLowerCase().includes(value));
        filtered.forEach(option => {
            const div = document.createElement("div");
            div.textContent = option;
            div.classList.add("autocomplete-suggestion");
            div.addEventListener("click", function() {
                if (selected.length >= maxSelections) {
                    alert("You can only select up to " + maxSelections + " options.");
                    return;
                }
                if (!selected.includes(option)) {
                    selected.push(option);
                    updateSelected();
                }
                input.value = "";
                suggestionContainer.innerHTML = "";
            });
            suggestionContainer.appendChild(div);
        });
    });

    // Hide suggestions when the input loses focus.
    input.addEventListener("blur", function() {
        setTimeout(() => suggestionContainer.innerHTML = "", 100);
    });

    function updateSelected() {
        selectedContainer.innerHTML = "";
        selected.forEach(item => {
            const span = document.createElement("span");
            span.textContent = item + " ×";
            span.classList.add("selected-badge");
            // Allow removal of a selection on click.
            span.addEventListener("click", function() {
                selected = selected.filter(i => i !== item);
                updateSelected();
            });
            selectedContainer.appendChild(span);
        });
    }

    return {
        getSelected: function() { return selected; }
    };
}

function submitSelections(majors, minors) {
    // Ensure the keys match the backend expectations: "dropdown1" for majors, "dropdown2" for minors.
    fetch("https://degree-planner-backend.onrender.com/api/submit-selections", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ dropdown1: majors, dropdown2: minors })
    })
    .then(response => response.text())
    .then(data => alert("Selections submitted successfully: " + data))
    .catch(error => console.error("Error submitting selections:", error));
}

function fetchStudentProgress() {
    fetch("https://degree-planner-backend.onrender.com/api/student-progress")
        .then(response => response.text())
        .then(data => {
            document.getElementById("studentProgress").textContent = data;
        })
        .catch(error => console.error("Error fetching student progress:", error));
}

function uploadFile() {
    const fileInput = document.getElementById("fileInput");
    const file = fileInput.files[0];
    
    if (!file) {
        alert("Please select a file first.");
        return;
    }
    
    const formData = new FormData();
    formData.append("file", file);
    
    fetch("https://degree-planner-backend.onrender.com/api/upload", {
        method: "POST",
        body: formData
    })
    .then(response => response.text())
    .then(data => {
        document.getElementById("response").textContent = data;
        console.log("Upload successful:", data);
    })
    .catch(error => {
        console.error("Error uploading file:", error);
    });
}