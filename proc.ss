(define dir_base "~/Liter/")
(define dir_list (list "4" "5.9" "5.10" "5.11" "5.12"))


(define (content dir_base dir_list)
  (map
   (lambda (x)
     (let
	 ((dir_full
	   (string-append dir_base x)))
       (procdir dir_full)))
   dir_list))

(define (procdir dir)
  (let*
      ((path_all
	(directory-list dir))
       (path_list
	(filter isfdf path_all)))
    (map
     (lambda (x) (read_file x))
     path_list)))

(define isfdf
  (lambda(pathname)
    (let* ((end (string-length pathname))
	   (profix ".fdf")
	   (n_fix (string-length profix)))
      (if (< end n_fix) #f
	  (string=? profix
		    (substring pathname (- end n_fix) end)) ))))

(define (read_file path)
  (let* ((fp (open-input-file path))
	 (content (get-string-all fp)))
    content))
