const express = require('express');
const { authenticateToken } = require('../middleware/authMiddleware');
const { likeVideo,dislikeVideo,likeComment,dislikeComment,getVideoLikeStatus } = require('../controllers/likeController')

const router = express.Router();

router.post('/like/video/:id',authenticateToken, likeVideo );
router.post('/dislike/video/:id',authenticateToken, dislikeVideo)
router.post('/like/comment/:id',authenticateToken, likeComment );
router.post('/dislike/comment/:id',authenticateToken, dislikeComment)
router.get('/status/:id',authenticateToken,getVideoLikeStatus)
module.exports = router;