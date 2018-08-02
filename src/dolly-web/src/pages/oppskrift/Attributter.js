const Attributter = {
	personinformasjon: [
		{
			label: 'Alder',
			items: [
				{ id: 'foedtEtter', type: 'date', label: 'Født etter' },
				{ id: 'foedtFoer', type: 'date', label: 'Født før' }
			]
		},
		{
			label: 'Nasjonalitet',
			items: [
				{
					id: 'statsborgerskap',
					type: 'select',
					label: 'Statsborgerskap',
					kodeverkNavn: 'statsborgerskap'
				}
			]
		},
		{
			label: 'Diverse',
			items: [
				{
					id: 'kjonn',
					type: 'select',
					label: 'Kjønn',
					options: [{ value: 'K', label: 'Kvinne' }, { value: 'M', label: 'Mann' }]
				}
			]
		}
	],
	adresser: {},
	familierelasjoner: {},
	sivilstand: {}
}

export default Attributter
