const express = require('express');
const router = express.Router();
const {getVideo,deleteVideo,updateVideo} =require ('../controllers/videoController')
const { getAllUsers,createUser,getUser,updateUsername, deleteUser, getUserVideos,createUserVideo,loginUser,checkEmailExists} =require ('../controllers/userController')
const { authenticateToken } = require('../middleware/authMiddleware.js');
router.post('/check-email',checkEmailExists)
router.get('/',authenticateToken,getAllUsers)
router.post('/',createUser)
router.post('/tokens',loginUser)
router.post('/login',loginUser)
router.get('/:id', getUser);
router.patch('/:id',authenticateToken,updateUsername);
router.put('/:id',authenticateToken, updateUsername);
router.delete('/:id',authenticateToken,deleteUser);


router.get('/:id/videos',getUserVideos)
router.post('/:id/videos',createUserVideo)

router.get('/:id/videos/:pid',getVideo)
router.put('/:id/videos/:pid',authenticateToken,updateVideo)
router.patch('/:id/videos/:pid',authenticateToken,updateVideo)
router.delete('/:id/videos/:pid',authenticateToken,deleteVideo)

module.exports = router;
