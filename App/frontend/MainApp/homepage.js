//? Globals for pagination and filter usage
let currentFilter = "all";
let currPage = 1;
const projectsPerPage = 3;

//? Attribute for date validation
document.getElementById('projectDeadline').setAttribute('min', new Date(new Date().setMonth(new Date().getMonth() + 1)).toISOString().split('T')[0]);

//? Function to display all the projects that are currently hosted on DS_crowdfunding app
async function displayProjects() {
    const container = document.getElementById("projectsContainer");
    const pagination = document.getElementById("pagination");
    const template = document.getElementById("projectCardTemplate").content;
    const loadingSpinner = document.getElementById("loadingSpinner");
    const noProjectsMessage = document.getElementById("noProjectsMessage");

    loadingSpinner.style.display = "block";
    noProjectsMessage.style.display = "none";

    // TODO: How to fix the personal 403 forbidden error on network [Down in the listeners where I assign "currentFilter" I accidentaly assign "MyProjects" if the second button of filter is pressed]
    try {
        let endpoint;
        const username = sessionStorage.getItem("username"); // Logged-in user's username
        const isAdmin = sessionStorage.getItem("isAdmin") === "true"; // Checking if the user is an admin
        const isLoggedIn = Boolean(username); // Checking if a user is logged in

        // Determining the endpoint based on the filter type [Optimized]
        const endpoints = {
            myProjects: "http://localhost:8080/api/project/personal",
            Pending: isAdmin ? "http://localhost:8080/api/project/all/PENDING" : null,
            all: "http://localhost:8080/api/project/all"
        };
        endpoint = endpoints[currentFilter] || endpoints.all;


        // Fetching projects from the endpoint
        const response = await fetch(endpoint, {
            headers: {
                Authorization: `Bearer ${sessionStorage.getItem("token")}`
            }
        });

        if (!response.ok) throw new Error("Failed to fetch projects.");

        const projects = await response.json();

        // Applying filtering logic based on user role and filter type [Optimized]
        let filteredProjects = projects.filter(project => 
        isAdmin 
            ? (currentFilter === "Pending" ? project.status === "PENDING" : true)
            : isLoggedIn 
                ? (currentFilter === "myProjects" 
                    ? project.organizer.username === username 
                    : ["ACTIVE", "COMPLETED"].includes(project.status))
                : project.status === "ACTIVE"
        );

        // Hiding loading spinner
        loadingSpinner.style.display = "none";

        // If no projects are available, displaying "No projects" message
        if (filteredProjects.length === 0) {
            noProjectsMessage.style.display = "block";
            return;
        }

        // Calculating pagination
        const startIndex = (currPage - 1) * projectsPerPage;
        const endIndex = currPage * projectsPerPage;

        // Cleaning the container and pagination
        container.innerHTML = "";
        pagination.innerHTML = "";

        // Getting the projects to display on the current page
        const projectsToDisplay = filteredProjects.slice(startIndex, endIndex);

        // Populating the project cards
        projectsToDisplay.forEach(project => {
            const projectCard = template.cloneNode(true);

            // Populating the template with project data [Optimized]
            const imageElement = projectCard.querySelector(".card-img-top");
            imageElement.src = project.image 
                ? `data:image/png;base64,${project.image}` 
                : "./img/favicon.png";

            projectCard.querySelector(".card-img-top").alt = project.title;
            projectCard.querySelector(".card-title").textContent = project.title;

            // Adding status circle color based on the project's status [Optimized]
            const statusMap = {
                "ACTIVE": { color: "green", text: "Active" },
                "PENDING": { color: "orange", text: "Pending" },
                "STOPPED": { color: "red", text: "Deactivated" },
                "COMPLETED": { color: "blue", text: "Completed" }
            };

            const { color = "white", text = "Unknown" } = statusMap[project.status] || {};
            projectCard.querySelector(".status-circle").style.backgroundColor = color;
            projectCard.querySelector(".status-text").textContent = text;

            // Calculating and display the progress percentage
            const progressPercentage = (project.currentAmount / project.goalAmount) * 100;
            const progressBar = projectCard.querySelector(".progress-bar");
            progressBar.style.width = `${progressPercentage}%`;
            progressBar.setAttribute("aria-valuenow", progressPercentage);
            projectCard.querySelector(".progress-text").textContent = `${progressPercentage.toFixed(1)}% Funded`;

            // Adding "Created by" text
            const cardBody = projectCard.querySelector(".card-body");
            const createdBy = document.createElement("p");
            createdBy.classList.add("mt-3", "text-center");
            createdBy.style.fontWeight = "bold";
            createdBy.style.color = "var(--text-color)";
            createdBy.style.textDecoration = "underline";
            createdBy.textContent = `Created by: ${project.organizer.username}`;
            cardBody.appendChild(createdBy);

            // Attaching event to the button
            const button = projectCard.querySelector("button");
            button.textContent = "Check Project's Info";
            button.onclick = () => handleSupport(project.projectID, project.status);

            // Appending the project card to the container
            container.appendChild(projectCard);
        });

        // Initializing pagination
        paginationInitialization(pagination, filteredProjects.length);
    } catch (error) {
        console.error("Error displaying projects:", error);
        showAlert("Failed to load projects. Please try again later.", "danger", 3000);
    }
}


