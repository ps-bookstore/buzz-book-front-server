const contents = document.querySelector(".contents");
const buttons = document.querySelector(".buttons");

const numOfContent = 175;
const showButton = 5;
const maxContent = 5;
const maxPage = Math.ceil(numOfContent / maxContent);
const maxButton = 5;
let page = 1;

const reviewCount = document.getElementById('review-count');
reviewCount.textContent = numOfContent.toString();

const dataList = [];

const getRandomInt = (min, max) => {
  return Math.floor(Math.random() * (max - min + 1)) + min;
};

const getDataList = async () => {
  // 서버 연결 시 아래의 for문을 주석처리
  // ------------------------------
  for (let i = 0; i < numOfContent; i++) {
    dataList.push({
      id: `나는야성호${i + 1}`,
      time: "2024.01.01",
      point: getRandomInt(1, 5),
      review: "ㅈㄴ노잼이네",
    });
  }
  // 서버연결하기---------------------
  try {
    const response = await axios.get("https://httpbin.org/get");
    console.log(response.data); // 요청이 성공한 경우

  } catch (error) {
    console.error(error); // 오류 처리
  }
  console.log(dataList);
};

const goPrevPage = () => {
  page -= maxButton;
  render(page);
};

const goNextPage = () => {
  page += maxButton;
  render(page);
};

const prev = document.createElement("button");
prev.classList.add("button");
prev.innerHTML = "&laquo;";
prev.addEventListener("click", goPrevPage);

const next = document.createElement("button");
next.classList.add("button");
next.innerHTML = "&raquo";
next.addEventListener("click", goNextPage);

const makeContent = (index) => {
  const content = document.createElement("div");
  let htmlObj = ``;

  htmlObj += `
    <div class="testimonial-item img-border-radius bg-light rounded p-4">
        <div class="position-relative">
          <div style="display: flex; flex-direction: column">
            <div class="review-list-title">
              <p class="text-dark">${dataList[index].id}</p>
              <div>`;
  for (let i = 0; i < dataList[index].point; i++) {
    htmlObj += `<i class="fas fa-star text-primary"></i>`;
  }
  for (let i = dataList[index].point; i < 5; i++) {
    htmlObj += `<i class="fas fa-star"></i>`;
  }

  htmlObj += `</div>
              <p class="review-time">${dataList[index].time}</p>
              </div>
            <p class="pt-2">
              ${dataList[index].review}
            </p>
          </div>
        </div>
      </div>
  `;
  content.innerHTML = htmlObj;
  return content;
};

const makeButton = (id) => {
  const button = document.createElement("button");
  button.classList.add("button");
  button.dataset.num = id;
  button.innerText = id;
  button.addEventListener("click", (e) => {
    Array.prototype.forEach.call(buttons.children, (button) => {
      if (button.dataset.num) button.classList.remove("active");
    });
    e.target.classList.add("active");
    renderContent(parseInt(e.target.dataset.num));
  });
  return button;
};

const renderContent = (page) => {
  getDataList();
  while (contents.hasChildNodes()) {
    contents.removeChild(contents.lastChild);
  }
  let index = (page - 1) * maxContent;

  for (let i = index; i < page * maxContent && i < numOfContent; i++) {
    contents.appendChild(makeContent(i));
  }
};

const renderButton = (page) => {
  while (buttons.hasChildNodes()) {
    buttons.removeChild(buttons.lastChild);
  }
  for (let id = page; id < page + maxButton && id <= maxPage; id++) {
    buttons.appendChild(makeButton(id));
  }
  buttons.children[0].classList.add("active");

  buttons.prepend(prev);
  buttons.append(next);

  if (page - maxButton < 1) buttons.removeChild(prev);
  if (page + maxButton > maxPage) buttons.removeChild(next);
};

const render = (page) => {
  renderContent(page);
  renderButton(page);
};

render(page);
