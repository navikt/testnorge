export const fixNamesAndDuplicatesOfStatus = (statusListe: []) => {
	return [
		...new Map(
			statusListe
				.map(
					(status: {
						id: string
						navn: string
						miljo: string
						melding: string
						orgnummer: string
						identer: Array<string>
					}) => {
						if (status.id?.includes('TPS_MESSAGING')) {
							status.navn = 'Tjenestebasert personsystem (TPS)'
						}
						return status
					}
				)
				.map((v) => {
					return [JSON.stringify([v.navn, v.miljo, v.identer]), v]
				})
		).values(),
	]
}
