const BestillingMapper = data => {
	switch (data) {
		case 'eksIdent':
			return 'eksIdent'
		default:
			return 'nyIdent'
	}
}

export default BestillingMapper
