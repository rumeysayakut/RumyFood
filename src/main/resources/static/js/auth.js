// ==========================================
// 1. GİRİŞ YAPMA (LOGIN) KISMI
// ==========================================
document.getElementById('loginForm')?.addEventListener('submit', function(e) {
    e.preventDefault();

    const data = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value
    };

    // Backend'e giriş isteği atıyoruz
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
        // Giriş başarılı! Kullanıcıyı tarayıcı hafızasına (Local Storage) kaydedelim
        localStorage.setItem('currentUser', JSON.stringify(user));

        // Rolüne göre sayfaya yönlendirelim
        if(user.role === 'OWNER') window.location.href = 'owner-dash.html';
        else if(user.role === 'CUSTOMER') window.location.href = 'customer-dash.html';
        else if(user.role === 'ADMIN') window.location.href = 'admin-dash.html';
    })
    .catch(error => alert("Giriş Hatası: " + error.message));
});

// ==========================================
// 2. KAYIT OLMA (REGISTER) KISMI
// ==========================================
document.getElementById('registerForm')?.addEventListener('submit', function(e) {
    e.preventDefault();

    // Backend'in beklediği RegisterRequest yapısına uygun veri hazırlıyoruz
    const registerData = {
        firstName: document.getElementById('regName').value,
        lastName: document.getElementById('regSurname').value,
        email: document.getElementById('regEmail').value,
        password: document.getElementById('regPassword').value,
        phone: document.getElementById('regPhone').value,
        role: document.getElementById('regRole').value // HTML'deki select kutusundan gelir
    };

    // Backend'e kayıt isteği atıyoruz
    fetch('/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(registerData)
    })
    .then(async response => {
        if (!response.ok) {
            // Backend hata mesajı dönerse (örn: "Bu mail zaten kayıtlı") yakala
            const errorText = await response.text();
            throw new Error(errorText);
        }
        return response.json();
    })
    .then(data => {
        // Başarılı olursa
        alert("Kayıt Başarılı! 🎉 Giriş sayfasına yönlendiriliyorsunuz...");
        window.location.href = 'login.html';
    })
    .catch(error => {
        // Hata olursa
        alert("Kayıt Hatası: " + error.message);
    });
});