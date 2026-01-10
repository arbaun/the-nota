@file:Suppress("SameParameterValue")

package com.arbadev.thenota.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

class QrGeneratorJson(private val content: ByteArray) {
    private val size = 57
    private val modules = Array(size){
        BooleanArray(size)
    }
    private val isFunction = Array(size){
        BooleanArray(size)
    }
    private fun calculateRS(data: ByteArray, ecCount:Int): ByteArray{
        val exp = IntArray(512)
        val log = IntArray(256)
        var x = 1
        for(i in 0 until 255){
            exp[i]= x;exp[i+255]=x;log[x]=i
            x = (x shl 1) xor (if(x and 0x80 !=0) 0x11d else 0)
        }
        var gen = intArrayOf(1)
        for(i in 0 until ecCount){
            val next = IntArray(gen.size+1)
            for(j in gen.indices){
                next[j]= next[j] xor exp[log[gen[j]]+i]
                next[j+1] = next[j+1] xor gen[j]
            }
            gen = next
        }
        val res = IntArray(ecCount)
        for(b in data){
            val f = log[(b.toInt() and 0xff) xor res[0]]
            for(j in 1 until ecCount){
                res[j-1] = res[j] xor (if (f!=-1)exp[f+log[gen[j]]] else 0)
            }
            res[ecCount-1] = if(f!=-1)exp[f+log[gen[ecCount]]] else 0
        }
        return ByteArray(ecCount){res[it].toByte()}
    }
    private fun addBits(list: MutableList<Boolean>, value:Int, len:Int){
        for(i in len-1 downTo 0){
            list.add(((value shr i)and 1)==1)
        }
    }
    private fun drawAligment(x: Int, y: Int){
        for (dx in 0..4)for (dy in 0..4){
            val isBlack = dx==0||dx==4||dy==0||dy==4||(dx==2 && dy==2)
            setModule(x+dx, y+dy,isBlack, true)
        }
    }

    private fun setModule(x: Int, y: Int, isBlack: Boolean, function: Boolean){
        modules[y][x]=isBlack
        isFunction[y][x]=function
    }
    private fun drawFinder(x:Int, y:Int){
        for(dx in 0..6){
            for(dy in 0..6){
                val isBlack = dx ==0||dx==6||dy==0||dy==6||(dx in 2..4 && dy in 2..4)
                setModule(x+dx,y+dy, isBlack, true)
            }
        }
    }
    /*private fun encodeData():List<Boolean>{
        val bytes = content.toByteArray(Charsets.ISO_8859_1)
        val maxDataBytes = 213
        val dataEncode = if(bytes.size>maxDataBytes)bytes.copyOfRange(0, maxDataBytes) else bytes
        val bits = mutableListOf<Boolean>()
        addBits(bits, 0x04, 16)
        addBits(bits, dataEncode.size,16)
        for(b in dataEncode)addBits(bits, b.toInt(),16)
        while (bits.size<maxDataBytes*8)bits.add(false)
        val dataBytes = ByteArray(maxDataBytes)
        for(i in 0 until maxDataBytes){
            var b =0
            for(j in 0..7)if(bits[i*8+j])b=b or (1 shl (7-j))
            dataBytes[i] = b.toByte()
        }
        val eCbytes = calculateRS(dataBytes, 58)
        val finalbits = bits.toMutableList()
        for(b in eCbytes)addBits(finalbits, b.toInt(),16)
        return finalbits
    }*/
    private fun fillData(data:List<Boolean>){
        var bitIndex = 0
        var up = true
        //var x= size-1
        for(x in size-1 downTo 1 step 2){
            val curX = if(x==6)x-1 else x
            val yRange = if(up)size-1 downTo 0 else 0 until size
            for(y in yRange){
                for(d in 0..1){
                    val tx = curX-d
                    if(!isFunction[y][tx]){
                        if(bitIndex< data.size){
                            val bit = data[bitIndex++]
                            modules[y][tx]= bit xor ((y + tx)%2 == 0)
                        }else{
                            modules[y][x]= (y + tx)%2==0
                        }
                    }
                }
            }
            up = !up
        }
    }
    @SuppressLint("UseKtx")
    private fun renderToBitmap(pixelSize: Int): Bitmap{
        val totalSize = size*pixelSize
        val bitmap = Bitmap.createBitmap(totalSize,totalSize, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(totalSize*totalSize)
        for(y in 0 until totalSize){
            for (x in 0 until totalSize){
                val isBlack = modules[y/pixelSize][x/pixelSize]
                pixels[y*totalSize+x]= if(isBlack) android.graphics.Color.BLACK else android.graphics.Color.WHITE

            }
        }
        bitmap.setPixels(pixels, 0, totalSize,0,0,totalSize,totalSize)
        return bitmap
    }
    private fun setupFunctionPatterns(){
        drawFinder(0,0)
        drawFinder(size-7,0)
        drawFinder(0, size-7)
        for(i in 8 until size -8){
            setModule(i, 6, i%2==0,true)
            setModule(6, i, i%2==0, true)
        }
        val pos = intArrayOf(6,26,50)
        for(r in pos){
            for (c in pos){
                if(!((r<9 && c<9)||(r<9&&c>size-9)||(r>size-9&&c<9))){
                    drawAligment(c-2,r-2)
                }
            }
        }
    }

    fun generate(pixelSize: Int): Bitmap?{
        //setupFunctionPatterns()
        //val bits = encodeData()
        //fillData(bits)
        //val cleanJson = content.replace("\\s".toRegex(),"")
        val hints = mapOf(EncodeHintType.CHARACTER_SET to "ISO-8859-1",
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L,
            EncodeHintType.MARGIN to 1)
        return try{
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(String(content, Charsets.ISO_8859_1), BarcodeFormat.QR_CODE,pixelSize,pixelSize,hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap= Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565)
            for(x in 0 until width){
                for (y in 0 until height){
                    bitmap.setPixel(x,y,if(bitMatrix.get(x,y)) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        }catch (e:Exception){
            null
        }

    }
}