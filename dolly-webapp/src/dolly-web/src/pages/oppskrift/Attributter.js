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
			items: [{ id: 'statsborgerskap', type: 'text', label: 'Statsborgerskap' }]
		},
		{
			label: 'Diverse',
			items: [
				{
					id: 'kjonn',
					type: ' text',
					label: 'Kjønn'
				}
			]
		}
	],
	adresser: {},
	familierelasjoner: {},
	sivilstand: {}
}

export default Attributter
