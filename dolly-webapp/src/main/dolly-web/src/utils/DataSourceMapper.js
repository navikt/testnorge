const DataSourceMapper = data => {
	switch (data) {
		case 'SIGRUN':
			return 'sigrunstub'
		case 'KRR':
			return 'krrstub'
		case 'AAREG':
			return 'aareg'
		case 'PDLF':
			return 'pdlforvalter'
		case 'ARENA':
			return 'arenaforvalter'
		case 'INST2':
			return 'institusjonsopphold'
		default:
			return 'tpsf'
	}
}

export default DataSourceMapper
