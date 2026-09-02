
        /* =========================================
           JAVASCRIPT LOGIC
           ========================================= */

        // Navigation Logic
        function navigate(sectionId, linkElement) {
            // 1. Hide all sections
            document.querySelectorAll('section').forEach(sec => {
                sec.classList.remove('active-section');
            });

            // 2. Show target section
            document.getElementById(sectionId).classList.add('active-section');

            // 3. Update Sidebar Active State
            document.querySelectorAll('.nav-links a').forEach(link => {
                link.classList.remove('active');
            });
            if (linkElement && linkElement.classList) linkElement.classList.add('active');

            // Toggle home background visibility (keep it steady)
            const bg = document.querySelector('.page-bg');
            if (bg) {
                if (sectionId === 'home') bg.classList.add('visible');
                else bg.classList.remove('visible');
            }

            // Close sidebar on mobile after selection
            if (window.innerWidth <= 768) {
                toggleSidebar();
            }
        }

        // Mobile Sidebar Toggle
        function toggleSidebar() {
            document.getElementById('sidebar').classList.toggle('open');
        }

        // Create and manage a persistent, fixed background element for Home so it stays steady
        (function createHomeBg(){
            if (document.querySelector('.page-bg')) return; // already present
            const bg = document.createElement('div');
            bg.className = 'page-bg';
            document.body.appendChild(bg);
            // Show it initially if home is active
            const active = document.querySelector('section.active-section');
            if (active && active.id === 'home') bg.classList.add('visible');
        })();

        // Toast Notification System
        function showToast(message, type = 'info') {
            const container = document.getElementById('toast-container');
            const toast = document.createElement('div');
            toast.className = `toast ${type}`;
            
            let icon = type === 'success' ? 'fa-check-circle' : 'fa-info-circle';
            
            toast.innerHTML = `
                <i class="fas ${icon}"></i>
                <span>${message}</span>
            `;

            container.appendChild(toast);

            // Remove after 3 seconds (animation timing matches CSS)
            setTimeout(() => {
                toast.remove();
            }, 3000);
        }

        // Booking Simulation Logic
        let isAnimating = false;

        function startBookingSimulation() {
            const pickup = document.getElementById('pickup').value;
            const dropoff = document.getElementById('dropoff').value;
            const cab = document.getElementById('cab');
            const statusBadge = document.getElementById('cab-status');

            // Simple Validation
            if (!pickup || !dropoff) {
                showToast("Please enter both pickup and drop-off locations.", "info");
                return;
            }

            if (isAnimating) return; // Prevent double clicks
            isAnimating = true;

            // Reset Animation
            cab.classList.remove('driving');
            cab.style.left = '0px'; // Force reset
            void cab.offsetWidth; // Trigger reflow

            // Step 1: Driver Found
            showToast("Searching for nearby drivers...", "info");
            statusBadge.innerText = "Connecting...";
            
            setTimeout(() => {
                // Step 2: Start Driving Animation
                cab.classList.add('driving');
                statusBadge.innerText = "Heading to Pickup";
                
                // Step 3: Reached Pickup (approx 45% of animation time)
                setTimeout(() => {
                    statusBadge.innerText = "You are in the car";
                    
                    // Step 4: Reached Destination (approx 90% of animation time)
                    setTimeout(() => {
                        statusBadge.innerText = "Arrived!";
                        showToast(`Ride completed to ${dropoff}. Thank you!`, "success");
                        isAnimating = false;
                    }, 2000); // Wait for remaining drive time
                }, 1800); // Wait for pickup time
                
            }, 1000); // Wait for "searching" simulation
        }

        // Contact Form Logic
        function submitContact() {
            const name = document.getElementById('contact-name').value;
            const email = document.getElementById('contact-email').value;
            const msg = document.getElementById('contact-msg').value;

            if(name && email && msg) {
                showToast("Message sent successfully! We'll reply soon.", "success");
                // Clear form
                document.getElementById('contact-name').value = '';
                document.getElementById('contact-email').value = '';
                document.getElementById('contact-msg').value = '';
            } else {
                showToast("Please fill in all fields.", "info");
            }
        }

        // Ensure sidebar links work with URL fragments and browser navigation
        window.addEventListener('DOMContentLoaded', function() {
            // Navigate to hash if present on load
            if (location.hash) {
                const id = location.hash.replace('#', '');
                const link = document.querySelector(`.nav-links a[href="#${id}"]`);
                navigate(id, link);
            }
        });

        window.addEventListener('hashchange', function() {
            const id = location.hash.replace('#', '') || 'home';
            const link = document.querySelector(`.nav-links a[href="#${id}"]`);
            navigate(id, link);
        });
    