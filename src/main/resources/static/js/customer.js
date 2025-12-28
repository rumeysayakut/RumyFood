// Global Değişkenler
let currentUser = JSON.parse(localStorage.getItem('currentUser'));
let customerProfileId = null;
let allFoods = [];

// --- Başlangıç ve ID Çözümleme --- [cite: 2025-12-27]
document.addEventListener('DOMContentLoaded', async () => {
    if (!currentUser) window.location.href = 'login.html';
    document.getElementById('customerName').innerText = currentUser.firstName;
    
    // Customer Profile ID'sini al (Cart işlemleri için şart)
    try {
        const res = await fetch(`/customer-profiles/by-user/${currentUser.id}`);
        const profile = await res.json();
        customerProfileId = profile.id;
    } catch (e) { console.error("Profil ID alınamadı"); }

    loadStats();
});

window.logout = () => { localStorage.removeItem('currentUser'); window.location.href = 'login.html'; };

window.showSection = function(id) {
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    document.getElementById(id + 'Section').classList.add('active');
    document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
    event.currentTarget.classList.add('active');

    if (id === 'menu') loadMenu();
    if (id === 'cart') loadCart();
    if (id === 'profile') loadProfileInfo();
};

// --- Anasayfa İstatistikleri (GÜNCELLENDİ) ---
async function loadStats() {
    try {
        // 1. Kullanıcı Sayısı
        const oRes = await fetch('/api/owner-profiles');
        const cRes = await fetch('/customer-profiles');
        document.getElementById('statUsers').innerText = (await oRes.json()).length + (await cRes.json()).length;

        // 2. Satılan Toplam Yemek (YENİ HESAPLAMA)
        const fRes = await fetch('/api/foods');
        const foods = await fRes.json();
        
        let totalSales = 0;
        foods.forEach(f => {
            // soldCount null gelirse 0 kabul et
            totalSales += (f.soldCount || 0);
        });
        document.getElementById('statSales').innerText = totalSales;

        // 3. Son eklenen 5 yemek
        const container = document.getElementById('lastFoodsContainer');
        container.innerHTML = '';
        foods.slice(-5).reverse().forEach(f => {
            container.innerHTML += `
                <div class="col-md-2">
                    <div class="card h-100 shadow-sm">
                        <img src="/uploads/${f.imageUrl}" class="card-img-top" style="height:100px; object-fit:cover;" onerror="this.src='https://via.placeholder.com/150'">
                        <div class="card-body p-2 text-center"><small class="fw-bold">${f.name}</small></div>
                    </div>
                </div>`;
        });
    } catch(e) { console.error(e); }
}

// --- Menü ve Sepete Ekleme --- [cite: 2025-12-15]
async function loadMenu() {
    const res = await fetch('/api/foods');
    allFoods = await res.json();
    renderFoods(allFoods);
}

function renderFoods(foods) {
    const container = document.getElementById('menuContainer');
    container.innerHTML = '';
    foods.forEach(f => {
        container.innerHTML += `
            <div class="col-md-4">
                <div class="card h-100 shadow-sm border-0">
                    <img src="/uploads/${f.imageUrl}" class="card-img-top food-img" style="height: 200px; object-fit: cover;" onerror="this.src='https://via.placeholder.com/300'">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title">${f.name}</h5>
                        <p class="text-muted small">${f.description || ''}</p>
                        <div class="mt-auto d-flex justify-content-between align-items-center">
                            <span class="badge bg-warning text-dark">${f.category}</span>
                            <button class="btn btn-outline-danger" onclick="addToCart(${f.id})">
                                <i class="fas fa-plus"></i> Sepete Ekle
                            </button>
                        </div>
                    </div>
                </div>
            </div>`;
    });
}

