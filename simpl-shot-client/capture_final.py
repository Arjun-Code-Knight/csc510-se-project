from poster.encode import multipart_encode
from poster.streaminghttp import register_openers
import os, sys, Queue, numpy, urllib2, json,urllib
from PySide.QtCore import *
from PySide.QtGui import *
import time
from ast import literal_eval
from poster.streaminghttp import register_openers
from poster.encode import multipart_encode
from PySide import QtGui, QtCore
ip = "localhost"
class TransWindow(QWidget,QPixmap):    
    def __init__(self,QPixmap,main_window, user, pri):
        global usrnm, private
        private = pri
        usrnm = user
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
        diff= abs(event.pos().x() - self.origin.x())
        print diff
        y_diff = abs(event.pos().y() - self.origin.y())
        print y_diff
        origin_left=self.origin.x()
        origin_top=self.origin.y()
        if event.pos().x() < self.origin.x() :
            origin_left= event.pos().x()
        if event.pos().y() <self.origin.y():
            origin_top=event.pos().y()   
        self.obj=QRect(origin_left,origin_top, diff,y_diff)
        self.rubberBand.hide()
        self.hide()
        px2=self.pixmap.copy(self.obj)
        px2.save(('temp_copy.jpg'))
        register_openers()
        ob = open("temp_copy.jpg","rb")

        
        dict = {}
        dict['username'] = str(usrnm)
        if private:
            dict['private'] = "TRUE"
        else:
            dict['private'] = "FALSE"
        datagen, headers = multipart_encode({"attachment":ob,"email":dict['username'], "private":dict['private']})
        print  dict
        request = urllib2.Request('http://' + ip + ':8080/uploadService/file',datagen, headers)
        print urllib2.urlopen(request).read()
        self.main_window.show()
        print "Thank you for your patience. You may continue using the tool."
        ob.close()
        os.remove("temp_copy.jpg")
        

        
class OptionsContainer(QWidget):
    def __init__(self,main_window, user):
        global usrnm
        usrnm = user
        QWidget.__init__(self)
        self.main_window = main_window
        self.layout = QGridLayout()
        self.setLayout(self.layout)
        
        self.lr = numpy.zeros(2)
        
        self.sa_show_bt = QPushButton("Show Area")
        self.sa_show_bt.setCheckable(True)
        self.cb = QtGui.QCheckBox('Check if Private Snippet', self)        
        self.sa_ul_bt = QPushButton("Start Clipping!")
        self.connect(self.sa_ul_bt, SIGNAL("clicked()"), self.select_area)
    
        self.sa_ur_bt = QPushButton("Show History")
        self.connect(self.sa_ur_bt, SIGNAL("clicked()"), self.show_history)
        
        self.sa_ur_share = QPushButton("Share")
        self.connect(self.sa_ur_share, SIGNAL("clicked()"), self.show_preview)

        self.layout.addWidget(self.cb,10,10,1,10)        
        self.layout.addWidget(self.sa_ul_bt,20,10,1,10)
        self.layout.addWidget(self.sa_ur_bt,30,10,1,10)
        self.layout.addWidget(self.sa_ur_share,40,10,1,10)
        self.sa_ur_share.hide()
   
    def show_history(self):
        data_returned = urllib2.urlopen("http://" + ip + ":8080/user/search/" + usrnm + "/").read()
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
        data_returned = urllib2.urlopen("http://" + ip + ":8080/user/search/" + usrnm + "/").read()
        all_urls = []
        print "data returned"
        print data_returned
        l = literal_eval(data_returned)
        temp=l[-1]['url']
        temp2 = temp[0:4] + temp[5:]
        all_urls.append(temp2)
        self.task=Thumbnail(all_urls)
            
    def select_area(self):
        if(self.cb.isChecked()):
            private = True
        else:
            private = False
        print "Private image:" , private
        print "USERNAME:", usrnm
        self.main_window.showMinimized()
        self.clicked  = False
        time.sleep(0.5)
        pixmap = QPixmap.grabWindow(QApplication.desktop().winId())
        print pixmap
        self.tw = TransWindow(pixmap, self.main_window, usrnm, private)
        self.tw.mouse_press = False
        self.tw.show()
        #px2 = pixmap.copy(self.tw.obj)
        #px2.save('temp_copy.jpg')
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
        for each in url:
            data = urllib2.urlopen(each).read()
            image = QtGui.QImage()
            image.loadFromData(data)
            lbl = QtGui.QLabel(self)
            lbl2=QtGui.QLabel(self)
            pixmap = QtGui.QPixmap(image)
            pixmap = pixmap.scaled(200, 200, QtCore.Qt.KeepAspectRatio)
            url_disp=QLineEdit()
            lbl.setPixmap(pixmap)
            url_disp.setText(each)
            self.layout.addWidget(lbl)
            self.layout.addWidget(url_disp)
        self.setGeometry(200, 200, 400, 400)
        self.setWindowTitle('Simple-Shot')
        self.show()

