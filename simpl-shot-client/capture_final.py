

import time
import sys,os
import threading
import Queue
import numpy

from PySide.QtGui import QPixmap, QApplication, QMainWindow,QWidget,\
                        QPushButton,QVBoxLayout,QPainter,QCursor,QSpinBox,\
                        QLabel,QGridLayout,QLineEdit,QButtonGroup,QRadioButton, \
                        QGroupBox,QLayout,QRubberBand,QFocusEvent,QLabel

from PySide.QtCore import SIGNAL,Qt, QThread,QRect,QSize,QEvent

from PySide import QtCore
from PySide import QtGui


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
        #if not self.rubberBand:
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
        #sys.exit()
        px2=self.pixmap.copy(self.obj)
        px2.save(('ffff.jpg'))

        
class OptionsContainer(QWidget):
    def __init__(self,main_window):
        QWidget.__init__(self)
        self.main_window = main_window
        self.layout = QGridLayout()
        self.setLayout(self.layout)
        
        self.lr = numpy.zeros(2)
        
        
        self.sa_show_bt = QPushButton("Show Area")
        self.sa_show_bt.setCheckable(True)
        
        
        self.sa_ul_bt = QPushButton("Select Upper Left")
        self.connect(self.sa_ul_bt, SIGNAL("clicked()"), self.select_area)
        
  
        self.layout.addWidget(self.sa_ul_bt,20,10,1,10)

        
   
        
    def select_area(self):
        print "select_area"
        self.main_window.showMinimized()
        self.clicked  = False
        time.sleep(0.5)
        pixmap = QPixmap.grabWindow(QApplication.desktop().winId())
        print pixmap
        #pixmap.save(('swdef.jpg'))
        self.tw = TransWindow(pixmap)
        self.tw.mouse_press = False
        self.tw.show()
        
        px2=pixmap.copy(self.tw.obj)
        px2.save('ffff.jpg')
        
    
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

