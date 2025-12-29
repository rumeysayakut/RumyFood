async function loadAllFoods() {
    try {
        const res = await fetch('/api/foods');
        if (!res.ok) throw new Error("Veri çekilemedi");
        
        const foods = await res.json();
        const container = document.getElementById('adminFoodContainer');
        if (!container) return; // Container yoksa çalışma
        
        container.innerHTML = '';
        
        foods.forEach(f => {
            // 1. ADIM: Tüm verileri güvenli hale getir (Null Check)
            const name = f.name || "İsimsiz";
            const category = f.category || "Kategorisiz";
            const soldCount = f.soldCount || 0;
            const id = f.id;
            
            // 2. ADIM: İç içe geçmiş objeyi (ownerProfile) güvenli kontrol et
            // Eğer ownerProfile null ise 'Bilinmiyor' yaz, hata verme.
            const restaurantName = (f.ownerProfile && f.ownerProfile.restaurantName) 
                                   ? f.ownerProfile.restaurantName 
                                   : 'Bilinmiyor';

            // 3. ADIM: Arama verisini oluştururken hepsini birleştir ve küçük harfe çevir
            const searchData = `${name} ${category} ${restaurantName}`.toLowerCase();
            
            const imgUrl = f.imageUrl ? `/uploads/${f.imageUrl}` : 'https://via.placeholder.com/150';

            container.innerHTML += `
                <div class="col-md-3 food-card-item" data-search="${searchData}">
                    <div class="card h-100 shadow-sm border-0">
                        <img src="${imgUrl}" class="card-img-top" style="height: 150px; object-fit: cover;" 
                             onerror="this.src='https://via.placeholder.com/150'">
                        <div class="card-body">
                            <h5 class="card-title fw-bold">${name}</h5>
                            <p class="small text-muted mb-1"><i class="fas fa-store"></i> ${restaurantName}</p>
                            <div class="d-flex justify-content-between align-items-center mt-2">
                                <span class="badge bg-secondary">${category}</span>
                                <span class="badge bg-success">Satış: ${soldCount}</span>
                            </div>
                            <button class="btn btn-danger btn-sm w-100 mt-3" onclick="deleteFoodAdmin(${id})">Sistemden Sil</button>
                        </div>
                    </div>
                </div>`;
        });
    } catch (e) { 
        console.error("Yemekler yüklenemedi:", e); 
        const container = document.getElementById('adminFoodContainer');
        if(container) container.innerHTML = `<div class="alert alert-danger">Veriler yüklenirken bir hata oluştu: ${e.message}</div>`;
    }
}