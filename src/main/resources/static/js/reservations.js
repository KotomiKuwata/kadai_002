document.addEventListener('DOMContentLoaded', function() {
	const dateInput = document.getElementById('reservationDate');
	const hourSelect = document.getElementById('reservationTime');
	const form = document.querySelector('form[data-store-id]');

	if (!form) return;
	const storeId = form.dataset.storeId;

	// Flatpickrの設定
	const fp = flatpickr(dateInput, {
		dateFormat: "Y-m-d",
		minDate: "today",
		locale: "ja",
		disableMobile: true,

		monthSelectorType: "dropdown",
		monthElement: {
			id: "monthSelect",
			name: "monthSelect"
		},

		yearElement: {
			id: "yearInput",
			name: "yearInput"
		},
		onChange: async function(selectedDates) {
			if (selectedDates.length > 0) {
				await updateAvailableHours(selectedDates[0]);
			}
		}
	});

	async function updateAvailableHours(selectedDate) {
  try {
    // 選択された日付をYYYY-MM-DD形式に変換（この部分を最初に移動）
    const formattedDate = selectedDate.toLocaleDateString('ja-JP', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    }).split('/').join('-');

    // 選択された日付が今日かどうかチェック
    const today = new Date();
    const selectedDay = new Date(selectedDate);

    if (today.toDateString() === selectedDay.toDateString()) {
      // 現在の時間を取得
      const currentHour = today.getHours();
      const currentMinutes = today.getMinutes();

      // APIからの応答を待つ
      const response = await fetch(`/api/stores/${storeId}/available-hours?date=${formattedDate}`);
      const data = await response.json();

      // 現在時刻以降の予約可能時間のみをフィルタリング
      const filteredHours = data.availableHours.filter(time => {
        const [hour, minutes] = time.split(':').map(Number);
        return hour > currentHour || (hour === currentHour && minutes > currentMinutes);
      });

      if (filteredHours.length === 0) {
        hourSelect.innerHTML = '<option value="">本日の予約可能な時間帯がありません</option>';
        return;
      }

      hourSelect.innerHTML = '<option value="">選択してください</option>';
      filteredHours.forEach(time => {
        const option = document.createElement('option');
        option.value = time;
        option.textContent = time;
        hourSelect.appendChild(option);
      });
      return;
    }

    const response = await fetch(`/api/stores/${storeId}/available-hours?date=${formattedDate}`);
    const data = await response.json();

    if (data.isClosed) {
      hourSelect.innerHTML = '<option value="">定休日</option>';
      return;
    }

    hourSelect.innerHTML = '<option value="">選択してください</option>';
    data.availableHours.forEach(time => {
      const option = document.createElement('option');
      option.value = time;
      option.textContent = time;
      hourSelect.appendChild(option);
    });
  } catch (error) {
    console.error('Error:', error);
    hourSelect.innerHTML = '<option value="">エラーが発生しました</option>';
  }
}
});