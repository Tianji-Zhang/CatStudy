(ql:quickload "cl-fad")
(ql:quickload "cl-ppcre")

(in-package "SB-IMPL")

(defvar dir_base "~/Liter/")
(defvar dir_list (list "4" "5.9" "5.10" "5.11" "5.12"))

(defun content (dir_base dir_list)
  (mapcar
   (lambda (x)
     (let
	 ((dir_full
	   (concatenate 'string dir_base x)))
       (cons x (procdir dir_full))))
   dir_list))

(defun procdir (dir)
  (let*
      ((path_all
	(cl-fad:list-directory dir))
       (path_list
	(remove-if-not isfdf path_all)))
    (mapcar (lambda (x) (readfile x)) path_list)))

(defvar isfdf
    #'(lambda(pathname)
	(search ".fdf" (namestring pathname))))

(defun readfile (path)
  (with-open-file (file path
			:if-does-not-exist nil)
    (let ((content (make-string (file-length file))))
      (handler-bind ((sb-int:stream-decoding-error
		      (lambda (c) (invoke-restart 'attempt-resync))))
	(read-sequence content file))
      (cons (namestring path) (proc_strs content)))))

(defun proc_strs (content)
  (let* ((reg-str "Contents[(](.*?)[)]/CreationDate")
	 (reg-s "\\\\[r]")
	 (reg-1 "\\\\[(]")
	 (reg-2 "\\\\[)]")
	 (reg-3 "Contents[(]")
	 (reg-4 "[)]/CreationDate")
	 (data (cl-ppcre:all-matches-as-strings reg-str content))
	 (reg-list (list reg-1 reg-2 reg-3 reg-4)))
    (mapcar
     (lambda (x)
       (cl-ppcre:split
	reg-s
	(reduce
	 (lambda (a b)  (cl-ppcre:regex-replace-all b a ""))
	 (cons x reg-list))))
     data)))

;(readfile "~/Liter/4/moe.txt")
; Output data Struct: (dir . (file . (list record_n)))

(defun write-doc (content filename)
  (with-open-file (f (pathname filename)
		     :if-exists :supersede
		     :if-does-not-exist :create)
    (output-f content f)))

(defun output-f (content f)
  (cond
    ((string content) (format f content #\linefeed))
    ((listp (cdr content))
     (mapcar (lambda (x)(output-f x f))(cdr content)))
    ((eql content '()) (format f ""))
    (t (progn
	 (if (funcall isfdf (car content))
	     (format f "File:"))
	 (format f (car content) #\linefeed)
	 (output-f (cdr content) f)
	 (format f "~%")))))


;(content dir_base dir_list)

(write-doc (content dir_base dir_list) "meow.dat")


