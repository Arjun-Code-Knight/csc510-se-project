import time
import sys,os
import threading
import Queue
import numpy
import urllib2
from PySide import QtGui, QtCore
from PySide.QtGui import QPixmap, QApplication, QMainWindow,QWidget,\
                        QPushButton,QVBoxLayout,QPainter,QCursor,QSpinBox,\
                        QLabel,QGridLayout,QLineEdit,QButtonGroup,QRadioButton, \
                        QGroupBox,QLayout,QRubberBand,QFocusEvent,QLabel

from PySide.QtCore import SIGNAL,Qt, QThread,QRect,QSize,QEvent

from PySide import QtCore
from PySide import QtGui
import json
from ast import literal_eval
from poster.streaminghttp import register_openers
from poster.encode import multipart_encode


class TransWindow(QWidget,QPixmap):
    
    def __init__(self,QPixmap):
        QWidget.__init__(self,None,Qt.Window)
        self.layout = QGridLayout()
        self.setLayout(self.layout)
        self.pixmap=QPixmap
        self.showMaximized()
        self.activateWindow()
        self.raise_()
        self.setWindowFlags(Qt.Window | Qt.WindowStaysOnTopHint  | Qt.X11BypassWindowManagerHint \
                            | Qt.FramelessWindowHint )
        self.obj=QRect(0,0,0,0)
        screenGeometry = QApplication.desktop().availableGeometry();
        self.setGeometry(screenGeometry)
        self.setWindowOpacity(0.5)
    
    def mousePressEvent(self, event):
        self.origin = event.pos()
        self.rubberBand = QRubberBand(QRubberBand.Rectangle, self)
        self.rubberBand.setGeometry(QRect(self.origin, QSize()))
        self.rubberBand.show()

    def mouseMoveEvent(self, event):
        self.rubberBand.setGeometry(QRect(self.origin, event.pos()).normalized())

    def mouseReleaseEvent(self, event):
        print self.origin
        print event.pos()
        self.obj=QRect(self.origin,event.pos())
        self.rubberBand.hide()
        self.hide()
        px2=self.pixmap.copy(self.obj)
        px2.save(('ffff.jpg'))
        register_openers()
        ob = open("ffff.jpg","rb")
        datagen, headers = multipart_encode({"attachment":ob,"USER":"TESTUSER3"})
        request = urllib2.Request('http://localhost:8080/uploadService/file',datagen, headers)
        print urllib2.urlopen(request).read()
        ob.close()
        os.remove("ffff.jpg")
        

        
class OptionsContainer(QWidget):
    def __init__(self,main_window):
        QWidget.__init__(self)
        self.main_window = main_window
        self.layout = QGridLayout()
        self.setLayout(self.layout)
        
        self.lr = numpy.zeros(2)
        
        self.sa_show_bt = QPushButton("Show Area")
        self.sa_show_bt.setCheckable(True)
        
        
        self.sa_ul_bt = QPushButton("Start Clipping!")
        self.connect(self.sa_ul_bt, SIGNAL("clicked()"), self.select_area)

        self.sa_ur_bt = QPushButton("Show History")
        self.connect(self.sa_ur_bt, SIGNAL("clicked()"), self.show_history)
        
        self.layout.addWidget(self.sa_ul_bt,20,10,1,10)
        self.layout.addWidget(self.sa_ur_bt,30,10,1,10)

        
   
    def show_history(self):
        data_returned = urllib2.urlopen("http://localhost:8080/user/TESTUSER3/").read()#testUSER3 hardcoded
        all_urls = []
        l = literal_eval(data_returned)
        for each in l:
            temp = each['url']
            temp2 = temp[0:4] + temp[5:]
            all_urls.append(temp2)
        print all_urls
        self.task = Thumbnail(all_urls)
        
        
    def select_area(self):
        print "select_area"
        self.main_window.showMinimized()
        self.clicked  = False
        time.sleep(0.5)
        pixmap = QPixmap.grabWindow(QApplication.desktop().winId())
        print pixmap
        self.tw = TransWindow(pixmap)
        self.tw.mouse_press = False
        self.tw.show()
        
        px2 = pixmap.copy(self.tw.obj)
        px2.save('ffff.jpg')
                
        
class Thumbnail(QtGui.QWidget):

    def __init__(self, url):
        super(Thumbnail, self).__init__()
        self.initUI(url)

    def initUI(self, url):
        self.widget_layout = QtGui.QVBoxLayout(self)
        self.scrollarea = QtGui.QScrollArea()
        self.scrollarea.setWidgetResizable(True)
        self.widget_layout.addWidget(self.scrollarea)
        self.widget = QtGui.QWidget()
        self.layout = QtGui.QVBoxLayout(self.widget)
        self.scrollarea.setWidget(self.widget)
        self.layout.setAlignment(QtCore.Qt.AlignHCenter)
        
        qbtn = QtGui.QPushButton('Quit', self)
        qbtn.clicked.connect(self.close)
        qbtn.resize(qbtn.sizeHint())
        qbtn.move(50, 50)
        
        for each in url:
            data = urllib2.urlopen(each).read()
            image = QtGui.QImage()
            image.loadFromData(data)
            lbl = QtGui.QLabel(self)
            pixmap = QtGui.QPixmap(image)
            pixmap = pixmap.scaled(100, 100, QtCore.Qt.KeepAspectRatio)
            lbl.setPixmap(pixmap)
            self.layout.addWidget(lbl)
        
        self.setGeometry(200, 200, 400, 400)
        self.setWindowTitle('Snippet Tool')
        self.show()

        
class MainWindow(QMainWindow):
    def __init__(self):
        #QMainWindow.__init__(self,None,Qt.WindowStaysOnTopHint)
        QMainWindow.__init__(self,None)
        self.options = OptionsContainer(self)
        self.setCentralWidget(self.options)
        
        self.arrow_icon = os.path.abspath(os.path.dirname(__file__)+"/cursor3.png")
        print "self.arrow_icon",self.arrow_icon
    
        
    def keyPressEvent(self, event):
        if event.key() == Qt.Key_Q:
            sys.exit()


        
if __name__ == '__main__':
    global app
    app = QApplication(sys.argv)
    
    window = MainWindow()
    window.show()
    #myQTestWidget = QCustomWidget()
    #myQTestWidget.show()
    
    try:
        sys.exit(app.exec_())
    except SystemExit as e:
        if e.code != 0:
            raise()
        os._exit(0)
    
    #sys.exit(app.exec_())

