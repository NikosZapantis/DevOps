//? Function to get project's ID from url
function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

//? Function to handle the "Report Analytics" redirection
function redirectToReports(projectId) {
    if (!projectId) {
        showAlert("Project ID not found.", "info", 3000);
        return;
    }
    // Redirecting to the reports page with the project ID in the URL
    window.location.href = `../Reports/reports.html?id=${projectId}`;
}

//? Function to display the project's info [optimized]
async function displayProjectInfo() {
    const projectId = getQueryParam('id');

    if (!projectId) {
        document.getElementById('project-info').innerHTML = `<p>Project not found!</p>`;
        return;
    }

    try {
        // Fetching project details from the backend
        const response = await fetch(`http://localhost:8080/api/project/${projectId}`);
        if (!response.ok) throw new Error('Failed to fetch project details.');

        const project = await response.json();

        // Storing funding data in localStorage so I dont have to call 2nd time the endpoint
        localStorage.setItem(`funds_${projectId}`, JSON.stringify(project.funds || []));

        const statusMap = {
            "ACTIVE": { color: "green", text: "Active" },
            "PENDING": { color: "orange", text: "Pending" },
            "STOPPED": { color: "red", text: "Deactivated" },
            "COMPLETED": { color: "blue", text: "Completed" }
        };

        const { color = "gray", text = "Unknown" } = statusMap[project.status] || {};
        const progressPercentage = Math.min(
            (project.currentAmount / project.goalAmount) * 100
        ).toFixed(2);

        const deadline = new Date(project.deadlineForGoal).toLocaleDateString();

        // Setting project details
        const projectImage = document.getElementById('project-image');
        projectImage.src = project.image ? `data:image/png;base64,${project.image}` : "../img/favicon.png";
        document.getElementById('project-image').alt = project.title;
        document.getElementById('project-title').textContent = project.title;
        document.getElementById('project-description').textContent = project.description;
        document.getElementById('project-deadline').textContent = deadline;
        document.getElementById('project-collected').textContent = project.currentAmount;
        document.getElementById('project-goal').textContent = project.goalAmount;
        document.getElementById('project-progress-percentage').textContent = progressPercentage;
        document.getElementById('project-creator').textContent = `${project.organizer.username}`;

        const progressBar = document.getElementById('project-progress-bar');
        progressBar.style.width = `${progressPercentage}%`;
        progressBar.setAttribute('aria-valuenow', progressPercentage);

        // Status circle
        const statusCircle = document.createElement('span');
        statusCircle.classList.add('status-circle');
        statusCircle.style.backgroundColor = color;

        const titleContainer = document.getElementById('project-title');
        titleContainer.insertAdjacentElement('beforebegin', statusCircle);

        // Admin actions and buttons
        const isAdmin = sessionStorage.getItem('isAdmin') === 'true';
        const adminActions = document.getElementById('adminActions');
        const approveBtn = document.getElementById('approveProjectBtn');
        const rejectBtn = document.getElementById('rejectProjectBtn');
        const adminReportBtn = document.getElementById('adminReportBtn');
        const reportIssueBtn = document.getElementById('reportIssueBtn');
        //? Show "Activate/Deactivate" button only for Admins & when status is ACTIVE or STOPPED
        const toggleStatusBtn = document.getElementById("toggleStatusBtn");

        if (isAdmin && (project.status === "ACTIVE" || project.status === "STOPPED")) {
            toggleStatusBtn.style.display = "block"; 
            toggleStatusBtn.textContent = project.status === "ACTIVE" ? "Deactivate" : "Activate";

            toggleStatusBtn.style.backgroundColor = project.status === "ACTIVE"
            ? "var(--secondary-color)"
            : "var(--primary-color)";

            // Adding event listener to toggle project status
            toggleStatusBtn.onclick = () => toggleProjectStatus(projectId, project.status);
        } else {
            toggleStatusBtn.style.display = "none";
        }

        // Showing/hiding admin actions based on status
        if (isAdmin && project.status === 'PENDING') {
            adminActions.style.display = 'block';
            approveBtn.style.display = 'block';
            rejectBtn.style.display = 'block';
            adminReportBtn.style.display = 'none';
        } else if (isAdmin && project.status !== 'PENDING') {
            adminActions.style.display = 'none';
            approveBtn.style.display = 'none';
            rejectBtn.style.display = 'none';
            adminReportBtn.style.display = 'block';
        } else {
            adminActions.style.display = 'none';
            approveBtn.style.display = 'none';
            rejectBtn.style.display = 'none';
            adminReportBtn.style.display = 'none';
        }

        // Showing/hiding "Fund Project" button
        const isLoggedIn = sessionStorage.getItem('token') || sessionStorage.getItem('username');
        const fundProjectBtn = document.getElementById('fundProjectBtn');
        fundProjectBtn.style.display = project.status === "ACTIVE" && isLoggedIn ? "block" : "none";

        // Showing/hiding report issue button
        reportIssueBtn.style.display = project.status === "ACTIVE" ? "block" : "none";

    } catch (error) {
        console.error('Error fetching project details:', error);
        document.getElementById('project-info').innerHTML = `<p>Failed to load project details. Please try again later.</p>`;
    }
}

