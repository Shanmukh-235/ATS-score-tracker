document.getElementById("resume").addEventListener("change", function () {
  const fileName = this.files[0] ? this.files[0].name : "No file chosen";
  document.getElementById("file-name").textContent = fileName;
});

document.addEventListener("DOMContentLoaded", function () {
  const scoreSpan = document.getElementById("actual-score");
  if (!scoreSpan) {
    console.error("Score element not found");
    return;
  }

  const scoreValue = parseInt(scoreSpan.textContent.trim());
  if (isNaN(scoreValue)) {
    console.error("Score is not a number:", scoreSpan.textContent);
    return;
  }

  const scoreElement = document.getElementById("animated-score");
  const progressBar = document.getElementById("progress-bar");
  const statusElement = document.getElementById("score-status");

  let currentScore = 0;

  const interval = setInterval(() => {
    if (currentScore >= scoreValue) {
      clearInterval(interval);
    } else {
      currentScore++;
      scoreElement.textContent = currentScore;
      progressBar.style.width = `${currentScore}%`;
    }
  }, 20);

  function updateStatus(score) {
    if (score <= 25) {
      statusElement.textContent = "Very Bad";
      progressBar.style.backgroundColor = "#e53935";
    } else if (score <= 50) {
      statusElement.textContent = "Needs Improvement";
      progressBar.style.backgroundColor = "#fb8c00";
    } else if (score <= 60) {
      statusElement.textContent = "Fair";
      progressBar.style.backgroundColor = "#fdd835";
    } else if (score <= 85) {
      statusElement.textContent = "Good";
      progressBar.style.backgroundColor = "#43a047";
    } else {
      statusElement.textContent = "Excellent";
      progressBar.style.backgroundColor = "#2e7d32";
    }
  }

  updateStatus(scoreValue);
});
