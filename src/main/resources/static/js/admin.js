// --- Navigasyon ---
window.showSection = function(id) {
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    document.getElementById(id + 'Section').classList.add('active');
    document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
    event.currentTarget.classList.add('active');

    if(id === 'home') loadStats(); // Anasayfa açılınca istatistikleri yükle
    if(id === 'users') loadUsers();
    if(id === 'foods') loadAllFoods();
};

window.logout = () => { localStorage.removeItem('currentUser'); window.location.href = 'login.html'; };

// --- İstatistikler (YENİ EKLENDİ) ---
async function loadStats() {
    try {
        // 1. Toplam Kullanıcı Sayısı
        const uRes = await fetch('/api/users');
        const users = await uRes.json();
        document.getElementById('statTotalUsers').innerText = users.length;

        // 2. Gerçekleşen Toplam Satış
        const fRes = await fetch('/api/foods');
        const foods = await fRes.json();
        
        let totalSales = 0;
        foods.forEach(f => {
            totalSales += (f.soldCount || 0);
        });
        document.getElementById('statTotalSales').innerText = totalSales;

    } catch (e) { console.error("İstatistik hatası:", e); }
}

// --- Kullanıcı İşlemleri ---
let allUsers = [];

async function loadUsers() {
    try {
        const res = await fetch('/api/users');
        allUsers = await res.json();
        renderUsers(allUsers);
    } catch (e) { console.error("Kullanıcılar yüklenemedi", e); }
}

function renderUsers(users) {
    const tbody = document.getElementById('userTableBody');
    tbody.innerHTML = '';
    users.forEach(u => {
        // Admin kendini silmesin diye kontrol
        if(u.role === 'ADMIN') return; 

        tbody.innerHTML += `
            <tr>
                <td>${u.id}</td>
                <td>${u.firstName} ${u.lastName}</td>
                <td>${u.email}</td>
                <td><span class="badge ${u.role === 'OWNER' ? 'bg-warning text-dark' : 'bg-info'}">${u.role}</span></td>
                <td><button class="btn btn-sm btn-danger" onclick="deleteUser(${u.id})"><i class="fas fa-trash"></i> Sil</button></td>
            </tr>`;
    });
}

window.filterUsers = () => {
    const role = document.getElementById('roleFilter').value;
    const filtered = role === 'ALL' ? allUsers : allUsers.filter(u => u.role === role);
    renderUsers(filtered);
};

window.deleteUser = async (id) => {
    if(confirm("Bu kullanıcı sistemden tamamen silinecek! Emin misiniz?")) {
        const res = await fetch(`/api/users/${id}`, { method: 'DELETE' });
        if(res.ok) loadUsers();
    }
};

// --- Yemek İşlemleri (HATA BURADA DÜZELTİLDİ) ---
async function loadAllFoods() {
    try {
        const res = await fetch('/api/foods');
        const foods = await res.json();
        const container = document.getElementById('adminFoodContainer');
        container.innerHTML = '';
        
        foods.forEach(f => {
            // HATA ÇÖZÜMÜ: Veri null gelse bile boş string kabul et
            const safeName = (f.name || "").toLowerCase();
            const safeCategory = (f.category || "").toLowerCase();
            const restaurantName = f.ownerProfile ? f.ownerProfile.restaurantName : 'Bilinmiyor';
            const imgUrl = f.imageUrl ? `/uploads/${f.imageUrl}` : 'https://via.placeholder.com/150';

            container.innerHTML += `
                <div class="col-md-3 food-card-item" data-search="${safeName} ${safeCategory} ${restaurantName.toLowerCase()}">
                    <div class="card h-100 shadow-sm border-0">
                        <img src="${imgUrl}" class="card-img-top" style="height: 150px; object-fit: cover;" onerror="this.src='https://via.placeholder.com/150'">
                        <div class="card-body">
                            <h5 class="card-title fw-bold">${f.name || 'İsimsiz'}</h5>
                            <p class="small text-muted mb-1"><i class="fas fa-store"></i> ${restaurantName}</p>
                            <div class="d-flex justify-content-between align-items-center mt-2">
                                <span class="badge bg-secondary">${f.category || 'Yok'}</span>
                                <span class="badge bg-success">Satış: ${f.soldCount || 0}</span>
                            </div>
                            <button class="btn btn-danger btn-sm w-100 mt-3" onclick="deleteFoodAdmin(${f.id})">Sistemden Sil</button>
                        </div>
                    </div>
                </div>`;
        });
    } catch (e) { console.error("Yemekler yüklenemedi", e); }
}

window.searchFoods = () => {
    // Arama yaparken de null kontrolü önemli
    const inputVal = document.getElementById('foodSearch');
    if (!inputVal) return;
    
    const val = inputVal.value.toLowerCase();
    document.querySelectorAll('.food-card-item').forEach(el => {
        const searchData = el.getAttribute('data-search') || "";
        el.style.display = searchData.includes(val) ? '' : 'none';
    });
};

window.deleteFoodAdmin = async (id) => {
    if(confirm("Bu yemek sistemden silinecek?")) {
        const res = await fetch(`/api/foods/${id}`, { method: 'DELETE' });
        if(res.ok) loadAllFoods();
    }
};

// Başlangıçta Anasayfayı (İstatistikleri) Yükle
document.addEventListener('DOMContentLoaded', () => {
    loadStats();
});