//? Function to approve a project
async function approveProject(projectId) {
    try {
        const response = await fetch(`http://localhost:8080/api/project/${projectId}/update-status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${sessionStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            showAlert('Project approved successfully.', "success", 3000);
            window.location.reload(); // Reloading the page to reflect the status change
        } else {
            const error = await response.json();
            showAlert(`Failed to approve project: ${error.message}`, "danger", 3000);
        }
    } catch (error) {
        console.error('Error approving project:', error);
        showAlert('An error occurred while approving the project.', "danger", 3000);
    }
}

//? Function to reject a project
async function rejectProject(projectId) {

    try {
        const response = await fetch(`http://localhost:8080/api/project/${projectId}/delete`, {
            method: 'DELETE',
            headers: {
                Authorization: `Bearer ${sessionStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            showAlert('Project rejected and deleted successfully.', "success", 3000);
            window.location.href = '../index.html'; // Redirecting to the homepage
        } else {
            const error = await response.json();
            showAlert(`Failed to reject project: ${error.message}`, "danger", 3000);
        }
    } catch (error) {
        console.error('Error rejecting project:', error);
        showAlert('An error occurred while rejecting the project.', "danger", 3000);
    }
}

//? Function to toggle project status
async function toggleProjectStatus(projectId, currentStatus) {
    try {
        // Calling the update-status endpoint
        const response = await fetch(`http://localhost:8080/api/project/${projectId}/update-status`, {
            method: 'PUT',
            headers: {
                Authorization: `Bearer ${sessionStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            // Reloading the project info after successful status change
            showAlert(`Project status updated successfully.`, "success", 3000);
            displayProjectInfo();
        } else {
            const error = await response.json();
            showAlert(`Failed to update project status: ${error.message}`, "danger", 3000);
        }
    } catch (error) {
        console.error("Error toggling project status:", error);
        showAlert("An error occurred while updating the project status.", "danger", 3000);
    }
}

//? Function to handle funding a project
async function fundProject(projectId, amount, message) {
    if (!amount || isNaN(amount) || amount < 1) {
        showAlert('Please provide a valid funding amount.', "info", 3000);
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/fund/${projectId}/new`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${sessionStorage.getItem('token')}`,
            },
            body: JSON.stringify({
                amount: parseFloat(amount),
                message: message.trim(),
                public: true
            }),
        });

        if (response.ok) {
            showAlert('Funding submitted successfully!', "success", 3000);
            const modal = bootstrap.Modal.getInstance(document.getElementById('fundProjectModal'));
            modal.hide(); // Closing the modal
        } else {
            const error = await response.json();
            showAlert(`Failed to fund project: ${error.message}`, "danger", 3000);
        }
    } catch (error) {
        console.error('Error funding project:', error);
        showAlert('An error occurred while submitting the funding.', "danger", 3000);
    }
}

//? Function to submit a report
async function submitReport(projectId, title, description) {
    try {
        const response = await fetch(`http://localhost:8080/api/report/${projectId}/new`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${sessionStorage.getItem('token')}`
            },
            body: JSON.stringify({
                title,
                description
            })
        });

        if (response.ok) {
            showAlert('Report submitted successfully.', "success", 3000);
            const modal = bootstrap.Modal.getInstance(document.getElementById('reportIssueModal'));
            modal.hide();
        } else {
            const error = await response.json();
            showAlert(`Failed to submit report: ${error.message}`, "danger", 3000);
        }
    } catch (error) {
        console.error('Error submitting report:', error);
        showAlert('An error occurred while submitting the report.', "danger", 3000);
    }
}

