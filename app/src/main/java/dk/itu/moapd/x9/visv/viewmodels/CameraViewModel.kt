/*
 * MIT License
 *
 * Copyright (c) 2026 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dk.itu.moapd.x9.visv.viewmodels

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class CameraViewModel : ViewModel() {
    /**
     * The current selected camera.
     */
    private var _selector = MutableLiveData<CameraSelector>()

    /**
     * A `LiveData` which publicly exposes any update in the camera selector.
     */
    val selector: LiveData<CameraSelector>
        get() = _selector

    /**
     * The last captured image Uri.
     */
    private var _imageUri = MutableLiveData<Uri?>()

    /**
     * A `LiveData` which publicly exposes any update in the last captured image Uri.
     */
    val imageUri: LiveData<Uri?>
        get() = _imageUri

    /**
     * This method will be executed when the user interacts with the camera selector component. It
     * sets the selector into the LiveData instance.
     *
     * @param selector A set of requirements and priorities used to select a camera.
     */
    fun onCameraSelectorChanged(selector: CameraSelector) {
        this._selector.value = selector
    }

    /**
     * Update the last captured image Uri.
     *
     * @param uri The new image Uri.
     */
    fun onImageUriChanged(uri: Uri?) {
        _imageUri.value = uri
    }

}