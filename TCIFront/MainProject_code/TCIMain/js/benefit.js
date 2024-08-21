document.addEventListener('DOMContentLoaded', () => {
    const updateContent = (category) => {
        const title = category.getAttribute('data-title');
        const description = category.getAttribute('data-description');
        const image = category.getAttribute('data-image');
        
        document.getElementById('benefits__title').textContent = title;
        document.getElementById('benefits__description').textContent = description;
        
        const backgroundImage = document.getElementById('background-image');
        backgroundImage.classList.remove('zoom-out');
        void backgroundImage.offsetWidth; // Force reflow
        backgroundImage.src = image;
        backgroundImage.classList.add('zoom-out');
    };

    // Initial setup
    const initialCategory = document.querySelector('.benefit__category.selected');
    if (initialCategory) {
        updateContent(initialCategory);
    }

    // Set up button click events
    document.querySelectorAll('.benefit__category').forEach(category => {
        category.addEventListener('click', () => {
            document.querySelectorAll('.benefit__category').forEach(cat => cat.classList.remove('selected'));
            category.classList.add('selected');
            updateContent(category);
        });
    });
});
