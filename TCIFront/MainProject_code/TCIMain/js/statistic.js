window.onload = function () {
    const chartConfigs = [
      //chart.js chart 예시3개
      {
        ctxId: 'statistic__traffic-chart--1',
        type: 'bar',
        labels: ['일주서로', '산록남로', '제성로', '1100로'],
        datasets: [
          { label: '5분', data: [4, 2, 1, 5], bgColor: 'rgba(255, 99, 132, 0.2)', borderColor: 'rgba(255, 99, 132, 1)' },
          { label: '15분', data: [3, 2, 2, 4], bgColor: 'rgba(54, 162, 235, 0.2)', borderColor: 'rgba(54, 162, 235, 1)' },
          { label: '1시간', data: [2, 1, 2, 6], bgColor: 'rgba(75, 192, 192, 0.2)', borderColor: 'rgba(75, 192, 192, 1)' }
        ]
      },
      {
        ctxId: 'statistic__traffic-chart--2',
        type: 'bar',
        labels: ['월', '화', '수', '목', '금', '토', '일'],
        datasets: [
          { label: '평균 교통량', data: [12, 15, 13, 14, 10, 9, 8], bgColor: 'rgba(255, 159, 64, 0.2)', borderColor: 'rgba(255, 159, 64, 1)' }
        ]
      },
      {
        ctxId: 'statistic__traffic-chart--3',
        type: 'bar',
        labels: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
        datasets: [
          { label: '월간 교통량', data: [30, 25, 35, 40, 50, 55, 60, 70, 65, 50, 45, 35], bgColor: 'rgba(153, 102, 255, 0.2)', borderColor: 'rgba(153, 102, 255, 1)' }
        ]
      }
    ];
    //chart 버튼 클릭하기
    chartConfigs.forEach(config => {
      const ctx = document.getElementById(config.ctxId).getContext('2d');
      new Chart(ctx, {
        type: config.type,
        data: {
          labels: config.labels,
          datasets: config.datasets.map(dataset => ({
            label: dataset.label,
            data: dataset.data,
            backgroundColor: dataset.bgColor,
            borderColor: dataset.borderColor,
            borderWidth: 1
          }))
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: { beginAtZero: true }
          }
        }
      });
    });
  
    const buttons = document.querySelectorAll('.statistic__menu-item');
    const carousel = document.querySelector('.statistic__carousel');
    //버튼 시 캐러셀 효과
    buttons.forEach((button, index) => {
      button.addEventListener('click', function() {
        buttons.forEach(btn => btn.classList.remove('selected'));
        button.classList.add('selected');
        carousel.style.transform = `translateX(${-index * 100}%)`;
      });
    });
  };