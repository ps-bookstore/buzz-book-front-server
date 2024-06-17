document.addEventListener('DOMContentLoaded', function () {
    var modal = document.querySelector('.review-modal');
    var btnOpenModal = document.querySelector('.btn-primary');
    var modalContent = document.querySelector('.review-modal-content');

    // 모달 열기
    btnOpenModal.addEventListener('click', function () {
        modal.style.display = 'block';
    });

    // 모달 바깥 영역 클릭 시 모달 닫기
    window.onclick = function (event) {
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    };
});


// const reviewEditor = new toastui.Editor({
//     el: document.querySelector('#review-editor'), // 에디터를 초기화할 요소
//     initialEditType: 'wysiwyg', // 초기 입력 모드 설정 ('markdown' 또는 'wysiwyg')
//     previewStyle: 'vertical', // 미리보기 스타일 ('vertical', 'horizontal')
//     // height: '200px', // 에디터 높이 설정
//     toolbarItems: [ // 툴바 아이템 설정
//         ['heading', 'bold', 'italic', 'strike'],
//         ['hr', 'quote'],
//         // 여기에 추가적인 툴바 아이템을 설정할 수 있습니다.
//     ],
//     // 여기에 필요한 추가 옵션을 추가할 수 있습니다.
// });
