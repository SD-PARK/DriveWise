document.addEventListener('DOMContentLoaded', function() {
  const scrollIndicators = document.querySelectorAll('.scroll-indicator');
  scrollIndicators.forEach(scrollIndicator => {
    scrollIndicator.addEventListener('click', function() {
      fullpage_api.moveSectionDown();
    });
  });
});
