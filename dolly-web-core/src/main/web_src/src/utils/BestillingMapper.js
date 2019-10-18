const BestillingMapper = data => {
	switch (data) {
		case 'EKSIDENT':
			return 'eksIdent'
		case 'MAL':
			return 'mal'
		default:
			return 'nyIdent'
	}
}

export default BestillingMapper
