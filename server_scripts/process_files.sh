inotifywait -m ~/Desktop/video_data/unprocessed -e create -e moved_to |
    while read dir action file; do
        echo "The file '$file' appeared in directory '$dir' via '$action'"
	python ./scripts/demo_inference.py \
	 --cfg ./configs/coco/resnet/256x192_res152_lr1e-3_1x-duc.yaml \
	--checkpoint ./pretrained_models/fast_421_res152_256x192.pth --video ~/Desktop/video_data/unprocessed/$file --save_video \
	--outdir ~/Desktop/video_data/processed/${file%%.*} \
	--vis
	
	python ~/Desktop/video_data/scripts/velocity_calculator.py ~/Desktop/video_data/processed/${file%%.*}
    done
