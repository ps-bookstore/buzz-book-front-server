const progressBar = document.querySelector(".nav-progress-bar");

let scrollNum = 0;
let documentHeight = 0;

// 전체 문서에서 얼마나 스크롤되었는지 계산
const getPercent = (scroll, total) => {
    return (scroll / total) * 100;
};

const updateProgressBar = () => {
    scrollNum = document.documentElement.scrollTop; // 또는 window.scrollY 사용 가능
    documentHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight;
    progressBar.style.width = getPercent(scrollNum, documentHeight) + "%";
};

window.addEventListener("scroll", updateProgressBar);

document.addEventListener('DOMContentLoaded', () => {
    const currentUrl = window.location.pathname;
    const menuLinks = document.querySelectorAll('.menu a');

    menuLinks.forEach(link => {
        if (link.getAttribute('href') === currentUrl) {
            link.classList.add('active');
        }
    });
});