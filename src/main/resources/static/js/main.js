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


const printStar = (point, changed) => {
    point = Number(point);
    console.log('별 개수 (점수)', point)
    let result = `<div class="rating ${changed ? 'rating-md' : 'rating-sm'} rating-half ml-[-10px]" id="ratingContainer">`;

    for (let i = 0; i < 11; i++) {
        const star =  (i % 2 !== 0) ? 1 : 2;
        if (i === 0) {
            result += `<input type="radio" name="rating-10" class="rating-hidden" `;
        } else {
            result += `<input type="radio" name="rating-10" class="mask mask-star-2 mask-half-${star} bg-green-500" `;
        }
        if (i === point) {
            result += `checked="checked"`;
        }
        if (changed) {
            result += ` disabled `;
        }
        result += ` />`;
    }
    result += `</div>`;
    return result;
}