class userReview(QDialog):
    def __init__(self, parent, user):
        global usrnm
        usrnm = user
        global prnt
        prnt = parent
        super(userReview, self).__init__(parent)
        self.agreement=QLabel()
        self.agreement.setText("\n Please review \n")
        layout = QFormLayout()
        layout.addWidget(self.agreement)


        self.r1 = QRadioButton("1")
        self.r2 = QRadioButton("2")
        self.r3 = QRadioButton("3")
        self.r4 = QRadioButton("4")
        self.r5 = QRadioButton("5")
        self.r5.setChecked(True)
        layout.addWidget(self.r1)
        layout.addWidget(self.r2)
        layout.addWidget(self.r3)
        layout.addWidget(self.r4)
        layout.addWidget(self.r5)

        
        self.qsbar = QtGui.QLineEdit(self)
        layout.addWidget(self.qsbar)
        
        self.setLayout(layout)

        
        
        self.nu = QPushButton()
        self.nu.setObjectName("submit")
        self.nu.setText("Submit!")

        layout.addWidget(self.nu)
        self.connect(self.nu, SIGNAL("clicked()"),self.button_click)

    def button_click(self):
        review = self.qsbar.text()
        print "USER = ", usrnm
        print "Review = ", review
        if self.r1.isChecked():
            rating = "1"
        elif self.r2.isChecked():
            rating = "2"
        elif self.r3.isChecked():
            rating = "3"
        elif self.r4.isChecked():
            rating = "4"
        else:
            rating = "5"
        review = urllib2.quote(review)
        data_returned = urllib2.urlopen("http://" + ip + ":8080/user/usersatisfaction/" + usrnm + "/" + rating + "/" + review + "/SOLUTION1-DESKTOPWITHOUTSEARCH/").read()
        self.close()
                

        
class MainWindow(QMainWindow):
    def __init__(self,user):
        global usrnm
        usrnm = user
        QMainWindow.__init__(self,None)
        self.options = OptionsContainer(self,usrnm)
        self.setCentralWidget(self.options)
        self.arrow_icon = os.path.abspath(os.path.dirname(__file__)+"/cursor3.png")
        print "self.arrow_icon",self.arrow_icon
        
    def closeEvent(self, event):
        
        reply = QtGui.QMessageBox.question(self, 'Message',
            "Do you want to review our app?", QtGui.QMessageBox.Yes | 
            QtGui.QMessageBox.No, QtGui.QMessageBox.No)

        if reply == QtGui.QMessageBox.Yes:
            event.ignore()
            window = userReview(self, usrnm)
            window.show()
        else:
            event.accept()

        
class DataForm(QDialog):
    def __init__(self, parent,email):
        super(DataForm, self).__init__(parent)
        self.agreement=QLabel()
        self.agreement.setText("\n Privacy Policy Form\n 1) Any personal information collected from you while using this app, will remain confidential \n 2) Any private screenshots taken with this app will not be visible to anyone else. However the images will be stored on the cloud.\n3) Any public screenshots taken with the app will be visible to all users of the app. However they will not have access to your personal details")
        self.setWindowTitle("Privacy Policy form")
        layout = QFormLayout()
        layout.addWidget(self.agreement)
        self.setLayout(layout)
        self.nu = QPushButton()
        self.nu.setObjectName("next")
        self.nu.setText("I have read and understood the privacy agreement and I agree")
        layout.addWidget(self.nu)
        self.email=email
        self.connect(self.nu, SIGNAL("clicked()"),self.button_click)
        
    def button_click(self):
            self.close()
            window = MainWindow(self.email)
            window.show()
            
