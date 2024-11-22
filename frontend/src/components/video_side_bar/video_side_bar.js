import './video_side_bar.css'
import Video from '../video_card/video_card';
function Video_side_bar({ videoList ,toggleVideoView}) {

    return (

        <div className='videos-bar'>
            {
                videoList.map((video) =>
                    <Video key={video._id} video={video} toggleView={toggleVideoView} />
                )
            }
        </div>
    );
}
export default Video_side_bar