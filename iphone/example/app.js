var win = Ti.UI.createWindow();

if( false ){
    // dummy for build
    Ti.UI.createImageView({
        image:'test.png'
    })
}

var GI = require('com.bongole.ti.gpuimage');
var iv = GI.createGPUImageView({
    image: 'momokuro.jpg'
})

var sepia = true;
var b = Ti.UI.createButton({
    title: 'push',
    top: 0
})

b.addEventListener('click', function(){
    if( sepia ){
        sepia = false;
        iv.filter = GI.SEPIA_FILTER
    }
    else{
        sepia = true;
        iv.filter = null;
    }
})

win.add(b);
win.add(iv);
win.open();
