# 🚀 Paralel Ray Casting Algoritması ile Nokta-İçinde-Poligon (Point-in-Polygon) Tespiti

## 📌 Proje Hakkında

Bu proje, **Paralel Programlama** dersi kapsamında geliştirilmiş olup, bir noktanın verilen bir poligonun içinde mi yoksa dışında mı olduğunu belirlemek için kullanılan **Ray Casting (Işın İzleme)** algoritmasının hem **ardışık (sequential)** hem de **paralel (parallel)** sürümlerini içermektedir.

Projenin temel amacı, büyük miktarda nokta verisi üzerinde gerçekleştirilen Point-in-Polygon hesaplamalarında paralel programlama tekniklerinin performansa olan etkisini incelemek ve karşılaştırmaktır.

---

## 🎥 Proje Tanıtım Videosu

📹 Proje videosuna aşağıdaki bağlantıdan ulaşabilirsiniz:

**Video Linki:**
[https://youtu.be/VHv8HkVzL3w]

---

## 📄 Teknik Rapor

📑 Proje teknik raporuna aşağıdaki bağlantıdan ulaşıp indirebilirsiniz:

**Teknik Rapor:**
[https://github.com/ozay-mehmet/Point-In-Polygon/blob/main/Mehmet%20%C3%96zay-22360859062-Paralel-Programlama.pdf]

---

# 🎯 Problem Tanımı

Bilgisayar grafiklerinde, coğrafi bilgi sistemlerinde (GIS), oyun geliştirmede ve robotik uygulamalarda sıkça karşılaşılan problemlerden biri, bir noktanın belirli bir poligonun içinde olup olmadığının belirlenmesidir.

Bu projede:

* Dışbükey (Convex) poligonlar
* İçbükey (Concave) poligonlar
* Rastgele oluşturulmuş test noktaları

üzerinde Ray Casting algoritması uygulanarak sonuçlar elde edilmiştir.

---

# 📚 Kullanılan Algoritma: Ray Casting

Ray Casting algoritması Point-in-Polygon probleminin çözümünde en yaygın kullanılan yöntemlerden biridir.

## Çalışma Prensibi

1. Test edilecek noktadan sağa doğru sonsuza uzanan yatay bir ışın çizilir.
2. Bu ışının poligon kenarlarıyla kesişme sayısı hesaplanır.
3. Kesişim sayısı:

* Tek ise → Nokta poligonun içindedir.
* Çift ise → Nokta poligonun dışındadır.

### Örnek

```text
     ***********
    *           *
    *     ●     *
    *           *
     ***********
```

Burada ● noktası poligonun içerisindedir.

---

# ⚡ Ardışık (Sequential) Çözüm

Ardışık çözümde tüm noktalar tek bir iş parçacığı (thread) tarafından işlenmektedir.

Her nokta için:

```text
Nokta Al
    ↓
Ray Casting Hesapla
    ↓
Sonucu Kaydet
```

Bu yöntem basit ve anlaşılır olmasına rağmen büyük veri kümelerinde performans açısından yetersiz kalabilmektedir.

### Zaman Karmaşıklığı

```text
O(P × N)

P = Nokta Sayısı
N = Poligon Kenar Sayısı
```

---

# ⚡ Paralel Çözüm

Paralel çözümde noktalar belirli parçalara bölünerek birden fazla iş parçacığına dağıtılmaktadır.

Her thread kendi nokta grubunu bağımsız olarak işlemektedir.

### Kullanılan Teknolojiler

* Java Thread Yapısı
* Executor Framework
* ForkJoinPool
* Callable
* Future

### İş Akışı

```text
Noktalar
   │
   ├── Thread 1
   ├── Thread 2
   ├── Thread 3
   └── Thread N
          │
          ▼
   Ray Casting Hesaplamaları
          │
          ▼
       Sonuçlar
```

Bu yaklaşım sayesinde işlem yükü çekirdekler arasında dağıtılarak toplam çalışma süresi azaltılmaktadır.

---

# 🏗️ Proje Yapısı

```text
PointInPolygon_Kaynak/
│
├── Main.java
├── BenchmarkRunner.java
├── SequentialSolver.java
├── ParallelSolver.java
├── RayCasting.java
├── PolygonUtils.java
└── Point.java

---

# 📂 Sınıf Açıklamaları

## Point.java

İki boyutlu nokta yapısını temsil eder.

Özellikler:

```java
double x;
double y;
```

Örnek:

```java
Point p = new Point(10.5, 20.3);
```

---

## RayCasting.java

Point-in-Polygon işlemlerinin gerçekleştirildiği temel sınıftır.

Ana metod:

```java
boolean isInside(Point point, List<Point> polygon)
```

Dönüş değeri:

```text
true  → Nokta içeride
false → Nokta dışarıda
```

---

## SequentialSolver.java

Ray Casting algoritmasının ardışık sürümünü içerir.

Görevleri:

* Noktaları sırayla işlemek
* Sonuçları hesaplamak
* Çalışma süresini ölçmek

---

## ParallelSolver.java

Ray Casting algoritmasının paralel sürümünü içerir.

Görevleri:

* İş yükünü thread'lere bölmek
* Paralel hesaplama yapmak
* Sonuçları birleştirmek
* Çalışma süresini ölçmek

---

## PolygonUtils.java

Test verilerinin oluşturulmasını sağlar.

### Sağladığı İşlevler

* Dışbükey poligon oluşturma
* İçbükey poligon oluşturma
* Rastgele nokta üretme
* Test senaryoları hazırlama

---

## Main.java

Projenin çalıştırıldığı ana sınıftır.

Bu sınıfta:

* Poligon oluşturulur
* Noktalar üretilir
* Sequential algoritma çalıştırılır
* Parallel algoritma çalıştırılır
* Sonuçlar karşılaştırılır

---

## BenchmarkRunner.java

Performans testlerinin gerçekleştirildiği sınıftır.

### Test Senaryoları

Farklı nokta sayıları:

```text
100
500
1000
5000
10000
50000
```

Farklı thread sayıları:

```text
1
2
4
8
```

Sonuçlar ortalama çalışma süreleri üzerinden değerlendirilmektedir.

---

# 📊 Performans Analizi

Proje kapsamında aşağıdaki metrikler karşılaştırılmıştır:

* Çalışma Süresi (ms)
* Hızlanma Katsayısı (Speedup)
* Ölçeklenebilirlik (Scalability)

### Speedup Hesabı

```text
Speedup = Sequential Süre / Parallel Süre
```

Elde edilen değer ne kadar yüksekse paralel uygulamanın başarısı o kadar yüksektir.

---

# ▶️ Projenin Çalıştırılması

Proje iki ana bileşenden oluşmaktadır: Java tabanlı algoritma ve analiz uygulaması (`PointInPolygon_Kaynak`) ile modern web tabanlı kullanıcı arayüzü (`web-ui`).

## 1. Java Uygulamasının (Main ve Benchmark) Çalıştırılması

Java kaynak kodları `PointInPolygon_Kaynak` dizini altındadır.

### Derleme

Öncelikle kaynak kodların bulunduğu dizine gidip kodları derleyin:

```bash
cd PointInPolygon_Kaynak
javac *.java
```

### Ana Programı Çalıştırma (Main)

Poligon oluşturma ve paralel vs ardışık çözüm karşılaştırmasını görmek için ana programı çalıştırabilirsiniz:

```bash
java Main
```
*(Not: Java 11 ve üzeri bir sürüm kullanmıyorsanız önce derlemelisiniz)*

### Benchmark Testlerini Çalıştırma

Farklı nokta ve thread sayıları ile geniş çaplı performans testlerini (Benchmark) başlatmak için:

```bash
java BenchmarkRunner
```

## 2. Web Arayüzünün (Web-UI) Çalıştırılması

Web arayüzü, poligon ve noktaları interaktif olarak oluşturmanıza ve test etmenize olanak tanır. Herhangi bir derlemeye ihtiyaç duymaz. `web-ui` klasörü altında bulunur.

### Seçenek A: VS Code "Live Server" Eklentisi ile (Önerilen)
1. `web-ui` klasöründeki `index.html` dosyasını VS Code ile açın.
2. Sağ tıklayıp **"Open with Live Server"** seçeneğini seçerek tarayıcıda görüntüleyin.

### Seçenek B: Python ile
Eğer bilgisayarınızda Python yüklüyse, terminal üzerinden sunucu başlatabilirsiniz:
```bash
cd web-ui
python -m http.server 8000
```
Ardından tarayıcınızdan `http://localhost:8000` adresine gidebilirsiniz.

### Seçenek C: Node.js (npx) ile
```bash
cd web-ui
npx serve
```
Terminalde belirtilen adrese (genellikle `http://localhost:3000`) tıklayarak arayüze erişebilirsiniz.

---

# 💻 Gereksinimler

* Java JDK 17 veya üzeri
* Çok çekirdekli işlemci (önerilir)

Java sürümünü kontrol etmek için:

```bash
java --version
```

---

# 🎓 Kazanımlar

Bu proje sayesinde aşağıdaki konular uygulamalı olarak incelenmiştir:

* Paralel Programlama
* İş Parçacığı Yönetimi (Thread Management)
* Executor Framework
* ForkJoinPool
* Performans Analizi
* Nokta-İçinde-Poligon Problemi
* Hesaplamalı Geometri (Computational Geometry)
* Speedup ve Ölçeklenebilirlik Analizi

---

# 🔮 Gelecekte Yapılabilecek Geliştirmeler

* GPU destekli hesaplama (CUDA/OpenCL)
* Gerçek zamanlı görselleştirme arayüzü
* Çokgen içerisinde boşluk (hole) desteği
* Dinamik yük dengeleme
* Dağıtık sistem desteği
* Büyük veri kümeleri üzerinde optimizasyon

---
