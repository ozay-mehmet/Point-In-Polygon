document.addEventListener('DOMContentLoaded', () => {
    const canvas = document.getElementById('pip-canvas');
    const ctx = canvas.getContext('2d');
    const typeSelect = document.getElementById('polygon-type');
    const btnClear = document.getElementById('btn-clear');
    const btnRandom = document.getElementById('btn-random');
    
    const statTotal = document.getElementById('stat-total');
    const statInside = document.getElementById('stat-inside');
    const statOutside = document.getElementById('stat-outside');

    let polygon = [];
    let points = []; 
    let animationId;

    // Theme Config
    const originX = canvas.width / 2;
    const originY = canvas.height / 2;
    const scale = 25; 
    
    const theme = {
        grid: 'rgba(255, 255, 255, 0.05)',
        polyFill: 'rgba(168, 85, 247, 0.15)', // Violet transparent
        polyStroke: '#a855f7', // Violet
        ray: 'rgba(236, 72, 153, 0.3)', // Pinkish ray
        intersect: '#fcd34d', // Amber
        inside: '#2dd4bf', // Teal
        outside: '#f43f5e' // Rose
    };

    function generateConvex() {
        const poly = [];
        const n = 10;
        const radius = 5.0;
        for (let i = 0; i < n; i++) {
            const angle = 2 * Math.PI * i / n;
            poly.push({ x: radius * Math.cos(angle), y: radius * Math.sin(angle) });
        }
        return poly;
    }

    function generateConcave() {
        const poly = [];
        const rOuter = 8.0;
        const rInner = 3.0;
        const arms = 6;
        for (let i = 0; i < arms * 2; i++) {
            const angle = Math.PI * i / arms;
            const r = (i % 2 === 0) ? rOuter : rInner;
            poly.push({ x: r * Math.cos(angle), y: r * Math.sin(angle) });
        }
        return poly;
    }

    function updatePolygon() {
        polygon = typeSelect.value === 'convex' ? generateConvex() : generateConcave();
        recalculatePoints();
    }

    function checkPointInPolygon(px, py) {
        let isInside = false;
        let intersections = [];
        
        for (let i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
            const xi = polygon[i].x, yi = polygon[i].y;
            const xj = polygon[j].x, yj = polygon[j].y;
            
            const intersect = ((yi > py) !== (yj > py))
                && (px < (xj - xi) * (py - yi) / (yj - yi) + xi);
                
            if (intersect) {
                isInside = !isInside;
                const intersectX = (xj - xi) * (py - yi) / (yj - yi) + xi;
                intersections.push(intersectX);
            }
        }
        return { isInside, intersections };
    }

    function recalculatePoints() {
        let insideCount = 0;
        points.forEach(p => {
            const result = checkPointInPolygon(p.logicalX, p.logicalY);
            p.isInside = result.isInside;
            p.intersections = result.intersections;
            if (p.isInside) insideCount++;
        });
        
        // Animate counter update
        statTotal.textContent = points.length;
        statInside.textContent = insideCount;
        statOutside.textContent = points.length - insideCount;
    }

    // Animation Loop
    function draw() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        // Draw Grid
        ctx.strokeStyle = theme.grid;
        ctx.lineWidth = 1;
        for (let i = 0; i < canvas.width; i += scale) {
            ctx.beginPath(); ctx.moveTo(i, 0); ctx.lineTo(i, canvas.height); ctx.stroke();
        }
        for (let i = 0; i < canvas.height; i += scale) {
            ctx.beginPath(); ctx.moveTo(0, i); ctx.lineTo(canvas.width, i); ctx.stroke();
        }

        // Draw Polygon with Glow
        if (polygon.length > 0) {
            ctx.beginPath();
            ctx.moveTo(originX + polygon[0].x * scale, originY - polygon[0].y * scale);
            for (let i = 1; i < polygon.length; i++) {
                ctx.lineTo(originX + polygon[i].x * scale, originY - polygon[i].y * scale);
            }
            ctx.closePath();
            
            ctx.shadowColor = theme.polyStroke;
            ctx.shadowBlur = 15;
            ctx.fillStyle = theme.polyFill;
            ctx.fill();
            
            ctx.lineWidth = 2;
            ctx.strokeStyle = theme.polyStroke;
            ctx.stroke();
            
            ctx.shadowBlur = 0; // reset shadow
        }

        // Draw Points with pop-in animation
        const now = Date.now();
        
        points.forEach(p => {
            const age = now - p.createdAt;
            const animProgress = Math.min(age / 400, 1); 
            // Easing function (easeOutBack)
            const easeObj = 1 + 2.70158 * Math.pow(animProgress - 1, 3) + 1.70158 * Math.pow(animProgress - 1, 2);
            const r = Math.max(0, 6 * easeObj); // Radius animation

            const canvasX = originX + p.logicalX * scale;
            const canvasY = originY - p.logicalY * scale;

            if (animProgress > 0.5) {
                // Draw Ray (fades in)
                ctx.beginPath();
                ctx.moveTo(canvasX, canvasY);
                ctx.lineTo(canvas.width, canvasY); 
                ctx.strokeStyle = theme.ray;
                ctx.setLineDash([4, 6]);
                ctx.globalAlpha = (animProgress - 0.5) * 2;
                ctx.stroke();
                ctx.setLineDash([]);
                ctx.globalAlpha = 1;

                // Draw Intersections
                p.intersections.forEach(ix => {
                    const ixCanvas = originX + ix * scale;
                    ctx.beginPath();
                    ctx.arc(ixCanvas, canvasY, 4 * animProgress, 0, Math.PI * 2);
                    ctx.fillStyle = theme.intersect;
                    ctx.shadowColor = theme.intersect;
                    ctx.shadowBlur = 10;
                    ctx.fill();
                    ctx.shadowBlur = 0;
                });
            }

            // Draw Point
            ctx.beginPath();
            ctx.arc(canvasX, canvasY, r, 0, Math.PI * 2);
            ctx.fillStyle = p.isInside ? theme.inside : theme.outside;
            ctx.shadowColor = p.isInside ? theme.inside : theme.outside;
            ctx.shadowBlur = 15 * animProgress;
            ctx.fill();
            ctx.strokeStyle = '#ffffff';
            ctx.lineWidth = 1.5;
            ctx.stroke();
            ctx.shadowBlur = 0;
        });

        animationId = requestAnimationFrame(draw);
    }

    // Event Listeners
    typeSelect.addEventListener('change', () => {
        updatePolygon();
    });

    btnClear.addEventListener('click', () => {
        points = [];
        recalculatePoints();
    });

    btnRandom.addEventListener('click', () => {
        const now = Date.now();
        for(let i=0; i<100; i++) {
            const rx = (Math.random() - 0.5) * 20; 
            const ry = (Math.random() - 0.5) * 20; 
            points.push({ 
                logicalX: rx, 
                logicalY: ry,
                createdAt: now + (i * 10) // Staggered animation
            });
        }
        recalculatePoints();
    });

    canvas.addEventListener('click', (e) => {
        const rect = canvas.getBoundingClientRect();
        const mouseX = e.clientX - rect.left;
        const mouseY = e.clientY - rect.top;

        const logicalX = (mouseX - originX) / scale;
        const logicalY = -(mouseY - originY) / scale;

        points.push({ 
            logicalX, 
            logicalY,
            createdAt: Date.now()
        });
        recalculatePoints();
    });

    // Initial Setup
    updatePolygon();
    animationId = requestAnimationFrame(draw);
});
