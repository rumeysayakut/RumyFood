/**
 * RumyFood - Owner Dashboard Master
 */

// --- 1. GLOBAL NAVİGASYON VE ÇIKIŞ ---
window.showSection = function(id) {
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    document.getElementById(id + 'Section').classList.add('active');
    
    // Sidebar link aktifliği
    document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
    event.currentTarget.classList.add('active');

    // Bölüme göre veri çek
    if (id === 'myFoods') loadOwnerFoods();
    if (id === 'profile') loadProfile();
    if (id === 'home') loadStats();
};

window.logout = () => { localStorage.removeItem('currentUser'); window.location.href = 'login.html'; };

window.previewImage = (input) => {
    const container = document.getElementById('imagePreviewContainer');
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = e => container.innerHTML = `<img src="${e.target.result}" class="img-thumbnail" style="height:100px;">`;
        reader.readAsDataURL(input.files[0]);
    }
};

// --- 2. YEMEK LİSTELEME --- [cite: 2025-12-15]
async function loadOwnerFoods() {
    const user = JSON.parse(localStorage.getItem('currentUser'));
    const ownerId = user?.ownerProfile?.id;
    if (!ownerId) return;

    try {
        const res = await fetch(`/api/foods/owner/${ownerId}`);
        const foods = await res.json();
        const body = document.getElementById('myFoodsTableBody');
        body.innerHTML = '';
        foods.forEach(f => {
            const imgUrl = f.imageUrl ? `/uploads/${f.imageUrl}` : 'https://via.placeholder.com/50';
            body.innerHTML += `
                <tr>
                    <td><img src="${imgUrl}" style="width:50px; height:50px; object-fit:cover;"></td>
                    <td>${f.name}</td>
                    <td><span class="badge bg-warning badge-category">${f.category || "Diger"}</span></td>
                    <td>${f.address}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary" onclick="editFood(${f.id})">Düzenle</button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteFood(${f.id})">Sil</button>
                    </td>
                </tr>`;
        });
    } catch (e) { console.error("Liste yükleme hatası:", e); }
}

window.filterMyFoods = () => {
    const val = document.getElementById('filterCategory').value;
    document.querySelectorAll('#myFoodsTableBody tr').forEach(row => {
        row.style.display = (val === 'All' || row.cells[2].textContent.trim() === val) ? '' : 'none';
    });
};

// --- 3. YEMEK EKLEME (POST) --- [cite: 2025-12-15]
document.getElementById('addFoodForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();
    const user = JSON.parse(localStorage.getItem('currentUser'));
    const ownerId = user?.ownerProfile?.id;

    const fd = new FormData();
    fd.append('name', document.getElementById('foodName').value);
    fd.append('category', document.getElementById('foodCategory').value);
    fd.append('description', document.getElementById('foodDescription').value);
    fd.append('address', document.getElementById('foodAddress').value);
    fd.append('imageFile', document.getElementById('foodImage').files[0]);

    const res = await fetch(`/api/foods/owner/${ownerId}`, { method: 'POST', body: fd });
    if (res.ok) {
        alert("Başarıyla eklendi!");
        this.reset();
        document.getElementById('imagePreviewContainer').innerHTML = ''; // Önizlemeyi temizle
        window.showSection('myFoods');
    }
});

// --- 4. GÜNCELLEME VE SİLME --- [cite: 2025-12-15]
window.editFood = async function(id) {
    try {
        const res = await fetch(`/api/foods/${id}`);
        const food = await res.json();
        document.getElementById('editFoodId').value = food.id;
        document.getElementById('editFoodName').value = food.name;
        document.getElementById('editFoodCategory').value = food.category;
        document.getElementById('editFoodDescription').value = food.description || "";
        document.getElementById('editFoodAddress').value = food.address;
        new bootstrap.Modal(document.getElementById('editFoodModal')).show();
    } catch (e) { alert("Hata: Veri çekilemedi"); }
};

