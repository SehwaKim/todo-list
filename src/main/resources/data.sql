insert into task (id, content, status, created_at) values (100, '양말 신기', 'DONE', now());
insert into task (id, content, status, created_at) values (101, '목도리 하기', 'TODO', now());
insert into task (id, content, status, created_at) values (102, '모자 쓰기', 'TODO', now());
insert into task (id, content, status, created_at) values (103, '장갑 끼기', 'TODO', now());
insert into task (id, content, status, created_at) values (104, '이어폰 챙기기', 'TODO', now());
insert into task (id, content, status, created_at) values (105, '토스트 먹기', 'TODO', now());

insert into task_dependency (parent_task_id, child_task_id) values (100, 105);
insert into task_dependency (parent_task_id, child_task_id) values (101, 104);
insert into task_dependency (parent_task_id, child_task_id) values (102, 103);
insert into task_dependency (parent_task_id, child_task_id) values (101, 103);
