const express = require('express');
const nodemailer = require('nodemailer');
const bodyParser = require('body-parser');
const path = require('path');

const app = express();
const PORT = 3000;

app.use(bodyParser.json());
app.use(express.static(__dirname));

// Nodemailer transport (using MailHog)
const transporter = nodemailer.createTransport({
  host: 'localhost',
  port: 1025,
  secure: false,
});

app.post('/send-email', async (req, res) => {
  const { to, subject, body } = req.body;

  try {
    await transporter.sendMail({
      from: '"Nikos Tester" <test@example.com>',
      to,
      subject,
      text: body,
    });
    res.send('Email sent successfully!');
  } catch (error) {
    console.error(error);
    res.status(500).send('Failed to send email.');
  }
});

app.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
});
