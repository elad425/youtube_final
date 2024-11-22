const express = require('express');
const { authenticateToken } = require('../middleware/authMiddleware');
const { loginUser } = require('../controllers/userController')
const { validate_token } = require('../controllers/tokenController')

const router = express.Router();

router.get('/', authenticateToken, validate_token);
router.post('/', loginUser)

module.exports = router;