document.getElementById('editFoodForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();
    const user = JSON.parse(localStorage.getItem('currentUser'));
    const foodId = document.getElementById('editFoodId').value;
    const fd = new FormData();
    fd.append('name', document.getElementById('editFoodName').value);
    fd.append('category', document.getElementById('editFoodCategory').value);
    fd.append('description', document.getElementById('editFoodDescription').value);
    fd.append('address', document.getElementById('editFoodAddress').value);
    if (document.getElementById('editFoodImage').files[0]) fd.append('imageFile', document.getElementById('editFoodImage').files[0]);

    const res = await fetch(`/api/foods/${foodId}/owner/${user.ownerProfile.id}`, { method: 'PUT', body: fd });
    if (res.ok) {
        alert("Güncellendi!");
        bootstrap.Modal.getInstance(document.getElementById('editFoodModal')).hide();
        loadOwnerFoods();
    }
});

window.deleteFood = async function(id) {
    const user = JSON.parse(localStorage.getItem('currentUser'));
    if (confirm("Silinsin mi?")) {
        const res = await fetch(`/api/foods/${id}/owner/${user.ownerProfile.id}`, { method: 'DELETE' });
        if (res.ok) loadOwnerFoods();
    }
};

// --- 5. PROFİL YÖNETİMİ --- [cite: 2025-12-20]
async function loadProfile() {
    const user = JSON.parse(localStorage.getItem('currentUser'));
    try {
        const res = await fetch(`/api/owner-profiles/by-user/${user.id}`);
        const profile = await res.json();
        document.getElementById('profileId').value = profile.id;
        document.getElementById('profileRestName').value = profile.restaurantName || "";
        document.getElementById('profilePhone').value = profile.phone || "";
        document.getElementById('profileDescription').value = profile.description || "";
        document.getElementById('profileUserName').value = user.firstName + " " + user.lastName;
        document.getElementById('profileEmail').value = user.email;
    } catch (e) { console.error("Profil yüklenemedi"); }
}

document.getElementById('updateProfileForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();
    const id = document.getElementById('profileId').value;
    const data = {
        restaurantName: document.getElementById('profileRestName').value,
        phone: document.getElementById('profilePhone').value,
        description: document.getElementById('profileDescription').value
    };
    const res = await fetch(`/api/owner-profiles/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    if (res.ok) alert("Profil güncellendi!");
});

// --- 6. İSTATİSTİKLER (GÜNCELLENDİ) ---
async function loadStats() {
    try {
        // 1. Kullanıcı Sayısı
        const oRes = await fetch('/api/owner-profiles');
        const cRes = await fetch('/customer-profiles');
        const totalUsers = (await oRes.json()).length + (await cRes.json()).length;
        document.getElementById('statTotalUsers').innerText = totalUsers;

        // 2. Satış Sayısı (YENİ HESAPLAMA)
        const fRes = await fetch('/api/foods');
        const foods = await fRes.json();

        let totalSales = 0;
        foods.forEach(f => {
            totalSales += (f.soldCount || 0);
        });
        document.getElementById('statTotalSales').innerText = totalSales;

        // 3. Son 5 Yemek
        const container = document.getElementById('lastFoodsContainer');
        container.innerHTML = '';

        foods.slice(-5).reverse().forEach(f => {
            const imgUrl = f.imageUrl ? `/uploads/${f.imageUrl}` : 'https://via.placeholder.com/150';
            container.innerHTML += `
                <div class="col-md-2 mb-3">
                    <div class="card h-100 shadow-sm border-0" style="border-radius: 12px; overflow: hidden;">
                        <img src="${imgUrl}" class="card-img-top" 
                             style="height: 120px; object-fit: cover;"
                             onerror="this.src='https://via.placeholder.com/150?text=${f.name}'">
                        <div class="card-body p-2 text-center bg-white">
                            <small class="fw-bold text-dark d-block text-truncate">${f.name}</small>
                            <span class="badge bg-light text-muted" style="font-size: 0.7rem;">${f.category || 'Lezzet'}</span>
                        </div>
                    </div>
                </div>`;
        });
    } catch (e) { 
        console.error("İstatistik yükleme hatası:", e);
        document.getElementById('statTotalUsers').innerText = "Hata"; 
    }
}

// Başlangıç
document.addEventListener('DOMContentLoaded', () => {
    const u = JSON.parse(localStorage.getItem('currentUser'));
    if (u) document.getElementById('ownerWelcomeName').innerText = u.firstName;
    loadStats();
});