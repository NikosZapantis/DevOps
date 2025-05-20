// Function to get project's ID from the URL
function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

// Function to fetch reports for the specific project
async function fetchReports(projectId) {
    const accordion = document.getElementById("reportsAccordion");

    if (!projectId) {
        accordion.innerHTML = `<p class="text-center text-danger">Project ID not found!</p>`;
        return;
    }

    try {
        // Calling the API to get the reports for the given project ID
        const response = await fetch(`http://localhost:8080/api/report/${projectId}/all`, {
            headers: {
                Authorization: `Bearer ${sessionStorage.getItem("token")}`, // Including the token for authentication
            },
        });

        if (!response.ok) throw new Error("Failed to fetch reports.");

        const reports = await response.json();

        if (reports.length === 0) {
            accordion.innerHTML = `<p class="text-center">No reports found for this project.</p>`;
            return;
        }

        // Looping through the reports and create the accordion items
        reports.forEach((report) => {
            const item = document.createElement("div");
            item.classList.add("accordion-item");

            item.innerHTML = `
                <h2 class="accordion-header" id="heading${report.reportID}">
                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${report.reportID}" aria-expanded="false" aria-controls="collapse${report.reportID}">
                        <strong>${report.title}&nbsp;</strong> - Reported By: ${report.user.username}
                    </button>
                </h2>
                <div id="collapse${report.reportID}" class="accordion-collapse collapse" aria-labelledby="heading${report.reportID}" data-bs-parent="#reportsAccordion">
                    <div class="accordion-body">
                        ${report.description}
                    </div>
                </div>
            `;
            accordion.appendChild(item);
        });
    } catch (error) {
        console.error("Error fetching reports:", error);
        accordion.innerHTML = `<p class="text-center text-danger">Failed to load reports. Please try again later.</p>`;
    }
}

// Fetching reports when the page loads
document.addEventListener("DOMContentLoaded", () => {
    const projectId = getQueryParam("id");
    fetchReports(projectId);
});
