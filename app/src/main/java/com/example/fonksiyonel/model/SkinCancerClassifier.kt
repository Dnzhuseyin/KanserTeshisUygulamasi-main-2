package com.example.fonksiyonel.model

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import java.io.IOException
import kotlin.math.roundToInt

/**
 * Modelin ham çıktısını içeren sınıf
 */
data class ModelOutput(
    val rawOutputs: FloatArray,
    val percentages: List<Float>,
    val classLabels: List<String> = listOf("Benign", "Malignant")
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelOutput

        if (!rawOutputs.contentEquals(other.rawOutputs)) return false
        if (percentages != other.percentages) return false
        if (classLabels != other.classLabels) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rawOutputs.contentHashCode()
        result = 31 * result + percentages.hashCode()
        result = 31 * result + classLabels.hashCode()
        return result
    }
}

class SkinCancerClassifier(private val context: Context) {
    private var interpreter: Interpreter? = null
    private val modelName = "skin_cancer_model.tflite"
    private val inputSize = 224 // Model expects 224x224 images
    private val numClasses = 2  // 2 classes: benign (iyi huylu), malignant (kötü huylu)

    init {
        try {
            val model = loadModelFile()
            val options = Interpreter.Options()
            interpreter = Interpreter(model, options)
            Log.d(TAG, "Model loaded successfully: $modelName")
        } catch (e: IOException) {
            Log.e(TAG, "Error loading model: ${e.message}", e)
        }
    }

    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer {
        try {
            val assetManager = context.assets
            val fileDescriptor = assetManager.openFd(modelName)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            Log.d(TAG, "Model file size: $declaredLength bytes")
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        } catch (e: IOException) {
            Log.e(TAG, "Error loading model file: ${e.message}")
            throw e
        }
    }

    /**
     * Modelin ham çıktısını döndüren fonksiyon
     */
    fun getModelRawOutput(uri: Uri): ModelOutput {
        try {
            // Load and preprocess the image
            val bitmap = loadAndResizeBitmap(uri, inputSize, inputSize)
            Log.d(TAG, "Image loaded and resized to ${inputSize}x${inputSize}")
            
            // Prepare input buffer - Ensure buffer is cleared before use
            val inputBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
            inputBuffer.order(ByteOrder.nativeOrder())
            inputBuffer.rewind() // Reset position to beginning
            
            // Normalize pixel values to [0, 1]
            val pixels = IntArray(inputSize * inputSize)
            bitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)
            
            // Log a sample of pixels to verify data
            Log.d(TAG, "Sample pixel value: ${pixels[inputSize * inputSize / 2]}")
            
            for (pixelValue in pixels) {
                // Extract RGB values
                val r = (pixelValue shr 16 and 0xFF) / 255.0f
                val g = (pixelValue shr 8 and 0xFF) / 255.0f
                val b = (pixelValue and 0xFF) / 255.0f
                
                // TensorFlow Lite expects RGB values
                inputBuffer.putFloat(r)
                inputBuffer.putFloat(g)
                inputBuffer.putFloat(b)
            }
            
            // Rewind buffer to prepare for reading
            inputBuffer.rewind()
            
            // Prepare output buffer
            val outputBuffer = Array(1) { FloatArray(numClasses) }
            
            // Run inference
            Log.d(TAG, "Running model inference...")
            interpreter?.run(inputBuffer, outputBuffer)
            
            // Get raw model output
            val rawOutput = outputBuffer[0]
            Log.d(TAG, "Raw model output: ${rawOutput.contentToString()}")
            
            // Yüzdelik değerlere dönüştür (opsiyonel)
            val percentages = rawOutput.map { (it * 100).roundToInt() / 100f }
            Log.d(TAG, "Output percentages: ${percentages}")
            
            return ModelOutput(
                rawOutputs = rawOutput,
                percentages = percentages.toList()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error in model inference: ${e.message}", e)
            // Return default values on error
            return ModelOutput(
                rawOutputs = FloatArray(numClasses) { 0f },
                percentages = List(numClasses) { 0f }
            )
        }
    }
    
    /**
     * Modelin çıktısını DiagnosisResult olarak da döndürebilmek için eski metodu tutuyoruz
     */
    fun classifyImage(uri: Uri): DiagnosisResult {
        val modelOutput = getModelRawOutput(uri)
        val result = modelOutput.rawOutputs
        
        // Find the class with highest probability
        var maxIndex = 0
        var maxProb = result[0]
        
        for (i in 1 until numClasses) {
            if (result[i] > maxProb) {
                maxProb = result[i]
                maxIndex = i
            }
        }
        
        // Map the index to cancer type
        val cancerType = when (maxIndex) {
            0 -> CancerType.BENIGN
            1 -> CancerType.MALIGNANT
            else -> CancerType.BENIGN
        }
        
        // Determine risk level based on cancer type and confidence
        val riskLevel = when {
            cancerType == CancerType.BENIGN -> RiskLevel.LOW
            maxProb > 0.9f -> RiskLevel.VERY_HIGH
            maxProb > 0.7f -> RiskLevel.HIGH
            maxProb > 0.5f -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }
        
        return DiagnosisResult(
            cancerType = cancerType,
            confidencePercentage = maxProb,
            riskLevel = riskLevel
        )
    }
    
    private fun loadAndResizeBitmap(uri: Uri, width: Int, height: Int): Bitmap {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
        
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
    
    fun close() {
        interpreter?.close()
        interpreter = null
    }
    
    companion object {
        private const val TAG = "SkinCancerClassifier"
    }
}
