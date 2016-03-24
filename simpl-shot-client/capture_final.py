
import os, sys, Queue, numpy, urllib2, json
from PySide.QtCore import *
from PySide.QtGui import *
import time
from ast import literal_eval
from poster.streaminghttp import register_openers
from poster.encode import multipart_encode
from PySide import QtGui, QtCore

class TransWindow(QWidget,QPixmap):
    
    def __init__(self,QPixmap,main_window):
        QWidget.__init__(self,None,Qt.Window)
        self.layout = QGridLayout()
        self.setLayout(self.layout)
        self.pixmap=QPixmap
        self.showMaximized()
        self.activateWindow()
        self.raise_()
        self.main_window =main_window
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
        ####
        diff= abs(event.pos().x() - self.origin.x())
        print diff
        y_diff = abs(event.pos().y() - self.origin.y())
        print y_diff
        #####
        origin_left=self.origin.x()
        origin_top=self.origin.y()
        if event.pos().x() < self.origin.x() :
            origin_left= event.pos().x()
        #print event.pos().y()
        if event.pos().y() <self.origin.y():
            origin_top=event.pos().y()   

        self.obj=QRect(origin_left,origin_top, diff,y_diff)   
        #self.obj=QRect(self.origin,event.pos())
        self.rubberBand.hide()
        self.hide()
        px2=self.pixmap.copy(self.obj)
        px2.save(('temp_copy.jpg'))
        register_openers()
        ob = open("temp_copy.jpg","rb")
        datagen, headers = multipart_encode({"attachment":ob,"USER":"TESTUSER3"})
        request = urllib2.Request('http://localhost:8080/uploadService/file',datagen, headers)
        print urllib2.urlopen(request).read()
        self.main_window.show()
        ob.close()
        os.remove("temp_copy.jpg")
        

        
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
        
        self.sa_ur_share = QPushButton("Share")
        self.connect(self.sa_ur_share, SIGNAL("clicked()"), self.show_preview)
        
        self.layout.addWidget(self.sa_ul_bt,20,10,1,10)
        self.layout.addWidget(self.sa_ur_bt,30,10,1,10)
        self.layout.addWidget(self.sa_ur_share,40,10,1,10)
        self.sa_ur_share.hide()

        
   
    def show_history(self):
        data_returned = urllib2.urlopen("http://localhost:8080/user/TESTUSER3/").read()#testUSER3 hardcoded
        all_urls = []
        print "data returned"
        print data_returned
        l = literal_eval(data_returned)
        for each in l:
            temp = each['url']
            temp2 = temp[0:4] + temp[5:]
            all_urls.append(temp2)
        print all_urls
        self.task = Thumbnail(all_urls)
        
    def show_preview(self):
        data_returned = urllib2.urlopen("http://localhost:8080/user/TESTUSER3/").read()#testUSER3 hardcoded
        all_urls = []
        print "data returned"
        print data_returned
        l = literal_eval(data_returned)
        temp=l[-1]['url']
        temp2 = temp[0:4] + temp[5:]
        all_urls.append(temp2)
        self.task=Thumbnail(all_urls)
            
    def select_area(self):
        print "select_area"
        self.main_window.showMinimized()
        self.clicked  = False
        time.sleep(0.5)
        pixmap = QPixmap.grabWindow(QApplication.desktop().winId())
        print pixmap
        self.tw = TransWindow(pixmap,self.main_window)
        self.tw.mouse_press = False
        self.tw.show()
        
        px2 = pixmap.copy(self.tw.obj)
        px2.save('temp_copy.jpg')
        self.sa_ur_share.show()        
        
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
        
        #qbtn = QtGui.QPushButton('Quit', self)
        #qbtn.clicked.connect(self.close)
        #qbtn.resize(qbtn.sizeHint())
        #qbtn.move(50, 50)
        
        for each in url:
            data = urllib2.urlopen(each).read()
            image = QtGui.QImage()
            image.loadFromData(data)
            lbl = QtGui.QLabel(self)
            lbl2=QtGui.QLabel(self)
            pixmap = QtGui.QPixmap(image)
            pixmap = pixmap.scaled(200, 200, QtCore.Qt.KeepAspectRatio)
            #print "data"
            #print each
            url_disp=QLineEdit()
            #lbl.setTextFormat(Qt.RichText)
            #print "<img src='http://ankit1590.s3.amazonaws.com/TESTUSER356d798e04c5dd119cc304889temp_copy.jpg' height=\"200\" width=\"200\" >ddd"
            #lbl.setText("<img src='http://ankit1590\.s3\.amazonaws\.com/TESTUSER356d798e04c5dd119cc304889temp_copy\.jpg' height=\"200\" width=\"200\" >ddd")
            lbl.setPixmap(pixmap)
            url_disp.setText(each)
            #lbl2.setText(each)
            self.layout.addWidget(lbl)
            self.layout.addWidget(url_disp)
        self.setGeometry(200, 200, 400, 400)
        self.setWindowTitle('Simple-Shot')
        self.show()

        
class MainWindow(QMainWindow):
    def __init__(self,usrnm):
        #QMainWindow.__init__(self,None,Qt.WindowStaysOnTopHint)
        QMainWindow.__init__(self,None)
        self.options = OptionsContainer(self)
        self.setCentralWidget(self.options)
        
        self.arrow_icon = os.path.abspath(os.path.dirname(__file__)+"/cursor3.png")
        print "self.arrow_icon",self.arrow_icon
    
    '''    
    def keyPressEvent(self, event):
        if event.key() == Qt.Key_Q:
            sys.exit()
    '''
class Form(QDialog):
    def __init__(self, parent=None):
        super(Form, self).__init__(parent)

        self.le = QLineEdit()
        self.le.setObjectName("username")
        self.le.setText("TestUser")

        self.pb = QPushButton()
        self.pb.setObjectName("login")
        self.pb.setText("Log In!") 

        self.nu = QPushButton()
        self.nu.setObjectName("new user")
        self.nu.setText("New user!")
        
        layout = QFormLayout()
        layout.addWidget(self.le)
        layout.addWidget(self.pb)
        
        layout.addWidget(self.nu)

        self.setLayout(layout)
        self.connect(self.pb, SIGNAL("clicked()"),self.button_click)
        self.connect(self.nu, SIGNAL("clicked()"),self.signup_form)
        self.setWindowTitle("Snippet Tool")

    def button_click(self):
        usrnm = self.le.text()
        self.close()
        print "Logged in as: ",usrnm
        window = MainWindow(usrnm)
        window.show()

    def signup_form(self):
        usrnm = self.le.text()
        self.close()
        print "Logged in as: ",usrnm
        window = MainWindow(usrnm)
        window.show()

if __name__ == '__main__':
    global app
    app = QApplication(sys.argv)
    window = Form()
    window.show()
    
    try:
        sys.exit(app.exec_())
    except SystemExit as e:
        if e.code != 0:
            raise()
        os._exit(0)
    

 