//? DOM listener
document.addEventListener('DOMContentLoaded', () => {
    displayProjectInfo();

    // Funding Project Button and Form
    const fundProjectBtn = document.getElementById('fundProjectBtn');
    const fundProjectForm = document.getElementById('fundProjectForm');
    const fundProjectModal = new bootstrap.Modal(document.getElementById('fundProjectModal'));

    // Showing modal on button click
    fundProjectBtn.addEventListener('click', () => {
        fundProjectModal.show();
    });

    // Handling form submission
    fundProjectForm.addEventListener('submit', (event) => {
        event.preventDefault();

        const projectId = getQueryParam('id');
        const amount = document.getElementById('fundAmount').value;
        const message = document.getElementById('fundMessage').value;

        if (!projectId) {
            showAlert('Project ID not found.', "warning", 3000);
            return;
        }

        fundProject(projectId, amount, message); // Calling funding function
    });

    // Admin action buttons
    const approveBtn = document.getElementById('approveProjectBtn');
    const rejectBtn = document.getElementById('rejectProjectBtn');

    // Approve button click handler
    approveBtn.addEventListener('click', () => {
        const projectId = getQueryParam('id');
        if (projectId) {
            approveProject(projectId); // Calling the approveProject function
        } else {
            showAlert('Project ID not found.', "warning", 3000);
        }
    });

    // Reject button click handler
    rejectBtn.addEventListener('click', () => {
        const projectId = getQueryParam('id');
        if (projectId) {
            rejectProject(projectId); // Calling the rejectProject function
        } else {
            showAlert('Project ID not found.', "warning", 3000);
        }
    });

    // Tooltip functionality for report issue button
    const reportBtn = document.getElementById('reportIssueBtn');
    const tooltip = document.getElementById('reportIssueTooltip');
    const reportAnalyticsBtn = document.getElementById('adminReportBtn');

    // Report Analytics button handler
    reportAnalyticsBtn.addEventListener("click", () => {
        const projectId = getQueryParam("id");
        redirectToReports(projectId);
    });

    // Displaying tooltip on hover
    reportBtn.addEventListener('mouseenter', (event) => {
        tooltip.classList.remove('d-none');
        tooltip.style.left = `${event.pageX}px`;
        tooltip.style.top = `${event.pageY}px`;
    });

    // Updating tooltip position as the mouse moves
    reportBtn.addEventListener('mousemove', (event) => {
        const offsetX = 15;
        const offsetY = -20;

        tooltip.style.left = `${event.pageX + offsetX}px`;
        tooltip.style.top = `${event.pageY + offsetY}px`;
    });

    // Hiding tooltip when mouse leaves the button
    reportBtn.addEventListener('mouseleave', () => {
        tooltip.classList.add('d-none');
    });

    // Report Issue Form Submission
    const reportForm = document.getElementById('reportIssueForm');
    reportForm.addEventListener('submit', (event) => {
        event.preventDefault();

        const projectId = getQueryParam('id');
        const issueTitle = document.getElementById('issueTitle').value;
        const issueDescription = document.getElementById('issueDescription').value;

        if(!projectId) {
            showAlert('Project ID not found.', "warning", 3000);
            return;
        }

        submitReport(projectId, issueTitle, issueDescription);
        modal.hide();
    });

    // Chat functionality
    const chatToggleBtn = document.getElementById("chatToggleBtn");
    const chatPanel = document.getElementById("chatPanel");
    const closeChatBtn = document.getElementById("closeChatBtn");
    const chatMessagesContainer = document.getElementById("chatMessages");

    // Listener for Toggling Chat Panel
    chatToggleBtn.addEventListener("click", () => {
        chatPanel.style.display = chatPanel.style.display === "flex" ? "none" : "flex";
        if (chatPanel.style.display === "flex") {
            loadFundMessages();
        }
    });

    // Closing Chat Panel
    closeChatBtn.addEventListener("click", () => {
        chatPanel.style.display = "none";
    });

    //? Function to fetch and display funders' messages
    async function loadFundMessages() {
        const projectId = getQueryParam("id");
        if (!projectId) {
            console.warn("Project ID is missing from URL parameters");
            return;
        }

        // Retrieving funding data from localStorage
        const storedFunds = JSON.parse(localStorage.getItem(`funds_${projectId}`)) || [];

        const chatMessagesContainer = document.getElementById("chatMessages");
        chatMessagesContainer.innerHTML = ""; // Cleaning previous messages

        if (storedFunds.length === 0) {
            chatMessagesContainer.innerHTML = "<p>No donations yet.</p>";
            return;
        }

        storedFunds.forEach(fund => {
            const messageElement = document.createElement("div");
            messageElement.classList.add("chat-message");
            messageElement.innerHTML = `
                <strong>${fund.user.username}</strong> has donated <strong>$${fund.amount}</strong>!<br>
                <i>"${fund.message}"</i>
            `;
            chatMessagesContainer.appendChild(messageElement);
        });
    }
});
