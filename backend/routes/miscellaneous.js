const express = require('express');
const { authenticateToken } = require('../middleware/authMiddleware');
const { deleteFileFromServer } = require('../controllers/miscellaneousController')

const router = express.Router();

router.delete('/deleteFile', authenticateToken, deleteFileFromServer);

module.exports = router;