window.filterMenu = (cat, btn) => {
    document.querySelectorAll('#menuSection .btn-group button, #menuSection .btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    const filtered = cat === 'All' ? allFoods : allFoods.filter(f => f.category === cat);
    renderFoods(filtered);
};

window.addToCart = async (foodId) => {
    if(!customerProfileId) return alert("Profil yükleniyor, bekleyin...");
    
    // Backend CartController POST isteği
    const res = await fetch(`/cart/add?customerId=${customerProfileId}&foodId=${foodId}&quantity=1`, {
        method: 'POST'
    });
    
    if(res.ok) {
        Swal.fire({ icon: 'success', title: 'Sepete Eklendi', timer: 1000, showConfirmButton: false });
    }
};

// --- Sepet İşlemleri --- [cite: 2025-12-15]
async function loadCart() {
    if(!customerProfileId) return;
    const res = await fetch(`/cart/customer/${customerProfileId}`);
    const items = await res.json();
    
    const tbody = document.getElementById('cartTableBody');
    tbody.innerHTML = '';
    
    if(items.length === 0) tbody.innerHTML = '<tr><td colspan="3" class="text-center">Sepetiniz boş.</td></tr>';

    items.forEach(item => {
        // Resim kontrolü eklendi
        const imgUrl = item.food.imageUrl ? `/uploads/${item.food.imageUrl}` : 'https://via.placeholder.com/50';
        
        tbody.innerHTML += `
            <tr>
                <td>
                    <div class="d-flex align-items-center">
                        <img src="${imgUrl}" style="width:50px; height:50px; object-fit:cover; border-radius:5px;" class="me-3">
                        <div>
                            <h6 class="m-0">${item.food.name}</h6>
                            <small class="text-muted">${item.food.description || ''}</small>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-secondary" onclick="updateQty(${item.id}, ${item.quantity - 1})">-</button>
                        <button class="btn btn-light" disabled>${item.quantity}</button>
                        <button class="btn btn-outline-secondary" onclick="updateQty(${item.id}, ${item.quantity + 1})">+</button>
                    </div>
                </td>
                <td>
                    <button class="btn btn-sm btn-danger" onclick="deleteItem(${item.id})"><i class="fas fa-trash"></i></button>
                </td>
            </tr>`;
    });
}

window.updateQty = async (itemId, newQty) => {
    if(newQty < 1) return;
    await fetch(`/cart/${itemId}?quantity=${newQty}`, { method: 'PUT' });
    loadCart();
};

window.deleteItem = async (itemId) => {
    await fetch(`/cart/${itemId}`, { method: 'DELETE' });
    loadCart();
};

// --- Satın Alma (GÜNCELLENDİ) ---
window.checkout = async () => {
    if(!customerProfileId) return;

    try {
        // Backend'deki yeni checkout endpoint'i
        const res = await fetch(`/cart/checkout/${customerProfileId}`, {
            method: 'POST'
        });

        if (res.ok) {
            Swal.fire({
                title: 'Sipariş Alındı! 🎉',
                text: 'Afiyet olsun! Satış sayacı güncellendi.',
                icon: 'success',
                confirmButtonText: 'Tamam'
            }).then(() => {
                // Sepeti görsel olarak temizle
                document.getElementById('cartTableBody').innerHTML = '<tr><td colspan="3" class="text-center">Sepetiniz boş.</td></tr>';
                // İstatistikleri (satış sayacını) güncelle
                loadStats();
            });
        } else {
            Swal.fire('Hata', 'Sepet boş veya işlem yapılamadı.', 'error');
        }
    } catch (error) {
        console.error("Satın alma hatası:", error);
    }
};

// --- Profil --- [cite: 2025-12-20]
function loadProfileInfo() {
    document.getElementById('userId').value = currentUser.id;
    document.getElementById('profName').value = currentUser.firstName;
    document.getElementById('profSurname').value = currentUser.lastName;
    document.getElementById('profEmail').value = currentUser.email;
}

document.getElementById('customerProfileForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('userId').value;
    const data = {
        firstName: document.getElementById('profName').value,
        lastName: document.getElementById('profSurname').value,
        email: document.getElementById('profEmail').value,
        password: currentUser.password,
        role: 'CUSTOMER'
    };
    
    const res = await fetch(`/api/users/${id}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    });
    
    if(res.ok) {
        const updatedUser = await res.json();
        localStorage.setItem('currentUser', JSON.stringify(updatedUser));
        currentUser = updatedUser;
        alert("Profil güncellendi!");
    }
});