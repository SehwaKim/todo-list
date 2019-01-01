# todo list
## 소개
스프링 부트와 리액트로 만든 todo list 앱
## 빌드환경
- jdk 1.8
- Maven 3.5.3
## 빌드방법
1. 터미널 접속
2. git clone https://github.com/SehwaKim/todo-list.git
3. git clone이 끝나면 clone된 프로젝트로 이동(cd todo-list) 
4. mvn install
5. java -jar target/todo-list-0.0.1-SNAPSHOT.jar
6. localhost:8080 으로 접속
## 사용방법
![main](./screenshot.png)
- todo 완료시키기
    - 내용을 더블클릭한다.
- 완료된 todo를 다시 미완료로 되돌리기
    - 줄그어진 내용을 다시 더블클릭한다.
- 새 todo 생성하기
    - 상단의 텍스트 입력란에 내용 입력 후 '일정추가' 버튼 클릭
    - 참조할 todo를 추가하고 싶으면 '참조 todo 추가' 체크박스 선택 후 각 todo 아이템을 맨 왼쪽의 체크박스 선택으로 고른다.
- 수정하기
    - 내용 옆에 연필 모양 아이콘 클릭, 상단의 텍스트 입력란에서 내용 수정 후 '수정하기' 버튼 클릭
    - 각 todo의 왼쪽 체크박스를 이용해서 참조하고 있는 TODO들을 추가, 삭제도 할 수 있다.
- 삭제하기
    - 각 todo의 맨 오른쪽 X 버튼을 클릭해서 지운다.
## 제약사항
- 참조하고 있는 todo들이 모두 완료 상태가 아니라면 todo를 완료할 수 없다.
- 어떤 todo를 삭제하려고 할 때 그 todo를 참조하는 todo가 하나라도 있으면 선행조건이기 때문에 삭제할 수 없다.
- 완료된 todo를 미완료로 바꿀 때, 그 todo를 참조하고 있던 todo들 중 완료상태였던 todo들도 다시 미완료 상태로 돌아간다.
- todo 간의 순환참조는 일어나지 않는다. 예를 들어 todo 1 -> todo 2 -> todo 3 -> todo 1 이런식으로 서로 순환참조를 하게되면 위의 제약사항에 의해 어떠한 todo도 완료상태가 될 수 없게 되기 때문이다.