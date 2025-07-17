document.addEventListener("DOMContentLoaded", function() {
    const toggleSwitch = document.getElementById("toggleUnreadSwitch");
    const allList = document.getElementById("all-chats-list");
    const unreadList = document.getElementById("unread-chats-list");

    toggleSwitch.addEventListener("change", function() {
        if (toggleSwitch.checked) {
            unreadList.style.display = "block";
            allList.style.display = "none";
        } else {
            unreadList.style.display = "none";
            allList.style.display = "block";
        }
    });
});
