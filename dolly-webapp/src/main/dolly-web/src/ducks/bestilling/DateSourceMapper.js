const DataSourceMapper = data => {
	switch (data) {
		case 'SIGRUN':
			return 'sigrunRequest'
		case 'TPSF':
		default:
			return 'tpsf'
	}
}

export default DataSourceMapper