class SignUp_Form(QDialog):
    def __init__(self, parent):
        super(SignUp_Form, self).__init__(parent)
        self.usnname = QLineEdit()
        self.usnname.setObjectName("username")
        self.usnname.setText("UserName")
        self.passwd = QLineEdit()
        self.passwd.setObjectName("password")
        self.passwd.setText("Password")
        self.em = QLineEdit()
        self.em.setObjectName("Email")
        self.em.setText("email")
        self.age = QLineEdit()
        self.age.setObjectName("age")
        self.age.setText("Age")
        self.sex = QLineEdit()
        self.sex.setObjectName("Sex")
        self.sex.setText("sex- enter F or M")
        self.nu = QPushButton()
        self.nu.setObjectName("next")
        self.nu.setText("Next!")
        self.connect(self.nu, SIGNAL("clicked()"),self.button_click)
        self.occ=QComboBox()
        self.occupation=QLabel()
        self.occupation.setText("Occupation :")
        self.occ.addItem("Technical")
        self.occ.addItem("Non-Technical")
        print self.occ.currentText()
        layout = QFormLayout()
        layout.addWidget(self.usnname)
        layout.addWidget(self.passwd)
        layout.addWidget(self.em)
        layout.addWidget(self.age)
        layout.addWidget(self.sex)
        layout.addWidget(self.occupation)
        layout.addWidget(self.occ)
        layout.addWidget(self.nu)
        self.setWindowTitle("Sign up")
        self.setLayout(layout)
        
    def button_click(self):
            usrnm = self.usnname.text()
            passwd = self.passwd.text()
            email = self.em.text()
            age = self.age.text()
            if age=="Age":
                age=0
            sex = self.sex.text()
            occ = self.occ.currentText()
            dict={}
            dict['userName']=str(usrnm)
            dict['password']=str(passwd)
            dict['email']=str(email)
            dict['age']=int(age)
            dict['sex']=str(sex)
            dict['occupation']=str(occ)
            print dict
            #####
            request = urllib2.Request('http://localhost:8080/user/signup')
            request.add_header('Content-Type','application/json')
            ##print request
            response=urllib2.urlopen(request,json.dumps(dict)).read()
            print response
            #######
            if "Yes" in response:
                self.close()
                window = DataForm(self,email)
                window.show()
            else :
                print "Try again"
            
class Form(QDialog):
    def __init__(self, parent=None):
        super(Form, self).__init__(parent)

        self.le = QLineEdit()
        self.le.setObjectName("email")
        self.le.setText("email")

        self.pw = QLineEdit()
        self.pw.setObjectName("password")
        self.pw.setText("password")
        
        self.pb = QPushButton()
        self.pb.setObjectName("login")
        self.pb.setText("Log In!") 

        self.nu = QPushButton()
        self.nu.setObjectName("new user")
        self.nu.setText("New user!")
        
        layout = QFormLayout()
        layout.addWidget(self.le)
        layout.addWidget(self.pw)
        layout.addWidget(self.pb)
        
        layout.addWidget(self.nu)

        self.setLayout(layout)
        self.connect(self.pb, SIGNAL("clicked()"),self.button_click)
        self.connect(self.nu, SIGNAL("clicked()"),self.signup_form)
        self.setWindowTitle("Snippet Tool")

    def button_click(self):
        import re
        email = self.le.text()
        passwd=self.pw.text()
        
        dict={}
        dict['password']=str(passwd)
        dict['email']=str(email)
        request = urllib2.Request('http://localhost:8080/user/login')
        request.add_header('Content-Type','application/json')
        response=urllib2.urlopen(request,json.dumps(dict)).read()
        #print response
        if "Yes" in response:
            #print dict(response)
            window = MainWindow(email)
            window.show()
            self.close()
        else :
            print "Try again"    
            
    def signup_form(self):
        self.close()
        window = SignUp_Form(self)
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
    

 
