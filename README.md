# 基于react-native0.82版本的图片选择器（TurboModule）

This is a fork of  [react-native-syan-image-picker](https://github.com/syanbo/react-native-syan-image-picker)

### 原生框架依赖

- Android:  [PictureSelector](https://github.com/LuckSiege/PictureSelector) - by [LuckSiege](https://github.com/LuckSiege)

- iOS:[TZImagePickerController](https://github.com/banchichen/TZImagePickerController) - by [banchichen](https://github.com/banchichen)

### 安装

```
yarn add @hotta/react-native-imagepicker@0.1.3
```

### 使用方法

```
import HTImagePicker from '@ht/react-native-image-picker/src';
const options: any = {
      mediaType: 'photo',
      sortOrder: 'asc',
    }
const response = await HTImagePicker.asyncShowImagePicker(options);
```
