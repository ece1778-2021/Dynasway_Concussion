inotifywait -m /home/saman/Desktop/Git/pose_tracker_web/posetrackerweb/main/videos -e create -e moved_to |
    while read dir action file; do
        echo "The file '$file' appeared in directory '$dir' via '$action'"
	python ./scripts/demo_inference.py \
	 --cfg ./configs/coco/resnet/256x192_res152_lr1e-3_1x-duc.yaml \
	--checkpoint ./pretrained_models/fast_421_res152_256x192.pth --video /home/saman/Desktop/Git/pose_tracker_web/posetrackerweb/main/videos/$file --save_video \
	--outdir /home/saman/Desktop/Git/pose_tracker_web/posetrackerweb/main/processed_videos/${file%%.*} \
	--vis
	
	python ~/Desktop/video_data/scripts/velocity_calculator_http.py /home/saman/Desktop/Git/pose_tracker_web/posetrackerweb/main/processed_videos/${file%%.*} 
	
    done
