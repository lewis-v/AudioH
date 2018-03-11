# AudioH
Android 语音录制及播放的处理

## 实现功能
1.实现Android语音录制的功能（目前封装只支持AMR格式）；

2.实现Android语音播放功能（听筒和扬声器播放）；

3.实现语音播放中，网络语音的缓存；

4.提供获取缓存大小和清理缓存的方法；
 
## 使用方法
### 1.导入依赖
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  

	dependencies {
	        compile 'com.github.lewis-v:AudioH:1.0.0'
	}
```
### 2.录音
```
 RecoderBuilder builder = new DefaultRecoderBuilder()
                .setMAX_LENGTH(60*1000)//最大录音60秒
                .setMIN_LENGTH(500)//最小录音0.5秒
                .setSAMPLEING_RATE(200)//录音监听回调间隔，200ms回调一次
                .setSaveFolderPath(Environment.getExternalStorageDirectory()+"/record/");
        AudioRecoderManager.getInstance()//获取单例
                .setAudioRecoderData(builder.create())//设置自定义配置，已有默认的配置，可不用配置
                .setAudioRecoderListener(new AudioRecoderListener() {//设置监听
                    @Override
                    public void onStart() {//开始播放

                    }

                    @Override
                    public void onStop(AudioRecoderData audioRecoderData) {//停止/结束播放
                        Log.e(TAG,audioRecoderData.getFilePath());
                    }

                    @Override
                    public void onFail(Exception e, String msg) {//录音时出现的错误
                        e.printStackTrace();
                    }

                    @Override
                    public void onCancel() {//录音取消

                    }

                    @Override
                    public void onSoundSize(int level) {//录音时声音大小的回调，分贝
                        Log.e(TAG,"level:"+level);
                    }
                });
                
  AudioRecoderManager.getInstance().start(this);//开始录音
  AudioRecoderManager.getInstance().stop(this);//结束录音
```
 
### 3.播放
```
AudioPlayManager.getInstance().init(this)//初始化播放
                .setPlayListener(new AudioPlayListener() {//设置播放监听
            @Override
            public void onPlay(String audioPath) {//开始播放

            }

            @Override
            public void onProgress(int progress, int maxSize) {//播放进度（未实现）

            }

            @Override
            public void onPause() {//播放暂停（未实现）

            }

            @Override
            public void onStop() {//停止播放

            }

            @Override
            public void onFail(Exception e, String msg) {//播放时出错

            }
        });
        
   
   AudioPlayManager.getInstance().play("http://39.108.236.30:47423/audio/UP699813445282012.amr"
                                ,this, AudioPlayMode.MEGAPHONE);//播放音频,放心这段音频是我朋友的声音。。嘻嘻
```
 
### 4.缓存获取及清理
缓存清理的方法未做线程处理，是同步进行的方法，需要开发者自己开子线程调用
```
 Log.e(TAG, String.valueOf(AudioPlayManager.getInstance().getCacheSize(this)));//获取缓存大小
 Log.e(TAG, String.valueOf(AudioPlayManager.getInstance().clearCache(this)));//清除缓存，并返回清除的大小
 
```
