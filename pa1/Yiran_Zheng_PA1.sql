--Yiran Zheng
--CS127
--zhengyr@brandeis.edu
--PA1

--Problem 1
INSERT INTO Periods
SELECT DISTINCT day, per
From Assigned
ORDER BY day, per;

--Problem 2
INSERT INTO Teachers 
SELECT DISTINCT tname, SUM(nper) AS tload
FROM Courses AS C, Taught_by AS T
WHERE C.cno = T.cno
GROUP BY tname 
ORDER BY tname;

--Problem 3
SELECT day, per, cno
FROM Assigned NATURAL INNER JOIN Taught_by
WHERE Taught_by.tname = 'Marsalis'
ORDER BY day, per;

--Problem 4
UPDATE Assigned
SET per = 4
WHERE cno = 10 AND per = 2 AND day = 'B';

--Problem 5
SELECT day, per, cno
FROM Taught_to NATURAL INNER JOIN Assigned
WHERE grade = 11 AND(hr = 'B' OR hr = 'Z')
ORDER BY day, per;

--Problem 6
UPDATE Assigned
SET per = 2
WHERE day = 'B' AND per = 4 AND cno = SOME (SELECT cno 
	FROM (Courses NATURAL INNER JOIN Taught_to) AS A
	WHERE A.subj = 'Mat' AND A.grade = 11 AND (A.hr = 'Z' OR A.hr = 'B'));
	
--Problem 7
SELECT tname, day, per, subj, grade, hr
FROM (Courses NATURAL INNER JOIN Assigned) NATURAL INNER JOIN (Taught_to NATURAL INNER JOIN Taught_by)
ORDER BY tname, day, per;

--Problem 8
SELECT A.grade, Grades.hr AS hr, day, per, subj
FROM ((Courses NATURAL INNER JOIN Taught_to) NATURAL INNER JOIN Assigned) AS A, Grades
WHERE A.grade = Grades.grade AND (A.hr = Grades.hr OR A.hr = 'Z')
ORDER BY A.grade, Grades.hr, day, per;

--Problem 9
SELECT DISTINCT A.tname, A.day, A.per
FROM (Taught_by NATURAL INNER JOIN Assigned) AS A, (Taught_by NATURAL INNER JOIN Assigned) AS B
WHERE A.day = B.day AND A.per = B.per AND A.cno <> B.cno AND A.tname = B.tname;

--Problem 10
SELECT DISTINCT A.grade, G.hr, A.day, A.per
FROM (Taught_to NATURAL INNER JOIN Assigned) AS A, Grades AS G, (Taught_to NATURAL INNER JOIN Assigned) AS B
WHERE A.grade = G.grade AND ((A.hr = B.hr AND A.hr = G.hr) OR (A.hr = 'Z' AND B.hr = 'Z') OR (A.hr = 'Z' AND G.hr = B.hr))
AND G.grade = B.grade AND A.cno <> B.cno AND A.day = B.day AND A.per = B.per;
	
--Problem 11
SELECT A.cno, A.day, A.per
FROM Assigned AS A, Assigned AS B
WHERE A.cno = B.cno AND A.day = B.day AND A.per <> B.per;

--Problem 12
SELECT A.cno, (MIN(nper) - COUNT(per)) AS NotYetAssigned
FROM Assigned AS A, Courses AS C
WHERE A.cno = C.cno
GROUP BY A.cno
HAVING MIN(nper) > COUNT(per);

--Problem 13
SELECT DISTINCT A.tname, A.subj
FROM (Taught_by NATURAL INNER JOIN Courses) AS A
WHERE A.subj = ALL (SELECT B.subj 
	FROM (Taught_by NATURAL INNER JOIN Courses) AS B WHERE A.tname = B.tname);

--Problem 14
SELECT DISTINCT tname, day, per
INTO Teacher_time
FROM Taught_by NATURAL INNER JOIN Assigned
ORDER BY tname, day, per;

SELECT tname, day, COUNT(per)
INTO Sort_window
FROM Teacher_time
GROUP BY tname, day
HAVING COUNT(per) > 1 AND COUNT(per) < 4 AND MAX(per) - MIN(per) > 1;

SELECT T.tname, T.day, (MAX(T.per)-MIN(T.per) - 1) AS windows
INTO Teacher_window1
FROM Sort_window AS A, Teacher_time AS T
WHERE A.count = 2 AND A.tname = T.tname AND A.day = T.day
GROUP BY T.tname, T.day
HAVING MAX(T.per) - MIN(T.per) > 1
ORDER BY T.tname, T.day;

SELECT T.tname, T.day, 1 AS windows
INTO Teacher_window2
FROM Sort_window AS A, Teacher_time AS T
WHERE A.count = 3 AND A.tname = T.tname AND A.day = T.day
GROUP BY T.tname, T.day
HAVING MAX(T.per) - MIN(T.per) > 2 
ORDER BY T.tname, T.day;

SELECT *
INTO Windows
FROM Teacher_window1 UNION (SELECT * FROM Teacher_window2)
ORDER BY tname, day, windows;

--Problem 15
SELECT tname, SUM(windows) AS total_windows
INTO More_windows
FROM Windows
GROUP BY tname
ORDER BY tname;

SELECT DISTINCT tname, 0 AS total_windows
INTO No_windows
FROM Teacher_time
WHERE Teacher_time.tname NOT IN (SELECT tname FROM Windows);

SELECT *
INTO Total_windows
FROM More_windows UNION (SELECT * FROM No_windows)
ORDER BY tname;

SELECT *
From Total_windows;

--Probem 16
SELECT SUM(T.total_windows) AS total_number_windows
FROM Total_windows AS T;

--Problem 17
SELECT DISTINCT A.cno, B.day, B.per
FROM (Taught_by NATURAL INNER JOIN Assigned) AS A, (Taught_by NATURAL INNER JOIN Assigned) AS B
WHERE A.tname = B.tname AND A.cno <> B.cno
ORDER BY A.cno, B.day, B.per;

--Problem 18
SELECT DISTINCT A.cno, G.grade, G.hr, A.day, A.per
INTO total_course_table
FROM (Taught_to NATURAL INNER JOIN assigned) AS A, Grades AS G 
WHERE A.grade = G.grade AND (A.hr = G.hr OR A.hr = 'Z')
ORDER BY A.cno, G.grade, G.hr, A.day, A.per;

SELECT DISTINCT A.cno, B.day, B.per
FROM total_course_table AS A, total_course_table AS B
WHERE A.cno<>B.cno AND A.grade = B.grade AND A.hr = B.hr
ORDER BY A.cno, B.day, B.per;

--Problem 19
SELECT tname, day, COUNT(per) AS number_of_period
INTO count_temp
FROM Taught_by NATURAL INNER JOIN Assigned
GROUP BY tname, day
ORDER BY tname, day;

SELECT A.tname, MAX(number_of_period)
INTO Max_temp
FROM count_temp AS A
GROUP BY A.tname
ORDER BY A.tname;

SELECT count_temp.tname, day, max
FROM count_temp INNER JOIN Max_temp ON count_temp.tname = Max_temp.tname 
	AND count_temp.number_of_period = Max_temp.max
ORDER BY count_temp.tname, day;

