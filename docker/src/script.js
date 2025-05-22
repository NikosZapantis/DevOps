document.getElementById('emailForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const to = document.getElementById('to').value;
    const subject = document.getElementById('subject').value;
    const body = document.getElementById('body').value;

    const response = await fetch('/send-email', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ to, subject, body })
    });

    const result = await response.text();
    document.getElementById('result').textContent = result;
});