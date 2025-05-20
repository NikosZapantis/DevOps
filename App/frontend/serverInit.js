const express = require('express');
const cors = require('cors');

const app = express();

// App is using static the current directory
app.use(express.static(__dirname));

// Server running on localhost port 7000
const port = 7000;
app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});