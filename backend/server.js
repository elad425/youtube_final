require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const multer = require('multer');
const videoRoutes = require('./routes/videos');
const usersRoutes = require('./routes/users');
const tokenRoutes = require('./routes/tokens');
const miscRoutes = require('./routes/miscellaneous');
const likeRoutes = require('./routes/likes')
const { sendInitialData } = require('./controllers/miscellaneousController');

const app = express();

app.use(express.json({ limit: "200mb" }));
app.use(express.urlencoded({ extended: true, limit: "200mb" }));
app.use((req, res, next) => {
    console.log(req.path, req.method);
    next();
});

const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'uploads/');
    },
    filename: (req, file, cb) => {
        cb(null, `${Date.now()}-${file.originalname}`);
    }
});
const upload = multer({ storage });
app.use('/uploads', express.static('uploads'));
app.use('/videos', express.static('videos'));
app.use('/thumbnails', express.static('thumbnails'));


// Upload route
app.post('/upload', upload.single('file'), (req, res) => {
    if (!req.file) {
        return res.status(400).send('No file uploaded.');
    }
    res.status(200).send({ filename: req.file.filename, path: req.file.path });
});

app.use('/api/videos', videoRoutes);
app.use('/api/users', usersRoutes);
app.use('/api/tokens', tokenRoutes);
app.use('/api/misc', miscRoutes);
app.use('/api/likes', likeRoutes);


mongoose.connect(process.env.MONGO_URI)
    .then(() => {
        app.listen(process.env.PORT, () => {
            console.log("Connected to DB and listening on port", process.env.PORT);
            sendInitialData();
        });
    })
    .catch((error) => {
        console.log(error);
    });
