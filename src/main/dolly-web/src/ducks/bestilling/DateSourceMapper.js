const DataSourceMapper = data => {
	switch (data) {
		case 'SIGRUN':
			return 'sigrunRequest'
		case 'KRR':
			return 'krrRequest'
		default:
			return 'tpsf'
	}
}

export default DataSourceMapper
