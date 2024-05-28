var socket = new SockJS('/ws');
var stompClient = Stomp.over(socket);
var canvas = document.getElementById('canvas');

stompClient.connect({}, function (frame) {
    console.log('Connected to WebSocket');
    stompClient.subscribe('/topic/nodes', function (messageOutput) {
        var nodePosition = JSON.parse(messageOutput.body);
        updateNodePosition(nodePosition.nodeId, nodePosition.x, nodePosition.y);
    });

    // 프로젝트의 노드들 초기 상태 로드
    fetch('/api/nodes/' + projectId)
        .then(response => response.json())
        .then(nodes => {
            nodes.forEach(node => {
                createOrUpdateNode(node);
            });
        });
}, function (error) {
    console.error('Error connecting to WebSocket:', error);
});

function updateNodePosition(nodeId, x, y) {
    var nodeElement = document.getElementById('node-' + nodeId);
    if (!nodeElement) {
        nodeElement = createNodeElement(nodeId);
        canvas.appendChild(nodeElement);
    }
    nodeElement.style.left = x + 'px';
    nodeElement.style.top = y + 'px';
}

function createNodeElement(nodeId) {
    var nodeElement = document.createElement('div');
    nodeElement.id = 'node-' + nodeId;
    nodeElement.classList.add('node');
    nodeElement.draggable = true;
    nodeElement.ondragend = function(event) {
        var x = event.clientX;
        var y = event.clientY;
        stompClient.send("/app/moveNode", {}, JSON.stringify({'nodeId': nodeId, 'x': x, 'y': y}));
    };
    return nodeElement;
}

function createOrUpdateNode(node) {
    var nodeElement = document.getElementById('node-' + node.id);
    if (!nodeElement) {
        nodeElement = createNodeElement(node.id);
        canvas.appendChild(nodeElement);
    }
    nodeElement.style.left = node.position.x + 'px';
    nodeElement.style.top = node.position.y + 'px';
    nodeElement.innerHTML = `
        <div>${node.text}</div>
        <img src="${node.imageURL}" alt="Node Image" style="max-width: 100px; max-height: 100px;">
    `;
}
