let stompClient = null;

function connect() {
    let socket = new SockJS('/ws-healthwatch');
    stompClient = Stomp.over(socket);
    stompClient.debug = null; // Disable noisy debug logs
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        
        // Subscribe to dashboard updates (all patients)
        stompClient.subscribe('/topic/dashboard', function (vitalReading) {
            updateDashboardVitals(JSON.parse(vitalReading.body));
        });
        
        // Subscribe to alerts
        stompClient.subscribe('/topic/alerts', function (alertMsg) {
            showAlert(JSON.parse(alertMsg.body));
        });
    });
}

function updateDashboardVitals(data) {
    // Update Heart Rate
    let hrElement = document.getElementById('hr-' + data.patientId);
    if (hrElement) hrElement.innerText = data.heartRate + ' bpm';

    // Update Blood Pressure
    let bpElement = document.getElementById('bp-' + data.patientId);
    if (bpElement) bpElement.innerText = data.systolicBp + '/' + data.diastolicBp + ' mmHg';

    // Update SpO2
    let spo2Element = document.getElementById('spo2-' + data.patientId);
    if (spo2Element) spo2Element.innerText = data.spo2 + '%';

    // Update Temperature
    let tempElement = document.getElementById('temp-' + data.patientId);
    if (tempElement) tempElement.innerText = data.temperature + '°C';
    
    // Add flashing effect for abnormal values
    checkThreshold(hrElement, data.heartRate, 60, 100);
    checkThreshold(bpElement, data.systolicBp, 90, 120);
    checkThreshold(spo2Element, data.spo2, 95, 100);
    checkThreshold(tempElement, data.temperature, 36.1, 37.2);
}

function checkThreshold(element, value, min, max) {
    if (!element) return;
    
    element.classList.remove('text-danger', 'text-warning');
    
    if (value < min || value > max) {
        element.classList.add('text-danger');
        element.style.animation = 'flash 1s infinite';
    } else {
        element.style.animation = 'none';
    }
}

function showAlert(alertData) {
    // Create toast notification
    console.log("Alert Received: ", alertData);
    // Simple alert implementation for now
    let alertContainer = document.getElementById('alert-container');
    if (!alertContainer) {
        alertContainer = document.createElement('div');
        alertContainer.id = 'alert-container';
        alertContainer.style.position = 'fixed';
        alertContainer.style.top = '20px';
        alertContainer.style.right = '20px';
        alertContainer.style.zIndex = '9999';
        document.body.appendChild(alertContainer);
    }
    
    let alertClass = 'bg-info';
    if (alertData.severity === 'WARNING') alertClass = 'bg-warning';
    if (alertData.severity === 'CRITICAL') alertClass = 'bg-danger text-white';
    
    let toast = document.createElement('div');
    toast.className = `toast show align-items-center mb-2 border-0 ${alertClass}`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                <strong>${alertData.patientName}</strong>: ${alertData.message}
            </div>
            <button type="button" class="btn-close me-2 m-auto ${alertData.severity === 'CRITICAL' ? 'btn-close-white' : ''}" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;
    
    alertContainer.appendChild(toast);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        toast.remove();
    }, 5000);
}

// Global flash animation
const style = document.createElement('style');
style.innerHTML = `
@keyframes flash {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}
`;
document.head.appendChild(style);

// Connect on load
window.onload = function() {
    connect();
};