//? Pagination Function [Optimized]
function paginationInitialization(pagination, totalProjects) {
    const totalPages = Math.ceil(totalProjects / projectsPerPage);

    // Ensuring currPage stays within bounds
    currPage = Math.min(Math.max(currPage, 1), totalPages);

    const ul = document.createElement("ul");
    ul.classList.add("pagination");

    const createPageItem = (text, page, isDisabled = false, isActive = false) => {
        const item = document.createElement("li");
        item.classList.add("page-item");
        if (isDisabled) item.classList.add("disabled");
        if (isActive) item.classList.add("active");

        const link = document.createElement("a");
        link.classList.add("page-link");
        link.href = "#";
        link.textContent = text;
        link.onclick = (e) => {
            e.preventDefault();
            if (!isDisabled && page !== null) goToPage(page);
        };
        item.appendChild(link);
        return item;
    };

    // Addnig "Previous" button
    ul.appendChild(createPageItem("Previous", currPage - 1, currPage === 1));

    // Always showing the first page
    ul.appendChild(createPageItem(1, 1, false, currPage === 1));

    if (currPage > 3) {
        // Adding ellipses if there's a gap between the first page and the visible range
        ul.appendChild(createPageItem("...", null, true));
    }

    // Determining the range of middle pages to display
    const startPage = Math.max(2, currPage - 1);
    const endPage = Math.min(totalPages - 1, currPage + 1);

    for (let i = startPage; i <= endPage; i++) {
        ul.appendChild(createPageItem(i, i, false, i === currPage));
    }

    if (currPage < totalPages - 2) {
        // Adding ellipses if there's a gap between the visible range and the last page
        ul.appendChild(createPageItem("...", null, true));
    }

    // Always showing the last page
    if (totalPages > 1) {
        ul.appendChild(createPageItem(totalPages, totalPages, false, currPage === totalPages));
    }

    // Adding "Next" button
    ul.appendChild(createPageItem("Next", currPage + 1, currPage === totalPages));

    // Appending the pagination to the container
    pagination.appendChild(ul);
}

function goToPage(page) {
    currPage = page;
    displayProjects();

    // console.log("Navigation to page: ", currPage);
}

//? handlingSupport function for specific's project information page
function handleSupport(projectID, projectStatus) {
    window.location.href = `./ProjectDisplay/project_display.html?id=${projectID}&status=${projectStatus}`;
}

//? Handling logout
function handleLogout() {
    // Cleaning sessionsStorage on logout
    sessionStorage.clear();

    //Redirecting to homepage
    window.location.href = "./index.html";
}




//! --- Listeners ---

//? Filter functionality listeners
document.getElementById("filterAll").addEventListener("click", () => {
    currentFilter = "all";
    currPage = 1; // Resetting to the first page
    displayProjects();
});

document.getElementById("filterMyProjects").addEventListener("click", () => {
    const isAdmin = sessionStorage.getItem("isAdmin") === "true";
    
    currentFilter = isAdmin ? "Pending" : "myProjects";
    currPage = 1; // Resetting to the first page
    displayProjects();
});

