// login.html içindeki form için
document.getElementById('loginForm')?.addEventListener('submit', function(e) {
    e.preventDefault();

    const data = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value
    };

    // Port 8086 ve path /auth/login olarak güncellendi
    fetch('/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(async response => {
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText);
        }
        return response.json();
    })
    .then(user => {
        // user nesnesi içinde role, firstName vb. tüm bilgiler gelecek
        localStorage.setItem('currentUser', JSON.stringify(user));
        
        // Rol bazlı yönlendirme
        if(user.role === 'OWNER') window.location.href = 'owner-dash.html';
        else if(user.role === 'CUSTOMER') window.location.href = 'customer-dash.html';
        else if(user.role === 'ADMIN') window.location.href = 'admin-dash.html';
    })
    .catch(error => alert("Hata: " + error.message));
});