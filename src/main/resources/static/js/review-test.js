//TEST용 JS 파일입니다.

const postReviewButton = document.getElementById('post-review-btn');

if (postReviewButton) {
    postReviewButton.addEventListener('click', event => {
        event.preventDefault(); // 기본 폼 제출 방지

        // FormData 객체 생성
        let formData = new FormData();
        formData.append('title', document.getElementById('title').value);
        formData.append('content', document.getElementById('content').value);
        formData.append('restaurantId', document.getElementById('restaurant').value);
        formData.append('rating', document.getElementById('rating').value);
        formData.append('waitingTime', document.getElementById('waitingTime').value);
        formData.append('visitTime', document.getElementById('visitTime').value);
        formData.append('imgFile', document.getElementById('imgFile').files[0]);  // 이미지 파일 추가

        // fetch 요청
        fetch(`/api/review`, {
            method: 'POST',
            body: formData,  // 'Content-Type': 'multipart/form-data' 헤더는 FormData 객체와 함께 자동으로 설정됩니다.
        }).then(response => {
            if (response.ok) {
                alert('등록 완료되었습니다');
                // 성공시 페이지 리디렉션
                //location.replace(`/someSuccessPage`);
            } else {
                alert('등록 실패. 다시 시도해 주세요.');
            }
        }).catch(error => {
            console.error('Error:', error);
            alert('등록 실패. 다시 시도해 주세요.');
        });
    });
}