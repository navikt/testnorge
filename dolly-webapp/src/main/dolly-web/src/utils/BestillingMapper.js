const BestillingMapper = data => {
	switch (data) {
		case 'EKSIDENT':
			return 'eksIdent'
		default:
			return 'nyIdent'
	}
}

export default BestillingMapper
