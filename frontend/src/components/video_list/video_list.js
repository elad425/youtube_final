import './video_list.css';
import Video from '../video_card/video_card';
import Videos from '../../data/video_data.json'
import { useState } from 'react';
function Video_list() {
  const [videoList, setVideoList] = useState(Videos)
  // const addVideo = (title,channel, description, thumbnail, video,channel_icon) => {
  //   const new_video = {
  //     "id": 11,
  //     "title": {title},
  //     "channel": {channel},
  //     "views": 0,
  //     "date": "today",
  //     "thumbnail": {thumbnail},
  //     "channel_icon": {channel_icon}
  //   }
  //   setVideoList([...videoList,new_video])
  // }

  return (

    <div className='videos-container-grid'>
      {
        videoList.map((video) =>
          <Video {...video} />
        )
      }
    </div>

  );
}

export default Video_list;
