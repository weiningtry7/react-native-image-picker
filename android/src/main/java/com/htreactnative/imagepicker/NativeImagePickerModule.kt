package com.htreactnative.imagepicker

import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.graphics.Color
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.facebook.react.module.annotations.ReactModule
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.InjectResourceSource
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.style.BottomNavBarStyle
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.style.SelectMainStyle
import com.luck.picture.lib.style.TitleBarStyle
import com.luck.picture.lib.utils.SdkVersionUtils
import java.io.File
import java.util.ArrayList


@ReactModule(name = NativeImagePickerModule.NAME)
class NativeImagePickerModule(reactContext: ReactApplicationContext) :
  NativeImagePickerSpec(reactContext) {
    val imageEngine = GlideEngine.createGlideEngine()
    var pickerOptions: ReadableMap? = null
  override fun getName(): String {
    return NAME
  }

    override fun asyncShowImagePicker(
        options: ReadableMap?,
        promise: Promise?
    ) {
        pickerOptions = options;
        val imageCount = options?.getInt("imageCount") ?: 1
        val isCamera = options?.getBoolean("isCamera") ?: false
        PictureSelector.create(reactApplicationContext.currentActivity)
            .openGallery(SelectMimeType.ofImage())
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            .setSelectionMode(
                if (imageCount > 1) SelectModeConfig.MULTIPLE else SelectModeConfig.SINGLE
            )
            .setMaxSelectNum(imageCount)
            .isDisplayCamera(isCamera)
            .setSelectorUIStyle(getImagePickerStyle())
            .setInjectLayoutResourceListener { _, resourceSource ->
                when (resourceSource) {
                    InjectResourceSource.MAIN_SELECTOR_LAYOUT_RESOURCE ->
                        R.layout.image_picker_fragment_selector_no_preview
                    else -> InjectResourceSource.DEFAULT_LAYOUT_RESOURCE
                }
            }
            .setImageEngine(imageEngine)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(localMediaList: ArrayList<LocalMedia?>?) {
                    var data = WritableNativeArray()
                    localMediaList?.forEach { media -> media?.let {
                        data.pushMap(getImageResult(media))
                    } }
                    promise?.resolve(data)
                }

                override fun onCancel() {
                    promise?.resolve(WritableNativeArray())
                }

            })
    }

    override fun asyncShowVideoPicker(
        options: ReadableMap?,
        promise: Promise?
    ) {
        pickerOptions = options;
        val videoCount = options?.getInt("videoCount") ?: 1
        val isCamera = options?.getBoolean("isCamera") ?: false
        PictureSelector.create(reactApplicationContext.currentActivity)
            .openGallery(SelectMimeType.ofVideo())
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setMaxSelectNum(1)
            .isDisplayCamera(isCamera)
            .isAutoVideoPlay(true)
            .setSelectorUIStyle(getNoCountStyle())
            .setInjectLayoutResourceListener { _, resourceSource ->
                when (resourceSource) {
                    InjectResourceSource.MAIN_SELECTOR_LAYOUT_RESOURCE ->
                        R.layout.image_picker_fragment_selector
                    InjectResourceSource.MAIN_ITEM_VIDEO_LAYOUT_RESOURCE ->
                        R.layout.image_picker_item_grid_video
                    else -> InjectResourceSource.DEFAULT_LAYOUT_RESOURCE
                }
            }
            .setImageEngine(imageEngine)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(localMediaList: ArrayList<LocalMedia?>?) {
                    var data = WritableNativeArray()
                    localMediaList?.forEach { media -> media?.let {
                        data.pushMap(getVideoResult(media))
                    } }
                    promise?.resolve(data)
                }

                override fun onCancel() {
                    promise?.reject("", "取消")
                }

            })
    }

    private fun getNoCountStyle(): PictureSelectorStyle {
        return PictureSelectorStyle().apply {
            selectMainStyle = SelectMainStyle().apply {
                // 隐藏选择后的数字/角标索引
                isSelectNumberStyle = false
                isPreviewSelectNumberStyle = false
                // 预览页点击“下一步”时，自动选择当前媒体并返回
                isCompleteSelectRelativeTop = true
                // 隐藏列表页和预览页的勾选按钮
                selectBackground = android.R.color.transparent
                previewSelectBackground = android.R.color.transparent
                selectText = "下一步"
                selectTextColor = Color.WHITE
                selectNormalText = "下一步"
                selectNormalTextColor = Color.WHITE
            }
            bottomBarStyle = BottomNavBarStyle().apply {
                isCompleteCountTips = false
                bottomPreviewNormalText = "" // 未选中任何文件时的文字设为空
                bottomPreviewSelectText = "" // 选中文件后的文字设为空

                // （可选保障）为了防止有默认的点击态背景，你可以顺手把它的颜色也设为透明
                bottomPreviewNormalTextColor = Color.WHITE
                bottomPreviewSelectTextColor = Color.WHITE
            }

            titleBarStyle = TitleBarStyle().apply {

            }

        }
    }

    private fun getImagePickerStyle(): PictureSelectorStyle {
        return PictureSelectorStyle().apply {
            selectMainStyle = SelectMainStyle().apply {
                // 保留多选勾选控件，但不显示选中序号
                isSelectNumberStyle = false
                isPreviewSelectNumberStyle = false
                // 将真正的完成按钮放到右上角
                isCompleteSelectRelativeTop = true
                // 预览页的选中按钮放到底部，避免与右上角“下一步”重合
                isPreviewSelectRelativeBottom = true
                selectText = "下一步"
                selectTextColor = Color.WHITE
                selectNormalText = "下一步"
                selectNormalTextColor = Color.WHITE
            }
            bottomBarStyle = BottomNavBarStyle().apply {
                isCompleteCountTips = false
            }
            titleBarStyle = TitleBarStyle().apply {
                // 左上角箭头已可返回，隐藏右上角原“取消”按钮
                isHideCancelButton = true
            }
        }
    }

    private fun getVideoResult(media: LocalMedia): WritableMap {
        val videoMap: WritableMap = WritableNativeMap()
        var path: String = media.path
        val isAndroidQ = SdkVersionUtils.isQ()
        val isAndroidR = SdkVersionUtils.isR()
        if (isAndroidQ) {
            path = media.availablePath
        }
        if (isAndroidR) {
            path = media.realPath;
        }
        videoMap.putString("path", "file://$path")
        videoMap.putString("mime", media.mimeType ?: "video/mp4")
        videoMap.putDouble("width", media.width.toDouble())
        videoMap.putDouble("height", media.height.toDouble())
        videoMap.putDouble("duration", media.duration * 1000.0)
        videoMap.putLong("size", media.size)
        videoMap.putString("filename", media.fileName)
        return videoMap
    }

    private fun getImageResult(media: LocalMedia): WritableMap {
        val imageMap: WritableMap = WritableNativeMap()
        var path: String = media.path

        if (media.isCompressed || media.isCut) {
            path = media.compressPath
        }

        if (media.isCut) {
            path = media.cutPath
        }

        val isAndroidQ = SdkVersionUtils.isQ()
        val isAndroidR = SdkVersionUtils.isR()
        if (isAndroidQ) {
            path = media.availablePath
        }
        if (isAndroidR) {
            path = media.realPath;
        }

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        imageMap.putDouble("width", options.outWidth.toDouble())
        imageMap.putDouble("height", options.outHeight.toDouble())
        imageMap.putString("type", "image")
        imageMap.putString("path", path)
        imageMap.putString("uri", "file://$path")
        imageMap.putString("original_uri", "file://" + media.path)
        imageMap.putInt("size", File(path).length().toInt())


        return imageMap
    }
    companion object {
    const val NAME = "NativeImagePicker"
  }
}
