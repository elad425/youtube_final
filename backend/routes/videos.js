const express = require('express');
const router = express.Router();
const { getVideos,getVideo,updateViews} =require ('../controllers/videoController')
const {createComment,getComments,updateComment,deleteComment} =require ('../controllers/commentController')
const { authenticateToken } = require('../middleware/authMiddleware.js');

router.post('/comment/',authenticateToken,createComment)
router.get('/comment/:id',getComments)
router.post('/comment/:id',authenticateToken,updateComment)
router.patch('/comment/:id',authenticateToken,updateComment)
router.delete('/comment/:id',authenticateToken,deleteComment)
router.patch('/:pid/views/:id', updateViews);
router.put('/:pid/views/:id', updateViews);

router.get('/', getVideos);
router.get('/:pid', getVideo);
module.exports = router;
