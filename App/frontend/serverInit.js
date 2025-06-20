const express = require('express');
const nodemailer = require('nodemailer');
const bodyParser = require('body-parser');
const cors = require('cors');

const app = express();
const port = 7000;

// App is using static the current directory
app.use(cors());
app.use(express.static(__dirname));
app.use(bodyParser.json());

// Nodemailer transporter configuration for mailhog 
const transp = nodemailer.createTransport({
    host: 'mailhog',
    port: 1025,
    secure: false,
    tls: {
        rejectUnauthorized: false
    }
});

// Endpoint to send email
app.post('/send-email', async (req, res) => {
    const { to, subject, text } = req.body;

    try {
        await transp.sendMail({
            from: '"CrowdFunding App" <no-reply@crowdfundsupport.com>',
            to: to,
            subject: subject,
            text: text,
        });

        res.status(200).json({ message: "Email sent successfully" });
    } catch (error) {
        console.error('Error sending email:', error);
        res.status(500).json({ message: "Failed to send email", error: error.message });
    }
});

app.listen(port, '0.0.0.0', () => {
    console.log(`Server is running on http://VM_IP:${port}`);
});
