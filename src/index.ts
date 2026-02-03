import NativeImagePicker, { defaultOptions, type ImagePickerOption, type SelectedPhoto } from './NativeImagePicker';
export type { SelectedPhoto, ImagePickerOption };
const HTNativeImagePicker = (() => {
  return {
    asyncShowImagePicker: (options: ImagePickerOption ) => {
      const _options = { ...defaultOptions, ...options }
      return NativeImagePicker.asyncShowImagePicker(_options)
    } 
  };
})()

export default HTNativeImagePicker
