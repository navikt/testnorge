const DataSourceMapper = data => {
	switch (data) {
		case 'SIGRUN':
			return 'sigrunstub'
		case 'TPSF':
		default:
			return 'tpsf'
	}
}

export default DataSourceMapper
