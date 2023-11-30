3팀 MQTT PROJECT

조원 : 유승진, 이지현, 임찬휘, 조강일, 홍충표

인터페이스 구성:

Executable.java
???
JSONObject를 관리함
보충 필요
???

Input.java
wireIn 함수를 정의한다.

Output.java
wireOut 함수를 정의한다.

클래스 구성 :

Config.java


Node.java
아이디와 쓰레드 그리고 밀리초 시간을 가지고 있음,
모든 노드의 조상

FunctionNode.java
Node-Red의 Function 블럭과 같은 기능을 하며
이전 노드에서 값을 받아오는 inWires,
다음 노드로 값을 보내는 outWires가 있으며
Wire를 통해 들어오거나 나가는 값들을 받아와 보내는 기본 클래스이다.

DebugNode.java
Node-Red의 디버그 블럭과 같은 기능을 하며
와이어 셋을 가지고 있으며
와이어 셋에 와이어를 넣어
와이어 셋을 계속 반복을 돌면서 각 와이어에 BlockingQueue에 메시지가 있으면 메시지를 출력한다.

