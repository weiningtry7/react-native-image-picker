import NativeImagePicker, { defaultOptions, type ImagePickerOption } from './NativeImagePicker';

const HTNativeImagePicker = (() => {
  return {
    asyncShowImagePicker: (options: ImagePickerOption ) => {
      const _options = { ...defaultOptions, ...options }
      return NativeImagePicker.asyncShowImagePicker(_options)
    } 
  };
})()

export default HTNativeImagePicker