//? DOM listener [Optimized]
document.addEventListener("DOMContentLoaded", function () {
    // Helper function to toggle element display
    const toggleDisplay = (element, show) => {
        if (element) element.style.display = show ? "block" : "none";
    };

    // Retrieving session data
    const accessToken = sessionStorage.getItem("token");
    const username = sessionStorage.getItem("username");
    const isAdmin = sessionStorage.getItem("isAdmin") === "true";

    // Filtering button and dropdown elements
    const filterButtonContainer = document.querySelector(".dropdown");
    const filterAll = document.getElementById("filterAll");
    const filterMyProjects = document.getElementById("filterMyProjects");

    // Setting filter visibility based on login state
    toggleDisplay(filterButtonContainer, !!username && !!accessToken);

    if (filterButtonContainer && username && accessToken) {
        // Updating filter options based on user role
        const isAdminFilter = isAdmin;
        filterAll.textContent = isAdminFilter ? "All Projects" : "All Projects";
        filterAll.id = isAdminFilter ? "filterActive" : "filterAll";
        filterMyProjects.textContent = isAdminFilter ? "Pending Verification" : "My Projects";
        filterMyProjects.id = isAdminFilter ? "filterPending" : "filterMyProjects";
    }

    // Adding eventlisteners to dropdown items
    document.querySelectorAll(".dropdown-item").forEach(item => {
        item.addEventListener("click", function () {
            // Updating active filter
            document.querySelectorAll(".dropdown-item").forEach(i => i.classList.remove("active"));
            this.classList.add("active");

            // Determining filter type and apply changes
            currentFilter = {
                filterAll: "all",
                filterActive: "all",
                filterMyProjects: "myProjects",
                filterPending: "Pending"
            }[this.id] || "all";

            currPage = 1; // Resetting page to the first
            displayProjects(); // Refreshing project display
        });
    });

    // New Project Form Submission
    const addNewForm = document.getElementById("addNewForm");
    if (addNewForm) {
        addNewForm.addEventListener("submit", async (event) => {
            event.preventDefault();

            if (!username || !accessToken) {
                showAlert("Please log in to create a new project.", "warning", 3000);
                return;
            }

            // Forming data validation
            const formElements = {
                imageFile: document.getElementById("projectImage").files[0],
                title: document.getElementById("projectTitle").value.trim(),
                description: document.getElementById("projectDescription").value.trim(),
                goalAmount: parseFloat(document.getElementById("projectGoal").value),
                deadlineForGoal: document.getElementById("projectDeadline").value
            };

            const { imageFile, title, description, goalAmount, deadlineForGoal } = formElements;

            if (!title || !description || !goalAmount || !deadlineForGoal) {
                showAlert("Please fill in all fields.", "warning", 3000);
                return;
            }

            if(goalAmount < 1000) {
                showAlert("The amount you provide as a goal must be at least 1000$.", "warning", 3000);
                return;
            }

            // Encode image as Base64 if provided
            let base64Image = null;
            if (imageFile) {
                if (imageFile.size > 5 * 1024 * 1024) {
                    showAlert("The selected image exceeds 5 MB. Please upload a smaller image.", "info", 3000);
                    return;
                }
                base64Image = await new Promise((resolve, reject) => {
                    const reader = new FileReader();
                    reader.onload = () => resolve(reader.result.split(",")[1]);
                    reader.onerror = () => reject(new Error("Failed to read image file"));
                    reader.readAsDataURL(imageFile);
                });
            }

            // Preparing project data and send request
            const newProject = {
                title,
                description,
                goalAmount,
                deadlineForGoal: `${deadlineForGoal}T00:00:00`,
                image: base64Image
            };

            try {
                const response = await fetch("http://localhost:8080/api/project/new", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${accessToken}`
                    },
                    body: JSON.stringify(newProject)
                });

                if (response.ok) {
                    showAlert("Project created successfully!", "success", 3000);

                    // Closing modal and reset form
                    bootstrap.Modal.getInstance(document.getElementById("addNewModal")).hide();
                    addNewForm.reset();
                    displayProjects();
                } else {
                    const error = await response.json();
                    showAlert(`Error creating project: ${error.message}`, "danger", 3000);
                }
            } catch {
                console.error("Error creating project:", error);
                showAlert("An error occurred while creating the project.", "danger", 3000);
            }
        });
    }

    // Navbar Content
    const headerRightContent = document.querySelector("#headerRightContent");
    const reportButton = document.querySelector(".custom-report-btn");

    if (accessToken && username) {
        // User logged in
        headerRightContent.innerHTML = `
            <span class="text-white me-3">Welcome, ${username}</span>
            <button class="btn btn-outline-light" onclick="handleLogout()">
                <i class="fa-solid fa-arrow-right-from-bracket"></i>
            </button>
        `;
        toggleDisplay(reportButton, isAdmin);
    } else {
        // User not logged in
        headerRightContent.innerHTML = `
            <button class="btn btn-outline-light" onclick="window.location.href='./LoginRegister/login_register.html'">
                Login / Register
            </button>
        `;
        toggleDisplay(reportButton, false);
    }

    // Initializing project display
    displayProjects();
});

//! ShowAlert logic for custom notifications
/**
 * Displays a Bootstrap alert in the specified container.
 * 
 * @param {string} message - The alert message to display.
 * @param {string} type - The alert type ('success', 'danger', 'warning', 'info', etc.).
 * @param {number} duration - Time in milliseconds to automatically dismiss the alert.
 */
function showAlert(message, type = 'info', duration = 5000) {
    const alertContainer = document.getElementById('alertContainer');
    if (!alertContainer) {
        console.error("Alert container not found. Add an element with id='alertContainer' in your HTML.");
        return;
    }

    // Creating the alert div
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.role = 'alert';

    // Setting the alert message
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;

    // Appending the alert to the container
    alertContainer.appendChild(alertDiv);

    // Automatically dismissing the alert after the specified duration (if provided)
    if (duration > 0) {
        setTimeout(() => {
            const alert = bootstrap.Alert.getOrCreateInstance(alertDiv);
            alert.close();
        }, duration);
    }
}
