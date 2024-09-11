
const stripe = Stripe('pk_test_51PjGIyFRY5VG4shkpqJOgbBIKa42NRQ4ZHuP5C03X6btIJnLFaAunpdOBc9t9fc04txhW8BiGFvdjkQIxb15gk0D00fYp1C0Uy');
const paymentButton = document.querySelector('#paymentButton');
console.log(sessionId);
paymentButton.addEventListener('click', () => {
	stripe.redirectToCheckout({
		sessionId: sessionId
	})
});
/*
const stripe = Stripe('pk_test_51PjGIyFRY5VG4shkpqJOgbBIKa42NRQ4ZHuP5C03X6btIJnLFaAunpdOBc9t9fc04txhW8BiGFvdjkQIxb15gk0D00fYp1C0Uy');
const paymentButton = document.getElementById('paymentButton');

paymentButton.addEventListener('click', (e) => {
	e.preventDefault();
	const sessionId = paymentButton.getAttribute('data-session-id');
	stripe.redirectToCheckout({ sessionId: sessionId })
		.then((result) => {
			if (result.error) {
				alert(result.error.message);
			}
		})
		.catch((error) => {
			console.error('Error:', error);
		});
});*/
