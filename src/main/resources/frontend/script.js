const ws = new WebSocket("ws://localhost:8081/weather");

const cityInput = document.getElementById("cityInput");
const cityDisplay = document.getElementById("cityDisplay");
const tempDisplay = document.getElementById("tempDisplay");
const descDisplay = document.getElementById("descDisplay");
const iconDisplay = document.getElementById("iconDisplay");

let animationLayer = null;

// Send selected city to server immediately
cityInput.addEventListener("change", () => {
    const city = cityInput.value.trim();
    if (city) ws.send(JSON.stringify({ city }));
});

// Clear previous animations
function clearAnimations() {
    if (animationLayer) {
        animationLayer.remove();
        animationLayer = null;
    }
}

// Rain animation
function addRain() {
    clearAnimations();
    animationLayer = document.createElement("div");
    animationLayer.className = "rain";
    for (let i = 0; i < 100; i++) {
        const drop = document.createElement("div");
        drop.style.left = Math.random() * 100 + "%";
        drop.style.animationDuration = 0.5 + Math.random() * 0.5 + "s";
        drop.style.height = 10 + Math.random() * 10 + "px";
        animationLayer.appendChild(drop);
    }
    document.body.appendChild(animationLayer);
}

// Snow animation
function addSnow() {
    clearAnimations();
    animationLayer = document.createElement("div");
    animationLayer.className = "snow";
    for (let i = 0; i < 50; i++) {
        const flake = document.createElement("div");
        flake.style.left = Math.random() * 100 + "%";
        flake.style.animationDuration = 2 + Math.random() * 3 + "s";
        flake.style.width = flake.style.height = 3 + Math.random() * 5 + "px";
        animationLayer.appendChild(flake);
    }
    document.body.appendChild(animationLayer);
}

// Sun animation
function addSun() {
    clearAnimations();
    animationLayer = document.createElement("div");
    animationLayer.className = "sun";
    document.body.appendChild(animationLayer);
}

// Update background based on weather type
function updateBackground(weatherMain) {
    clearAnimations();
    switch (weatherMain.toLowerCase()) {
        case 'clear':
            document.body.style.background = "linear-gradient(to right, #f6d365, #fda085)";
            addSun();
            break;
        case 'clouds':
            document.body.style.background = "linear-gradient(to right, #bdc3c7, #2c3e50)";
            break;
        case 'rain':
        case 'drizzle':
            document.body.style.background = "linear-gradient(to right, #4e54c8, #8f94fb)";
            addRain();
            break;
        case 'thunderstorm':
            document.body.style.background = "linear-gradient(to right, #141e30, #243b55)";
            addRain();
            break;
        case 'snow':
            document.body.style.background = "linear-gradient(to right, #7f8c8d, #34495e)"; // darker snow
            addSnow();
            break;
        case 'mist':
        case 'fog':
            document.body.style.background = "linear-gradient(to right, #757f9a, #d7dde8)";
            break;
        default:
            document.body.style.background = "linear-gradient(to right, #2980b9, #6dd5fa)";
    }
}

// Handle messages from WebSocket (multicast by city)
ws.onmessage = (event) => {
    const data = JSON.parse(event.data);

    if (data.error) {
        cityDisplay.textContent = "Error: " + data.error;
        tempDisplay.textContent = "-- °C";
        descDisplay.textContent = "";
        iconDisplay.src = "";
        clearAnimations();
        return;
    }

    cityDisplay.textContent = data.name;
    tempDisplay.textContent = data.main.temp + " °C";
    descDisplay.textContent = data.weather[0].description;
    iconDisplay.src = `https://openweathermap.org/img/wn/${data.weather[0].icon}@2x.png`;

    updateBackground(data.weather[0].main);
};

// Fetch initial city immediately
window.addEventListener("load", () => {
    const city = cityInput.value.trim();
    if (city) ws.send(JSON.stringify({ city }));
});
