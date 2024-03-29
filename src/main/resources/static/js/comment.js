
//댓글 생성 및 동적 추가 로직
    document.getElementById('commentBtn').addEventListener('click', function() {
        const content = document.getElementById('content').value; // 댓글 내용 가져오기

        if (!content) {
            alert('댓글 내용을 입력해주세요.');
            return;
        }
        let reviewId = window.location.pathname.split('/')[2];
        // 서버에 댓글 내용을 전송하는 AJAX 요청
        fetch(`/api/comment/review/${reviewId}`, { // 실제 댓글을 생성하는 API URL로 변경해주세요.
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ content: content }),
        })
            .then(response => response.json())
            .then(data => {
                // 댓글 목록에 새 댓글 추가
                const commentsList = document.getElementById('comments-list');
                const newComment = document.createElement('div');

                newComment.setAttribute('data-comment-id', data.id);
                newComment.innerHTML = `
           
                    <p><strong>작성자:</strong> <span>${data.username || '익명'}</span></p>
                    <p><strong>댓글:</strong> <span class="comment-content">${data.content}</span>
                    <input type="text" class="comment-edit-field" value="${data.content}" name="content" style="display: none;">
                    </p>
                    <p>작성일: <span>${new Date(data.createdAt).toLocaleString()}</span></p>
                    <button type="button" class="edit-comment-btn">수정</button>
                    <button type="button" class="save-comment-btn" style="display: none;">저장</button>
                    <button type="button" class="delete-comment-btn">삭제</button>
                
            `;
                commentsList.appendChild(newComment);

                // 댓글 입력 필드 초기화
                document.getElementById('content').value = '';
            })
            .catch(error => {
                console.error('댓글 생성 중 오류 발생:', error);
                alert('댓글 생성에 실패했습니다.');
            });
    });


//댓글 수정 로직
document.getElementById('comments-list').addEventListener('click', function(event) {
    // 이벤트가 발생한 요소가 수정 버튼인 경우
    if (event.target.classList.contains('edit-comment-btn')) {
        const button = event.target;
        const commentDiv = event.target.closest('div[data-comment-id]');
        const commentContent = commentDiv.querySelector('.comment-content');
        const editField = commentDiv.querySelector('.comment-edit-field');
        const saveButton = commentDiv.querySelector('.save-comment-btn');

        // 텍스트와 "수정" 버튼 숨기고, 입력 필드와 "저장" 버튼 보이기
        commentContent.style.display = 'none';
        button.style.display = 'none';
        editField.style.display = '';
        saveButton.style.display = '';
        // 수정 관련 로직 여기에 구현...
        // alert('수정 버튼이 클릭되었습니다: ' + commentDiv.getAttribute('data-comment-id'));
    }

    // 이벤트가 발생한 요소가 수정완료 버튼인 경우
    else if (event.target.classList.contains('save-comment-btn')) {
        const button = event.target;
        const commentDiv = event.target.closest('div[data-comment-id]');
        const commentId = commentDiv.getAttribute('data-comment-id');
        const editField = commentDiv.querySelector('.comment-edit-field');
        const commentContent = commentDiv.querySelector('.comment-content');
        const editButton = commentDiv.querySelector('.edit-comment-btn');

        // fetch 요청으로 변경사항 저장
        let reviewId = window.location.pathname.split('/')[2];
        fetch(`/api/comment/review/${reviewId}/${commentId}`, {
            method: 'PUT',
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({content: editField.value}),
        }).then(response => {
            if (response.ok) {
                // 성공 시 UI 업데이트
                commentContent.textContent = editField.value;
                editField.style.display = 'none';
                button.style.display = 'none';
                commentContent.style.display = '';
                editButton.style.display = '';
                alert('수정 완료되었습니다');
            } else {
                alert('수정 실패. 다시 시도해 주세요.');
            }
        }).catch(error => {
            console.error('Error:', error);
            alert('등록 실패. 다시 시도해 주세요.');
        });
    }

    // 이벤트가 발생한 요소가 삭제 버튼인 경우
    else if (event.target.classList.contains('delete-comment-btn')) {
        const commentDiv = event.target.closest('div[data-comment-id]');
        // 삭제 관련 로직 여기에 구현...
        const commentId = commentDiv.getAttribute('data-comment-id');
        let reviewId = window.location.pathname.split('/')[2];

        fetch(`/api/comment/review/${reviewId}/${commentId}`, {
            method: 'DELETE',
        }).then(response => {
            if (response.ok) {
                // 성공 시 댓글 요소 삭제
                commentDiv.remove();
                alert('삭제가 완료되었습니다');
            } else {
                alert('삭제 실패. 다시 시도해 주세요.');
            }
        }).catch(error => {
            console.error('Error:', error);
            alert('삭제 실패. 다시 시도해 주세요.');
        });
    }
});

    // "수정" 버튼 클릭 이벤트 리스너를 모든 "수정" 버튼에 등록
    document.querySelectorAll('.edit-comment-btn').forEach(function (button) {
        button.addEventListener('click', function () {
            const commentDiv = button.closest('div');
            const commentContent = commentDiv.querySelector('.comment-content');
            const editField = commentDiv.querySelector('.comment-edit-field');
            const saveButton = commentDiv.querySelector('.save-comment-btn');

            // 텍스트와 "수정" 버튼 숨기고, 입력 필드와 "저장" 버튼 보이기
            commentContent.style.display = 'none';
            button.style.display = 'none';
            editField.style.display = '';
            saveButton.style.display = '';
        });
    });

    // "저장" 버튼 클릭 이벤트 리스너를 모든 "저장" 버튼에 등록
    document.querySelectorAll('.save-comment-btn').forEach(function (button) {
        button.addEventListener('click', function () {
            const commentDiv = button.closest('div');
            const commentId = commentDiv.getAttribute('data-comment-id');
            const editField = commentDiv.querySelector('.comment-edit-field');
            const commentContent = commentDiv.querySelector('.comment-content');
            const editButton = commentDiv.querySelector('.edit-comment-btn');

            // fetch 요청으로 변경사항 저장
            let reviewId = window.location.pathname.split('/')[2];
            fetch(`/api/comment/review/${reviewId}/${commentId}`, {
                method: 'PUT',
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({content: editField.value}),
            }).then(response => {
                if (response.ok) {
                    // 성공 시 UI 업데이트
                    commentContent.textContent = editField.value;
                    editField.style.display = 'none';
                    button.style.display = 'none';
                    commentContent.style.display = '';
                    editButton.style.display = '';
                    alert('수정 완료되었습니다');
                } else {
                    alert('수정 실패. 다시 시도해 주세요.');
                }
            }).catch(error => {
                console.error('Error:', error);
                alert('등록 실패. 다시 시도해 주세요.');
            });
        });
    });

    // "삭제" 버튼 클릭 이벤트 리스너를 모든 "삭제" 버튼에 등록
    document.querySelectorAll('.delete-comment-btn').forEach(function (button) {
        button.addEventListener('click', function () {
            const commentDiv = button.closest('div');
            const commentId = commentDiv.getAttribute('data-comment-id');
            let reviewId = window.location.pathname.split('/')[2];

            fetch(`/api/comment/review/${reviewId}/${commentId}`, {
                method: 'DELETE',
            }).then(response => {
                if (response.ok) {
                    // 성공 시 댓글 요소 삭제
                    commentDiv.remove();
                    alert('삭제가 완료되었습니다');
                } else {
                    alert('삭제 실패. 다시 시도해 주세요.');
                }
            }).catch(error => {
                console.error('Error:', error);
                alert('삭제 실패. 다시 시도해 주세요.');
            });
        });
    });