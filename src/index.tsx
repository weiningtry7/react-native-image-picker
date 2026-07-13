import NativeImagePicker from './NativeImagePicker';

const HTNativeImagePicker = (() => {
  return {
    asyncShowImagePicker: NativeImagePicker.asyncShowImagePicker,
    asyncShowVideoPicker: NativeImagePicker.asyncShowVideoPicker,
  };
})()

export default HTNativeImagePicker
